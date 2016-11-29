package org.psesd.srx.services.xsre.exceptions

/** Enumeration of common exception messages.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object ExceptionMessage {
  final val AmazonS3Unauthorized = "Amazon S3 connection failed with 403: Forbidden. Check Amazon S3 configuration or environment variables."
  final val XsdInvalidException = "XSD resources are invalid."
  final val XsdNotFoundException = "XSD resource '%s' could not be found."
}