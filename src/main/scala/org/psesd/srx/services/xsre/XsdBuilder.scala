package org.psesd.srx.services.xsre

import org.psesd.srx.services.xsre.exceptions.XsdNotFoundException
import org.psesd.srx.shared.core.extensions.TypeExtensions._

import scala.collection.mutable.ArrayBuffer
import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Elem, Node, NodeSeq, XML}

/** Builds XSD schema from multiple input sources.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object XsdBuilder {

  private val removeIncludeNode = new RewriteRule {
    override def transform(n: Node): NodeSeq = n match {
      case e: Elem if (e \ "@schemaLocation").text.nonEmpty => NodeSeq.Empty
      case n => n
    }
  }

  def mergeSchemas(xsdFiles: List[XsdFile], rootFileName: String): String = {

    val includeXsds = new ArrayBuffer[XsdFile]()

    // attempt to find the root xsd in the collection of available xsds
    val rootXsdFile = xsdFiles.find(f => f.fileName == rootFileName).orNull

    if (rootXsdFile != null) {
      // load xsd content into XML node
      rootXsdFile.rootNode = XML.loadString(rootXsdFile.content)

      // append included xsds
      appendIncludeXsds(xsdFiles, includeXsds, rootXsdFile.rootNode)

      // remove original <xs:include/> elements
      rootXsdFile.rootNode = new RuleTransformer(removeIncludeNode).transform(rootXsdFile.rootNode).head

      // add each included xsd child element (not the <xs:schema> element itself) to the root xsd
      for (includeXsd <- includeXsds) {
        for (includeNode <- includeXsd.rootNode.child) {
          rootXsdFile.rootNode = new RuleTransformer(new AddChildrenTo("schema", includeNode)).transform(rootXsdFile.rootNode).head
        }
      }

      // return the modified root xsd string
      rootXsdFile.rootNode.toString

    } else {
      // could not find root xsd so throw exception
      throw new XsdNotFoundException(rootFileName)
    }
  }

  private def appendIncludeXsds(xsdFiles: List[XsdFile], includeXsds: ArrayBuffer[XsdFile], rootNode: Node): Unit = {
    // for each <xs:include/> element
    (rootNode \ "include").foreach { includeNode =>

      // get the schemaLocation attribute
      val schemaLocation = (includeNode \ "@schemaLocation").text

      // find the xsd that matches the schemaLocation
      val includeXsd = getIncludeXsd(xsdFiles, schemaLocation)

      // if we found an xsd and it is not already in the list of xsds to include
      if (includeXsd != null
        && includeXsds.find(f => includeXsd.fileName.toLowerCase == f.fileName.toLowerCase).orNull == null) {

        // load xsd content into XML node
        includeXsd.rootNode = XML.loadString(includeXsd.content)

        // add the xsd file to the list of xsds to include
        includeXsds += includeXsd

        // recursively append additional includes within this include xsd
        appendIncludeXsds(xsdFiles, includeXsds, includeXsd.rootNode)

        // remove the child <xs:include> elements from this include xsd
        includeXsd.rootNode = new RuleTransformer(removeIncludeNode).transform(includeXsd.rootNode).head
      }
    }
  }

  private def getIncludeXsd(xsdFiles: List[XsdFile], schemaLocation: String): XsdFile = {
    if (schemaLocation.isNullOrEmpty) {
      null
    } else {
      xsdFiles.find(f => schemaLocation.toLowerCase.endsWith(f.fileName.toLowerCase)).orNull
    }
  }

  private def addChild(n: Node, newChild: Node) = n match {
    case Elem(prefix, label, attribs, scope, child@_*) =>
      Elem(prefix, label, attribs, scope, true, child ++ newChild: _*)
  }

  private class AddChildrenTo(label: String, newChild: Node) extends RewriteRule {
    override def transform(n: Node): Node = n match {
      case n@Elem(_, `label`, _, _, _*) => addChild(n, newChild)
      case other => other
    }
  }

}
