package org.psesd.srx.services.xsre

import org.psesd.srx.shared.core.config.ConfigCache
import org.psesd.srx.shared.core.exceptions.EnvironmentException
import org.psesd.srx.shared.core.extensions.TypeExtensions._

/** Represents zone-specific configuration and xSRE schema.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
class XsreConfig(val zoneId: String) {
  private val zoneConfigXml = ConfigCache.getConfig(zoneId, XsreServer.srxService.service.name).zoneConfigXml

  // nav to resource type="xSRE" and grab config data
  private val xsreConfigXml = (zoneConfigXml \ "resource").find(r => (r \ "@type").text.toLowerCase() == "xsre")
  if(xsreConfigXml.isEmpty) {
    throw new EnvironmentException("XSRE configuration missing for zone '%s'.".format(zoneId))
  }

  val cacheBucketName: String = (xsreConfigXml.get \ "cache" \ "bucketName").textRequired("cache.bucketName")
  val cachePath: String = (xsreConfigXml.get \ "cache" \ "path").textRequired("cache.path")
  val schemaPath: String = (xsreConfigXml.get \ "schema" \ "path").textRequired("schema.path")
  val schemaRootFileName: String = (xsreConfigXml.get \ "schema" \ "rootFileName").textRequired("schema.rootFileName")
}
