package org.psesd.srx.services.xsre

import org.json4s.JValue
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif.SifRequestAction.SifRequestAction
import org.psesd.srx.shared.core.sif.{SifHttpStatusCode, SifRequestAction, SifRequestParameter, _}
import org.psesd.srx.shared.core.{SrxResource, SrxResourceErrorResult, SrxResourceResult, SrxResourceService}

import scala.collection.concurrent.TrieMap
import scala.xml.Node

/** Represents a Config Cache method result.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
class ConfigCacheResult(
                         requestAction: SifRequestAction,
                         httpStatusCode: Int
                       ) extends SrxResourceResult {
  statusCode = httpStatusCode

  def toJson: Option[JValue] = {
    requestAction match {

      case SifRequestAction.Delete =>
        Option(SifDeleteResponse().addResult("0", statusCode).toXml.toJsonString.toJson)

      case _ =>
        None
    }
  }

  def toXml: Option[Node] = {

    requestAction match {

      case SifRequestAction.Delete =>
        Option(SifDeleteResponse().addResult("0", statusCode).toXml)

      case _ =>
        None
    }
  }
}

/** Config Cache methods.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
object ConfigCache extends SrxResourceService {

  val cache = new TrieMap[String, ZoneConfig]

  def create(resource: SrxResource, parameters: List[SifRequestParameter]): SrxResourceResult = {
    SrxResourceErrorResult(SifHttpStatusCode.InternalServerError, new Exception("ConfigCache CREATE method not implemented."))
  }

  def delete(parameters: List[SifRequestParameter]): SrxResourceResult = {
    try {
      cache.clear()
      new ConfigCacheResult(SifRequestAction.Delete, SifRequestAction.getSuccessStatusCode(SifRequestAction.Delete))
    } catch {
      case e: Exception =>
        SrxResourceErrorResult(SifHttpStatusCode.InternalServerError, e)
    }
  }

  def query(parameters: List[SifRequestParameter]): SrxResourceResult = {
    SrxResourceErrorResult(SifHttpStatusCode.InternalServerError, new Exception("ConfigCache QUERY method not implemented."))
  }

  def update(resource: SrxResource, parameters: List[SifRequestParameter]): SrxResourceResult = {
    SrxResourceErrorResult(SifHttpStatusCode.InternalServerError, new Exception("ConfigCache UPDATE method not implemented."))
  }

  def getConfig(zoneId: String): ZoneConfig = {
    val config = cache.get(zoneId)
    if(config.isDefined) {
      config.get
    } else {
      val zoneConfig = new ZoneConfig(zoneId)
      cache.put(zoneId, zoneConfig)
      zoneConfig
    }
  }

}
