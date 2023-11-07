package scenarios.api

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import java.io.{BufferedWriter, FileWriter}
import scala.concurrent.duration._
import scenarios.utils._

object casedocapi {

  val config: Config = ConfigFactory.load()

  val s2sUrl = Environment.s2sUrl
  val IdamAPI = Environment.idamAPI
  val CaseDocAPI = Environment.caseDocUrl
  val ccdDataStoreUrl = "http://ccd-data-store-api-#{env}.service.core-compute-#{env}.internal"
  val ccdClientId = "ccd_gateway"
  val ccdRedirectUri = "https://ccd-data-store-api-#{env}.service.core-compute-#{env}.internal/oauth2redirect"
  val ccdGatewayClientSecret = config.getString("auth.ccdGatewayCS")
  val constantThinkTime = Environment.constantthinkTime
  val ccdScope = "openid profile authorities acr roles openid profile roles"
  val feed1mb = csv("casedocdata/1mbIds.csv").random
  val feed2mb = csv("casedocdata/2mbIds.csv").random
  val feed3mb = csv("casedocdata/3mbIds.csv").random
  val feed5mb = csv("casedocdata/5mbIds.csv").random
  val feed10mb = csv("casedocdata/10mbIds.csv").random

  val S2SLogin = 

    exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"probate_backend\"}")) //probate_backend
      .check(bodyString.saveAs("bearerToken"))) //docUploadBearerToken
      .exitHereIfFailed

  val idamLogin =

    exec(http("GetIdamToken")
      .post(IdamAPI + "/o/token?client_id=" + ccdClientId + "&client_secret=" + ccdGatewayClientSecret + "&grant_type=password&scope=" + ccdScope + "&username=ccdloadtest751@gmail.com&password=Password12")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status.is(200))
      .check(jsonPath("$.access_token").saveAs("accessToken")))

  val CCDAPI_ProbateCreate = 

    exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/event-triggers/applyForGrant/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Probate_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"applyForGrant\",\n    \"summary\": \"test case\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"#{eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/#{caseId}/event-triggers/paymentSuccessApp/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_Probate_PaymentSuccessful")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_PaymentSuccess.json")))

    .pause(Environment.constantthinkTime.seconds)

  val caseDocUpload = 

    exec(http("CaseDocApi_Upload#{filename}")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation") 
      .formParam("jurisdictionId", "PROBATE") 
      .bodyPart(RawFileBodyPart("files", "#{filename}")
        .fileName("#{filename}")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken")))

    .pause(Environment.constantthinkTime.seconds) 

  val addDocToCase = 

    exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/#{caseId}/event-triggers/boUploadDocumentsAwaitingDoc/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Probate_DocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/casedocapi/CaseDocSubmitEvent.json")))

    .pause(Environment.constantthinkTime.seconds)

  val caseDocDownload =

    exec(http("CaseDocApi_Download#{filename}")
      .get(CaseDocAPI + "/cases/documents/#{Document_ID}/binary")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json"))

    .pause(Environment.constantthinkTime.seconds)
}