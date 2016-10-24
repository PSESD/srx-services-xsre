package org.psesd.srx.services.xsre

import org.psesd.srx.shared.core.exceptions.ArgumentNullOrEmptyOrWhitespaceException
import org.psesd.srx.shared.core.extensions.TypeExtensions._

/** xSRE file container.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class XsreFile(val id: String, val content: String) {
  if (id.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("id")
  }

  if (content.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("content")
  }

  val name = {
    XsreFile.getName(id)
  }
}

object XsreFile {

  def getName(id: String): String = {
    if (id.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("id")
    }
    "%s.xml".format(id)
  }

}