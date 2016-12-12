package org.psesd.srx.services.xsre

import org.psesd.srx.services.xsre.exceptions.XsdNotFoundException
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException, EnvironmentException, ExceptionMessage}
import org.scalatest.FunSuite

class SchemaCacheTests extends FunSuite {

  test("getSchema zone null") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val result = SchemaCache.getSchema(null)
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("zoneId")
    assert(thrown.getMessage.equals(expected))
  }

  test("getSchema invalid schema path") {
    val thrown = intercept[EnvironmentException] {
      val result = SchemaCache.getSchema("foo")
    }
    val message = thrown.getMessage
    val expected = "The requested ZoneConfig resource was not found."
    assert(message.equals(expected))
  }

  if (Environment.name == "local") {
    test("getSchema valid zone config") {

      // ensure we get a valid schema object on first get
      val result = SchemaCache.getSchema("test")
      assert(result != null)
      // ensure second get = the same schema object
      //val result2 = SchemaCache.getSchema("test")
      //assert(result2.equals(result))
    }
  }

}
