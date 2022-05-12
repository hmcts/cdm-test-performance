package uk.gov.hmcts.ccd.corecasedata.scenarios.utils

object XuiHeaders {

  val baseURL = Environment.xuiBaseURL

  val headers_23 = Map(
		"Accept" -> "application/json",
		"Content-Type" -> "application/json",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin",
		"sec-ch-ua" -> """Chromium";v="88", "Google Chrome";v="88", ";Not A Brand";v="99""",
		"sec-ch-ua-mobile" -> "?0",
		"x-dtpc" -> "2$174557655_79h28vFBJHFFRNTUGVNMLMDVUMCPJIUPWVGEMG-0e24")

  val headers_5 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "2$595308963_803h12vGPUVHCQAOWPMASHWHNFGLKUMKEKFNFBO-0e7")

  val headers_CSPost = Map(
    "Accept" -> "application/json",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin",
		"sec-ch-ua" -> """Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87""",
		"sec-ch-ua-mobile" -> "?0")

  val headers_CSGet = Map(
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin",
		"sec-ch-ua" -> """Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87""",
		"sec-ch-ua-mobile" -> "?0")

}