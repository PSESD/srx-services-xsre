# Srx-Services-xSre
**Stores and retrieves xSRE XML for District to CBO transfers.**

***
XSREs are retrieved via a GET request using the following URL format:

```
https://[baseUrl]/xsres/[studentId];zoneId=[zoneId];contextId=[contextId]
```

Variable | Description | Example
--------- | ----------- | -------
baseUrl   | URL of the deployment environment hosting the adapter endpoints. | srx-services-xsre-dev.herokuapp.com
studentId | Unique identifier of a student xSRE record to retrieve. | 123
zoneId    | Zone containing the requested student xSRE record | seattle
contextId | Client context of request. | CBO


The following required headers must be present in the GET request:

Header | Description | Example
------ | ----------- | -------
x-forwarded-proto | Must be set to: https | https
x-forwarded-port | Must be set to: 443 | 443
timeStamp | Must be set to a valid date/time in the following format: yyyy-MM-ddTHH:mm:ss:S | 2015-08-31T20:41:56.794820
authorization | Must be set to a valid HMAC-SHA256 encrypted authorization token | SIF_HMACSHA256 YmU4NjBjNDctNmJkNS00OTUzL...


The following optional headers may also be included:

Header | Description | Example
------ | ----------- | -------
messageType | If specified, must be set to: REQUEST | REQUEST
serviceType | If specified, must be set to: OBJECT | OBJECT
requestAction | If specified, must be set to: QUERY | QUERY
requestType | If specified, must be set to: IMMEDIATE | IMMEDIATE
messageId | Consumer-generated. If specified, must be set to a valid UUID | ba74efac-94c1-42bf-af8b-9b149d067816
requestId | Consumer-generated. If specified, must be set to a valid UUID | ba74efac-94c1-42bf-af8b-9b149d067816

***
## Refreshing the xSRE record cache
***

The xSRE cache for a zone is refreshed via a POST request using the following URL format:

```
https://[baseUrl]/xsres/refresh;zoneId=[zoneId];contextId=[contextId]
```

Variable | Description | Example
--------- | ----------- | -------
baseUrl   | URL of the deployment environment hosting the adapter endpoints. | srx-services-xsre-dev.herokuapp.com
zoneId    | Zone containing the requested student xSRE record | seattle
contextId | Client context of request. | CBO


The following required headers must be present in the GET request:

Header | Description | Example
------ | ----------- | -------
x-forwarded-proto | Must be set to: https | https
x-forwarded-port | Must be set to: 443 | 443
timeStamp | Must be set to a valid date/time in the following format: yyyy-MM-ddTHH:mm:ss:S | 2015-08-31T20:41:56.794820
authorization | Must be set to a valid HMAC-SHA256 encrypted authorization token | SIF_HMACSHA256 YmU4NjBjNDctNmJkNS00OTUzL...


The following optional headers may also be included:

Header | Description | Example
------ | ----------- | -------
messageType | If specified, must be set to: REQUEST | REQUEST
serviceType | If specified, must be set to: FUNCTIONAL | FUNCTIONAL
requestAction | If specified, must be set to: CREATE | CREATE
requestType | If specified, must be set to: DELAYED | DELAYED
messageId | Consumer-generated. If specified, must be set to a valid UUID | ba74efac-94c1-42bf-af8b-9b149d067816
requestId | Consumer-generated. If specified, must be set to a valid UUID | ba74efac-94c1-42bf-af8b-9b149d067816


In addition, the request body may optionally contain XML specifying which student to refresh. If the request body is omitted, all authorized student ids will be refreshed.

The adapter will respond with either a payload containing an Accepted response or an error.

In addition to a response body, a messageId header will be returned in the response that can be used to retrieve status updates on the progress of the originating request.

Using the returned messageId a subsequent call to the adapter's messages endpoint can be used to monitor the status of a xSREs REFRESH request as it re-converts and re-caches student xSRE records from the school district's latest source data.


### Examples:

***
##### xSRE GET request
```
GET
https://srx-services-xsre-dev.herokuapp.com/xsres/123;zoneId=seattle;contextId=CBO

messageType: REQUEST
serviceType: OBJECT
requestAction: QUERY
requestType: IMMEDIATE
x-forwarded-proto: https
x-forwarded-port: 443
timeStamp: 2015-08-31T20:41:56.794820
authorization: SIF_HMACSHA256 YmU4NjBjNDctNmJkNS00OTUzL...
```

