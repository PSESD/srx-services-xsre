package org.psesd.srx.services.xsre

import com.amazonaws.services.s3.model.AmazonS3Exception
import org.json4s.JValue
import org.psesd.srx.services.xsre.exceptions.AmazonS3UnauthorizedException
import org.psesd.srx.shared.core.config.{AmazonS3Config, Environment}
import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException, SrxResourceNotFoundException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.io.AmazonS3Client
import org.psesd.srx.shared.core.sif.SifRequestAction.SifRequestAction
import org.psesd.srx.shared.core.sif.{SifHttpStatusCode, SifRequestAction, SifRequestParameter, _}
import org.psesd.srx.shared.core.{SrxResource, SrxResourceErrorResult, SrxResourceResult, SrxResourceService}

import scala.xml.Node

/** Represents a Student Record Exchange entity.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
class Xsre(val id: String, val xsre: String) extends SrxResource {
  if (id.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("id parameter")
  }
  if (xsre.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("xsre parameter")
  }

  def toJson: JValue = {
    toXml.toJsonStringNoRoot.toJson
  }

  def toXml: Node = {
    xsre.toXml
  }

}

/** Represents a Student Record Exchange method result.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
class XsreResult(
                  requestAction: SifRequestAction,
                  httpStatusCode: Int,
                  id: String,
                  xsre: String
                ) extends SrxResourceResult {
  statusCode = httpStatusCode

  def toJson: Option[JValue] = {
    requestAction match {

      case SifRequestAction.Create =>
        Option(SifCreateResponse().addResult(id, statusCode).toXml.toJsonString.toJson)

      case SifRequestAction.Delete =>
        Option(SifDeleteResponse().addResult(id, statusCode).toXml.toJsonString.toJson)

      case SifRequestAction.Query =>
        if (statusCode == SifHttpStatusCode.Ok) {
          val sb = new StringBuilder("[")
          sb.append(xsre.toJson.toJsonString)
          sb.append("]")
          Some(sb.toString.toJson)
        } else {
          None
        }

      case SifRequestAction.Update =>
        Option(SifUpdateResponse().addResult(id, statusCode).toXml.toJsonString.toJson)

      case _ =>
        None
    }
  }

  def toXml: Option[Node] = {

    requestAction match {

      case SifRequestAction.Create =>
        Option(SifCreateResponse().addResult(id, statusCode).toXml)

      case SifRequestAction.Delete =>
        Option(SifDeleteResponse().addResult(id, statusCode).toXml)

      case SifRequestAction.Query =>
        if (statusCode == SifHttpStatusCode.Ok) {
          Option(xsre.toXml)
        } else {
          None
        }

      case SifRequestAction.Update =>
        Option(SifUpdateResponse().addResult(id, statusCode).toXml)

      case _ =>
        None
    }
  }
}

/** Student Record Exchange methods.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
object Xsre extends SrxResourceService {
  def apply(xsre: String): Xsre = {
    if (xsre == null) {
      throw new ArgumentNullException("xsre parameter")
    }
    Xsre(xsre.toXml, None)
  }

  def apply(xsreXml: Node, parameters: Option[List[SifRequestParameter]]): Xsre = {
    if (xsreXml == null) {
      throw new ArgumentNullException("xsreXml parameter")
    }
    val rootElementName = xsreXml.label.toLowerCase
    if (rootElementName != "xsre") {
      throw new ArgumentInvalidException("root element '%s'".format(rootElementName))
    }
    val id = (xsreXml \ "localId").textOption.getOrElse("")
    new Xsre(
      id,
      xsreXml.toXmlString
    )
  }

  def create(resource: SrxResource, parameters: List[SifRequestParameter]): SrxResourceResult = {
    SrxResourceErrorResult(SifHttpStatusCode.InternalServerError, new Exception("xSRE CREATE method not implemented."))
  }

  def delete(parameters: List[SifRequestParameter]): SrxResourceResult = {
    val zoneId = getZoneIdFromRequestParameters(parameters)
    val id = getKeyIdFromRequestParameters(parameters)
    if (zoneId.isEmpty || id.isEmpty || id.get == "-1") {
      if(zoneId.isEmpty) {
        SrxResourceErrorResult(SifHttpStatusCode.BadRequest, new ArgumentInvalidException("zoneId parameter"))
      } else {
        SrxResourceErrorResult(SifHttpStatusCode.BadRequest, new ArgumentInvalidException("id parameter"))
      }
    } else {
      try {
        deleteXsre(zoneId.get, id.get)
        new XsreResult(SifRequestAction.Delete, SifRequestAction.getSuccessStatusCode(SifRequestAction.Delete), id.get, "")
      } catch {
        case e: Exception =>
          SrxResourceErrorResult(SifHttpStatusCode.InternalServerError, e)
      }
    }
  }

  def query(parameters: List[SifRequestParameter]): SrxResourceResult = {
    val zoneId = getZoneIdFromRequestParameters(parameters)
    val id = getKeyIdFromRequestParameters(parameters)
    if (zoneId.isEmpty || id.isEmpty || id.get == "-1") {
      if(zoneId.isEmpty) {
        SrxResourceErrorResult(SifHttpStatusCode.BadRequest, new ArgumentInvalidException("zoneId parameter"))
      } else {
        SrxResourceErrorResult(SifHttpStatusCode.BadRequest, new ArgumentInvalidException("id parameter"))
      }
    } else {
      try {
        val xSre = getXsre(zoneId.get, id.get)
        if (xSre.isDefined) {
          new XsreResult(SifRequestAction.Query, SifRequestAction.getSuccessStatusCode(SifRequestAction.Query), id.get, xSre.get)
        } else {
          SrxResourceErrorResult(SifHttpStatusCode.NotFound, new SrxResourceNotFoundException("xSRE"))
        }
      } catch {
        case e: Exception =>
          SrxResourceErrorResult(SifHttpStatusCode.InternalServerError, e)
      }
    }
  }

  def update(resource: SrxResource, parameters: List[SifRequestParameter]): SrxResourceResult = {
    if (resource == null) {
      throw new ArgumentNullException("resource parameter")
    }
    val zoneId = getZoneIdFromRequestParameters(parameters)
    val id = getKeyIdFromRequestParameters(parameters)
    if (zoneId.isEmpty || id.isEmpty || id.get == "-1") {
      if(zoneId.isEmpty) {
        SrxResourceErrorResult(SifHttpStatusCode.BadRequest, new ArgumentInvalidException("zoneId parameter"))
      } else {
        SrxResourceErrorResult(SifHttpStatusCode.BadRequest, new ArgumentInvalidException("id parameter"))
      }
    } else {
      try {
        val xsre = resource.asInstanceOf[Xsre]
        updateXsre(zoneId.get, id.get, xsre)
        new XsreResult(SifRequestAction.Update, SifRequestAction.getSuccessStatusCode(SifRequestAction.Update), id.get, "")
      } catch {
        case e: Exception =>
          SrxResourceErrorResult(SifHttpStatusCode.InternalServerError, e)
      }
    }
  }

  protected def getKeyIdFromRequestParameters(parameters: List[SifRequestParameter]): Option[String] = {
    var result: Option[String] = None
    try {
      val id = getIdFromRequestParameters(parameters)
      if (id.isDefined) {
        result = Some(id.get)
      }
    } catch {
      case e: Exception =>
        result = Some("-1")
    }
    result
  }

  private def deleteXsre(zoneId: String, xsreId: String): Unit = {
    try {
      val fileName = XsreFile.getName(xsreId)
      val s3Client = getS3Client(zoneId)
      s3Client.delete(fileName)
      s3Client.shutdown
    } catch {
      case s3: AmazonS3Exception =>
        s3.getErrorCode match {
          case "403 Forbidden" =>
            throw new AmazonS3UnauthorizedException
          case _ =>
            throw s3
        }

      case ex: Exception =>
        throw ex
    }
  }

  private def getS3Client(zoneId: String) = {
    val accessKey = Environment.getProperty(AmazonS3Config.AccessKeyKey)
    val bucketName = Environment.getProperty(AmazonS3Config.BucketNameKey)
    val path = zoneId
    val secret = Environment.getProperty(AmazonS3Config.SecretKey)
    val socketTimeout = Environment.getProperty(AmazonS3Config.SocketTimeoutKey).toInt
    new AmazonS3Client(accessKey, secret, socketTimeout, bucketName, path)
  }

  private def getXsre(zoneId: String, xsreId: String): Option[String] = {
    var xsreFile: XsreFile = null

    try {
      val fileName = XsreFile.getName(xsreId)
      val s3Client = getS3Client(zoneId)
      if (s3Client.fileExists(fileName)) {
        xsreFile = new XsreFile(xsreId, s3Client.download(fileName))
      }
      s3Client.shutdown
    } catch {
      case s3: AmazonS3Exception =>
        s3.getErrorCode match {
          case "403 Forbidden" =>
            throw new AmazonS3UnauthorizedException
          case _ =>
            throw s3
        }

      case ex: Exception =>
        throw ex
    }

    if(xsreFile == null || xsreFile.content.isNullOrEmpty) {
      None
    } else {
      Some(xsreFile.content)
    }
  }

  private def getZoneIdFromRequestParameters(parameters: List[SifRequestParameter]): Option[String] = {
    if (parameters != null && parameters.nonEmpty) {
      val idParameter = parameters.find(p => p.key.toLowerCase == "zoneid").orNull
      if (idParameter != null) {
        Some(idParameter.value)
      } else {
        None
      }
    } else {
      None
    }
  }

  private def updateXsre(zoneId: String, xsreId: String, xsre: Xsre): Unit = {
    try {
      val fileName = XsreFile.getName(xsreId)
      val s3Client = getS3Client(zoneId)
      s3Client.upload(fileName, xsre.xsre)
      s3Client.shutdown
    } catch {
      case s3: AmazonS3Exception =>
        s3.getErrorCode match {
          case "403 Forbidden" =>
            throw new AmazonS3UnauthorizedException
          case _ =>
            throw s3
        }

      case ex: Exception =>
        throw ex
    }
  }

}
