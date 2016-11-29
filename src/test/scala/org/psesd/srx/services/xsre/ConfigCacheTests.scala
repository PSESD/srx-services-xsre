package org.psesd.srx.services.xsre

import org.psesd.srx.shared.core.exceptions.EnvironmentException
import org.psesd.srx.shared.core.sif.SifRequestParameter
import org.scalatest.FunSuite

class ConfigCacheTests extends FunSuite {

  test("constructor invalid zone") {
    val zoneId = "foo"
    val thrown = intercept[EnvironmentException] {
      ConfigCache.getConfig(zoneId)
    }
    assert(thrown.getMessage.equals("XSRE configuration missing for zone 'foo'."))
  }

  test("get zone config") {
    val zoneId = "test"
    val zoneConfig = ConfigCache.getConfig(zoneId)
    assert(zoneConfig.zoneId.equals(zoneId))
    assert(zoneConfig.cacheBucketName.equals("p2-xsre-cache-dev"))
    assert(zoneConfig.cachePath.equals("test"))
    assert(zoneConfig.schemaPath.equals("xsd/sif_3_3"))
    assert(zoneConfig.schemaRootFileName.equals("SIFNAxSRE.xsd"))
  }

  test("clear cache") {
    val result = ConfigCache.delete(List[SifRequestParameter]())
    assert(result.statusCode == 200)
  }

}
