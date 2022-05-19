package uk.gov.hmcts.ccd.corecasedata.scenarios

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._

object ccdcaseactivity {

val config: Config = ConfigFactory.load()

val IdamURL = Environment.idamURL
val IdamAPI = Environment.idamAPI
val CCDEnvurl = Environment.ccdEnvurl
val s2sUrl = Environment.s2sUrl
val ccdRedirectUri = "https://ccd-data-store-api-perftest.service.core-compute-perftest.internal/oauth2redirect"
val ccdDataStoreUrl = "http://ccd-data-store-api-perftest.service.core-compute-perftest.internal"
val escaseDataUrl = "https://ccd-api-gateway-web-perftest.service.core-compute-perftest.internal"
val ccdCaseActivityUrl = "http://ccd-case-activity-api-perftest.service.core-compute-perftest.internal"
val ccdClientId = "ccd_gateway"
val ccdGatewayClientSecret = config.getString("auth.ccdGatewayCS")
val ccdScope = "openid profile authorities acr roles openid profile roles"
val caseActivityFeeder = csv("CaseActivityData.csv").random
val caseActivityListFeeder = csv("CaseActivityListData.csv").random
val feedXUIUserData = csv("XUISearchUsers.csv").circular

val CDSGetRequest =

  feed(feedXUIUserData)

  .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

  .exec(http("GetIdamToken")
      .post(IdamAPI + "/o/token?client_id=ccd_gateway&client_secret=" + ccdGatewayClientSecret + "&grant_type=password&scope=" + ccdScope + "&username=${email}&password=Password12")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status.is(200))
      .check(jsonPath("$.access_token").saveAs("access_token")))
      .exitHereIfFailed

val CaseActivityRequest = 

  feed(caseActivityFeeder)

  .exec(http("CaseActivity_GET")
    .get(ccdCaseActivityUrl + "/cases/${caseRef}/activity")
    .header("Content-Type", "application/json")
    .header("ServiceAuthorization", "Bearer ${bearerToken}")
    .header("Authorization", "Bearer ${access_token}"))

  .exec(http("CaseActivity_OPTIONS")
    .options(ccdCaseActivityUrl + "/cases/${caseRef}/activity")
    .header("Content-Type", "application/json")
    .header("ServiceAuthorization", "Bearer ${bearerToken}")
    .header("Authorization", "Bearer ${access_token}"))

  .pause(Environment.constantthinkTime)

  .exec(http("CaseActivity_POST")
    .post(ccdCaseActivityUrl + "/cases/${caseRef}/activity")
    .header("Content-Type", "application/json")
    .header("ServiceAuthorization", "Bearer ${bearerToken}")
    .header("Authorization", "Bearer ${access_token}")
    .body(StringBody("{\n  \"activity\": \"view\"\n}")))

  .exec(http("CaseActivity_OPTIONS")
    .options(ccdCaseActivityUrl + "/cases/${caseRef}/activity")
    .header("Content-Type", "application/json")
    .header("ServiceAuthorization", "Bearer ${bearerToken}")
    .header("Authorization", "Bearer ${access_token}"))

  .pause(Environment.constantthinkTime)

val CaseActivityList = 

  feed(caseActivityListFeeder)

  .exec(http("CaseActivityList_GET")
    .get(ccdCaseActivityUrl + "/cases/${caseList}/activity")
    .header("Content-Type", "application/json")
    .header("ServiceAuthorization", "Bearer ${bearerToken}")
    .header("Authorization", "Bearer ${access_token}"))

  .exec(http("CaseActivityList_OPTIONS")
    .options(ccdCaseActivityUrl + "/cases/${caseList}/activity")
    .header("Content-Type", "application/json")
    .header("ServiceAuthorization", "Bearer ${bearerToken}")
    .header("Authorization", "Bearer ${access_token}"))

  .pause(Environment.constantthinkTime)

}