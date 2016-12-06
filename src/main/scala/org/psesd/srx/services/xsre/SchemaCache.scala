package org.psesd.srx.services.xsre

import java.io.StringReader
import javax.xml.transform.Source
import javax.xml.transform.sax.SAXSource
import javax.xml.validation.{Schema, SchemaFactory}

import org.psesd.srx.services.xsre.exceptions.{XsdInvalidException, XsdNotFoundException}
import org.psesd.srx.shared.core.config.{AmazonS3Config, Environment, ZoneConfig}
import org.psesd.srx.shared.core.exceptions._
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.io.AmazonS3Client
import org.xml.sax.InputSource

import scala.collection.concurrent.TrieMap
import scala.collection.mutable.ArrayBuffer

/** Cache of shared xsd Schema objects for SRE/xSRE validation.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * @author Margarett Ly (iTrellis, LLC)
  **/
object SchemaCache {
  private val schemas = new TrieMap[String, Schema]

  private def getKey(zoneConfig: ZoneConfig): String = {
    "%s_%s".format(zoneConfig.schemaPath, zoneConfig.schemaRootFileName)
  }

  def getSchema(zoneId: String): Schema = {
    if (zoneId.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("zoneId")
    }

    val zoneConfig = new ZoneConfig(zoneId, XsreServer.srxService.service.name)
    val key = getKey(zoneConfig)

    var schema: Schema = schemas.getOrElse(key, null)

    // if not found, construct and cache for future use
    if (schema == null) {
      val schemaLang = "http://www.w3.org/2001/XMLSchema"
      val factory = SchemaFactory.newInstance(schemaLang)

      // create schema and handle invalid XSD content
      val mergedXsd = XsdBuilder.mergeSchemas(getXsdFiles(zoneConfig), zoneConfig.schemaRootFileName)
      val sources: Array[Source] = Array(new SAXSource(new InputSource(new StringReader(mergedXsd))))
      try {
        schema = factory.newSchema(sources)
      } catch {
        case e: Exception =>
          throw new XsdInvalidException()
      }

      schemas.put(key, schema)
    }

    schema
  }

  private def getXsdFiles(zoneConfig: ZoneConfig): List[XsdFile] = {
    val xsdList = new ArrayBuffer[XsdFile]()

    val xsdPath = zoneConfig.schemaPath
    val xsdFullPath = "%s/%s".format(Environment.getProperty(AmazonS3Config.PathKey), xsdPath)
    val s3Client = AmazonS3Client(xsdFullPath)

    val files = s3Client.list("", false)
    // if no files were found at specified location, throw exception
    if(files.isEmpty){
      throw new XsdNotFoundException(xsdPath)
    }

    for (file <- files) {
      if (file.toLowerCase().endsWith(".xsd")) {
        val fileName = file.substring(xsdFullPath.length + 1)
        try {
          xsdList += new XsdFile(
            xsdPath,
            fileName,
            s3Client.download(fileName))
        } catch {
          case e: Exception =>
            throw new XsdNotFoundException(file)
        }
      }
    }

    s3Client.shutdown
    xsdList.toList
  }

}
