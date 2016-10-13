package org.psesd.srx.services.xsre

/** Provides information about current build.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
object Build {
  val name: String = org.psesd.srx.services.xsre.BuildInfo.name

  val version: String = org.psesd.srx.services.xsre.BuildInfo.version

  val scalaVersion: String = org.psesd.srx.services.xsre.BuildInfo.scalaVersion

  val sbtVersion: String = org.psesd.srx.services.xsre.BuildInfo.sbtVersion

  val buildNumber: Int = org.psesd.srx.services.xsre.BuildInfo.buildNumber

  val javaVersion: String = scala.util.Properties.javaVersion
}