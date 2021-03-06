package org.psesd.srx.services.xsre

import org.http4s._
import org.http4s.dsl._
import org.psesd.srx.shared.core._
import org.psesd.srx.shared.core.config.{ConfigCache, Environment}
import org.psesd.srx.shared.core.sif._

import scala.concurrent.ExecutionContext

/** SRX xSRE server.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object XsreServer extends SrxServer {

  private final val ServerUrlKey = "SERVER_URL"
  private final val ConfigCacheResource = "configcache"
  private final val XsreResource = SrxResourceType.MasterXsres.toString

  private final val DatasourceClassNameKey = "DATASOURCE_CLASS_NAME"
  private final val DatasourceMaxConnectionsKey = "DATASOURCE_MAX_CONNECTIONS"
  private final val DatasourceTimeoutKey = "DATASOURCE_TIMEOUT"
  private final val DatasourceUrlKey = "DATASOURCE_URL"

  val sifProvider: SifProvider = new SifProvider(
    SifProviderUrl(Environment.getProperty(ServerUrlKey)),
    SifProviderSessionToken(Environment.getProperty(Environment.SrxSessionTokenKey)),
    SifProviderSharedSecret(Environment.getProperty(Environment.SrxSharedSecretKey)),
    SifAuthenticationMethod.SifHmacSha256
  )

  val srxService: SrxService = new SrxService(
    new SrxServiceComponent(Build.name, Build.version + "." + Build.buildNumber),
    List[SrxServiceComponent](
      new SrxServiceComponent("java", Build.javaVersion),
      new SrxServiceComponent("scala", Build.scalaVersion),
      new SrxServiceComponent("sbt", Build.sbtVersion)
    )
  )

  override def serviceRouter(implicit executionContext: ExecutionContext) = HttpService {

    case req@GET -> Root =>
      Ok()

    case _ -> Root =>
      NotImplemented()

    case req@GET -> Root / _ if services(req, SrxResourceType.Ping.toString) =>
      Ok(true.toString)

    case req@GET -> Root / _ if services(req, SrxResourceType.Info.toString) =>
      respondWithInfo(getDefaultSrxResponse(req))


    /* XSRE */

    case req@GET -> Root / _ if services(req, XsreResource) =>
      // executeRequest(req, None, xSreResource, Xsre)
      // GET ALL xSREs is not allowed
      MethodNotAllowed()

    case req@GET -> Root / `XsreResource` / _ =>
      executeRequest(req, None, XsreResource, Xsre)

    case req@POST -> Root / _ if services(req, XsreResource) =>
      // executeRequest(req, None, xSreResource, Xsre, Xsre.apply)
      // POST xSREs is not allowed - use PUT instead to Update if exists else Create
      MethodNotAllowed()

    case req@PUT -> Root / _ if services(req, XsreResource) =>
      MethodNotAllowed()

    case req@PUT -> Root / `XsreResource` / _ =>
      executeRequest(req, None, XsreResource, Xsre, Xsre.apply)

    case req@DELETE -> Root / _ if services(req, XsreResource) =>
      MethodNotAllowed()

    case req@DELETE -> Root / `XsreResource` / _ =>
      executeRequest(req, None, XsreResource, Xsre)


    /* CONFIG CACHE */

    case req@DELETE -> Root / _ if services(req, ConfigCacheResource) =>
      executeRequest(req, None, ConfigCacheResource, ConfigCache)


    case _ =>
      NotFound()
  }

}
