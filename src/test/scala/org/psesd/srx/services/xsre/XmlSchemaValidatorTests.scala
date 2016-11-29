package org.psesd.srx.services.xsre

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.scalatest.FunSuite

class XmlSchemaValidatorTests extends FunSuite {

  test("validate xmlContent null") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val result = XmlSchemaValidator.validate(null, null)
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("xmlContent")
    assert(thrown.getMessage.equals(expected))
  }

  test("validate xmlContent empty") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val result = XmlSchemaValidator.validate("", null)
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("xmlContent")
    assert(thrown.getMessage.equals(expected))
  }

  test("validate xmlContent whitespace") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val result = XmlSchemaValidator.validate(" ", null)
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("xmlContent")
    assert(thrown.getMessage.equals(expected))
  }

  test("validate schema null") {
    val thrown = intercept[ArgumentNullException] {
      val result = XmlSchemaValidator.validate("<xml/>", null)
    }
    val expected = ExceptionMessage.NotNull.format("schema")
    assert(thrown.getMessage.equals(expected))
  }

}