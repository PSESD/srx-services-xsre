package org.psesd.srx.services.xsre

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.util.EntityUtils
import org.http4s.dsl._
import org.http4s.{Method, Request}
import org.psesd.srx.shared.core.CoreResource
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.extensions.HttpTypeExtensions._
import org.psesd.srx.shared.core.sif._
import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

class XsreServerTests extends FunSuite {

  private final val ServerDuration = 8000
  private lazy val tempServer = Future {
    delayedInterrupt(ServerDuration)
    intercept[InterruptedException] {
      startServer()
    }
  }
  private val pendingInterrupts = new ThreadLocal[List[Thread]] {
    override def initialValue = Nil
  }

  test("service") {
    assert(XsreServer.srxService.service.name.equals(Build.name))
    assert(XsreServer.srxService.service.version.equals(Build.version + "." + Build.buildNumber))
    assert(XsreServer.srxService.buildComponents(0).name.equals("java"))
    assert(XsreServer.srxService.buildComponents(0).version.equals(Build.javaVersion))
    assert(XsreServer.srxService.buildComponents(1).name.equals("scala"))
    assert(XsreServer.srxService.buildComponents(1).version.equals(Build.scalaVersion))
    assert(XsreServer.srxService.buildComponents(2).name.equals("sbt"))
    assert(XsreServer.srxService.buildComponents(2).version.equals(Build.sbtVersion))
  }

  test("ping (localhost)") {
    if (Environment.isLocal) {
      val expected = "true"
      var actual = ""
      tempServer onComplete {
        case Success(x) =>
          assert(actual.equals(expected))
        case _ =>
      }

      // wait for server to init
      Thread.sleep(2000)

      // ping server and collect response
      val httpclient: CloseableHttpClient = HttpClients.custom().disableCookieManagement().build()
      val httpGet = new HttpGet("http://localhost:%s/ping".format(Environment.getPropertyOrElse("SERVER_PORT", "80")))
      val response = httpclient.execute(httpGet)
      actual = EntityUtils.toString(response.getEntity)
    }
  }

  test("root") {
    val getRoot = Request(Method.GET, uri("/"))
    val task = XsreServer.service.run(getRoot)
    val response = task.run
    assert(response.status.code.equals(SifHttpStatusCode.Ok))
  }

  test("ping") {
    if (Environment.isLocal) {
      val getPing = Request(Method.GET, uri("/ping"))
      val task = XsreServer.service.run(getPing)
      val response = task.run
      val body = response.body.value
      assert(response.status.code.equals(SifHttpStatusCode.Ok))
      assert(body.equals(true.toString))
    }
  }

  test("info (localhost)") {
    if (Environment.isLocal) {
      val sifRequest = new SifRequest(TestValues.sifProvider, CoreResource.Info.toString)
      val response = new SifConsumer().query(sifRequest)
      val responseBody = response.body.getOrElse("")
      assert(response.statusCode.equals(SifHttpStatusCode.Ok))
      assert(response.contentType.get.equals(SifContentType.Xml))
      assert(responseBody.contains("<service>"))
    }
  }


  /* XSRE ROUTES */

  test("create xSRE not allowed") {
    if (Environment.isLocal) {
      val resource = "masterXsres"
      val sifRequest = new SifRequest(TestValues.sifProvider, resource, SifZone("test"), SifContext())
      sifRequest.generatorId = Some(TestValues.generatorId)
      sifRequest.body = Some(TestValues.testXsre.xsre)
      val response = new SifConsumer().create(sifRequest)
      assert(response.statusCode.equals(SifHttpStatusCode.MethodNotAllowed))
    }
  }

  test("update all xSREs not allowed") {
    if (Environment.isLocal) {
      val resource = "masterXsres"
      val sifRequest = new SifRequest(TestValues.sifProvider, resource, SifZone("test"), SifContext())
      sifRequest.generatorId = Some(TestValues.generatorId)
      sifRequest.body = Some(TestValues.testXsre.xsre)
      val response = new SifConsumer().update(sifRequest)
      assert(response.statusCode.equals(SifHttpStatusCode.MethodNotAllowed))
    }
  }

