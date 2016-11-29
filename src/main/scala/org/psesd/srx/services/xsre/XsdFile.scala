package org.psesd.srx.services.xsre

import scala.xml.Node

/** XSD file container.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class XsdFile(val rootPath: String, val filePath: String, val content: String) {

  val fileName: String = {
    if (filePath.contains("/")) {
      filePath.substring(filePath.lastIndexOf("/") + 1)
    } else {
      filePath
    }
  }

  var rootNode: Node = _
}
