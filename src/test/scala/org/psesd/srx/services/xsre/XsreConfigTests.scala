package org.psesd.srx.services.xsre

import org.psesd.srx.shared.core.exceptions.EnvironmentException
import org.scalatest.FunSuite

class XsreConfigTests extends FunSuite {

  test("constructor invalid zone") {
    val zoneId = "foo"
    val thrown = intercept[EnvironmentException] {
      new XsreConfig(zoneId)
    }
    assert(thrown.getMessage.equals("The requested ZoneConfig resource was not found."))
  }

  test("constructor valid") {
    val zoneId = "test"
    val xsreConfig = new XsreConfig(zoneId)
    assert(xsreConfig.zoneId.equals(zoneId))
    assert(xsreConfig.cacheBucketName.equals("srx-development"))
    assert(xsreConfig.cachePath.equals("cache/xsres/test"))
    assert(xsreConfig.schemaPath.equals("config/schemas/sif_3_3"))
    assert(xsreConfig.schemaRootFileName.equals("SIFNAxSRE.xsd"))
  }

}
