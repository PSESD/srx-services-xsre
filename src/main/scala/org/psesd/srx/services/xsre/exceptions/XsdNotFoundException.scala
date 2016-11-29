package org.psesd.srx.services.xsre.exceptions

/** Exception for XSD resource not found.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class XsdNotFoundException(val path: String) extends Exception(
  ExceptionMessage.XsdNotFoundException.format(path)
)
