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
import scala.util.Random

object ccddatastore {

val config: Config = ConfigFactory.load()

val ccdDataStoreUrl = "http://ccd-data-store-api-${env}.service.core-compute-${env}.internal"
val CaseDocAPI = Environment.caseDocUrl

val ccdScope = "openid profile authorities acr roles openid profile roles"
val feedCaseSearchData = csv("caseSearchData.csv").random
val feedEthosSearchData = csv("EthosSearchData.csv").random

val constantThinkTime = Environment.constantthinkTime

private val rng: Random = new Random()
private def niNumber(): String = rng.alphanumeric.filter(_.isDigit).take(8).mkString
private def firstName(): String = rng.alphanumeric.filter(_.isLetter).take(10).mkString
private def lastName(): String = rng.alphanumeric.filter(_.isLetter).take(10).mkString

val headers_0 = Map( //Authorization token needs to be generated with idam login
  "Authorization" -> "AdminApiAuthToken ",
  "Content-Type" -> "application/json")

  val CCDAPI_ProbateCreate = 

    exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/event-triggers/applyForGrant/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Provate_ValidateCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"applyForGrant\",\n    \"summary\": \"test case\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      )

    .exec(http("API_Probate_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"applyForGrant\",\n    \"summary\": \"test case\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_ProbateCaseEvents =

    exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/paymentSuccessApp/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_Probate_ValidatePaymentSuccessful")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_PaymentSuccess.json")))

    .exec(http("API_Probate_PaymentSuccessful")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_PaymentSuccess.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/boStopCaseForCaseCreated/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken4")))

    .exec(http("API_Probate_ValidateStopCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {\n    \"boCaseStopReasonList\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"caseStopReason\": \"Other\"\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"boStopCaseForCaseCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken4}\",\n  \"ignore_warning\": false\n}")))

    .exec(http("API_Probate_StopCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {\n    \"boCaseStopReasonList\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"caseStopReason\": \"Other\"\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"boStopCaseForCaseCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken4}\",\n  \"ignore_warning\": false\n}"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_ProbateDocUpload = 

    exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/boUploadDocumentsStop/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(session => {
      session.set("FileName1", "1MB.pdf")
    })

    .exec(http("API_Probate_DocUploadProcess")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer ${access_token}")
      .header("ServiceAuthorization", "${probate_backendBearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "${CaseType}")
      .formParam("jurisdictionId", "${Jurisdiction}")
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
        .fileName("${FileName1}")
        .transferEncoding("binary"))
      .check(regex("""http://(.+)/""").saveAs("DMURL"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken")))

    .exec(http("API_Probate_ValidateDocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_Probate_DocUpload.json")))

    .exec(http("API_Probate_DocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_Probate_DocUpload.json")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_SSCSCreate =

    exec(http("API_SSCS_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/event-triggers/validAppealCreated/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(_.setAll(
        ("NINumber", niNumber()),
        ("firstname", firstName()),
        ("lastname", lastName())
    ))

    .exec(http("API_SSCS_ValidateCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_CreateValidAppeal.json")))

    .exec(http("API_SSCS_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_CreateValidAppeal.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_SSCSCaseEvents =

    exec(http("API_SSCS_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/uploadDocument/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(session => {
      session.set("FileName1", "1MB.pdf")
    })

    .exec(http("API_SSCS_DocUploadProcess")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer ${access_token}")
      .header("ServiceAuthorization", "${sscsBearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "Benefit") 
      .formParam("jurisdictionId", "SSCS") 
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
        .fileName("${FileName1}")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken")))

    .exec(http("API_SSCS_ValidateDocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_DocUpload.json")))

    .exec(http("API_SSCS_DocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_DocUpload.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_SSCS_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/addHearing/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken4")))

    .exec(http("API_SSCS_ValidateAddHearing")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_AddHearing.json")))

    .exec(http("API_SSCS_AddHearing")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_AddHearing.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_SSCS_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/dwpActionDirection/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken5")))

    .exec(http("API_SSCS_ValidateActionDirection")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_ActionDirection.json")))

    .exec(http("API_SSCS_ActionDirection")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_ActionDirection.json")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_CMCCreate =

    exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/event-triggers/CreateClaim/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_CMC_ValidateCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"CreateClaim\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}")))

    .exec(http("API_CMC_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"CreateClaim\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_CMCCaseEvents =

    exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/StayClaim/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken1")))

    .exec(http("API_CMC_ValidateCaseStayed")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"StayClaim\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken1}\",\n  \"ignore_warning\": false\n}")))

    .exec(http("API_CMC_CaseStayed")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"StayClaim\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken1}\",\n  \"ignore_warning\": false\n}")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/ReviewedPaperResponse/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken1")))

    .exec(_.setAll(  
      "FileName1"  -> ("1MB.pdf")
    ))

    .exec(http("API_CMC_DocUploadProcess")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer ${access_token}")
      .header("ServiceAuthorization", "${xui_webappBearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "${CaseType}") 
      .formParam("jurisdictionId", "${Jurisdiction}") 
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
        .fileName("${FileName1}")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken")))

    .exec(http("API_CMC_ValidateDocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/cmc/CMC_DocUpload.json")))

    .exec(http("API_CMC_DocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/cmc/CMC_DocUpload.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/ClaimNotes/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_CMC_ValidateClaimNotes")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"ClaimNotes\",\n    \"summary\": \"Test Claim Note\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken2}\",\n  \"ignore_warning\": false\n}")))

    .exec(http("API_CMC_ClaimNotes")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"ClaimNotes\",\n    \"summary\": \"Test Claim Note\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken2}\",\n  \"ignore_warning\": false\n}")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/LinkLetterHolder/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken4")))

    .exec(http("API_CMC_ValidateLinkLetterHolder")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"LinkLetterHolder\",\n    \"summary\": \"perf test\",\n    \"description\": \"link letter holder perf test description\"\n  },\n  \"event_token\": \"${eventToken4}\",\n  \"ignore_warning\": false\n}")))

    .exec(http("API_CMC_LinkLetterHolder")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"LinkLetterHolder\",\n    \"summary\": \"perf test\",\n    \"description\": \"link letter holder perf test description\"\n  },\n  \"event_token\": \"${eventToken4}\",\n  \"ignore_warning\": false\n}")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/LiftStay/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken5")))

    .exec(http("API_CMC_ValidateLiftStay")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"LiftStay\",\n    \"summary\": \"perf test\",\n    \"description\": \"lift stay perf testing description\"\n  },\n  \"event_token\": \"${eventToken5}\",\n  \"ignore_warning\": false\n}")))

    .exec(http("API_CMC_LiftStay")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"LiftStay\",\n    \"summary\": \"perf test\",\n    \"description\": \"lift stay perf testing description\"\n  },\n  \"event_token\": \"${eventToken5}\",\n  \"ignore_warning\": false\n}")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_DivorceSolicitorCreate =

    exec(http("API_Divorce_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/event-triggers/solicitorCreate/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Divorce_ValidateSolCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/divorce/CCD_DivorceCreateSol.json")))

    .exec(http("API_Divorce_SolCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/divorce/CCD_DivorceCreateSol.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_DivorceSolicitorCaseEvents = 

    exec(http("API_Divorce_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/UpdateLanguage/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_Divorce_ValidateSolUpdateLanguage")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(StringBody("{\"data\":{\"LanguagePreferenceWelsh\":\"No\"},\"event\":{\"id\":\"UpdateLanguage\",\"summary\":\"\",\"description\":\"\"},\"event_token\":\"${eventToken2}\",\"ignore_warning\":false}")))

    .exec(http("API_Divorce_SolUpdateLanguage")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(StringBody("{\"data\":{\"LanguagePreferenceWelsh\":\"No\"},\"event\":{\"id\":\"UpdateLanguage\",\"summary\":\"\",\"description\":\"\"},\"event_token\":\"${eventToken2}\",\"ignore_warning\":false}")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_Divorce_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/solicitorStatementOfTruthPaySubmit/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(http("API_Divorce_ValidateolCaseSubmit")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(StringBody("{\n   \"data\":{\n      \"SolUrgentCase\":\"No\",\n      \"SolServiceMethod\":\"courtService\",\n      \"SolStatementOfReconciliationCertify\":\"Yes\",\n      \"SolStatementOfReconciliationDiscussed\":\"Yes\",\n      \"D8StatementOfTruth\":\"Yes\",\n      \"solSignStatementofTruth\":\"Yes\",\n      \"SolStatementOfReconciliationName\":\"Vuser\",\n      \"SolStatementOfReconciliationFirm\":\"Perf\",\n      \"StatementOfReconciliationComments\":null,\n      \"solApplicationFeeInPounds\":\"550\",\n      \"SolPaymentHowToPay\":\"feesHelpWith\",\n      \"D8HelpWithFeesReferenceNumber\":\"perfte\",\n      \"solApplicationFeeOrderSummary\":{\n         \"PaymentReference\":null,\n         \"PaymentTotal\":\"55000\",\n         \"Fees\":[\n            {\n               \"value\":{\n                  \"FeeCode\":\"FEE0002\",\n                  \"FeeAmount\":\"55000\",\n                  \"FeeDescription\":\"Filing an application for a divorce, nullity or civil partnership dissolution\",\n                  \"FeeVersion\":\"5\"\n               }\n            }\n         ]\n      }\n   },\n   \"event\":{\n      \"id\":\"solicitorStatementOfTruthPaySubmit\",\n      \"summary\":\"\",\n      \"description\":\"\"\n   },\n   \"event_token\":\"${eventToken3}\",\n   \"ignore_warning\":false\n}")))

    .exec(http("API_Divorce_SolCaseSubmit")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(StringBody("{\n   \"data\":{\n      \"SolUrgentCase\":\"No\",\n      \"SolServiceMethod\":\"courtService\",\n      \"SolStatementOfReconciliationCertify\":\"Yes\",\n      \"SolStatementOfReconciliationDiscussed\":\"Yes\",\n      \"D8StatementOfTruth\":\"Yes\",\n      \"solSignStatementofTruth\":\"Yes\",\n      \"SolStatementOfReconciliationName\":\"Vuser\",\n      \"SolStatementOfReconciliationFirm\":\"Perf\",\n      \"StatementOfReconciliationComments\":null,\n      \"solApplicationFeeInPounds\":\"550\",\n      \"SolPaymentHowToPay\":\"feesHelpWith\",\n      \"D8HelpWithFeesReferenceNumber\":\"perfte\",\n      \"solApplicationFeeOrderSummary\":{\n         \"PaymentReference\":null,\n         \"PaymentTotal\":\"55000\",\n         \"Fees\":[\n            {\n               \"value\":{\n                  \"FeeCode\":\"FEE0002\",\n                  \"FeeAmount\":\"55000\",\n                  \"FeeDescription\":\"Filing an application for a divorce, nullity or civil partnership dissolution\",\n                  \"FeeVersion\":\"5\"\n               }\n            }\n         ]\n      }\n   },\n   \"event\":{\n      \"id\":\"solicitorStatementOfTruthPaySubmit\",\n      \"summary\":\"\",\n      \"description\":\"\"\n   },\n   \"event_token\":\"${eventToken3}\",\n   \"ignore_warning\":false\n}")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_DivorceNFDCreate = 

    exec(http("API_NFD_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/event-triggers/solicitor-create-application/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_NFD_SolCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/nfd/CCD_CreateNFDApp.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

  val CCDAPI_IACCreate =

    exec(http("API_IAC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/event-triggers/startAppeal/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_IAC_ValidateCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/iac/IACCreateCase.json")))

    .exec(http("API_IAC_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/iac/IACCreateCase.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

  val CCDAPI_FPLCreate =

    exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/event-triggers/openCase/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_FPL_ValidateCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("FPLCreateCase.json")))

    .exec(http("API_FPL_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("FPLCreateCase.json"))//.asJson
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_FPLCaseEvents = 

    exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/enterChildren/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken1")))

    .exec(http("API_FPL_ValidateAddChildren")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterChildren.json")))

    .exec(http("API_FPL_AddChildren")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterChildren.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/enterRespondents/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_FPL_ValidateEnterRespondents")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterRespondents.json")))

    .exec(http("API_FPL_EnterRespondents")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterRespondents.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/enterGrounds/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(http("API_FPL_ValidateEnterGrounds")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterGrounds.json")))

    .exec(http("API_FPL_EnterGrounds")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterGrounds.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/uploadDocuments/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken4")))

     .exec(session => {
      session.set("FileName1", "1MB.pdf")
    })

    .exec(http("API_FPL_DocUploadProcess")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer ${access_token}")
      .header("ServiceAuthorization", "${xui_webappBearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "${CaseType}") 
      .formParam("jurisdictionId", "${Jurisdiction}") 
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
        .fileName("${FileName1}")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken")))

    .exec(http("API_FPL_ValidateDocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_UploadDocuments.json")))

    .exec(http("API_FPL_DocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_UploadDocuments.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/ordersNeeded/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken5")))
    
    .exec(http("API_FPL_ValidateOrdersNeeded")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_OrdersNeeded.json")))

    .exec(http("API_FPL_OrdersNeeded")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_OrdersNeeded.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/hearingNeeded/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken6")))

    .exec(http("API_FPL_ValidateHearingNeeded")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_HearingNeeded.json")))

    .exec(http("API_FPL_HearingNeeded")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_HearingNeeded.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/enterLocalAuthority/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken7")))

    .exec(http("API_FPL_ValidateEnterLocalAuthority")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_LocalAuthority.json")))

    .exec(http("API_FPL_EnterLocalAuthority")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_LocalAuthority.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/otherProposal/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(http("API_FPL_ValidateOtherProposal")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_OtherProposal.json")))

    .exec(http("API_FPL_OtherProposal")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_OtherProposal.json")))

    .pause(Environment.constantthinkTime)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/event-triggers/submitApplication/token")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken8"))
      .check(jsonPath("$.case_details.case_data.draftApplicationDocument.document_url").saveAs("documentUrl"))
      .check(jsonPath("$.case_details.case_data.draftApplicationDocument.document_filename").saveAs("documentFilename")))

    .exec(http("API_FPL_ValidateSubmitApplication")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/fpl/CCD_FPL_SubmitApplication.json")))

    .exec(http("API_FPL_SubmitApplication")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/fpl/CCD_FPL_SubmitApplication.json")))

    .pause(Environment.constantthinkTime)

  val CCDAPI_EthosJourney =

    feed(feedEthosSearchData)

    .exec(http("CCD_EthosSearch")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?case_reference=${EthosCaseRef}")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(status in (200)))

    .pause(Environment.constantthinkTime)

    .exec(http("CCD_EthosViewCase")
      .get(ccdDataStoreUrl + "/cases/${EthosCaseRef}")
      .header("ServiceAuthorization", "Bearer ${ccd_dataBearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .header("Experimental","true")
      .check(status in (200)))

    .pause(Environment.constantthinkTime)

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
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val RJUpdateSupplementaryCaseData =

    exec(http("CCD_UpdateSupplementaryCaseData")
      .post(ccdDataStoreUrl + "/cases/${caseId}/supplementary-data")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n    \"supplementary_data_updates\": {\n        \"$inc\": {\n        \t\"orgs_assigned_users.aca-11\": 1\n        }\n    }\n}")))

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