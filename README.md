# Srx-Services-xSre
**Retrieves, creates, updates, and deletes xSRE XML for District to CBO transfers.**

***
* XSREs are retrieved via a GET request.
* XSREs are created via a PUT request, NOT a POST request.
* XSREs are updated via a PUT request.
* XSREs are deleted via a DELETE request.

All requests use the following URL format:

```
https://[baseUrl]/xsres/[studentId];zoneId=[zoneId];contextId=[contextId]
```

Variable | Description | Example
--------- | ----------- | -------
baseUrl   | URL of the deployment environment hosting the adapter endpoints. | srx-services-xsre-dev.herokuapp.com
studentId | Unique identifier of a student xSRE record to retrieve. | 999
zoneId    | Zone containing the requested student xSRE record | seattle
contextId | Client context of request. | CBO


The following required headers must be present in the GET request:

Header | Description | Example
------ | ----------- | -------
authorization | Must be set to a valid HMAC-SHA256 encrypted authorization token | SIF_HMACSHA256 YmU4NjBjNDctNmJkNS00OTUzL...
timeStamp | Must be set to a valid date/time in the following format: yyyy-MM-ddTHH:mm:ss:S | 2015-08-31T20:41:56.794820
x-forwarded-port | Must be set to: 443 | 443
x-forwarded-proto | Must be set to: https | https

The following optional headers may also be included:

Header | Description | Example
------ | ----------- | -------
accept | Used to indicate when JSON is expected in response (application/json), else results default to XML | application/xml
Content-Type | Required when body is present - tells receiver how to parse body of message. Supported: application/json, application/xml (default) | application/xml
generatorId | Identification token of the “generator” of this request or event | testgenerator
messageId | Consumer-generated. If specified, must be set to a valid UUID | ba74efac-94c1-42bf-af8b-9b149d067816
messageType | If specified, must be set to: REQUEST | REQUEST
queueId | Contains the identity of one of the Consumer’s assigned Queues to which the delayed Response from the Service Provider to this request must be routed. |
requestAction | If specified, must be set to: QUERY, UPDATE, or DELETE | QUERY
requestId | Consumer-generated. If specified, must be set to a valid UUID | ba74efac-94c1-42bf-af8b-9b149d067816
requestType | If specified, must be set to: IMMEDIATE | IMMEDIATE
responseAction | Must match requestAction | QUERY
serviceType | If specified, must be set to: OBJECT | OBJECT
user-agent | Browser and operating system info |
x-forwarded-for | CBO making original request |


#### Example xSRE GET request

```
GET
https://srx-services-xsre-dev.herokuapp.com/xsres/999;zoneId=seattle;contextId=CBO

messageType: REQUEST
serviceType: OBJECT
requestAction: QUERY
requestType: IMMEDIATE
x-forwarded-proto: https
x-forwarded-port: 443
timeStamp: 2015-08-31T20:41:56.794820
authorization: SIF_HMACSHA256 YmU4NjBjNDctNmJkNS00OTUzL...
```

#### Example xSRE GET response
```
messageId: dcf5d63d-5d07-4b6b-a985-6ca3b6514d1a
messageType: RESPONSE
serviceType: OBJECT
requestAction: QUERY
timeStamp: 2015-08-31T20:41:56.794820

<xSre refId="afbdfd84-af6e-4f14-ab1f-9d43467696a3" xmlns:sif="http://www.sifassociation.org/datamodel/na/3.3" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
    <name>
      <familyName>Stark</familyName>
      <givenName>Brandon</givenName>
      <middleName>Eddard</middleName>
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
  </xSre>
```

***
#### Example xSRE PUT request

```
PUT
https://srx-services-xsre-dev.herokuapp.com/xsres/999;zoneId=seattle;contextId=CBO

messageType: REQUEST
serviceType: OBJECT
requestAction: UPDATE
requestType: IMMEDIATE
x-forwarded-proto: https
x-forwarded-port: 443
timeStamp: 2015-08-31T20:41:56.794820
authorization: SIF_HMACSHA256 YmU4NjBjNDctNmJkNS00OTUzL...

<xSre refId="afbdfd84-af6e-4f14-ab1f-9d43467696a3" xmlns:sif="http://www.sifassociation.org/datamodel/na/3.3" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
    <name>
      <familyName>Stark</familyName>
      <givenName>Brandon</givenName>
      <middleName>Ned</middleName>
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
  </xSre>
```

#### Example xSRE PUT response
```
messageId: dcf5d63d-5d07-4b6b-a985-6ca3b6514d1a
messageType: RESPONSE
serviceType: OBJECT
requestAction: UPDATE
timeStamp: 2015-08-31T20:41:56.794820

<updateResponse>
  <updates>
    <update id="999" statusCode="200"/>
  </updates>
</updateResponse>
```

***
#### Example xSRE DELETE request

```
DELETE
https://srx-services-xsre-dev.herokuapp.com/xsres/999;zoneId=seattle;contextId=CBO

messageType: REQUEST
serviceType: OBJECT
requestAction: DELETE
requestType: IMMEDIATE
x-forwarded-proto: https
x-forwarded-port: 443
timeStamp: 2015-08-31T20:41:56.794820
authorization: SIF_HMACSHA256 YmU4NjBjNDctNmJkNS00OTUzL...
```

#### Example xSRE DELETE response
```
messageId: dcf5d63d-5d07-4b6b-a985-6ca3b6514d1a
messageType: RESPONSE
serviceType: OBJECT
requestAction: DELETE
timeStamp: 2015-08-31T20:41:56.794820

<deleteResponse>
  <deletes>
    <delete id="999" statusCode="200"/>
  </deletes>
</deleteResponse>
```
