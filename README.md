# Srx-Services-xSre
**Retrieves, creates, updates, and deletes xSRE XML for District to CBO transfers.**

***

## Configuration

### Environment Variables
Variable | Description | Example
-------- | ----------- | -------
AMAZON_S3_ACCESS_KEY | AWS S3 access key for SRX cache. | (see Heroku)
AMAZON_S3_BUCKET_NAME | AWS S3 bucket name for SRX cache. | (see Heroku)
AMAZON_S3_PATH | Root path to files within SRX cache. | (see Heroku)
AMAZON_S3_SECRET | AWS S3 secret for SRX cache. | (see Heroku)
AMAZON_S3_TIMEOUT | AWS S3 request timeout in ms. | 300000
ENVIRONMENT | Deployment environment name.  | development
LOG_LEVEL | Logging level (info, debug, error). | debug
ROLLBAR_ACCESS_TOKEN | Rollbar access token for error logging. | (see Heroku)
ROLLBAR_URL| URL to Rollbar API. | https://api.rollbar.com/api/1/item/
SERVER_API_ROOT| Root path for this service. | (typically leave blank)
SERVER_HOST  | Host IP for this service. | 127.0.0.1
SERVER_NAME | Server name for this service. | localhost
SERVER_PORT | Port this service listens on. | 8080
SERVER_URL | URL for this service. | http://localhost
SRX_ENVIRONMENT_URL | HostedZone environment URL. | https://psesd.hostedzone.com/svcs/dev/requestProvider
SRX_SESSION_TOKEN | HostedZone session token assigned to this service. | (see HostedZone configuration)
SRX_SHARED_SECRET  | HostedZone shared secret assigned to this service. | (see HostedZone configuration)

### HostedZone
The xSRE service (srx-services-xsre) must be registered in HostedZone as a new "environment" (application) that provides the following "services" (resources):

 * masterXsres

Once registered, the supplied HostedZone session token and shared secret should be set in the srx-services-admin host server (Heroku) environment variables (see above).

This xSRE service must be further configured in HostedZone as follows:

Service | Zone | Context | Provide | Query | Create | Update | Delete
------- | ---- | ------- | ------- | ----- | ------ | ------ | ------
masterXsres | default | default | X | X |  | X|X
masterXsres | [district*] | default | X |  |  | |
masterXsres | [district*] | district | X |  |  | |
masterXsres | test | default | X | X |  | X|X
masterXsres | test | district | X | X |  | X|X
masterXsres | test | test | X | X |  | X|X
srxMessages | default | default |   |   | X |  |
srxMessages | [district*] | default |   |   | X |  |
srxMessages | [district*] | district |   |   | X |  |
srxMessages | test | district |   |   | X |  |
srxMessages | test | test |   |   | X |  |
srxZoneConfig | default | default |   |X |  |  |
srxZoneConfig | [district*] | default |   |X |  |  |
srxZoneConfig | default | default |   |X |  |  |
srxZoneConfig | test | default |   |X |  |  |
srxZoneConfig | test | district |   |X |  |  |
srxZoneConfig | test | test |   |X |  |  |



## Usage

### MasterXSREs
* MasterXSREs are retrieved via a GET request.
* MasterXSREs are created via a PUT request, NOT a POST request.
* MasterXSREs are updated via a PUT request.
* MasterXSREs are deleted via a DELETE request.

All requests use the following URL format:

```
https://[baseUrl]/masterXsres/[studentId];zoneId=[zoneId];contextId=[contextId]
```

Variable | Description | Example
--------- | ----------- | -------
baseUrl   | URL of the deployment environment hosting the adapter endpoints. | srx-services-xsre-dev.herokuapp.com
studentId | Unique identifier of a student xSRE record to retrieve. | 999
zoneId    | Zone containing the requested student xSRE record | seattle
contextId | Client context of request. | CBO


The following required headers must be present in requests:

