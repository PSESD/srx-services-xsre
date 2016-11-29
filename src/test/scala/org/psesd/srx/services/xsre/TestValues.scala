package org.psesd.srx.services.xsre

import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.sif._

object TestValues {

  lazy val sifAuthenticationMethod = SifAuthenticationMethod.SifHmacSha256
  lazy val sessionToken = SifProviderSessionToken(Environment.getProperty(Environment.SrxSessionTokenKey))
  lazy val sharedSecret = SifProviderSharedSecret(Environment.getProperty(Environment.SrxSharedSecretKey))
  lazy val sifUrl: SifProviderUrl = SifProviderUrl("http://localhost:%s".format(Environment.getPropertyOrElse("SERVER_PORT", "80")))
  lazy val sifProvider = new SifProvider(sifUrl, sessionToken, sharedSecret, sifAuthenticationMethod)
  lazy val timestamp: SifTimestamp = SifTimestamp("2015-02-24T20:51:59.878Z")
  lazy val authorization = new SifAuthorization(sifProvider, timestamp)
  lazy val generatorId = "srx-services-xsre test"
  lazy val testXsre = Xsre(<xSre refId="afbdfd84-af6e-4f14-ab1f-9d43467696a3" xmlns:sif="http://www.sifassociation.org/datamodel/na/3.3" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
    <name>
      <familyName>Person</familyName>
      <givenName>Some</givenName>
      <middleName>Mock</middleName>
    </name>
    <localId>999</localId>
    <otherIds>
      <otherId>
        <type>State</type>
        <id>1234567890</id>
      </otherId>
    </otherIds>
    <demographics>
      <races>
        <race>
          <race>White</race>
        </race>
      </races>
      <sex>Male</sex>
      <birthDate>1996-10-17</birthDate>
    </demographics>
    <reportDate>2015-05-29</reportDate>
  </xSre>, None)
  lazy val testXsreInvalid = Xsre(<xSre><localId>999</localId></xSre>, None)

}
