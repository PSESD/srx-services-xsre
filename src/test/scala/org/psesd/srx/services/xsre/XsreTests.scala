package org.psesd.srx.services.xsre

import org.psesd.srx.shared.core.SrxResourceErrorResult
import org.psesd.srx.shared.core.exceptions.ArgumentInvalidException
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif.{SifHttpStatusCode, SifRequestParameter}
import org.scalatest.FunSuite

class XsreTests extends FunSuite {

  test("constructor") {
    val id = "999"
    val xsreXml = <xsre><localId>{id}</localId></xsre>
    val xsre = new Xsre(id, xsreXml.toXmlString)
    assert(xsre.id.equals(id))
    assert(xsre.xsre.contains("<xsre>"))
  }

  test("factory") {
    val id = "999"
    val xsreXml = <xsre><localId>{id}</localId></xsre>
    val xsre = Xsre(xsreXml.toXmlString)
    assert(xsre.id.equals(id))
    assert(xsre.xsre.contains("<xsre>"))
  }

  test("factory invalid") {
    val thrown = intercept[ArgumentInvalidException] {
      val xsre = Xsre(<foo></foo>, None)
    }

    assert(thrown.getMessage.equals("The root element 'foo' is invalid."))
  }

  test("node") {
    val id = "999"
    val xsreXml = <xsre><localId>{id}</localId></xsre>
    val xsre = Xsre(xsreXml, None)
    assert(xsre.id.equals(id))
    assert(xsre.xsre.contains("<xsre>"))
  }

  test("create not implemented") {
    val result = Xsre.create(TestValues.testXsre, TestValues.testXsreParameters)
    assert(!result.success)
    assert(result.statusCode == SifHttpStatusCode.InternalServerError)
    assert(result.exceptions.head.getMessage == "xSRE CREATE method not implemented.")
  }

  test("update no zoneId") {
    val result = Xsre.update(TestValues.testXsre, List[SifRequestParameter]())
    assert(!result.success)
    assert(result.statusCode == SifHttpStatusCode.BadRequest)
    assert(result.exceptions.head.getMessage == "The zoneId parameter is invalid.")
    assert(result.toXml.isEmpty)
  }

  test("update no id") {
    val result = Xsre.update(TestValues.testXsre, List[SifRequestParameter](SifRequestParameter("zoneId", "test")))
    assert(!result.success)
    assert(result.statusCode == SifHttpStatusCode.BadRequest)
    assert(result.exceptions.head.getMessage == "The id parameter is invalid.")
    assert(result.toXml.isEmpty)
  }

  test("update invalid") {
    val result = Xsre.update(TestValues.testXsreInvalid, TestValues.testXsreParameters).asInstanceOf[SrxResourceErrorResult]
    assert(!result.success)
    assert(result.statusCode == SifHttpStatusCode.BadRequest)
    assert(result.exceptions.head.getMessage.contains("Attribute 'refId' must appear on element 'xSre'"))
  }

  test("update valid") {
    val result = Xsre.update(TestValues.testXsre, TestValues.testXsreParameters).asInstanceOf[XsreResult]
    assert(result.success)
    assert(result.exceptions.isEmpty)
    assert(result.toXml.get.toXmlString.contains("id=\"%s\"".format("999")))
  }

  test("query no zoneId") {
    val result = Xsre.query(List[SifRequestParameter]())
    assert(!result.success)
    assert(result.statusCode == SifHttpStatusCode.BadRequest)
    assert(result.exceptions.head.getMessage == "The zoneId parameter is invalid.")
    assert(result.toXml.isEmpty)
  }

  test("query no id") {
    val result = Xsre.query(List[SifRequestParameter](SifRequestParameter("zoneId", "test")))
    assert(!result.success)
    assert(result.statusCode == SifHttpStatusCode.BadRequest)
    assert(result.exceptions.head.getMessage == "The id parameter is invalid.")
    assert(result.toXml.isEmpty)
  }

  test("query id = -1") {
    val result = Xsre.query(List[SifRequestParameter](SifRequestParameter("zoneId", "test"), SifRequestParameter("id", "-1")))
    assert(!result.success)
    assert(result.statusCode == SifHttpStatusCode.BadRequest)
    assert(result.exceptions.head.getMessage == "The id parameter is invalid.")
    assert(result.toXml.isEmpty)
  }

  test("query not found") {
    val result = Xsre.query(List[SifRequestParameter](SifRequestParameter("zoneId", "test"), SifRequestParameter("id", "-99")))
    assert(!result.success)
    assert(result.statusCode == SifHttpStatusCode.NotFound)
    assert(result.exceptions.head.getMessage == "The requested xSRE resource was not found.")
    assert(result.toXml.isEmpty)
  }

  test("query valid") {
    val result = Xsre.query(TestValues.testXsreParameters)
    assert(result.success)
    assert(result.statusCode == SifHttpStatusCode.Ok)
    assert(result.toXml.isDefined)
  }

  test("delete no zoneId") {
    val result = Xsre.delete(List[SifRequestParameter]())
    assert(!result.success)
    assert(result.statusCode == SifHttpStatusCode.BadRequest)
    assert(result.exceptions.head.getMessage == "The zoneId parameter is invalid.")
    assert(result.toXml.isEmpty)
  }

  test("delete no id") {
    val result = Xsre.delete(List[SifRequestParameter](SifRequestParameter("zoneId", "test")))
    assert(!result.success)
    assert(result.statusCode == SifHttpStatusCode.BadRequest)
    assert(result.exceptions.head.getMessage == "The id parameter is invalid.")
    assert(result.toXml.isEmpty)
  }

  test("delete valid") {
    val result = Xsre.delete(TestValues.testXsreParameters).asInstanceOf[XsreResult]
    assert(result.success)
    assert(result.exceptions.isEmpty)
    assert(result.toXml.get.toXmlString.contains("id=\"%s\"".format("999")))
  }

  test("validate valid") {
    Xsre.validateXsre("test", TestValues.testXsre)
  }


}