Header | Description | Example
------ | ----------- | -------
authorization | Must be set to a valid HMAC-SHA256 encrypted authorization token | SIF_HMACSHA256 YmU4NjBjNDctNmJkNS00OTUzL...
timeStamp | Must be set to a valid date/time in the following format: yyyy-MM-ddTHH:mm:ss:SSSZ | 2016-12-20T18:09:18.539Z

The following optional headers may also be included:

Header | Description | Example
------ | ----------- | -------
accept | Used to indicate when JSON is expected in response (application/json), else results default to XML | application/xml
Content-Type | Required when body is present - tells receiver how to parse body of message. Supported: application/json, application/xml (default) | application/xml
generatorId | Identification token of the “generator” of this request or event | testgenerator
messageId | Consumer-generated. If specified, must be set to a valid UUID | ba74efac-94c1-42bf-af8b-9b149d067816
messageType | If specified, must be set to: REQUEST | REQUEST
requestAction | If specified, must be set to: QUERY, UPDATE, or DELETE | QUERY
requestId | Consumer-generated. If specified, must be set to a valid UUID | ba74efac-94c1-42bf-af8b-9b149d067816
requestType | Must be set to IMMEDIATE or DELAYED. Defaults to IMMEDIATE. | IMMEDIATE
responseAction | Must match requestAction | QUERY
serviceType | If specified, must be set to: OBJECT | OBJECT
x-forwarded-for | CBO making original request |


#### Example MasterXSRE GET request

```
GET
https://srx-services-xsre-dev.herokuapp.com/masterXsres/999;zoneId=seattle;contextId=CBO

messageType: REQUEST
serviceType: OBJECT
requestAction: QUERY
requestType: IMMEDIATE
x-forwarded-proto: https
x-forwarded-port: 443
timeStamp: 2016-12-20T18:09:17.861Z
authorization: SIF_HMACSHA256 YmU4NjBjNDctNmJkNS00OTUzL...
```

#### Example MasterXSRE GET response
```
messageId: dcf5d63d-5d07-4b6b-a985-6ca3b6514d1a
messageType: RESPONSE
serviceType: OBJECT
requestAction: QUERY
timeStamp: 2016-12-20T18:09:18.447Z

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
#### Example MasterXSRE PUT request

```
PUT
https://srx-services-xsre-dev.herokuapp.com/masterXsres/999;zoneId=seattle;contextId=CBO

messageType: REQUEST
serviceType: OBJECT
requestAction: UPDATE
requestType: IMMEDIATE
x-forwarded-proto: https
x-forwarded-port: 443
timeStamp: 2016-12-20T18:09:17.861Z
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

#### Example MasterSRE PUT response
```
messageId: dcf5d63d-5d07-4b6b-a985-6ca3b6514d1a
messageType: RESPONSE
serviceType: OBJECT
requestAction: UPDATE
timeStamp: 2016-12-20T18:09:18.447Z

<updateResponse>
  <updates>
    <update id="999" statusCode="200"/>
  </updates>
</updateResponse>
```

***
#### Example MasterXSRE DELETE request

```
DELETE
https://srx-services-xsre-dev.herokuapp.com/masterXsres/999;zoneId=seattle;contextId=CBO

messageType: REQUEST
serviceType: OBJECT
requestAction: DELETE
requestType: IMMEDIATE
x-forwarded-proto: https
x-forwarded-port: 443
timeStamp: 2016-12-20T18:09:17.861Z
authorization: SIF_HMACSHA256 YmU4NjBjNDctNmJkNS00OTUzL...
```

#### Example MasterXSRE DELETE response
```
messageId: dcf5d63d-5d07-4b6b-a985-6ca3b6514d1a
messageType: RESPONSE
serviceType: OBJECT
requestAction: DELETE
timeStamp: 2016-12-20T18:09:18.447Z

<deleteResponse>
  <deletes>
    <delete id="999" statusCode="200"/>
  </deletes>
</deleteResponse>
```
