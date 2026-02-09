package scenarios.api

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios.utils._

import scala.concurrent.duration._

object elasticsearch {

  val config: Config = ConfigFactory.load()
  val IdamAPI = Environment.idamAPI
  val s2sUrl = Environment.s2sUrl
  val ccdDataStoreUrl = "http://ccd-data-store-api-#{env}.service.core-compute-#{env}.internal"
  val ccdClientId = "ccd_gateway"
  val ccdGatewayClientSecret = config.getString("auth.ccdGatewayCS")

  val ccdScope = "openid profile authorities acr roles openid profile roles"
  val feedCaseSearchData = csv("caseSearchData.csv").random
  val feedWorkbasketData = csv("workbasketCaseTypes.csv").random //circular
  val feedXUIUserData = csv("XUISearchUsers.csv").circular
  val feedProbateUserData = csv("ProbateUserData.csv").circular
  val feedSSCSUserData = csv("SSCSUserData.csv").circular
  val feedDivorceUserData = csv("DivorceUserData.csv").circular

  val CDSGetRequest =

    feed(feedXUIUserData)

    .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

    .exec(http("GetIdamToken")
      .post(IdamAPI + "/o/token?client_id=ccd_gateway&client_secret=" + ccdGatewayClientSecret + "&grant_type=password&scope=" + ccdScope + "&username=#{email}&password=Password12")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status.is(200))
      .check(jsonPath("$.access_token").saveAs("access_token")))
      .exitHereIfFailed

  val ElasticSearchGetVaryingSizes =

    feed(feedWorkbasketData)

    .exec(http("CCD_SearchCaseEndpoint_ElasticSearchGet25Cases_#{caseType}")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "#{caseType}")
      .body(StringBody("{\n    \"query\": {\n        \"match_all\": {}\n    },\n    \"size\": 25\n}")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("CCD_SearchCaseEndpoint_ElasticSearchGet50Cases_#{caseType}")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "#{caseType}")
      .body(StringBody("{\n    \"query\": {\n        \"match_all\": {}\n    },\n    \"size\": 50\n}")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("CCD_SearchCaseEndpoint_ElasticSearchGet75Cases_#{caseType}")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "#{caseType}")
      .body(StringBody("{\n    \"query\": {\n        \"match_all\": {}\n    },\n    \"size\": 75\n}")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("CCD_SearchCaseEndpoint_ElasticSearchGet100Cases_#{caseType}")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "#{caseType}")
      .body(StringBody("{\n    \"query\": {\n        \"match_all\": {}\n    },\n    \"size\": 100\n}")))

    .pause(Environment.constantthinkTime.seconds)

  val ElasticSearchWorkbasket =

    feed(feedWorkbasketData)

    .exec(http("CCD_SearchCaseEndpoint_ElasticSearchWorkbasket25_#{caseType}")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "#{caseType}")
      .queryParam("use_case", "WORKBASKET")
      .queryParam("view", "WORKBASKET")
      .queryParam("page", "1")
      .body(StringBody("{\n    \"query\": {\n        \"match_all\": {}\n    },\n    \"size\": 25\n}")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("CCD_SearchCaseEndpoint_ElasticSearchWorkbasket50_#{caseType}")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "#{caseType}")
      .queryParam("use_case", "WORKBASKET")
      .queryParam("view", "WORKBASKET")
      .queryParam("page", "1")
      .body(StringBody("{\n    \"query\": {\n        \"match_all\": {}\n    },\n    \"size\": 50\n}")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("CCD_SearchCaseEndpoint_ElasticSearchWorkbasket75_#{caseType}")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "#{caseType}")
      .queryParam("use_case", "WORKBASKET")
      .queryParam("view", "WORKBASKET")
      .queryParam("page", "1")
      .body(StringBody("{\n    \"query\": {\n        \"match_all\": {}\n    },\n    \"size\": 75\n}")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("CCD_SearchCaseEndpoint_ElasticSearchWorkbasket100_#{caseType}")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "#{caseType}")
      .queryParam("use_case", "WORKBASKET")
      .queryParam("view", "WORKBASKET")
      .queryParam("page", "1")
      .body(StringBody("{\n    \"query\": {\n        \"match_all\": {}\n    },\n    \"size\": 100\n}")))

    .pause(Environment.constantthinkTime.seconds)

}