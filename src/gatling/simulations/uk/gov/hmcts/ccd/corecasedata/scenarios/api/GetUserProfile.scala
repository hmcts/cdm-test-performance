package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment
import scala.concurrent.duration._

object GetUserProfile {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val feedUserData = csv("UserProfileJurisdictions.csv").random

  val headers_0 = Map(
    "Content-Type" -> "application/json",
    "Authorization" -> "Bearer ",
    "ServiceAuthorization" -> "Bearer ")

  val SearchJurisdiction = feed(feedUserData)
    .exec(http("CUP_GetJurisdiction")
    .get("http://ccd-user-profile-api-perftest.service.core-compute-perftest.internal/users?jurisdiction=${UPJurisdiction}")
      .headers(headers_0))

    .exec {
      session =>
        println("Selected jurisdiction is ")
        println(session("UPJurisdiction").as[String])
        session}

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val SearchAllUsers = exec(http("CUP_GetAllUsers")
    .get("http://ccd-user-profile-api-perftest.service.core-compute-perftest.internal/users")
      .headers(headers_0))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

}