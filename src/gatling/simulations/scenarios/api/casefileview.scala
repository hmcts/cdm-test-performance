package scenarios.api

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios.utils._

import scala.concurrent.duration._

object casefileview {

  val config: Config = ConfigFactory.load()

  val ccdGatewayClientSecret = config.getString("auth.ccdGatewayCS")
  val ccdScope = "openid profile authorities acr roles openid profile roles"

  val S2SLogin = 

    exec(http("GetS2SToken")
      .post(Environment.s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"xui_webapp\"}"))
      .check(bodyString.saveAs("BearerToken")))
      .exitHereIfFailed

      .pause(Environment.constantthinkTime.seconds)

  val idamLogin =

    exec(http("GetIdamToken")
      .post(Environment.idamAPI + "/o/token?client_id=ccd_gateway&client_secret=" + ccdGatewayClientSecret + "&grant_type=password&scope=" + ccdScope + "&username=BeftaCW001@gmail.com&password=Password12")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status.is(200))
      .check(jsonPath("$.access_token").saveAs("accessToken")))
      .exitHereIfFailed

    .pause(Environment.constantthinkTime.seconds)

  val caseDocUpload = 

    exec(_.setAll(  
      "FileName1"  -> ("1MB.pdf")
    ))

    .exec(http("CaseDocApi_Upload1mb")
      .post(Environment.caseDocUrl + "/cases/documents")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{BearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "FT_CaseFileView_1")
      .formParam("jurisdictionId", "BEFTA_MASTER")
      .bodyPart(RawFileBodyPart("files", "#{FileName1}")
        .fileName("#{FileName1}")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken1")))

    .pause(Environment.constantthinkTime.seconds)

  val createCase =

    exec(http("API_CFV_GetEventToken")
      .get(Environment.ccdDataStoreUrl + "/caseworkers/5ab7f2c2-c288-4afb-9607-10da5a4980d8/jurisdictions/BEFTA_MASTER/case-types/FT_CaseFileView_1/event-triggers/CREATE/token")
      .header("ServiceAuthorization", "Bearer #{BearerToken}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_CFV_CreateCase")
      .post(Environment.ccdDataStoreUrl + "/caseworkers/5ab7f2c2-c288-4afb-9607-10da5a4980d8/jurisdictions/BEFTA_MASTER/case-types/FT_CaseFileView_1/cases")
      .header("ServiceAuthorization", "Bearer #{BearerToken}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/casefileview/CreateRequest1.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

  val createCase5Doc =

    exec(http("API_CFV_GetEventToken")
      .get(Environment.ccdDataStoreUrl + "/caseworkers/5ab7f2c2-c288-4afb-9607-10da5a4980d8/jurisdictions/BEFTA_MASTER/case-types/FT_CaseFileView_1/event-triggers/CREATE/token")
      .header("ServiceAuthorization", "Bearer #{BearerToken}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_CFV_CreateCase")
      .post(Environment.ccdDataStoreUrl + "/caseworkers/5ab7f2c2-c288-4afb-9607-10da5a4980d8/jurisdictions/BEFTA_MASTER/case-types/FT_CaseFileView_1/cases")
      .header("ServiceAuthorization", "Bearer #{BearerToken}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/casefileview/CreateRequest5Docs.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

  val caseFileViewPutCategories = 

    exec(http("API_CFV_PutDocumentData")
      .put(Environment.ccdDataStoreUrl + "/documentData/caseref/#{caseId}")
      .header("ServiceAuthorization", "Bearer #{BearerToken}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/casefileview/PutDocumentCategory1.json")))

    .pause(Environment.constantthinkTime.seconds)

  val caseFileViewPut2Categories = 

    exec(http("API_CFV_PutDocumentData")
      .put(Environment.ccdDataStoreUrl + "/documentData/caseref/#{caseId}")
      .header("ServiceAuthorization", "Bearer #{BearerToken}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/casefileview/Put2DocCategories.json")))

    .pause(Environment.constantthinkTime.seconds)

  val caseFileViewGet = {

    repeat(150) {

      exec(http("API_CFV_GetCategoriesAndDocuments")
        .get(Environment.ccdDataStoreUrl + "/categoriesAndDocuments/#{caseId}")
        .header("ServiceAuthorization", "Bearer #{BearerToken}")
        .header("Authorization", "Bearer #{accessToken}"))

      .pause(Environment.constantthinkTime.seconds)
    }
  }

}