package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment
import scala.concurrent.duration._

object PBGoR {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val feedUserData = csv("ProbateUserData.csv").circular
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val caseActivityRepeat = 3

  val probateHeader = Map(
		"Pragma" -> "no-cache",
		"Accept" -> "application/json",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Cache-Control" -> "no-cache",
    "Connection" -> "keep-alive",
    "Content-Type" -> "application/json",
    "Origin" -> CCDEnvurl,
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-site",
		"experimental" -> "true")

  val DocumentOpen = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "none",
    "Upgrade-Insecure-Requests" -> "1")

  val submitLogin = group("PB_Login") {

    exec(http("PBGoR_020_005_Login")
      .post(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .disableFollowRedirect
      .headers(idam_header)
      .formParam("username", "${Username}") //${CCDUserName}
      .formParam("password", "${Password}")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrf}")
      .check(headerRegex("Location", "(?<=code=)(.*)&client").saveAs("authCode"))
      .check(status.in(200, 302)))
      //.exitHereIfFailed

    .exec(http("PBGoR_020_010_Login")
      .get(CCDEnvurl + "/config")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("PBGoR_020_015_Login")
      .options(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("PBGoR_020_020_Login")
      .get(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("PBGoR_020_025_Login")
      .get(CCDEnvurl + "/config")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("PBGoR_020_030_Login")
      .options(BaseURL + "/data/caseworkers/:uid/profile"))
    //.exitHereIfFailed

    .exec(http("PBGoR_020_035_Login")
      .get(BaseURL + "/data/caseworkers/:uid/profile")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("PBGoR_020_040_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types?access=read"))

    .exec(http("PBGoR_020_045_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types?access=read")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("PBGoR_020_050_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/work-basket-inputs"))
    //.exitHereIfFailed

    .exec(http("PBGoR_020_055_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?view=WORKBASKET&state=BOCaseStopped&page=1"))
    //.exitHereIfFailed

    .exec(http("PBGoR_020_060_Login")
      .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata?state=BOCaseStopped"))
    //.exitHereIfFailed

    .exec(http("PBGoR_020_065_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/work-basket-inputs")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("PBGoR_020_070_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?view=WORKBASKET&state=BOCaseStopped&page=1")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("PBGoR_020_075_Login")
      .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata?state=BOCaseStopped")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("PBGoR_020_080_Login")
			.get("/activity/cases/1552650446279756,1574715851299047,1574771353085053,1574771425565793,1574775065167620,1574775076679514,1574775081771140,1574775085031665,1574775090059446,1574775116202087,1574775125129875,1574775125356445,1574775164890403,1574775167970699,1574775170224035,1574775201506996,1574775205680128,1574775230602188,1574775232314675,1574775247646285,1574775263929649,1574775275516038,1574775282732867,1574775283695253,1574775292722858/activity")
			// .headers(headers_2)
      .headers(probateHeader)
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8"))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val PBCreateCase =
    exec(http("PBGoR_030_005_CreateCasePage")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(CommonHeader))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("PBGoR_030_010_CreateCaseDetails")
      .get(BaseURL + "/data/internal/case-types/${CaseType}/event-triggers/applyForGrant?ignore-warning=false")
      // .headers(headers_1)
      .headers(probateHeader)
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
      .check(jsonPath("$.event_token").saveAs("New_Case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("PBGoR_030_015_CreateCaseSubmit")
      .post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?ignore-warning=false")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"applyForGrant\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("New_Case_Id")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val PBPaymentSuccessful =
    exec(http("PBGoR_040_005_PaymentSuccessfulPage")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/paymentSuccessApp?ignore-warning=false")
      // .headers(headers_0)
      .headers(probateHeader)
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("PBGoR_040_010_PaymentSuccessfulDetails")
      .post("/data/case-types/${CaseType}/validate?pageId=paymentSuccessAppboPaymentSuccessfulAppPage1")
      // .headers(headers_4)
      .headers(probateHeader)
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .body(StringBody("{\n  \"data\": {\n    \"applicationSubmittedDate\": \"2019-03-01\"\n  },\n  \"event\": {\n    \"id\": \"paymentSuccessApp\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"applicationSubmittedDate\": \"2019-03-01\"\n  },\n  \"case_reference\": \"${New_Case_Id}\"\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("PBGoR_040_015_PaymentSuccessfulSubmit")
      .post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/events")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {\n    \"applicationSubmittedDate\": \"2019-03-01\"\n  },\n  \"event\": {\n    \"id\": \"paymentSuccessApp\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val PBDocUpload =
    exec(http("PBGoR_050_005_DocumentUploadPage")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/boUploadDocumentsForCaseCreated?ignore-warning=false")
      // .headers(headers_0)
      .headers(probateHeader)
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(session => {
        session.set("FileName1", "1MB.pdf")
      })

    .exec(http("PBGoR_050_010_DocumentUploadToDM")
      .post(BaseURL + "/documents")
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
        .fileName("${FileName1}")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(status.is(200))
      .check(regex("""http://(.+)/""").saveAs("DMURL"))
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID")))

    .exec(http("PBGoR_050_015_DocumentUploadProcess")
      .post("/data/case-types/${CaseType}/validate?pageId=boUploadDocumentsForCaseCreatedboUploadDocumentPage1")
      // .headers(headers_4)
      .headers(probateHeader)
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .body(StringBody("{\n  \"data\": {\n    \"boDocumentsUploaded\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"DocumentType\": \"deathCertificate\",\n          \"Comment\": \"test 1mb file\",\n          \"DocumentLink\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}/binary\",\n            \"document_filename\": \"${FileName1}\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"boUploadDocumentsForCaseCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"boDocumentsUploaded\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"DocumentType\": \"deathCertificate\",\n          \"Comment\": \"test 1mb file\",\n          \"DocumentLink\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}/binary\",\n            \"document_filename\": \"${FileName1}\"\n          }\n        }\n      }\n    ]\n  },\n  \"case_reference\": \"${New_Case_Id}\"\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("PBGoR_050_020_DocumentUploadSubmit")
      .post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/events")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {\n    \"boDocumentsUploaded\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"DocumentType\": \"deathCertificate\",\n          \"Comment\": \"test 1mb file\",\n          \"DocumentLink\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}/binary\",\n            \"document_filename\": \"${FileName1}\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"boUploadDocumentsForCaseCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val PBStopCase =

    exec(http("PBGoR_060_005_StopCase")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/boStopCaseForCaseCreated?ignore-warning=false")
      // .headers(headers_9)
      .headers(probateHeader)
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("PBGoR_060_010_StopCaseAddReason")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=boStopCaseForCaseCreatedboStopCaseForCaseCreatedPage1")
      // .headers(headers_10)
      .headers(probateHeader)
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .body(StringBody("{\n  \"data\": {\n    \"boCaseStopReasonList\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"caseStopReason\": \"Other\"\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"boStopCaseForCaseCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"boCaseStopReasonList\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"caseStopReason\": \"Other\"\n        }\n      }\n    ]\n  },\n  \"case_reference\": \"${New_Case_Id}\"\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("PBGoR_060_015_StopCaseSubmit")
      .post("/data/cases/${New_Case_Id}/events")
      // .headers(headers_11)
      .headers(probateHeader)
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .body(StringBody("{\n  \"data\": {\n    \"boCaseStopReasonList\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"caseStopReason\": \"Other\"\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"boStopCaseForCaseCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val PBSearch =
    exec(http("PBGoR_070_005_SearchForCase")
      .get("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata")
      .headers(CommonHeader))

    .exec(http("PBGoR_070_010_SearchForCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?view=WORKBASKET&page=1")
      // .headers(headers_7)
      .headers(probateHeader))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val PBView =

    exec(http("PBGoR_080_005_OpenCase")
      .get("/data/internal/cases/${New_Case_Id}")
      // .headers(headers_8)
      .headers(probateHeader))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("PBGoR_080_010_OpenDocument")
      .get("/documents/${Document_ID}/binary")
      .headers(DocumentOpen))

  val PBCaseActivity =

    doIf(session => session.contains("New_Case_Id")) {
      repeat(caseActivityRepeat) {
        exec(http("PB_CaseActivity")
          .options("/activity/cases/${New_Case_Id}/activity")
          // .headers(headers_2)
          .headers(probateHeader)
          .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8"))

        .pause(1)

        .exec(http("PB_CaseActivity")
          .get("/activity/cases/${New_Case_Id}/activity")
          // .headers(headers_2)
          .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8"))

        .pause(1)

        .exec(http("PB_CaseActivity")
          .post("/activity/cases/${New_Case_Id}/activity")
          .body(StringBody("{\n  \"activity\": \"view\"\n}"))
          // .headers(headers_5)
          .headers(probateHeader))

        .pause(1)

        .exec(http("PB_CaseActivity")
          .options("/activity/cases/${New_Case_Id}/activity")
          // .headers(headers_2)
          .headers(probateHeader)
          .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8"))

          .pause(MinThinkTime seconds, MaxThinkTime seconds)
      }
    }
  }