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

  val caseDocUpload1mb = 

    exec(http("CaseDocApi_Upload1mb")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation") //GrantOfRepresentation
      .formParam("jurisdictionId", "PROBATE") //PROBATE
      .bodyPart(RawFileBodyPart("files", "1MB.pdf")
        .fileName("1MB.pdf")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID1mb"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken1")))

  val caseDocUpload5mb =

    exec(http("CaseDocApi_Upload5mb")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation")
      .formParam("jurisdictionId", "PROBATE")
      .bodyPart(RawFileBodyPart("files", "5MB.pdf")
        .fileName("5MB.pdf")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID5mb"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken2")))

  val caseDocUpload10mb =

    exec(http("CaseDocApi_Upload10mb")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation")
      .formParam("jurisdictionId", "PROBATE")
      .bodyPart(RawFileBodyPart("files", "10MB.pdf")
        .fileName("10MB.pdf")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID10mb"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken2")))

  val caseDocUpload20mb =

    exec(http("CaseDocApi_Upload20mb")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation")
      .formParam("jurisdictionId", "PROBATE")
      .bodyPart(RawFileBodyPart("files", "20MB.pdf")
        .fileName("20MB.pdf")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID20mb"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken2")))

  val caseDocUpload50mb =

    exec(http("CaseDocApi_Upload50mb")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation")
      .formParam("jurisdictionId", "PROBATE")
      .bodyPart(RawFileBodyPart("files", "50MB.pdf")
        .fileName("50MB.pdf")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID50mb"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken2")))

  val caseDocUpload100mb =

    exec(http("CaseDocApi_Upload100mb")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation")
      .formParam("jurisdictionId", "PROBATE")
      .bodyPart(RawFileBodyPart("files", "100MB.pdf")
        .fileName("100MB.pdf")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID100mb"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken2")))

  val caseDocUpload250mb =

    exec(http("CaseDocApi_Upload250mb")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation")
      .formParam("jurisdictionId", "PROBATE")
      .bodyPart(RawFileBodyPart("files", "250MB.pdf")
        .fileName("250MB.pdf")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID250mb"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken2")))

  val caseDocUpload500mb =

    exec(http("CaseDocApi_Upload500mb")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation")
      .formParam("jurisdictionId", "PROBATE")
      .bodyPart(RawFileBodyPart("files", "500MB.pdf")
        .fileName("500MB.pdf")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID500mb"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken2")))

  val caseDocUpload1000mb =

    exec(http("CaseDocApi_Upload1000mb")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation")
      .formParam("jurisdictionId", "PROBATE")
      .bodyPart(RawFileBodyPart("files", "1000MB.pdf")
        .fileName("1000MB.pdf")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID1000mb"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken2")))

  val addDocToCase = 

    exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/539560/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/#{caseId}/event-triggers/boUploadDocumentsStop/token")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Probate_DocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/539560/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/casedocapi/CaseDocSubmitEvent.json")))

    .pause(Environment.constantthinkTime.seconds)

  val caseDocDownload1mb =

    exec(http("CaseDocApi_Download1mb")
      .get(CaseDocAPI + "/cases/documents/#{Document_ID1mb}/binary")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json"))

  val caseDocDownload5mb =

    exec(http("CaseDocApi_Download5mb")
      .get(CaseDocAPI + "/cases/documents/#{Document_ID5mb}/binary")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json"))

  val caseDocDownload10mb =

    exec(http("CaseDocApi_Download10mb")
      .get(CaseDocAPI + "/cases/documents/#{Document_ID10mb}/binary")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json"))

  val caseDocDownload20mb =

    exec(http("CaseDocApi_Download20mb")
      .get(CaseDocAPI + "/cases/documents/#{Document_ID20mb}/binary")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json"))

  val caseDocDownload50mb =

    exec(http("CaseDocApi_Download50mb")
      .get(CaseDocAPI + "/cases/documents/#{Document_ID50mb}/binary")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json"))

  val caseDocDownload100mb =

    exec(http("CaseDocApi_Download100mb")
      .get(CaseDocAPI + "/cases/documents/#{Document_ID100mb}/binary")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json"))

  val caseDocDownload250mb =

    exec(http("CaseDocApi_Download250mb")
      .get(CaseDocAPI + "/cases/documents/#{Document_ID250mb}/binary")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json"))

  val caseDocDownload500mb =

    exec(http("CaseDocApi_Download500mb")
      .get(CaseDocAPI + "/cases/documents/#{Document_ID500mb}/binary")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json"))

  val caseDocDownload1000mb =

    exec(http("CaseDocApi_Download1000mb")
      .get(CaseDocAPI + "/cases/documents/#{Document_ID1000mb}/binary")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{bearerToken}")
      .header("accept", "application/json"))






}