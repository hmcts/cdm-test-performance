package uk.gov.hmcts.ccd.corecasedata.scenarios

import java.text.SimpleDateFormat
import java.util.Date

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
import java.io.{BufferedWriter, FileWriter}
import io.gatling.core.check.jsonpath.JsonPathCheckType
import com.fasterxml.jackson.databind.JsonNode

object ccddatastore {

val config: Config = ConfigFactory.load()

val IdamURL = Environment.idamURL
val IdamAPI = Environment.idamAPI
val CCDEnvurl = Environment.ccdEnvurl
val s2sUrl = Environment.s2sUrl
val ccdRedirectUri = "https://ccd-data-store-api-perftest.service.core-compute-perftest.internal/oauth2redirect"
val ccdDataStoreUrl = "http://ccd-data-store-api-perftest.service.core-compute-perftest.internal"
val escaseDataUrl = "https://ccd-api-gateway-web-perftest.service.core-compute-perftest.internal"
val dmStoreUrl = "http://dm-store-perftest.service.core-compute-perftest.internal"
val ccdClientId = "ccd_gateway"
val ccdGatewayClientSecret = config.getString("ccdGatewayCS")

val ccdScope = "openid profile authorities acr roles openid profile roles"
val feedCSUserData = csv("CaseSharingUsers_Large.csv").circular
val feedCaseSearchData = csv("caseSearchData.csv").random
val feedWorkbasketData = csv("workbasketCaseTypes.csv").circular
val feedXUISearchData = csv("XUISearchData.csv").circular
val feedXUIUserData = csv("XUISearchUsers.csv").circular
val feedProbateUserData = csv("ProbateUserData.csv").circular
val feedProbateSolicitorUserData = csv("ProbateSolicitorUserData.csv").circular
val feedSSCSUserData = csv("SSCSUserData.csv").circular
val feedDivorceUserData = csv("DivorceSolUserData.csv").circular
val feedCMCUserData = csv("CMCUserData.csv").circular
val feedIACUserData = csv("IACUserData.csv").circular
val feedFPLUserData = csv("FPLUserData.csv").circular
val feedFRUserData = csv("FRUserData.csv").circular
val feedEthosUserData = csv("EthosUserData.csv").circular
val feedEthosSearchData = csv("EthosSearchData.csv").random

val MinThinkTime = Environment.minThinkTime
val MaxThinkTime = Environment.maxThinkTime
val constantThinkTime = Environment.constantthinkTime

val headers_0 = Map( //Authorization token needs to be generated with idam login
  "Authorization" -> "AdminApiAuthToken ",
  "Content-Type" -> "application/json")

val CDSGetRequest =

  feed(feedXUIUserData)

  .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

