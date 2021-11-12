package uk.gov.hmcts.ccd.corecasedata.scenarios

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import java.io.{BufferedWriter, FileWriter}
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._

object casedocapi {

  val config: Config = ConfigFactory.load()

  val s2sUrl = Environment.s2sUrl
  val IdamAPI = Environment.idamAPI
  val CaseDocAPI = Environment.caseDocUrl
  val ccdDataStoreUrl = "http://ccd-data-store-api-perftest.service.core-compute-perftest.internal"
  val ccdClientId = "ccd_gateway"
  val ccdRedirectUri = "https://ccd-data-store-api-perftest.service.core-compute-perftest.internal/oauth2redirect"
  val ccdGatewayClientSecret = config.getString("ccdGatewayCS")
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
      .body(StringBody("{\"microservice\":\"sscs\"}")) //probate_backend
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

  val idamLogin =

    exec(http("GetIdamToken")
      .post(IdamAPI + "/o/token?client_id=" + ccdClientId + "&client_secret=" + ccdGatewayClientSecret + "&grant_type=password&scope=" + ccdScope + "&username=ccdloadtest751@gmail.com&password=Password12")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status.is(200))
      .check(jsonPath("$.access_token").saveAs("accessToken")))

  val caseDocUpload = 

    exec(_.setAll(  
      "FileName1"  -> ("1MB.pdf")
    ))

    .exec(http("CaseDocApi_Upload1mb")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "${bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "Benefit") //GrantOfRepresentation
      .formParam("jurisdictionId", "SSCS") //PROBATE
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
        .fileName("${FileName1}")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID1"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken1")))

    // .exec {
    //   session =>
    //     val fw = new BufferedWriter(new FileWriter("1mbFileIds.csv", true))
    //     try {
    //       fw.write(session("caseId").as[String] + "," + session("Document_ID1").as[String] + "\r\n")
    //     }
    //     finally fw.close()
    //     session
    // }

    .pause(Environment.constantthinkTime)

    .exec(_.setAll(  
      "FileName1"  -> ("2MB.pdf")
    ))

    .exec(http("CaseDocApi_Upload2mb")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "${bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation")
      .formParam("jurisdictionId", "PROBATE")
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
        .fileName("1MB.pdf")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID2"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken2")))

    // .exec {
    //   session =>
    //     val fw = new BufferedWriter(new FileWriter("2mbFileIds.csv", true))
    //     try {
    //       fw.write(session("caseId").as[String] + "," + session("Document_ID2").as[String] + "\r\n")
    //     }
    //     finally fw.close()
    //     session
    // }

    .pause(Environment.constantthinkTime)

    .exec(_.setAll(  
      "FileName1"  -> ("3MB.pdf")
    ))

    .exec(http("CaseDocApi_Upload3mb")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "${bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation")
      .formParam("jurisdictionId", "PROBATE")
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
        .fileName("1MB.pdf")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID3"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken3")))

    // .exec {
    //   session =>
    //     val fw = new BufferedWriter(new FileWriter("3mbFileIds.csv", true))
    //     try {
    //       fw.write(session("caseId").as[String] + "," + session("Document_ID3").as[String] + "\r\n")
    //     }
    //     finally fw.close()
    //     session
    // }

    .pause(Environment.constantthinkTime)

    .exec(_.setAll(  
      "FileName1"  -> ("5MB.pdf")
    ))

    .exec(http("CaseDocApi_Upload5mb")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "${bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation")
      .formParam("jurisdictionId", "PROBATE")
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
        .fileName("1MB.pdf")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID4"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken4")))

    // .exec {
    //   session =>
    //     val fw = new BufferedWriter(new FileWriter("5mbFileIds.csv", true))
    //     try {
    //       fw.write(session("caseId").as[String] + "," + session("Document_ID4").as[String] + "\r\n")
    //     }
    //     finally fw.close()
    //     session
    // }

    .pause(Environment.constantthinkTime)
    
    .exec(_.setAll(  
      "FileName1"  -> ("10MB.pdf")
    ))

    .exec(http("CaseDocApi_Upload10mb")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "${bearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation")
      .formParam("jurisdictionId", "PROBATE")
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
        .fileName("1MB.pdf")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID5"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken5")))

    // .exec {
    //   session =>
    //     val fw = new BufferedWriter(new FileWriter("10mbFileIds.csv", true))
    //     try {
    //       fw.write(session("caseId").as[String] + "," + session("Document_ID5").as[String] + "\r\n")
    //     }
    //     finally fw.close()
    //     session
    // }

    .pause(Environment.constantthinkTime)

  val addDocToCase = 

    exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/539560/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${caseId}/event-triggers/boUploadDocumentsStop/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${accessToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Probate_DocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/539560/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${accessToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/casedocapi/CaseDocSubmitEvent.json")))

    .pause(Environment.constantthinkTime)

  val caseDocDownload =

    feed(feed1mb)
    .feed(feed2mb)
    .feed(feed3mb)
    .feed(feed5mb)
    .feed(feed10mb)

    .repeat(2) {

      exec(http("CaseDocApi_Download1mb")
        .get(CaseDocAPI + "/cases/documents/${1mbId}/binary")
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "${bearerToken}")
        .header("accept", "application/json"))

      .pause(Environment.constantthinkTime)

      .exec(http("CaseDocApi_Download2mb")
        .get(CaseDocAPI + "/cases/documents/${2mbId}/binary")
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "${bearerToken}")
        .header("accept", "application/json"))

      .pause(Environment.constantthinkTime)

      .exec(http("CaseDocApi_Download3mb")
        .get(CaseDocAPI + "/cases/documents/${3mbId}/binary")
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "${bearerToken}")
        .header("accept", "application/json"))

      .pause(Environment.constantthinkTime)

      .exec(http("CaseDocApi_Download5mb")
        .get(CaseDocAPI + "/cases/documents/${5mbId}/binary")
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "${bearerToken}")
        .header("accept", "application/json"))

      .pause(Environment.constantthinkTime)

      .exec(http("CaseDocApi_Download10mb")
        .get(CaseDocAPI + "/cases/documents/${10mbId}/binary")
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "${bearerToken}")
        .header("accept", "application/json"))

      .pause(Environment.constantthinkTime)
    }

}