***
##### xSRE GET response
```
messageId: dcf5d63d-5d07-4b6b-a985-6ca3b6514d1a
messageType: RESPONSE
serviceType: OBJECT
requestAction: QUERY
timeStamp: 2015-08-31T20:41:56.794820

<?xml version="1.0" encoding="utf-8"?>
<xSre refId="6b54b58b-c681-4420-b90c-a5215b2ad3a2" xmlns:sif="http://www.sifassociation.org/datamodel/na/3.3" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:psesd="psesd">
  <name>
    <familyName cedsId="000172">Stark</familyName>
    <givenName cedsId="000115">Rickon</givenName>
  </name>
  <localId cedsId="001071">123</localId>
  <phoneNumber>
    <number cedsId="000279">555-555-1212</number>
    <primaryIndicator cedsId="000219">True</primaryIndicator>
  </phoneNumber>
  <demographics>
    <races>
      <race cedsId="000016">AmericanIndianOrAlaskaNative</race>
    </races>
    <hispanicLatinoEthnicity cedsId="000144">False</hispanicLatinoEthnicity>
    <sex cedsId="000255">Male</sex>
  </demographics>
  ...  
</xSre>
```


***
##### xSRE REFRESH request
```
POST
https://srx-services-xsre-dev.herokuapp.com/sres/sre;zoneId=seattle;contextId=CBO

messageType: REQUEST
serviceType: FUNCTIONAL
requestAction: CREATE
requestType: DELAYED
x-forwarded-proto: https
x-forwarded-port: 443
timeStamp: 2015-08-31T20:41:56.794820
authorization: SIF_HMACSHA256 YmU4NjBjNDctNmJkNS00OTUzL...
x-psesd-iv: g123j894w479q712
```

***
##### xSRE REFRESH response
```
messageId: 68cacf4f-bacd-432d-819e-8fd2385f7657
messageType: RESPONSE
serviceType: FUNCTIONAL
requestAction: CREATE
timeStamp: 2015-08-31T20:41:56.794820

<payload id="df3e17cc-d8ee-4940-915d-de7cc87a2033"
  xmlns="http://www.sifassociation.org/infrastructure/3.0.1">
  <response>Accepted</response>
</payload>
```


***
##### xSRE REFRESH get latest message request
```
GET
https://srx-services-xsre-dev.herokuapp.com/messages/68cacf4f-bacd-432d-819e-8fd2385f7657
```

***
##### xSRE REFRESH get latest message response
```
<payload id="7a23d211-d115-4ef8-9eac-bf76e462dfbe"
  xmlns="http://www.sifassociation.org/infrastructure/3.0.1">
  <response>
    <message>
      <messageId>68cacf4f-bacd-432d-819e-8fd2385f7657</messageId>
      <timestamp>2016-01-07T20:45:53.451Z</timestamp>
      <operation>XsresRefresh</operation>
      <status>Processing</status>
      <source>runscope-dev</source>
      <destination>seattle</destination>
      <description>Begin refreshing xSRE cache.</description>
      <body>Begin processing 1 xSREs for zone 'seattle'.</body>      
      <sourceIP>54.67.28.47</sourceIP>
      <userAgent>runscope-radar/2.0</userAgent>
    </message>
  </response>
</payload>
```


***
###### xSRE REFRESH get all messages request
```
GET
https://srx-services-xsre-dev.herokuapp.com/messages/68cacf4f-bacd-432d-819e-8fd2385f7657/messages
```

***
##### xSRE REFRESH get all messages response
```
<payload id="7a23d211-d115-4ef8-9eac-bf76e462dfbe"
  xmlns="http://www.sifassociation.org/infrastructure/3.0.1">
  <response>
    <message>
      <messageId>68cacf4f-bacd-432d-819e-8fd2385f7657</messageId>
      <timestamp>2016-01-07T20:45:53.451Z</timestamp>
      <operation>XsresRefresh</operation>
      <status>Processing</status>
      <source>runscope-dev</source>
      <destination>seattle</destination>
      <description>Begin refreshing xSRE cache.</description>
      <body>Begin processing 1 xSREs for zone 'seattle'.</body>      
      <sourceIP>54.67.28.47</sourceIP>
      <userAgent>runscope-radar/2.0</userAgent>
    </message>
    <message>
      <messageId>68cacf4f-bacd-432d-819e-8fd2385f7657</messageId>
      <timestamp>2016-01-07T20:02:04.717Z</timestamp>
      <operation>XsresRefresh</operation>
      <status>Completed</status>
      <source>runscope-dev</source>
      <destination>seattle</destination>
      <description>Finished successfully refreshing xSRE cache.</description>
      <body>Finished successfully processing 1 xSREs for zone 'seattle'.</body>
      <sourceIP>54.67.28.47</sourceIP>
      <userAgent>runscope-radar/2.0</userAgent>
    </message>
  </response>
</payload>
```