  test("update xSRE valid") {
    if (Environment.isLocal) {
      val resource = "masterXsres/999"
      val sifRequest = new SifRequest(TestValues.sifProvider, resource, SifZone("test"), SifContext())
      sifRequest.generatorId = Some(TestValues.generatorId)
      sifRequest.body = Some(TestValues.testXsre.xsre)
      val response = new SifConsumer().update(sifRequest)
      assert(response.statusCode.equals(SifHttpStatusCode.Ok))
    }
  }

  test("query all xSREs not allowed") {
    if (Environment.isLocal) {
      val resource = "masterXsres"
      val sifRequest = new SifRequest(TestValues.sifProvider, resource, SifZone("test"), SifContext())
      sifRequest.generatorId = Some(TestValues.generatorId)
      val response = new SifConsumer().query(sifRequest)
      assert(response.statusCode.equals(SifHttpStatusCode.MethodNotAllowed))
    }
  }

  test("query xSRE not found") {
    if (Environment.isLocal) {
      val resource = "masterXsres/-987"
      val sifRequest = new SifRequest(TestValues.sifProvider, resource, SifZone("test"), SifContext())
      sifRequest.generatorId = Some(TestValues.generatorId)
      val response = new SifConsumer().query(sifRequest)
      assert(response.statusCode.equals(SifHttpStatusCode.NotFound))
    }
  }

  test("query xSRE valid") {
    if (Environment.isLocal) {
      val resource = "masterXsres/123"
      val sifRequest = new SifRequest(TestValues.sifProvider, resource, SifZone("test"), SifContext())
      sifRequest.generatorId = Some(TestValues.generatorId)
      val response = new SifConsumer().query(sifRequest)
      val body = response.getBodyXml
      assert(response.statusCode.equals(SifHttpStatusCode.Ok))
      assert((body.get \ "localId").text == "123")
    }
  }

  test("delete all xSREs not allowed") {
    if (Environment.isLocal) {
      val resource = "masterXsres"
      val sifRequest = new SifRequest(TestValues.sifProvider, resource, SifZone("test"), SifContext())
      sifRequest.generatorId = Some(TestValues.generatorId)
      val response = new SifConsumer().delete(sifRequest)
      assert(response.statusCode.equals(SifHttpStatusCode.MethodNotAllowed))
    }
  }

  test("delete xSRE valid") {
    if (Environment.isLocal) {
      val resource = "masterXsres/999"
      val sifRequest = new SifRequest(TestValues.sifProvider, resource, SifZone("test"), SifContext())
      sifRequest.generatorId = Some(TestValues.generatorId)
      val response = new SifConsumer().delete(sifRequest)
      assert(response.statusCode.equals(SifHttpStatusCode.Ok))
    }
  }

  test("delete configCache valid") {
    if (Environment.isLocal) {
      val resource = "configcache"
      val sifRequest = new SifRequest(TestValues.sifProvider, resource, SifZone("test"), SifContext())
      sifRequest.generatorId = Some(TestValues.generatorId)
      val response = new SifConsumer().delete(sifRequest)
      assert(response.statusCode.equals(SifHttpStatusCode.Ok))
    }
  }


  private def delayedInterrupt(delay: Long) {
    delayedInterrupt(Thread.currentThread, delay)
  }

  private def delayedInterrupt(target: Thread, delay: Long) {
    val t = new Thread {
      override def run() {
        Thread.sleep(delay)
        target.interrupt()
      }
    }
    pendingInterrupts.set(t :: pendingInterrupts.get)
    t.start()
  }

  private def startServer(): Unit = {
    if (Environment.isLocal) {
      XsreServer.main(Array[String]())
    }
  }

  private def printlnResponse(response: SifResponse): Unit = {
    println("STATUS CODE: " + response.statusCode.toString)
    for (header <- response.getHeaders) {
      println("%s=%s".format(header._1, header._2))
    }
    println(response.getBody(SifContentType.Xml))
  }

}