  .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "ccdloadtest1@gmail.com") //${email}
      .formParam("password", "Password12")
      .formParam("redirectUri", ccdRedirectUri)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))
      .exitHereIfFailed

  .exec(http("OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))
      .exitHereIfFailed

  .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      //.header("Cookie", "Idam.Session=${authCookie}")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))
      .exitHereIfFailed

  // val XUIIdamLogin =

  // feed(feedXUIUserData)

  // .exec(http("GetS2SToken")
  //     .post(s2sUrl + "/testing-support/lease")
  //     .header("Content-Type", "application/json")
  //     .body(StringBody("{\"microservice\":\"ccd_data\"}"))
  //     .body(StringBody("{\n  \"size\": 25\n}"))
  //     .check(bodyString.saveAs("bearerToken")))
  //     .exitHereIfFailed

  // .exec(http("OIDC01_Authenticate")
  //     .post(IdamAPI + "/authenticate")
  //     .header("Content-Type", "application/x-www-form-urlencoded")
  //     .formParam("username", "${email}") //${userEmail}
  //     .formParam("password", "Password12")
  //     .formParam("redirectUri", ccdRedirectUri)
  //     .formParam("originIp", "0:0:0:0:0:0:0:1")
  //     .check(status is 200)
  //     .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))
  //     .exitHereIfFailed

  // .exec(http("OIDC02_Authorize_CCD")
  //     .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
  //     .header("Content-Type", "application/x-www-form-urlencoded")
  //     .header("Cookie", "Idam.Session=${authCookie}")
  //     .header("Content-Length", "0")
  //     .check(status is 302)
  //     .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))
  //     .exitHereIfFailed

  //   //MkVIBs0dfCwTIBeU-enTRbfGUh0

  // .exec(http("OIDC03_Token_CCD")
  //     .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
  //     .header("Content-Type", "application/x-www-form-urlencoded")
  //     .header("Content-Length", "0")
  //     //.header("Cookie", "Idam.Session=${authCookie}")
  //     .check(status is 200)
  //     .check(jsonPath("$.access_token").saveAs("access_token")))
  //     .exitHereIfFailed

  
  //Case Sharing Requests - for manage-case-assignment API//

  val CreateCaseForCaseSharing =

    // exec(http("GetIdamUserID")
    //   .get("https://idam-api.perftest.platform.hmcts.net/users?email=${email}") //1f65a0df-b064-4f9b-85ea-3eec5a28ce86 ${caseSharingUser}
    //   .headers(headers_0)
    //   .check(jsonPath("$.id").saveAs("userId")))

    exec(http("PB_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/539560/jurisdictions/PROBATE/case-types/GrantOfRepresentation/event-triggers/applyForGrant/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("PB_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/539560/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      // .body(StringBody("{\n  \"data\": {\n    \"solsSolicitorFirmName\": \"jon & ola\",\n    \"solsSolicitorAddress\": {\n      \"AddressLine1\": \"Flat 12\",\n      \"AddressLine2\": \"Bramber House\",\n      \"AddressLine3\": \"Seven Kings Way\",\n      \"PostTown\": \"Kingston Upon Thames\",\n      \"County\": \"\",\n      \"PostCode\": \"KT2 5BU\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"solsSolicitorAppReference\": \"test\",\n    \"solsSolicitorEmail\": \"ccdorg-mvgvh_mcccd.user52@mailinator.com\",\n    \"solsSolicitorPhoneNumber\": null,\n    \"organisationPolicy\": {\n      \"OrgPolicyCaseAssignedRole\": \"[Claimant]\",\n      \"OrgPolicyReference\": null,\n      \"Organisation\": {\n        \"OrganisationID\": \"IGWEE4D\",\n        \"OrganisationName\": \"ccdorg-mvgvh\"\n      }\n    }\n  },\n  \"event\": {\n    \"id\": \"solicitorCreateApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      // .body(StringBody(" {   \n \t\"data\" : {\n      \"TextField\" : \"textField1\",\n      \"TextAreaField\" : \"textAreaField1\",\n      \"AddressField\" : {\n        \"AddressLine1\" : \"102 Petty France\",\n        \"AddressLine2\" : \"CCD\",\n        \"AddressLine3\" : \"c/o HMCTS Reform\",\n        \"Country\" : \"UK\"\n      },\n      \"OrganisationPolicyField1\" : {\n        \"OrgPolicyCaseAssignedRole\" : \"[Claimant]\",\n        \"OrgPolicyReference\" : \"ref\",\n        \"Organisation\" : {\n          \"OrganisationID\" : \"orgID1\",\n          \"OrganisationName\" : \"orgName1\"\n        }\n      },\n      \"OrganisationPolicyField2\" : {\n        \"OrgPolicyCaseAssignedRole\" : \"[Defendant]\",\n        \"OrgPolicyReference\" : \"ref\",\n        \"Organisation\" : {\n          \"OrganisationID\" : \"orgID2\",\n          \"OrganisationName\" : \"orgName2\"\n        }\n      }\n    },\n    \"event\" : {\n      \"id\" : \"createCase\",\n      \"summary\" : \"\",\n      \"description\" : \"\"\n    },\n    \"event_token\" : \"${eventToken}\",\n    \"ignore_warning\" : false,\n    \"draft_id\" : null\n \t\n }"))
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"applyForGrant\",\n    \"summary\": \"test case\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(1)

  val CCDLogin_Probate = 

    feed(feedProbateUserData)

    // .exec(http("GetIdamUserID")
    //   .get("https://idam-api.perftest.platform.hmcts.net/users?email=${ProbateUserName}") //1f65a0df-b064-4f9b-85ea-3eec5a28ce86 ${caseSharingUser}
    //   .headers(headers_0)
    //   .check(jsonPath("$.id").saveAs("userId"))
    //   .check(status.saveAs("statusvalue")))

    // .doIf(session=>session("statusvalue").as[String].contains("200")) {
    //   exec {
    //     session =>
    //       val fw = new BufferedWriter(new FileWriter("ProbateEmailAndIdamIDs.csv", true))
    //       try {
    //         fw.write(session("ProbateUserName").as[String] + "," + session("userId").as[String] + "\r\n")
    //       }
    //       finally fw.close()
    //       session
    //   }
    // }

    .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

    .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${ProbateUserName}") //${email}
      .formParam("password", "${ProbateUserPassword}")
      .formParam("redirectUri", ccdRedirectUri)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))
      .exitHereIfFailed

   .exec(http("OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))
      .exitHereIfFailed

   .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))
      .exitHereIfFailed

  val CCDAPI_ProbateCreate = 

    exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/event-triggers/applyForGrant/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Probate_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"applyForGrant\",\n    \"summary\": \"test case\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_ProbateCaseEvents =

    exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${caseId}/event-triggers/paymentSuccessApp/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_Probate_PaymentSuccessful")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_PaymentSuccess.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${caseId}/event-triggers/boStopCaseForCaseCreated/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken4")))

    .exec(http("API_Probate_StopCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {\n    \"boCaseStopReasonList\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"caseStopReason\": \"Other\"\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"boStopCaseForCaseCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken4}\",\n  \"ignore_warning\": false\n}"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_ProbateDocUpload = 

    exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${caseId}/event-triggers/boUploadDocumentsForCaseCreated/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(session => {
      session.set("FileName1", "1MB.pdf")
    })

    .exec(http("API_Probate_DocUploadProcess")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
        .fileName("${FileName1}")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(status.is(200))
      .check(regex("""http://(.+)/""").saveAs("DMURL"))
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID")))

    .exec(http("API_Probate_DocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {\n    \"boDocumentsUploaded\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"DocumentType\": \"deathCertificate\",\n          \"Comment\": \"test 1mb file\",\n          \"DocumentLink\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}/binary\",\n            \"document_filename\": \"${FileName1}\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"boUploadDocumentsForCaseCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken3}\",\n  \"ignore_warning\": false\n}")))

    .pause(Environment.constantthinkTime)

  val CCDLogin_ProbateSolicitor = 

    feed(feedProbateSolicitorUserData)

    // .exec(http("GetIdamUserID")
    //   .get("https://idam-api.perftest.platform.hmcts.net/users?email=${ProbateUserName}") //1f65a0df-b064-4f9b-85ea-3eec5a28ce86 ${caseSharingUser}
    //   .headers(headers_0)
    //   .check(jsonPath("$.id").saveAs("userId"))
    //   .check(status.saveAs("statusvalue")))

    // .doIf(session=>session("statusvalue").as[String].contains("200")) {
    //   exec {
    //     session =>
    //       val fw = new BufferedWriter(new FileWriter("ProbateEmailAndIdamIDs.csv", true))
    //       try {
    //         fw.write(session("ProbateUserName").as[String] + "," + session("userId").as[String] + "\r\n")
    //       }
    //       finally fw.close()
    //       session
    //   }
    // }

    .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

    .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${ProbateUserName}") //${email}
      .formParam("password", "${ProbateUserPassword}")
      .formParam("redirectUri", ccdRedirectUri)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))
      .exitHereIfFailed

   .exec(http("OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))
      .exitHereIfFailed

   .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))
      .exitHereIfFailed

  val CCDAPI_ProbateSolicitorCreate =

    exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Probate_SolCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      // .body(StringBody("{\n  \"data\": {\n    \"solsSolicitorFirmName\": \"test firm\",\n    \"solsSolicitorIsExec\": \"Yes\",\n    \"solsSOTForenames\": \"John\",\n    \"solsSOTSurname\": \"Smith\",\n    \"solsSolicitorIsMainApplicant\": \"Yes\",\n    \"solsSolicitorAddress\": {\n      \"AddressLine1\": \"Flat 1\",\n      \"AddressLine2\": \"Bramber House\",\n      \"AddressLine3\": \"Seven Kings Way\",\n      \"PostTown\": \"Kingston Upon Thames\",\n      \"County\": \"\",\n      \"PostCode\": \"KT2 5BU\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"solsSolicitorAppReference\": \"perftest\",\n    \"solsSolicitorEmail\": \"john@test.com\",\n    \"solsSolicitorPhoneNumber\": null\n  },\n  \"event\": {\n    \"id\": \"solicitorCreateApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": \"DRAFT168458\"\n}"))
      .body(StringBody("{\"data\":{\"solsSolicitorWillSignSOT\":\"Yes\",\"solsSOTForenames\":\"John\",\"solsSOTSurname\":\"Smith\",\"solsSolicitorIsExec\":\"Yes\",\"solsSolicitorIsApplying\":\"Yes\",\"solsSolicitorFirmName\":\"Test Firm\",\"solsSolicitorAddress\":{\"AddressLine1\":\"Flat 10\",\"AddressLine2\":\"Bramber House\",\"AddressLine3\":\"Seven Kings Way\",\"PostTown\":\"Kingston Upon Thames\",\"County\":\"\",\"PostCode\":\"KT2 5BU\",\"Country\":\"United Kingdom\"},\"solsSolicitorEmail\":\"john@test.com\",\"solsSolicitorPhoneNumber\":null,\"solsSolicitorAppReference\":\"12345\"},\"event\":{\"id\":\"solicitorCreateApplication\",\"summary\":\"\",\"description\":\"\"},\"event_token\":\"${eventToken}\",\"ignore_warning\":false,\"draft_id\":\"DRAFT168646\"}"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_ProbateSolicitorCaseEvents = 

    exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${caseId}/event-triggers/solicitorUpdateApplication/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_Probate_SolUpdateApp")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_Probate_SolUpdateApp.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${caseId}/event-triggers/solicitorUpdateProbate/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(http("API_Probate_SolUpdateProbate")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_Probate_SolUpdateProbate.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${caseId}/event-triggers/solicitorReviewAndConfirm/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken4")))

    .exec(http("API_Probate_SolSubmitApp")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_Probate_SolSubmitApp.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDLogin_SSCS = 

    feed(feedSSCSUserData)

    .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

    .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${SSCSUserName}") //${email}
      .formParam("password", "${SSCSUserPassword}")
      .formParam("redirectUri", ccdRedirectUri)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))
      .exitHereIfFailed

   .exec(http("OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))
      .exitHereIfFailed

   .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))
      .exitHereIfFailed

  val CCDAPI_SSCSCreate =

    exec(http("API_SSCS_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${SSCSJurisdiction}/case-types/${SSCSCaseType}/event-triggers/appealCreated/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_SSCS_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${SSCSJurisdiction}/case-types/${SSCSCaseType}/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {\n    \"caseReference\": null,\n    \"caseCreated\": \"2021-02-03\",\n    \"region\": null,\n    \"appeal\": {\n      \"receivedVia\": \"Online\",\n      \"mrnDetails\": {\n        \"dwpIssuingOffice\": \"DWP\",\n        \"mrnDate\": \"2020-12-22\",\n        \"mrnLateReason\": null,\n        \"mrnMissingReason\": null\n      },\n      \"appellant\": {\n        \"name\": {\n          \"title\": \"Mr\",\n          \"firstName\": \"John\",\n          \"middleName\": null,\n          \"lastName\": \"Smith\"\n        },\n        \"identity\": {\n          \"dob\": \"2000-03-01\",\n          \"nino\": \"AB1234567Z\"\n        },\n        \"address\": {\n          \"line1\": \"24 Test Street\",\n          \"line2\": null,\n          \"line3\": null,\n          \"town\": \"London\",\n          \"county\": null,\n          \"postcode\": \"KT2 5BU\",\n          \"country\": \"UK\"\n        },\n        \"contact\": {\n          \"phone\": \"07123456789\",\n          \"mobile\": null,\n          \"email\": null\n        },\n        \"isAppointee\": \"No\",\n        \"appointee\": {\n          \"name\": {\n            \"title\": null,\n            \"firstName\": null,\n            \"middleName\": null,\n            \"lastName\": null\n          },\n          \"identity\": {\n            \"dob\": null,\n            \"nino\": null\n          },\n          \"address\": {\n            \"line1\": null,\n            \"line2\": null,\n            \"line3\": null,\n            \"town\": null,\n            \"county\": null,\n            \"postcode\": null,\n            \"country\": null\n          },\n          \"contact\": {\n            \"phone\": null,\n            \"mobile\": null,\n            \"email\": null\n          }\n        },\n        \"isAddressSameAsAppointee\": null\n      },\n      \"benefitType\": {\n        \"code\": null,\n        \"description\": null\n      },\n      \"hearingType\": null,\n      \"hearingOptions\": {\n        \"wantsToAttend\": null,\n        \"languageInterpreter\": null,\n        \"other\": null,\n        \"signLanguageType\": null\n      },\n      \"appealReasons\": {\n        \"reasons\": [],\n        \"otherReasons\": null\n      },\n      \"supporter\": {\n        \"name\": {\n          \"title\": null,\n          \"firstName\": null,\n          \"middleName\": null,\n          \"lastName\": null\n        },\n        \"contact\": {\n          \"phone\": null,\n          \"mobile\": null,\n          \"email\": null\n        }\n      },\n      \"rep\": {\n        \"hasRepresentative\": null\n      },\n      \"signer\": null\n    },\n    \"regionalProcessingCenter\": {\n      \"name\": null,\n      \"address1\": null,\n      \"address2\": null,\n      \"address3\": null,\n      \"address4\": null,\n      \"postcode\": null,\n      \"city\": null,\n      \"phoneNumber\": null,\n      \"faxNumber\": null,\n      \"email\": null\n    },\n    \"panel\": {\n      \"assignedTo\": null,\n      \"medicalMember\": null,\n      \"disabilityQualifiedMember\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"appealCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_SSCSCaseEvents =

    exec(http("API_SSCS_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${SSCSJurisdiction}/case-types/${SSCSCaseType}/cases/${caseId}/event-triggers/uploadDocument/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(session => {
      session.set("FileName1", "1MB.pdf")
    })

    .exec(http("API_SSCS_DocUploadProcess")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
        .fileName("${FileName1}")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(status.is(200))
      .check(regex("""http://(.+)/""").saveAs("DMURL"))
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID")))

    .exec(http("API_SSCS_DocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${SSCSJurisdiction}/case-types/${SSCSCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {\n    \"sscsDocument\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"documentType\": \"Other evidence\",\n          \"documentEmailContent\": null,\n          \"documentDateAdded\": \"2019-11-12\",\n          \"documentComment\": \"${FileName1} upload\",\n          \"documentFileName\": null,\n          \"documentLink\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}/binary\",\n            \"document_filename\": \"${FileName1}\"\n          }\n        }\n      },\n      {\n        \"id\": null,\n        \"value\": {\n          \"documentType\": null,\n          \"documentEmailContent\": null,\n          \"documentDateAdded\": null,\n          \"documentComment\": null,\n          \"documentFileName\": null\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"uploadDocument\",\n    \"summary\": \"${FileName1} upload doc\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken3}\",\n  \"ignore_warning\": false\n}")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_SSCS_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${SSCSJurisdiction}/case-types/${SSCSCaseType}/cases/${caseId}/event-triggers/addHearing/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken4")))

    .exec(http("API_SSCS_AddHearing")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${SSCSJurisdiction}/case-types/${SSCSCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("CCD_SSCS_AddHearing.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_SSCS_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${SSCSJurisdiction}/case-types/${SSCSCaseType}/cases/${caseId}/event-triggers/dwpActionDirection/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken5")))

    .exec(http("API_SSCS_ActionDirection")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${SSCSJurisdiction}/case-types/${SSCSCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("CCD_SSCS_ActionDirection.json")))

    .pause(Environment.constantthinkTime)

  val CCDLogin_CMC =

    feed(feedCMCUserData)

    .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

    .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${CMCUserName}") 
      .formParam("password", "${CMCUserPassword}")
      .formParam("redirectUri", ccdRedirectUri)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))
      .exitHereIfFailed

   .exec(http("OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))
      .exitHereIfFailed

   .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))
      .exitHereIfFailed

  val CCDAPI_CMCCreate =

    exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${CMCJurisdiction}/case-types/${CMCCaseType}/event-triggers/CreateClaim/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_CMC_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${CMCJurisdiction}/case-types/${CMCCaseType}/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"CreateClaim\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_CMCCaseEvents =

    exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${CMCJurisdiction}/case-types/${CMCCaseType}/cases/${caseId}/event-triggers/StayClaim/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken1")))

    .exec(http("API_CMC_CaseStayed")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${CMCJurisdiction}/case-types/${CMCCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"StayClaim\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken1}\",\n  \"ignore_warning\": false\n}")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${CMCJurisdiction}/case-types/${CMCCaseType}/cases/${caseId}/event-triggers/ClaimNotes/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_CMC_ClaimNotes")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${CMCJurisdiction}/case-types/${CMCCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"ClaimNotes\",\n    \"summary\": \"Test Claim Note\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken2}\",\n  \"ignore_warning\": false\n}")))

    .pause(Environment.constantthinkTime)

    // .exec(http("API_CMC_GetEventToken")
    //   .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${CMCJurisdiction}/case-types/${CMCCaseType}/cases/${caseId}/event-triggers/attachScannedDocs/token")
    //   .header("ServiceAuthorization", "Bearer ${bearerToken}")
    //   .header("Authorization", "Bearer ${access_token}")
    //   .header("Content-Type","application/json")
    //   .check(jsonPath("$.token").saveAs("eventToken3")))

    // .exec(http("API_CMC_AttachScannedDocs")
    //   .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${CMCJurisdiction}/case-types/${CMCCaseType}/cases/${caseId}/events")
    //   .header("ServiceAuthorization", "Bearer ${bearerToken}")
    //   .header("Authorization", "Bearer ${access_token}")
    //   .header("Content-Type","application/json")
    //   .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"attachScannedDocs\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken3}\",\n  \"ignore_warning\": false\n}")))

    // .pause(Environment.constantthinkTime)

    .exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${CMCJurisdiction}/case-types/${CMCCaseType}/cases/${caseId}/event-triggers/LinkLetterHolder/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken4")))

    .exec(http("API_CMC_LinkLetterHolder")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${CMCJurisdiction}/case-types/${CMCCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"LinkLetterHolder\",\n    \"summary\": \"perf test\",\n    \"description\": \"link letter holder perf test description\"\n  },\n  \"event_token\": \"${eventToken4}\",\n  \"ignore_warning\": false\n}")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${CMCJurisdiction}/case-types/${CMCCaseType}/cases/${caseId}/event-triggers/LiftStay/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken5")))

    .exec(http("API_CMC_LiftStay")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${CMCJurisdiction}/case-types/${CMCCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"LiftStay\",\n    \"summary\": \"perf test\",\n    \"description\": \"lift stay perf testing description\"\n  },\n  \"event_token\": \"${eventToken5}\",\n  \"ignore_warning\": false\n}")))

    .pause(Environment.constantthinkTime)

  val CCDLogin_Divorce =

    feed(feedDivorceUserData)

    .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

    .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${DivorceUserName}") //${DivorceUserName}
      .formParam("password", "Pass19word") //${DivorceUserPassword}
      .formParam("redirectUri", ccdRedirectUri)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))
      .exitHereIfFailed

   .exec(http("OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))
      .exitHereIfFailed

   .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))
      .exitHereIfFailed

  val CCDAPI_DivorceSolicitorCreate =

    exec(http("API_Divorce_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/event-triggers/solicitorCreate/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Divorce_SolCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      // .body(StringBody("{\n  \"data\": {\n    \"PetitionerSolicitorName\": \"john smith\",\n    \"PetitionerSolicitorFirm\": \"solicitor ltd\",\n    \"DerivedPetitionerSolicitorAddr\": \"12 test street, solicitor lane, kt25bu\",\n    \"D8SolicitorReference\": \"0123456\",\n    \"PetitionerSolicitorPhone\": \"07123456789\",\n    \"PetitionerSolicitorEmail\": \"johnsmith@solicitorltd.com\",\n    \"SolicitorAgreeToReceiveEmails\": \"Yes\",\n    \"D8PetitionerFirstName\": \"Jane\",\n    \"D8PetitionerLastName\": \"Deer\",\n    \"D8PetitionerNameDifferentToMarriageCert\": \"No\",\n    \"D8DivorceWho\": \"husband\",\n    \"D8InferredPetitionerGender\": \"female\",\n    \"D8MarriageIsSameSexCouple\": \"No\",\n    \"D8DerivedPetitionerHomeAddress\": \"14 divorce street, london, kt25bu\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerContactDetailsConfidential\": \"keep\",\n    \"D8RespondentFirstName\": \"Steve\",\n    \"D8RespondentLastName\": \"Smith\",\n    \"D8RespondentNameAsOnMarriageCertificate\": \"No\",\n    \"D8InferredRespondentGender\": \"male\",\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceSendToSol\": \"No\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"14 divorcee street, london, kt25bu\",\n    \"D8MarriageDate\": \"2000-10-02\",\n    \"D8MarriagePetitionerName\": \"Jane Deer\",\n    \"D8MarriageRespondentName\": \"Steve Smith\",\n    \"D8MarriedInUk\": \"Yes\",\n    \"D8JurisdictionConnection\": [\n      \"G\"\n    ],\n    \"D8ReasonForDivorce\": \"unreasonable-behaviour\",\n    \"D8ReasonForDivorceBehaviourDetails\": \"bad behaviour\",\n    \"D8LegalProceedings\": \"No\",\n    \"D8FinancialOrder\": \"No\",\n    \"D8DivorceCostsClaim\": \"No\",\n    \"D8DocumentsUploaded\": []\n  },\n  \"event\": {\n    \"id\": \"solicitorCreate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      // .body(StringBody("{\n  \"data\": {\n    \"LanguagePreferenceWelsh\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"hwfCreate\",\n    \"summary\": \"perf test case\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .body(ElFileBody("bodies/divorce/CCD_DivorceCreateSol.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_DivorceSolicitorCaseEvents = 

    // exec(http("API_Divorce_GetEventToken")
    //   .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/${caseId}/event-triggers/uploadDocument/token")
    //   .header("ServiceAuthorization", "Bearer ${bearerToken}")
    //   .header("Authorization", "Bearer ${access_token}")
    //   .header("Content-Type","application/json")
    //   .check(jsonPath("$.token").saveAs("eventToken3")))

    //  .exec(session => {
    //   session.set("FileName1", "1MB.pdf")
    // })

    // .exec(http("API_Divorce_DocUploadProcess")
    //   .post(dmStoreUrl + "/documents")
    //   .header("ServiceAuthorization", "Bearer ${bearerToken}")
    //   .bodyPart(RawFileBodyPart("files", "${FileName1}")
    //     .fileName("${FileName1}")
    //     .transferEncoding("binary"))
    //   .asMultipartForm
    //   .formParam("classification", "PUBLIC")
    //   .check(status.is(200))
    //   .check(regex("""http://(.+)/""").saveAs("DMURL"))
    //   .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID")))

    // .exec(http("API_Divorce_DocUpload")
    //   .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/${caseId}/events")
    //   .header("ServiceAuthorization", "Bearer ${bearerToken}")
    //   .header("Authorization", "Bearer ${access_token}")
    //   .header("Content-Type","application/json")      
    //   .body(StringBody("{\n  \"data\": {\n    \"D8DocumentsUploaded\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"DocumentType\": \"other\",\n          \"DocumentEmailContent\": null,\n          \"DocumentDateAdded\": null,\n          \"DocumentComment\": null,\n          \"DocumentFileName\": null,\n          \"DocumentLink\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n            \"document_filename\": \"${FileName1}\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"uploadDocument\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken3}\",\n  \"ignore_warning\": false\n}")))

    // .pause(Environment.constantthinkTime)

    // .exec(http("API_Divorce_GetEventToken")
    //   .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/${caseId}/event-triggers/AddNote/token")
    //   .header("ServiceAuthorization", "Bearer ${bearerToken}")
    //   .header("Authorization", "Bearer ${access_token}")
    //   .header("Content-Type","application/json")
    //   .check(jsonPath("$.token").saveAs("eventToken2")))

    // .exec(http("API_Divorce_AddNote")
    //   .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/${caseId}/events")
    //   .header("ServiceAuthorization", "Bearer ${bearerToken}")
    //   .header("Authorization", "Bearer ${access_token}")
    //   .header("Content-Type","application/json")      
    //   .body(ElFileBody("bodies/divorce/CCD_Divorce_AddNote.json")))

    // .pause(Environment.constantthinkTime)

    // .exec(http("API_Divorce_GetEventToken")
    //   .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/${caseId}/event-triggers/generalApplicationReceived/token")
    //   .header("ServiceAuthorization", "Bearer ${bearerToken}")
    //   .header("Authorization", "Bearer ${access_token}")
    //   .header("Content-Type","application/json")
    //   .check(jsonPath("$.token").saveAs("eventToken1")))

    // .exec(http("API_Divorce_GeneralApplicationReceived")
    //   .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/${caseId}/events")
    //   .header("ServiceAuthorization", "Bearer ${bearerToken}")
    //   .header("Authorization", "Bearer ${access_token}")
    //   .header("Content-Type","application/json")      
    //   .body(ElFileBody("bodies/divorce/CCD_Divorce_GeneralApplicationReceived.json")))

    // .pause(Environment.constantthinkTime)

    exec(http("API_Divorce_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/${caseId}/event-triggers/UpdateLanguage/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_Divorce_SolUpdateLanguage")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(StringBody("{\"data\":{\"LanguagePreferenceWelsh\":\"No\"},\"event\":{\"id\":\"UpdateLanguage\",\"summary\":\"\",\"description\":\"\"},\"event_token\":\"${eventToken2}\",\"ignore_warning\":false}")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_Divorce_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/${caseId}/event-triggers/solicitorStatementOfTruthPaySubmit/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(http("API_Divorce_SolCaseSubmit")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(StringBody("{\n   \"data\":{\n      \"SolUrgentCase\":\"No\",\n      \"SolServiceMethod\":\"courtService\",\n      \"SolStatementOfReconciliationCertify\":\"Yes\",\n      \"SolStatementOfReconciliationDiscussed\":\"Yes\",\n      \"D8StatementOfTruth\":\"Yes\",\n      \"solSignStatementofTruth\":\"Yes\",\n      \"SolStatementOfReconciliationName\":\"Vuser\",\n      \"SolStatementOfReconciliationFirm\":\"Perf\",\n      \"StatementOfReconciliationComments\":null,\n      \"solApplicationFeeInPounds\":\"550\",\n      \"SolPaymentHowToPay\":\"feesHelpWith\",\n      \"D8HelpWithFeesReferenceNumber\":\"perfte\",\n      \"solApplicationFeeOrderSummary\":{\n         \"PaymentReference\":null,\n         \"PaymentTotal\":\"55000\",\n         \"Fees\":[\n            {\n               \"value\":{\n                  \"FeeCode\":\"FEE0002\",\n                  \"FeeAmount\":\"55000\",\n                  \"FeeDescription\":\"Filing an application for a divorce, nullity or civil partnership dissolution\",\n                  \"FeeVersion\":\"5\"\n               }\n            }\n         ]\n      }\n   },\n   \"event\":{\n      \"id\":\"solicitorStatementOfTruthPaySubmit\",\n      \"summary\":\"\",\n      \"description\":\"\"\n   },\n   \"event_token\":\"${eventToken3}\",\n   \"ignore_warning\":false\n}")))

    .pause(Environment.constantthinkTime)

  val CCDLogin_IAC =

    feed(feedIACUserData)

    .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

    .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${IACUserName}") 
      .formParam("password", "${IACUserPassword}")
      .formParam("redirectUri", ccdRedirectUri)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))
      .exitHereIfFailed

   .exec(http("OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))
      .exitHereIfFailed

   .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))
      .exitHereIfFailed

  val CCDAPI_IACCreate =

    exec(http("API_IAC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${IACJurisdiction}/case-types/${IACCaseType}/event-triggers/startAppeal/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_IAC_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${IACJurisdiction}/case-types/${IACCaseType}/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("IACCreateCase.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

  val CCDLogin_FPL =

    feed(feedFPLUserData)

    .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

    .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${FPLUserName}") 
      .formParam("password", "${FPLUserPassword}")
      .formParam("redirectUri", ccdRedirectUri)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))
      .exitHereIfFailed

   .exec(http("OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))
      .exitHereIfFailed

   .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))
      .exitHereIfFailed

  val CCDAPI_FPLCreate =

    exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/event-triggers/openCase/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_FPL_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("FPLCreateCase.json")).asJson
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_FPLCaseEvents = 

    exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/event-triggers/enterChildren/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken1")))

    .exec(http("API_FPL_AddChildren")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterChildren.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/event-triggers/enterRespondents/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_FPL_EnterRespondents")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterRespondents.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/event-triggers/enterGrounds/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(http("API_FPL_EnterGrounds")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterGrounds.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/event-triggers/uploadDocuments/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken4")))

     .exec(session => {
      session.set("FileName1", "1MB.pdf")
    })

    .exec(http("API_FPL_DocUploadProcess")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
        .fileName("${FileName1}")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(status.is(200))
      .check(regex("""http://(.+)/""").saveAs("DMURL"))
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID")))

    .exec(http("API_FPL_DocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_UploadDocuments.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/event-triggers/ordersNeeded/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken5")))

    .exec(http("API_FPL_OrdersNeeded")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_OrdersNeeded.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/event-triggers/hearingNeeded/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken6")))

    .exec(http("API_FPL_HearingNeeded")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_HearingNeeded.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/event-triggers/enterApplicant/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken7")))

    .exec(http("API_FPL_EnterOrganisationDetails")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_OrganisationDetails.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/event-triggers/otherProposal/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(http("API_FPL_OtherProposal")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_OtherProposal.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/event-triggers/submitApplication/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken8"))
      // .check(regex("""documents/(.*)","document_filename":"draft""").saveAs("documentId"))
      // .check(regex("""document_sssssfilename":"draft(.+?)","documeeeeeeent_binary_url""").saveAs("documentName"))
      .check(jsonPath("$.case_details.case_data.draftApplicationDocument.document_url").saveAs("documentUrl"))
      .check(jsonPath("$.case_details.case_data.draftApplicationDocument.document_filename").saveAs("documentFilename")))

    .exec(http("API_FPL_SubmitApplication")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FPLJurisdiction}/case-types/${FPLCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/fpl/CCD_FPL_SubmitApplication.json")))

    .pause(Environment.constantthinkTime)

  val CCDLogin_FR =

    feed(feedFRUserData)

    .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

    .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${FRUserName}") 
      .formParam("password", "${FRUserPassword}")
      .formParam("redirectUri", ccdRedirectUri)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))
      .exitHereIfFailed

   .exec(http("OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))
      .exitHereIfFailed

   .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))
      .exitHereIfFailed

  val CCDAPI_FRCreate =

    exec(http("API_FR_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FRJurisdiction}/case-types/${FRCaseType}/event-triggers/FR_solicitorCreate/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_FR_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FRJurisdiction}/case-types/${FRCaseType}/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("FRCreateCase.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_FRCaseEvents =

    exec(http("API_FR_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FRJurisdiction}/case-types/${FRCaseType}/cases/${caseId}/event-triggers/FR_amendApplicationDetails/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2"))
      .check(regex("""/documents/(.+?)","document_filename""").find(0).saveAs("d81JointDocumentId"))
      // .check(regex(s"""latestConsentOrder":\\{"document_url":"${dmStoreUrl}/documents/([a-z0-9-]+?)"""").saveAs("consentOrderDocumentId"))
      .check(jsonPath("$.case_details.case_data.latestConsentOrder.document_url").saveAs("consentOrderDocumentId")))

    .exec(http("API_FR_AmendApplicationDetails")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${FRJurisdiction}/case-types/${FRCaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("CCD_FR_AmendApplicationDetails.json")))

    .pause(Environment.constantthinkTime)

  val CCDLogin_Ethos =

    feed(feedEthosUserData)

    .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

    .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${EthosUserName}") 
      .formParam("password", "${EthosUserPassword}")
      .formParam("redirectUri", ccdRedirectUri)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))
      .exitHereIfFailed

   .exec(http("OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))
      .exitHereIfFailed

   .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))
      .exitHereIfFailed

  val CCDAPI_EthosJourney =

    feed(feedEthosSearchData)

    .exec(http("CCD_EthosSearch")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${EthosJurisdiction}/case-types/${EthosCaseType}/cases?case_reference=${EthosCaseRef}")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(status in (200)))

    .pause(Environment.constantthinkTime)

    .exec(http("CCD_EthosViewCase")
      .get(ccdDataStoreUrl + "/cases/${EthosCaseRef}")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .header("Experimental","true")
      .check(status in (200)))

    .pause(Environment.constantthinkTime)

  //CreateCaseForCaseSharing

    // .exec(http("DIV_GetEventToken")
    //   .get(ccdDataStoreUrl + "/caseworkers/539560/jurisdictions/DIVORCE/case-types/DIVORCE/event-triggers/CREATE/token")
    //   .header("ServiceAuthorization", "Bearer ${bearerToken}")
    //   .header("Authorization", "Bearer ${access_token}")
    //   .header("Content-Type","application/json")
    //   .check(jsonPath("$.token").saveAs("eventToken")))

    // .exec(http("DIV_CreateCase")
    //   .post(ccdDataStoreUrl + "/caseworkers/539560/jurisdictions/DIVORCE/case-types/DIVORCE/cases")
    //   .header("ServiceAuthorization", "Bearer ${bearerToken}")
    //   .header("Authorization", "Bearer ${access_token}")
    //   .header("Content-Type","application/json")
    //   .body(StringBody("{\n  \"data\": {\n    \"D8legalProcess\": null,\n    \"createdDate\": null,\n    \"D8ScreenHasMarriageBroken\": null,\n    \"D8ScreenHasRespondentAddress\": null,\n    \"D8ScreenHasMarriageCert\": null,\n    \"D8ScreenHasPrinter\": null,\n    \"D8DivorceWho\": null,\n    \"D8MarriageIsSameSexCouple\": null,\n    \"D8InferredPetitionerGender\": null,\n    \"D8InferredRespondentGender\": null,\n    \"D8MarriageDate\": null,\n    \"D8MarriedInUk\": null,\n    \"D8CertificateInEnglish\": null,\n    \"D8CertifiedTranslation\": null,\n    \"D8MarriagePlaceOfMarriage\": null,\n    \"D8CountryName\": null,\n    \"D8MarriagePetitionerName\": null,\n    \"D8MarriageRespondentName\": null,\n    \"D8PetitionerNameDifferentToMarriageCert\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerFirstName\": null,\n    \"D8PetitionerLastName\": null,\n    \"D8DerivedPetitionerCurrentFullName\": null,\n    \"D8PetitionerNameChangedHow\": [],\n    \"D8PetitionerNameChangedHowOtherDetails\": null,\n    \"D8PetitionerContactDetailsConfidential\": null,\n    \"D8PetitionerHomeAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedPetitionerHomeAddress\": null,\n    \"D8PetitionerCorrespondenceAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedPetitionerCorrespondenceAddr\": null,\n    \"D8PetitionerCorrespondenceUseHomeAddress\": null,\n    \"D8RespondentNameAsOnMarriageCertificate\": null,\n    \"D8RespondentFirstName\": null,\n    \"D8RespondentLastName\": null,\n    \"D8DerivedRespondentCurrentName\": null,\n    \"D8DerivedRespondentSolicitorDetails\": null,\n    \"D8RespondentHomeAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedRespondentCorrespondenceAddr\": null,\n    \"D8RespondentSolicitorName\": null,\n    \"D8RespondentSolicitorCompany\": null,\n    \"D8RespondentCorrespondenceSendToSol\": null,\n    \"D8RespondentSolicitorAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedRespondentSolicitorAddr\": null,\n    \"D8RespondentCorrespondenceUseHomeAddress\": null,\n    \"D8RespondentKnowsHomeAddress\": null,\n    \"D8RespondentLivesAtLastAddress\": null,\n    \"D8LivingArrangementsTogetherSeparated\": null,\n    \"D8LivingArrangementsLastLivedTogether\": null,\n    \"D8LivingArrangementsLiveTogether\": null,\n    \"D8LivingArrangementsLastLivedTogethAddr\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedLivingArrangementsLastLivedAddr\": null,\n    \"D8LegalProceedings\": null,\n    \"D8LegalProceedingsRelated\": [],\n    \"D8LegalProceedingsDetails\": null,\n    \"D8ReasonForDivorce\": null,\n    \"D8DerivedStatementOfCase\": null,\n    \"D8ReasonForDivorceBehaviourDetails\": null,\n    \"D8ReasonForDivorceDesertionDate\": null,\n    \"D8ReasonForDivorceDesertionAgreed\": null,\n    \"D8ReasonForDivorceDesertionDetails\": null,\n    \"D8ReasonForDivorceSeperationDate\": null,\n    \"D8ReasonForDivorceAdultery3rdPartyFName\": null,\n    \"D8ReasonForDivorceAdultery3rdPartyLName\": null,\n    \"D8DerivedReasonForDivorceAdultery3dPtyNm\": null,\n    \"D8ReasonForDivorceAdulteryDetails\": null,\n    \"D8ReasonForDivorceAdulteryKnowWhen\": null,\n    \"D8ReasonForDivorceAdulteryWishToName\": null,\n    \"D8ReasonForDivorceAdulteryKnowWhere\": null,\n    \"D8ReasonForDivorceAdulteryWhereDetails\": null,\n    \"D8ReasonForDivorceAdulteryWhenDetails\": null,\n    \"D8ReasonForDivorceAdulteryIsNamed\": null,\n    \"D8ReasonForDivorceAdultery3rdAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8FinancialOrder\": null,\n    \"D8FinancialOrderFor\": [],\n    \"D8HelpWithFeesNeedHelp\": null,\n    \"D8HelpWithFeesAppliedForFees\": null,\n    \"D8HelpWithFeesReferenceNumber\": null,\n    \"D8PaymentMethod\": null,\n    \"D8DivorceCostsClaim\": null,\n    \"D8DivorceIsNamed\": null,\n    \"D8DivorceClaimFrom\": [],\n    \"D8JurisdictionConfidentLegal\": null,\n    \"D8JurisdictionConnection\": [],\n    \"D8JurisdictionLastTwelveMonths\": null,\n    \"D8JurisdictionPetitionerDomicile\": null,\n    \"D8JurisdictionPetitionerResidence\": null,\n    \"D8JurisdictionRespondentDomicile\": null,\n    \"D8JurisdictionRespondentResidence\": null,\n    \"D8JurisdictionHabituallyResLast6Months\": null,\n    \"Payments\": [],\n    \"D8DocumentsUploaded\": [],\n    \"D8DocumentsGenerated\": [],\n    \"D8StatementOfTruth\": null,\n    \"D8DivorceUnit\": null,\n    \"D8Cohort\": null\n  },\n  \"event\": {\n    \"id\": \"create\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"D8legalProcess\": null,\n    \"createdDate\": null,\n    \"D8ScreenHasMarriageBroken\": null,\n    \"D8ScreenHasRespondentAddress\": null,\n    \"D8ScreenHasMarriageCert\": null,\n    \"D8ScreenHasPrinter\": null,\n    \"D8DivorceWho\": null,\n    \"D8MarriageIsSameSexCouple\": null,\n    \"D8InferredPetitionerGender\": null,\n    \"D8InferredRespondentGender\": null,\n    \"D8MarriageDate\": null,\n    \"D8MarriedInUk\": null,\n    \"D8CertificateInEnglish\": null,\n    \"D8CertifiedTranslation\": null,\n    \"D8MarriagePlaceOfMarriage\": null,\n    \"D8CountryName\": null,\n    \"D8MarriagePetitionerName\": null,\n    \"D8MarriageRespondentName\": null,\n    \"D8PetitionerNameDifferentToMarriageCert\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerFirstName\": null,\n    \"D8PetitionerLastName\": null,\n    \"D8DerivedPetitionerCurrentFullName\": null,\n    \"D8PetitionerNameChangedHow\": [],\n    \"D8PetitionerNameChangedHowOtherDetails\": null,\n    \"D8PetitionerContactDetailsConfidential\": null,\n    \"D8PetitionerHomeAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedPetitionerHomeAddress\": null,\n    \"D8PetitionerCorrespondenceAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedPetitionerCorrespondenceAddr\": null,\n    \"D8PetitionerCorrespondenceUseHomeAddress\": null,\n    \"D8RespondentNameAsOnMarriageCertificate\": null,\n    \"D8RespondentFirstName\": null,\n    \"D8RespondentLastName\": null,\n    \"D8DerivedRespondentCurrentName\": null,\n    \"D8DerivedRespondentSolicitorDetails\": null,\n    \"D8RespondentHomeAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedRespondentCorrespondenceAddr\": null,\n    \"D8RespondentSolicitorName\": null,\n    \"D8RespondentSolicitorCompany\": null,\n    \"D8RespondentCorrespondenceSendToSol\": null,\n    \"D8RespondentSolicitorAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedRespondentSolicitorAddr\": null,\n    \"D8RespondentCorrespondenceUseHomeAddress\": null,\n    \"D8RespondentKnowsHomeAddress\": null,\n    \"D8RespondentLivesAtLastAddress\": null,\n    \"D8LivingArrangementsTogetherSeparated\": null,\n    \"D8LivingArrangementsLastLivedTogether\": null,\n    \"D8LivingArrangementsLiveTogether\": null,\n    \"D8LivingArrangementsLastLivedTogethAddr\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedLivingArrangementsLastLivedAddr\": null,\n    \"D8LegalProceedings\": null,\n    \"D8LegalProceedingsRelated\": [],\n    \"D8LegalProceedingsDetails\": null,\n    \"D8ReasonForDivorce\": null,\n    \"D8DerivedStatementOfCase\": null,\n    \"D8ReasonForDivorceBehaviourDetails\": null,\n    \"D8ReasonForDivorceDesertionDate\": null,\n    \"D8ReasonForDivorceDesertionAgreed\": null,\n    \"D8ReasonForDivorceDesertionDetails\": null,\n    \"D8ReasonForDivorceSeperationDate\": null,\n    \"D8ReasonForDivorceAdultery3rdPartyFName\": null,\n    \"D8ReasonForDivorceAdultery3rdPartyLName\": null,\n    \"D8DerivedReasonForDivorceAdultery3dPtyNm\": null,\n    \"D8ReasonForDivorceAdulteryDetails\": null,\n    \"D8ReasonForDivorceAdulteryKnowWhen\": null,\n    \"D8ReasonForDivorceAdulteryWishToName\": null,\n    \"D8ReasonForDivorceAdulteryKnowWhere\": null,\n    \"D8ReasonForDivorceAdulteryWhereDetails\": null,\n    \"D8ReasonForDivorceAdulteryWhenDetails\": null,\n    \"D8ReasonForDivorceAdulteryIsNamed\": null,\n    \"D8ReasonForDivorceAdultery3rdAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8FinancialOrder\": null,\n    \"D8FinancialOrderFor\": [],\n    \"D8HelpWithFeesNeedHelp\": null,\n    \"D8HelpWithFeesAppliedForFees\": null,\n    \"D8HelpWithFeesReferenceNumber\": null,\n    \"D8PaymentMethod\": null,\n    \"D8DivorceCostsClaim\": null,\n    \"D8DivorceIsNamed\": null,\n    \"D8DivorceClaimFrom\": [],\n    \"D8JurisdictionConfidentLegal\": null,\n    \"D8JurisdictionConnection\": [],\n    \"D8JurisdictionLastTwelveMonths\": null,\n    \"D8JurisdictionPetitionerDomicile\": null,\n    \"D8JurisdictionPetitionerResidence\": null,\n    \"D8JurisdictionRespondentDomicile\": null,\n    \"D8JurisdictionRespondentResidence\": null,\n    \"D8JurisdictionHabituallyResLast6Months\": null,\n    \"Payments\": [],\n    \"D8DocumentsUploaded\": [],\n    \"D8DocumentsGenerated\": [],\n    \"D8StatementOfTruth\": null,\n    \"D8DivorceUnit\": null,\n    \"D8Cohort\": null\n  }\n}"))
    //   .check(jsonPath("$.id").saveAs("caseId")))

    // .pause(1)

  val GetAssignedUsers = 

    exec(http("AAC_010_GetAssignedUsersAndRoles")
      .get(ccdDataStoreUrl + "/case-users")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("experimental","true")
      .queryParam("case_ids", "${caseToShare}"))

    .pause(Environment.constantthinkTime)

  /////////////////////////////////////////////////////////////////////////////////

  //Respondent Journey Requests - Create Case & Update Supplementary Case Data//

  val RJCreateCase = 

    exec(http("GetEventToken")
        .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication/token")
        .header("ServiceAuthorization", "Bearer ${bearerToken}")
        .header("Authorization", "Bearer ${access_token}")
        .header("Content-Type","application/json")
        .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("CreateCase")
        .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases")
        .header("ServiceAuthorization", "Bearer ${bearerToken}")
        .header("Authorization", "Bearer ${access_token}")
        .header("Content-Type","application/json")
        .body(StringBody("{\n  \"data\": {\n    \"solsSolicitorFirmName\": \"jon & ola\",\n    \"solsSolicitorAddress\": {\n      \"AddressLine1\": \"Flat 12\",\n      \"AddressLine2\": \"Bramber House\",\n      \"AddressLine3\": \"Seven Kings Way\",\n      \"PostTown\": \"Kingston Upon Thames\",\n      \"County\": \"\",\n      \"PostCode\": \"KT2 5BU\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"solsSolicitorAppReference\": \"test\",\n    \"solsSolicitorEmail\": \"ccdorg-mvgvh_mcccd.user52@mailinator.com\",\n    \"solsSolicitorPhoneNumber\": null,\n    \"organisationPolicy\": {\n      \"OrgPolicyCaseAssignedRole\": \"[Claimant]\",\n      \"OrgPolicyReference\": null,\n      \"Organisation\": {\n        \"OrganisationID\": \"IGWEE4D\",\n        \"OrganisationName\": \"ccdorg-mvgvh\"\n      }\n    }\n  },\n  \"event\": {\n    \"id\": \"solicitorCreateApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
        //.body(StringBody(" {   \n \t\"data\" : {\n      \"TextField\" : \"textField1\",\n      \"TextAreaField\" : \"textAreaField1\",\n      \"AddressField\" : {\n        \"AddressLine1\" : \"102 Petty France\",\n        \"AddressLine2\" : \"CCD\",\n        \"AddressLine3\" : \"c/o HMCTS Reform\",\n        \"Country\" : \"UK\"\n      },\n      \"OrganisationPolicyField1\" : {\n        \"OrgPolicyCaseAssignedRole\" : \"[Claimant]\",\n        \"OrgPolicyReference\" : \"ref\",\n        \"Organisation\" : {\n          \"OrganisationID\" : \"orgID1\",\n          \"OrganisationName\" : \"orgName1\"\n        }\n      },\n      \"OrganisationPolicyField2\" : {\n        \"OrgPolicyCaseAssignedRole\" : \"[Defendant]\",\n        \"OrgPolicyReference\" : \"ref\",\n        \"Organisation\" : {\n          \"OrganisationID\" : \"orgID2\",\n          \"OrganisationName\" : \"orgName2\"\n        }\n      }\n    },\n    \"event\" : {\n      \"id\" : \"createCase\",\n      \"summary\" : \"\",\n      \"description\" : \"\"\n    },\n    \"event_token\" : \"${eventToken}\",\n    \"ignore_warning\" : false,\n    \"draft_id\" : null\n \t\n }"))
        .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val RJUpdateSupplementaryCaseData =

    exec(http("CCD_UpdateSupplementaryCaseData")
        .post(ccdDataStoreUrl + "/cases/${caseId}/supplementary-data")
        .header("ServiceAuthorization", "Bearer ${bearerToken}")
        .header("Authorization", "Bearer ${access_token}")
        .header("Content-Type","application/json")
        .body(StringBody("{\n    \"supplementary_data_updates\": {\n        \"$inc\": {\n        \t\"orgs_assigned_users.aca-11\": 1\n        }\n    }\n}")))
        //.body(StringBody("{\n\t\"supplementary_data_updates\": {\n\t\t\"$set\": {\n\t\t\t\"orgs_assigned_users.OrgA\": 22,\n            \"orgs_assigned_users.OrgC\": \"Test\"\n\t\t},\n\t\t\"$inc\": {\n\t\t\t\"orgs_assigned_users.OrgB\": 1\n\t\t}\n\t}\n}")))

    .pause(Environment.constantthinkTime)

  //Respondent Journey Requests - Create Case & Update Supplementary Case Data//

  val RJElasticSearchGetRef =

    feed(feedCaseSearchData)

    .exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "${caseType}") //${caseType}
      .body(StringBody("{ \n   \"query\":{ \n      \"bool\":{ \n         \"filter\":{ \n            \"wildcard\":{ \n               \"reference\":\"${caseId}\"\n            }\n         }\n      }\n   }\n}"))
      .check(status in  (200)))

    .pause(Environment.constantthinkTime)

  val ETGetToken = 

    exec(http("GetEventToken")
        .get(ccdDataStoreUrl + "/caseworkers/554156/jurisdictions/EMPLOYMENT/case-types/Leeds/event-triggers/initiateCase/token")
        .header("ServiceAuthorization", "Bearer ${bearerToken}")
        .header("Authorization", "Bearer ${access_token}")
        .header("Content-Type","application/json")
        .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(4)

  val feedEthosCaseRef = csv("EthosCaseRef.csv")

  val ETCreateCase =

    feed(feedEthosCaseRef)

    .exec(http("CreateCase")
        .post(ccdDataStoreUrl + "/caseworkers/554156/jurisdictions/EMPLOYMENT/case-types/Leeds/cases")
        .header("ServiceAuthorization", "Bearer ${bearerToken}")
        .header("Authorization", "Bearer ${access_token}")
        .header("Content-Type","application/json")
        .body(ElFileBody("Ethos_SingleCase.json"))
        .check(jsonPath("$.id").saveAs("caseId"))
        .check(status.saveAs("statusvalue")))

//    .doIf(session=>session("statusvalue").as[String].contains("200")) {
//      exec {
//        session =>
//          val fw = new BufferedWriter(new FileWriter("CreateSingles.csv", true))
//          try {
//            fw.write(session("caseId").as[String] + "\r\n")
//          }
//          finally fw.close()
//          session
//      }
//    }

}