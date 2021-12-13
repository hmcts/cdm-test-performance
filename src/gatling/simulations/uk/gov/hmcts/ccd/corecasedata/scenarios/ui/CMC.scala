package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment
import scala.concurrent.duration._

object CMC {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val feedUserData = csv("CMCUserData.csv").circular
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val cmcCaseActivityRepeat = 2

  val headers_0 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_1 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_2 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_3 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> CCDEnvurl,
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_5 = Map(
		"Accept" -> "application/json",
		"Content-Type" -> "application/json",
		"DNT" -> "1",
		"Origin" -> CCDEnvurl,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-site",
		"sec-ch-ua" -> """Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87""",
		"sec-ch-ua-mobile" -> "?0")

  val headers_8 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_9 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> CCDEnvurl,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_11 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val CMCLogin = group ("CMC_Login") {

    // ====================================================================
    // Enter email and password - Click Sign In button
    // ====================================================================

    exec(http("CMC_020_005_Login")
      .post(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .disableFollowRedirect
      .headers(idam_header)
      .formParam("username", "${Username}")
      .formParam("password", "${Password}")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrf}")
      .check(headerRegex("Location", "(?<=code=)(.*)&client").saveAs("authCode"))
      .check(status.in(200, 302)))

    .exec(http("CMC_020_010_Login")
      .get(CCDEnvurl + "/config")
      .headers(CommonHeader))

    .exec(http("CMC_020_015_Login")
      .options(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .headers(CommonHeader))

    .exec(http("CMC_020_020_Login")
      .get(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .headers(CommonHeader))

    .exec(http("CMC_020_025_Login")
      .get(CCDEnvurl + "/config")
      .headers(CommonHeader))

    .exec(http("CMC_020_030_Login")
      .options(BaseURL + "/data/caseworkers/:uid/profile"))

    .exec(http("CMC_020_035_Login")
      .get(BaseURL + "/data/caseworkers/:uid/profile")
      .headers(CommonHeader))

    .exec(http("CMC_020_040_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types?access=read")
      .headers(CommonHeader))

    .exec(http("CMC_020_045_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types?access=read")
      .headers(CommonHeader))

    .exec(http("CMC_020_050_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/work-basket-inputs"))

    .exec(http("CMC_020_055_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?view=WORKBASKET&state=TODO&page=1"))

    .exec(http("CMC_020_060_Login")
      .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata?state=TODO"))

    .exec(http("CMC_020_065_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/work-basket-inputs")
      .headers(CommonHeader))

    .exec(http("CMC_020_070_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?view=WORKBASKET&state=TODO&page=1")
      .headers(CommonHeader))

    .exec(http("CMC_020_075_Login")
      .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata?state=TODO")
      .headers(CommonHeader))

    .exec(http("CMC_020_080_Login")
      .get("/activity/cases/1552650446279756,1574715851299047,1574771353085053,1574771425565793,1574775065167620,1574775076679514,1574775081771140,1574775085031665,1574775090059446,1574775116202087,1574775125129875,1574775125356445,1574775164890403,1574775167970699,1574775170224035,1574775201506996,1574775205680128,1574775230602188,1574775232314675,1574775247646285,1574775263929649,1574775275516038,1574775282732867,1574775283695253,1574775292722858/activity")
      .headers(headers_2))
  }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val CMCCreateCase = 

    // ====================================================================
    // Click the Create Case Button
    // ====================================================================
  
    exec(http("CMC_030_005_CreateCasePage")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(CommonHeader))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // ====================================================================
    // Proceed to the Case Details Screen
    // ====================================================================

    .exec(http("CMC_030_010_CreateCaseDetails")
      .get(BaseURL + "/data/internal/case-types/${CaseType}/event-triggers/CreateClaim?ignore-warning=false")
      .headers(headers_1)
      .check(jsonPath("$.event_token").saveAs("New_Case_event_token")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // ====================================================================
    // Enter details and click Submit
    // ====================================================================      

    .exec(http("CMC_030_015_CreateCaseSubmit")
      .post(BaseURL + "/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?ignore-warning=false")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"CreateClaim\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("New_Case_Id")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

//  .exec { session =>
//    val s1 = new File("src/gatling/resources/CMCData.csv")
//    val writer = new PrintWriter(new FileOutputStream(new File("/src/gatling/resources/CMCData.csv"), true))
//    writer.write(session("New_Case_Id").as[String].trim)
//    writer.write(",")
//    writer.write(session("New_Case_Id").as[String].trim)
//    writer.close()
//    session
//  }

  val CMCStayCase = 

    // ====================================================================
    // Select Stay Claim from the drop down and click Go
    // ====================================================================

    exec(http("CMC_040_005_StayClaim")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/StayClaim?ignore-warning=false")
      .headers(headers_11)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // ====================================================================
    // Submit the Stay Claim step
    // ====================================================================

    .exec(http("CMC_040_005_StayClaimSubmit")
      .post("/data/cases/${New_Case_Id}/events")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"StayClaim\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val CMCWaitingTransfer = 
  
    // ====================================================================
    // Select Waiting Transfer from the drop down and click Go
    // ====================================================================

    exec(http("CMC_050_005_WaitingTransfer")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/WaitingTransfer?ignore-warning=false")
      .headers(headers_11)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // ====================================================================
    // Submit the Waiting Transfer step
    // ====================================================================

    .exec(http("CMC_050_005_WaitingTransferSubmit")
      .post("/data/cases/${New_Case_Id}/events")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"WaitingTransfer\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val CMCTransfer = 
  
    // ====================================================================
    // Select Transfer from the drop down and click Go
    // ====================================================================

    exec(http("CMC_060_005_Transfer")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/Transfer?ignore-warning=false")
      .headers(headers_11)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // ====================================================================
    // Submit the Transfer step
    // ====================================================================    

    .exec(http("CMC_060_005_TransferSubmit")
      .post("/data/cases/${New_Case_Id}/events")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"Transfer\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val CMCAttachScannedDocs = 
  
    // ====================================================================
    // Select Attached Scanned Docs from the drop down and click Go
    // ====================================================================

    exec(http("CMC_070_005_AttachScannedDocs")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/attachScannedDocs?ignore-warning=false")
      .headers(headers_11)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // ====================================================================
    // Submit the Attach Scanned Docs step
    // ====================================================================

    .exec(http("CMC_070_005_AttachScannedDocsSubmit")
      .post("/data/cases/${New_Case_Id}/events")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"attachScannedDocs\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val CMCSupportUpdate = 

    // ====================================================================
    // Select Support Update from the drop down and click Go
    // ====================================================================  
  
    exec(http("CMC_080_005_SupportUpdate")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/SupportUpdate?ignore-warning=false")
      .headers(headers_11)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // ====================================================================
    // Submit the Support Update step
    // ==================================================================== 

    .exec(http("CMC_080_005_SupportUpdateSubmit")
      .post("/data/cases/${New_Case_Id}/events")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"SupportUpdate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val CMCSearchAndView = 

    // ====================================================================
    // Navigate to the search page
    // ==================================================================== 
  
    exec(http("CMC_090_005_SearchPage")
      .get("/data/internal/case-types/${CaseType}/work-basket-inputs")
      .headers(headers_0))

    // ====================================================================
    // Search for the newly created case
    // ==================================================================== 

    .exec(http("CMC_090_010_SearchForCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?view=WORKBASKET&page=1&case_reference=${New_Case_Id}")
      .headers(CommonHeader))

    .exec(http("CMC_090_015_SearchForCase")
      .get("/data/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases/pagination_metadata?case_reference=${New_Case_Id}")
      .headers(CommonHeader))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // ====================================================================
    // Open the Case
    // ==================================================================== 

    .exec(http("CMC_100_OpenCase")
      .get("/data/internal/cases/${New_Case_Id}")
      .headers(headers_8))

  val CMCCaseActivity = 

    doIf(session => session.contains("New_Case_Id")) {
      repeat(cmcCaseActivityRepeat) {
        exec(http("CMC_CaseActivity")
          .options("/activity/cases/${New_Case_Id}/activity")
          .headers(headers_2))

        .pause(1)

        .exec(http("CMC_CaseActivity")
          .get("/activity/cases/${New_Case_Id}/activity")
          .headers(headers_2))

        .pause(1)

        .exec(http("CMC_CaseActivity")
          .post("/activity/cases/${New_Case_Id}/activity")
          .body(StringBody("{\n  \"activity\": \"view\"\n}"))
          .headers(headers_5))

        .pause(1)

        .exec(http("CMC_CaseActivity")
          .options("/activity/cases/${New_Case_Id}/activity")
          .headers(headers_2))

        .pause(Environment.caseActivityPause)
      }
    }

}
