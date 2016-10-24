package org.psesd.srx.services.xsre.exceptions

/** Exception for unauthorized Amazon S3 requests.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class AmazonS3UnauthorizedException() extends SecurityException(
  ExceptionMessage.AmazonS3Unauthorized
)
