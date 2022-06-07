package uk.gov.hmcts.ccd.corecasedata.scenarios.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

  val idamURL = "https://idam-web-public.${env}.platform.hmcts.net"
  val idamAPI = "https://idam-api.${env}.platform.hmcts.net"
  val ccdEnvurl = "https://www-ccd.${env}.platform.hmcts.net"
  val ccdDataStoreUrl = "ccd-data-store-api-${env}.service.core-compute-${env}.internal"
  val baseURL = "https://gateway-ccd.${env}.platform.hmcts.net"
  val s2sUrl = "http://rpe-service-auth-provider-${env}.service.core-compute-${env}.internal"
  val xuiMCUrl = "https://manage-case.${env}.platform.hmcts.net/oauth2/callback"
  val xuiBaseURL = "https://manage-case.${env}.platform.hmcts.net"
  val dmStore = "http://dm-store-${env}.service.core-compute-${env}.internal"
  val caseDocUrl = "http://ccd-case-document-am-api-${env}.service.core-compute-${env}.internal"
  val userProfileUrl = "http://ccd-user-profile-api-${env}.service.core-compute-${env}.internal"

  val minThinkTime = 10 //10
  val maxThinkTime = 30 //30

  val constantthinkTime = 7 //7

  val xuiCaseActivityPause = 10 //5
  val xuiCaseActivityListPause = 30 //30
  val ccdCaseActivityPause = 5
  val caseActivityPause = 5

  val minWaitForNextIteration = 60 //60
  val maxWaitForNextIteration = 80 //80
  val HttpProtocol = http

  val commonHeader = Map(
    "Accept" -> "application/json",
    "Content-Type" -> "application/json",
    "Origin" -> ccdEnvurl)

  val docCommonHeader = Map(
    "Content-Type" -> "application/pdf",
    "Origin" -> ccdEnvurl)

  val idam_header = Map(
    "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Origin" -> idamURL,
    "Upgrade-Insecure-Requests" -> "1",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "sec-fetch-user" -> "?1",
    "cache-control" -> "max-age=0")
}
