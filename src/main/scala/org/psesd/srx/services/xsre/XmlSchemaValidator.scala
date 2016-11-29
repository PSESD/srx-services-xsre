package org.psesd.srx.services.xsre

import java.io.StringReader
import javax.xml.stream.{XMLInputFactory, XMLStreamConstants, XMLStreamReader}
import javax.xml.transform.stax.StAXSource
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema

import org.psesd.srx.services.xsre.exceptions.XmlValidationException
import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.xml.sax.{ErrorHandler, SAXParseException}

import scala.collection.mutable.ArrayBuffer

/** Validates XML content against a collection of XSDs.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
object XmlSchemaValidator {

  def validate(xmlContent: String, schema: Schema): Boolean = {
    if (xmlContent.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("xmlContent")
    }
    if (schema == null) {
      throw new ArgumentNullException("schema")
    }

    // create validator with custom exception handler
    val streamSource = new StreamSource(new StringReader(xmlContent))
    val reader = XMLInputFactory.newInstance.createXMLStreamReader(streamSource)
    val errorHandler = new SaxParseErrorHandler(reader, xmlContent)
    val validator = schema.newValidator()
    validator.setErrorHandler(errorHandler)

    // validate and throw a validation exception if needed
    validator.validate(new StAXSource(reader))
    if (errorHandler.exceptions.nonEmpty) {
      throw errorHandler.exceptions.head
    }

    true
  }

  class SaxParseErrorHandler(reader: XMLStreamReader, xmlContent: String) extends ErrorHandler {
    var exceptions = new ArrayBuffer[XmlValidationException]()

    override def error(e: SAXParseException): Unit = {
      warning(e)
    }

    override def warning(e: SAXParseException): Unit = {
      val eventType = reader.getEventType
      var elementName = ""
      eventType match {
        case XMLStreamConstants.START_ELEMENT
             | XMLStreamConstants.END_ELEMENT
             | XMLStreamConstants.ENTITY_REFERENCE =>
          elementName = reader.getLocalName
        case _ =>
          elementName = reader.getText
      }
      exceptions += new XmlValidationException(e.getMessage, e.getCause, elementName, e.getLineNumber, e.getColumnNumber, xmlContent)
      ()
    }

    override def fatalError(e: SAXParseException): Unit = {
      warning(e)
    }
  }

}