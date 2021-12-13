package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment
import scala.concurrent.duration._

object SSCS {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val feedUserData = csv("SSCSUserData.csv").circular
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val sscsCaseActivityRepeat = 3

  // val headers_0 = Map(
  //   "Sec-Fetch-Mode" -> "cors",
  //   "experimental" -> "true")

  val headers_0 = Map(
		//"Origin" -> "https://www-ccd.perftest.platform.hmcts.net",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-site",
    	"experimental" -> "true")

  val headers_1 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-jurisdiction-configs.v2+json;charset=UTF-8",
		"Origin" -> CCDEnvurl,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-site",
		"experimental" -> "true")

  val headers_2 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
		"Origin" -> CCDEnvurl,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-site",
		"experimental" -> "true")

  val headers_3 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
	  	"Origin" -> CCDEnvurl,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-site",
		"experimental" -> "true")

  val headers_4 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
		"Origin" -> CCDEnvurl,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-site",
		"experimental" -> "true")

  val headers_5 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8",
		"Origin" -> CCDEnvurl,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-site",
		"experimental" -> "true")

  val headers_6 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-jurisdiction-configs.v2+json;charset=UTF-8",
		"Content-Type" -> "application/json",
		"Origin" -> CCDEnvurl,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-site",
		"experimental" -> "true")

	val headers_7 = Map(
		"Accept" -> "application/json",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Accept-Language" -> "en-US,en;q=0.9",
		"Cache-Control" -> "no-cache",
		"Connection" -> "keep-alive",
		"Content-Type" -> "application/json",
		"Origin" -> CCDEnvurl,
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-site")

	val headers_8 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
		"Content-Type" -> "application/json",
		"Sec-Fetch-Mode" -> "cors",
		"experimental" -> "true")

	val headers_9 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
		"Sec-Fetch-Mode" -> "cors",
		"experimental" -> "true")

	val headers_10 = Map(
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

  	val headers_11 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8",
		"Content-Type" -> "application/json",
		"Origin" -> CCDEnvurl,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-site",
		"experimental" -> "true")

  	val headers_12 = Map(
		"Content-Type" -> "application/json",
		"Origin" -> CCDEnvurl,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-site")

  val headers_15 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Accept-Language" -> "en-US,en;q=0.9",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "none",
		"Upgrade-Insecure-Requests" -> "1")

  val SSCSLogin = group("SSCS_Login") {

	exec(http("SSCS_020_005_Login")
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

	.exec(http("SSCS_020_010_Login")
		.get(CCDEnvurl + "/config")
		.headers(headers_1))

	.exec(http("SSCS_020_015_Login")
		.get("/oauth2?code=${authCode}&redirect_uri=www-ccd.perftest.platform.hmcts.net/oauth2redirect")
		.headers(headers_2))

	.exec(http("SSCS_020_020_Login")
		.get(CCDEnvurl + "/config")
		.headers(headers_1))

	.exec(http("SSCS_020_025_Login")
		.get("/activity/cases/0/activity")
		.headers(headers_2))
	//.check(status.is(404))

	.exec(http("SSCS_020_030_Login")
		.get("/data/internal/profile")
		.headers(headers_5))

	.exec(http("SSCS_020_035_Login")
		.get("/data/internal/jurisdiction-ui-configs/?ids=DIVORCE&ids=AUTOTEST1&ids=CMC&ids=PROBATE&ids=SSCS&ids=EMPLOYMENT")
		.headers(headers_6))

	.exec(http("SSCS_020_040_Login")
		.get("/data/internal/jurisdiction-ui-configs/?ids=DIVORCE&ids=AUTOTEST1&ids=CMC&ids=PROBATE&ids=SSCS&ids=EMPLOYMENT")
		.headers(headers_6))

	.exec(http("SSCS_020_045_Login")
		.get("/data/internal/jurisdiction-ui-configs/?ids=DIVORCE&ids=AUTOTEST1&ids=CMC&ids=PROBATE&ids=SSCS&ids=EMPLOYMENT")
		.headers(headers_6))

	.exec(http("SSCS_020_050_Login")
		.get("/data/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases/pagination_metadata")
		.headers(headers_12))

	.exec(http("SSCS_020_055_Login")
		.get("/aggregated/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases?view=WORKBASKET&page=1")
		.headers(headers_12)
		// .check(regex("""case_id":"(.+?)","case_fields""").findAll.optional.saveAs("caseNumbers"))
    )

	.exec(http("SSCS_020_060_Login")
		.get("/data/internal/case-types/Benefit/work-basket-inputs")
		.headers(headers_11))

	// .exec(http("SSCS_020_065_Login")
	// 	.get("/activity/cases/${caseNumbers}/activity")
	// 	.headers(headers_2))

	// .exec {
	// session =>
	// 		println(session("Current logged in user is: "))
	// 		println(session("SSCSUserName").as[String])
	// 	session
	// }

	}
  .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val SSCSCreateCase =

    exec(http("SSCS_030_005_CreateCasePage")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(CommonHeader))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("SSCS_030_010_CreateCaseDetailsPage")
		.get("/data/internal/case-types/Benefit/event-triggers/appealCreated?ignore-warning=false")
		.headers(headers_3)
		.check(jsonPath("$.event_token").saveAs("New_Case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("SSCS_030_015_CreateCaseSubmit")
		.post("/data/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases?ignore-warning=false")
		.headers(CommonHeader)
		.body(StringBody("{\n  \"data\": {\n    \"caseReference\": null,\n    \"caseCreated\": \"2020-05-12\",\n    \"region\": null,\n    \"appeal\": {\n      \"receivedVia\": \"Online\",\n      \"mrnDetails\": {\n        \"dwpIssuingOffice\": \"DWP\",\n        \"mrnDate\": \"2019-11-22\",\n        \"mrnLateReason\": null,\n        \"mrnMissingReason\": null\n      },\n      \"appellant\": {\n        \"name\": {\n          \"title\": \"Mr\",\n          \"firstName\": \"Daniel\",\n          \"middleName\": null,\n          \"lastName\": \"Smith\"\n        },\n        \"identity\": {\n          \"dob\": \"2000-03-01\",\n          \"nino\": \"AB1234567Z\"\n        },\n        \"address\": {\n          \"line1\": \"24 Test Street\",\n          \"line2\": null,\n          \"line3\": null,\n          \"town\": \"London\",\n          \"county\": null,\n          \"postcode\": \"KT2 5BU\",\n          \"country\": \"UK\"\n        },\n        \"contact\": {\n          \"phone\": \"07123456789\",\n          \"mobile\": null,\n          \"email\": null\n        },\n        \"isAppointee\": \"No\",\n        \"appointee\": {\n          \"name\": {\n            \"title\": null,\n            \"firstName\": null,\n            \"middleName\": null,\n            \"lastName\": null\n          },\n          \"identity\": {\n            \"dob\": null,\n            \"nino\": null\n          },\n          \"address\": {\n            \"line1\": null,\n            \"line2\": null,\n            \"line3\": null,\n            \"town\": null,\n            \"county\": null,\n            \"postcode\": null,\n            \"country\": null\n          },\n          \"contact\": {\n            \"phone\": null,\n            \"mobile\": null,\n            \"email\": null\n          }\n        },\n        \"isAddressSameAsAppointee\": null\n      },\n      \"benefitType\": {\n        \"code\": null,\n        \"description\": null\n      },\n      \"hearingType\": null,\n      \"hearingOptions\": {\n        \"wantsToAttend\": null,\n        \"languageInterpreter\": null,\n        \"other\": null,\n        \"signLanguageType\": null\n      },\n      \"appealReasons\": {\n        \"reasons\": [],\n        \"otherReasons\": null\n      },\n      \"supporter\": {\n        \"name\": {\n          \"title\": null,\n          \"firstName\": null,\n          \"middleName\": null,\n          \"lastName\": null\n        },\n        \"contact\": {\n          \"phone\": null,\n          \"mobile\": null,\n          \"email\": null\n        }\n      },\n      \"rep\": {\n        \"hasRepresentative\": null\n      },\n      \"signer\": null\n    },\n    \"regionalProcessingCenter\": {\n      \"name\": null,\n      \"address1\": null,\n      \"address2\": null,\n      \"address3\": null,\n      \"address4\": null,\n      \"postcode\": null,\n      \"city\": null,\n      \"phoneNumber\": null,\n      \"faxNumber\": null,\n      \"email\": null\n    },\n    \"panel\": {\n      \"assignedTo\": null,\n      \"medicalMember\": null,\n      \"disabilityQualifiedMember\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"appealCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
		.check(jsonPath("$.id").saveAs("New_Case_Id")))

	.pause(MinThinkTime seconds, MaxThinkTime seconds)

  val SSCSDocUpload = exec(http("SSCS_040_005_DocumentUploadPage1")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/uploadDocument?ignore-warning=false")
        .headers(headers_9)
        .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

	.pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(session => {
      session.set("FileName1", "1MB.pdf")
    })

    .exec(http("SSCS_040_010_DocumentUploadToDM1")
      .post(BaseURL + "/documents")
      .bodyPart(RawFileBodyPart("files", "${FileName1}") 
        .fileName("${FileName1}") 
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(status.is(200))
      .check(regex("""http://(.+)/""").saveAs("DMURL"))
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID")))

    .exec(http("SSCS_040_015_DocumentUploadProcess")
      .post("/data/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases/${New_Case_Id}/events")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {\n    \"sscsDocument\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"documentType\": \"Other evidence\",\n          \"documentEmailContent\": null,\n          \"documentDateAdded\": \"2019-11-12\",\n          \"documentComment\": \"${FileName1} upload\",\n          \"documentFileName\": null,\n          \"documentLink\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}/binary\",\n            \"document_filename\": \"${FileName1}\"\n          }\n        }\n      },\n      {\n        \"id\": null,\n        \"value\": {\n          \"documentType\": null,\n          \"documentEmailContent\": null,\n          \"documentDateAdded\": null,\n          \"documentComment\": null,\n          \"documentFileName\": null\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"uploadDocument\",\n    \"summary\": \"${FileName1} upload doc\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

	.pause(MinThinkTime seconds, MaxThinkTime seconds)

  val SSCSSearchAndView = 
    exec(http("SSCS_050_005_SearchForCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases?view=WORKBASKET&state=appealCreated&page=1")
      .headers(headers_7))

    .exec(http("SSCS_050_010_SearchForCase")
      .get("/data/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases/pagination_metadata?state=appealCreated")
      .headers(CommonHeader))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("SSCS_060_005_OpenCase")
      .get("/data/internal/cases/${New_Case_Id}")
      .headers(headers_8))

	.pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("SSCS_060_010_OpenDocument")
      .get("/documents/${Document_ID}/binary")
      .headers(headers_15))

	val SSCSCaseActivity =
	
	  doIf(session => session.contains("New_Case_Id")) {
		repeat(sscsCaseActivityRepeat) {
			exec(http("SSCS_CaseActivity")
				.options("/activity/cases/${New_Case_Id}/activity")
				.headers(headers_2))

			.pause(1)

			.exec(http("SSCS_CaseActivity")
				.get("/activity/cases/${New_Case_Id}/activity")
				.headers(headers_2))

			.pause(1)

			.exec(http("SSCS_CaseActivity")
				.post("/activity/cases/${New_Case_Id}/activity")
				.body(StringBody("{\n  \"activity\": \"view\"\n}"))
				.headers(headers_10))

			.pause(1)

			.exec(http("SSCS_CaseActivity")
				.options("/activity/cases/${New_Case_Id}/activity")
				.headers(headers_2))

			.pause(Environment.caseActivityPause)
		}
	}

  // .exec {
  //   session =>
  //     println(session("New_Case_Id").as[String])
  //     println(session("Document_ID").as[String])
  //     session
  // }
}