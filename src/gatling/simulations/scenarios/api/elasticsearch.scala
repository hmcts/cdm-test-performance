package scenarios.api

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios.utils._
import java.io.{BufferedWriter, FileWriter}
import scala.concurrent.duration._

object elasticsearch {

  val config: Config = ConfigFactory.load()
  val IdamAPI = Environment.idamAPI
  val CCDEnvurl = Environment.ccdEnvurl
  val s2sUrl = Environment.s2sUrl
  val ccdDataStoreUrl = "http://ccd-data-store-api-#{env}.service.core-compute-#{env}.internal"
  val ccdClientId = "ccd_gateway"
  val ccdGatewayClientSecret = config.getString("auth.ccdGatewayCS")

  val ccdScope = "openid profile authorities acr roles openid profile roles"
  val feedCaseSearchData = csv("caseSearchData.csv").random
  val feedWorkbasketData = csv("workbasketCaseTypes.csv").circular
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
        .body(StringBody("{\n\t\"query\": {\n\t\t\"match_all\": {}\n\t\t},\n\t\t\"size\": 25,\n\t\t\"sort\":[ \n      { \n         \"last_modified\":\"desc\"\n      },\n      \"_score\"\n   ]\n}")))

      .pause(Environment.constantthinkTime.seconds)

      .exec(http("CCD_SearchCaseEndpoint_ElasticSearchGet50Cases_#{caseType}")
        .post(ccdDataStoreUrl + "/searchCases")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("Authorization", "Bearer #{access_token}")
        .header("Content-Type","application/json")
        .queryParam("ctid", "#{caseType}")
        .body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":50,\"sort\":[{\"created_date\":\"DESC\"}]}")))

      .pause(Environment.constantthinkTime.seconds)

      .exec(http("CCD_SearchCaseEndpoint_ElasticSearchGet75Cases_#{caseType}")
        .post(ccdDataStoreUrl + "/searchCases")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("Authorization", "Bearer #{access_token}")
        .header("Content-Type","application/json")
        .queryParam("ctid", "#{caseType}")
        .body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":75,\"sort\":[{\"created_date\":\"DESC\"}]}")))

      .pause(Environment.constantthinkTime.seconds)

      .exec(http("CCD_SearchCaseEndpoint_ElasticSearchGet100Cases_#{caseType}")
        .post(ccdDataStoreUrl + "/searchCases")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("Authorization", "Bearer #{access_token}")
        .header("Content-Type","application/json")
        .queryParam("ctid", "#{caseType}")
        .body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":100,\"sort\":[{\"created_date\":\"DESC\"}]}")))

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
        .body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":25,\"sort\":[{\"created_date\":\"DESC\"}]}")))

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
        .body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":50,\"sort\":[{\"created_date\":\"DESC\"}]}")))

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
        .body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":75,\"sort\":[{\"created_date\":\"DESC\"}]}")))

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
        .body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":100,\"sort\":[{\"created_date\":\"DESC\"}]}")))

      .pause(Environment.constantthinkTime.seconds)

    val CitizenSearch =

      feed(feedWorkbasketData)

      .exec(http("CCD_SearchCaseEndpoint_CitizenSearch")
        .get(ccdDataStoreUrl + "/citizens/539560/jurisdictions/#{jurisdiction}/case-types/#{caseType}/cases") //1f65a0df-b064-4f9b-85ea-3eec5a28ce86
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("Authorization", "Bearer #{access_token}")
        .header("Content-Type","application/json")
        .check(status in (200)))

      .pause(Environment.constantthinkTime.seconds)

    val CaseworkerSearch =

      feed(feedWorkbasketData)

      .exec(http("CCD_CaseworkerSearch")
        .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{jurisdiction}/case-types/#{caseType}/cases")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("Authorization", "Bearer #{access_token}")
        .header("Content-Type","application/json")
        .check(status in (200)))

    //   .exec {
    //     session =>
    //     println(session("caseType").as[String])
    //     session
    // }

      .pause(Environment.constantthinkTime.seconds)

    val ElasticSearchWorkbasketGoR =

      exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
        .post(ccdDataStoreUrl + "/searchCases")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("Authorization", "Bearer #{access_token}")
        .header("Content-Type","application/json")
        .queryParam("ctid", "GrantOfRepresentation")
        .queryParam("use_case", "WORKBASKET")
        .queryParam("view", "WORKBASKET")
        .queryParam("page", "1")
        .queryParam("state", "IntCaseCreated")
        .body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":25,\"sort\":[{\"created_date\":\"DESC\"}]}"))
        .check(status in (200)))

      .pause(Environment.constantthinkTime.seconds)

    val ElasticSearchWorkbasketGo1000 =

      exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
        .post(ccdDataStoreUrl + "/internal/searchCases")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("Authorization", "Bearer #{access_token}")
        .header("Content-Type","application/json")
        .queryParam("ctid", "GrantOfRepresentation")
        .body(StringBody("{\"query\":{\"match_all\":{}},\"size\":10000}"))
        .check(status in (200)))

      .pause(Environment.constantthinkTime.seconds)

    val GatewaySearchWorkbasketGoR1000 =

      exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
        .post("https://gateway-ccd.#{env}.platform.hmcts.net/data/internal/searchCases")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("Authorization", "Bearer #{access_token}")
        .header("Content-Type","application/json")
        .queryParam("ctid", "GrantOfRepresentation")
        .body(StringBody("{\"query\":{\"match_all\":{}},\"size\":10000}"))
        .check(status in (200)))

      .pause(Environment.constantthinkTime.seconds)

    val ElasticSearchWorkbasketSSCS =

      exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
        .post(ccdDataStoreUrl + "/searchCases")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("Authorization", "Bearer #{access_token}")
        .header("Content-Type","application/json")
        .queryParam("ctid", "Benefit")
        .queryParam("use_case", "WORKBASKET")
        .queryParam("view", "WORKBASKET")
        .queryParam("page", "1")
        .queryParam("case.evidenceHandled", "No")
        .body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":25,\"sort\":[{\"created_date\":\"DESC\"}]}"))
        .check(status in (200)))

      .pause(Environment.constantthinkTime.seconds)

    val ElasticSearchGet25Divorce =

      exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
        .post(ccdDataStoreUrl + "/searchCases")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("Authorization", "Bearer #{access_token}")
        .header("Content-Type","application/json")
        .queryParam("ctid", "DIVORCE")
        .body(StringBody("{\n\t\"query\": {\n\t\t\"match_all\": {}\n\t\t},\n\t\t\"size\": 25,\n\t\t\"sort\":[ \n      { \n         \"last_modified\":\"desc\"\n      },\n      \"_score\"\n   ]\n}"))
        .check(status in  (200)))

      .pause(Environment.constantthinkTime.seconds)


    val ElasticSearchGetRef =

      feed(feedCaseSearchData)

      .exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
        .post(ccdDataStoreUrl + "/searchCases")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("Authorization", "Bearer #{access_token}")
        .header("Content-Type","application/json")
        .queryParam("ctid", "#{caseType}") //#{caseType}
        .body(StringBody("{ \n   \"query\":{ \n      \"bool\":{ \n         \"filter\":{ \n            \"wildcard\":{ \n               \"reference\":\"#{caseId}\"\n            }\n         }\n      }\n   }\n}"))
        .check(status in  (200)))

      .pause(Environment.constantthinkTime.seconds)

    val ElasticSearchGetByDate =

      exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
        .post(ccdDataStoreUrl + "/searchCases")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("Authorization", "Bearer #{access_token}")
        .header("Content-Type","application/json")
        .queryParam("ctid", "GrantOfRepresentation")
        .body(StringBody("{\n   \"query\":{\n      \"bool\":{\n         \"filter\":{\n            \"term\":{\n               \"created_date\":\"2020-07-20\"\n            }\n         }\n      }\n   }\n}"))
        .check(status in  (200)))

      .pause(Environment.constantthinkTime.seconds)

    val ElasticSearchEthos =

      exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
        .post(ccdDataStoreUrl + "/searchCases")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("Authorization", "Bearer #{access_token}")
        .header("Content-Type","application/json")
        .queryParam("ctid", "Scotland")
        .body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":25,\"sort\":[{\"created_date\":\"DESC\"}]}"))
        .check(status in  (200)))

      .pause(Environment.constantthinkTime.seconds)
}