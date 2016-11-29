package org.psesd.srx.services.xsre

import org.psesd.srx.shared.core.exceptions.EnvironmentException
import org.scalatest.FunSuite

class ZoneConfigTests extends FunSuite {

  test("constructor invalid zone") {
    val zoneId = "foo"
    val thrown = intercept[EnvironmentException] {
      new ZoneConfig(zoneId)
    }
    assert(thrown.getMessage.equals("XSRE configuration missing for zone 'foo'."))
  }

  test("constructor valid") {
    val zoneId = "test"
    val zoneConfig = new ZoneConfig(zoneId)
    assert(zoneConfig.zoneId.equals(zoneId))
    assert(zoneConfig.cacheBucketName.equals("p2-xsre-cache-dev"))
    assert(zoneConfig.cachePath.equals("test"))
    assert(zoneConfig.schemaPath.equals("xsd/sif_3_3"))
    assert(zoneConfig.schemaRootFileName.equals("SIFNAxSRE.xsd"))
  }

}
