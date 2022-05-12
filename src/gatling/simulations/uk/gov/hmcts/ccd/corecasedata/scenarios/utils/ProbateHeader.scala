package uk.gov.hmcts.ccd.corecasedata.scenarios.utils

object ProbateHeader {

 val baseURL = Environment.baseURL

 val commonHeader = Map(
  "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Sec-Fetch-Mode" -> "navigate",
  "Sec-Fetch-Site" -> "cross-site",
  "Upgrade-Insecure-Requests" -> "1")

 val headers_28 = Map(
  "Accept" -> "application/json",
  "Content-Type" -> "application/json",
  "Sec-Fetch-Mode" -> "cors")

 val headers_search = Map(
  "Content-Type" -> "application/json",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Connection" -> "Keep-Alive",
  "Pragma" -> "no-cache",
  "Sec-Fetch-Dest" -> "empty",
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "same-origin")

 val headers_viewCase = Map(
  "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
  "Content-Type" -> "application/json",
  "Pragma" -> "no-cache",
  "Sec-Fetch-Dest" -> "empty",
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "same-origin",
  "experimental" -> "true")

 val headers_casecreate = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$484214221_400h45vVNUHLCFUOLBQIRUKGTWUKCORNUWNRADP-0")


 val headers_casefilter= Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$484214221_400h48vVNUHLCFUOLBQIRUKGTWUKCORNUWNRADP-0",
  "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-filter")

 val headers_casedata = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "origin" -> "https://manage-case.perftest.platform.hmcts.net",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$484214221_400h50vVNUHLCFUOLBQIRUKGTWUKCORNUWNRADP-0")

 val headers_draft = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-draft-create.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "origin" -> "https://manage-case.perftest.platform.hmcts.net",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$484214221_400h51vVNUHLCFUOLBQIRUKGTWUKCORNUWNRADP-0")


 val headers_casedataprofile = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$484214221_400h52vVNUHLCFUOLBQIRUKGTWUKCORNUWNRADP-0",
  "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-create/PROBATE/GrantOfRepresentation/solicitorCreateApplication/solicitorCreateApplicationsolicitorCreateApplicationPage1")

 val headers_solappcreated = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "origin" -> "https://manage-case.perftest.platform.hmcts.net",
  "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
  "request-id" -> "|M5xCL.OuK2x",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$484214221_400h63vVNUHLCFUOLBQIRUKGTWUKCORNUWNRADP-0")

 val headers_saveandviewcase= Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
  "request-id" -> "|M5xCL.CZEGn",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$484214221_400h65vVNUHLCFUOLBQIRUKGTWUKCORNUWNRADP-0")

  val headers_0 = Map(
		"Accept" -> "application/json, text/plain, */*",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin")

	val headers_1 = Map(
		"Content-Type" -> "application/json",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin")

}
