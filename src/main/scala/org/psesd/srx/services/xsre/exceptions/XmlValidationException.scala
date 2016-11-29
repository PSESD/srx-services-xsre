package org.psesd.srx.services.xsre.exceptions

/** Object representing XML schema validation error details.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class XmlValidationException(message: String,
                             cause: Throwable,
                             elementName: String,
                             lineNumber: Int,
                             columnNumber: Int,
                             xmlContent: String) extends RuntimeException(message, cause) {

  def getColumnNumber: Int = columnNumber

  def getElementName: String = elementName

  def getLineNumber: Int = lineNumber

  override def getMessage: String = {
    "XML schema validation error. Line=%s; Column=%s; Element=%s; Error=%s".format(lineNumber, columnNumber, elementName, message)
  }
}
