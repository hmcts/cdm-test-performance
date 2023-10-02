package scenarios.api

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios.utils._
import scala.concurrent.duration._

object dmstore {

  val config: Config = ConfigFactory.load()

  val s2sUrl = Environment.s2sUrl
  val dmStoreUrl = "http://dm-store-#{env}.service.core-compute-#{env}.internal"
  val feedProbateUserData = csv("ProbateUserData.csv").circular
  val constantThinkTime = Environment.constantthinkTime

  val S2SLogin = 

    feed(feedProbateUserData)

    .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

  val API_DocUpload1mb = 
  
    exec(http("API_DocUploadProcess1mb")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .bodyPart(RawFileBodyPart("files", "1MB.pdf")
        .fileName("1MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID1")))

  val API_DocUpload5mb = 

    exec(http("API_DocUploadProcess5mb")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .bodyPart(RawFileBodyPart("files", "5MB.pdf")
        .fileName("5MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID2")))

  val API_DocUpload10mb =

    exec(http("API_DocUploadProcess10mb")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .bodyPart(RawFileBodyPart("files", "10MB.pdf")
        .fileName("10MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID3")))

  val API_DocUpload20mb =

    exec(http("API_DocUploadProcess20mb")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .bodyPart(RawFileBodyPart("files", "20MB.pdf")
        .fileName("20MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID4")))

  val API_DocUpload50mb =

    exec(http("API_DocUploadProcess50mb")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .bodyPart(RawFileBodyPart("files", "50MB.pdf")
        .fileName("50MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID5")))

  val API_DocUpload100mb =

    exec(http("API_DocUploadProcess100mb")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .bodyPart(RawFileBodyPart("files", "100MB.pdf")
        .fileName("100MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID6")))

  val API_DocUpload250mb =

    exec(http("API_DocUploadProcess250mb")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .bodyPart(RawFileBodyPart("files", "250MB.pdf")
        .fileName("250MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID7")))

  val API_DocUpload500mb =

    exec(http("API_DocUploadProcess500mb")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .bodyPart(RawFileBodyPart("files", "500MB.pdf")
        .fileName("500MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID8")))

  val API_DocUpload1000mb =

    exec(http("API_DocUploadProcess1000mb")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .bodyPart(RawFileBodyPart("files", "1000MB.pdf")
        .fileName("1000MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID9")))

  val API_DocDownload1mb = 

    exec(http("API_DocDownloadProcess1mb")
      .get(dmStoreUrl + "/documents/#{Document_ID1}/binary")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("accept","*/*")
      .header("user-id", "#{Username}")
      .header("user-roles", "caseworker"))

  val API_DocDownload5mb =

    exec(http("API_DocDownloadProcess5mb")
      .get(dmStoreUrl + "/documents/#{Document_ID2}/binary")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("accept","*/*")
      .header("user-id", "#{Username}")
      .header("user-roles", "caseworker"))

  val API_DocDownload10mb =

    exec(http("API_DocDownloadProcess10mb")
      .get(dmStoreUrl + "/documents/#{Document_ID3}/binary")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("accept","*/*")
      .header("user-id", "#{Username}")
      .header("user-roles", "caseworker"))

  val API_DocDownload20mb =

    exec(http("API_DocDownloadProcess20mb")
      .get(dmStoreUrl + "/documents/#{Document_ID4}/binary")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("accept","*/*")
      .header("user-id", "#{Username}")
      .header("user-roles", "caseworker"))

  val API_DocDownload50mb =

    exec(http("API_DocDownloadProcess50mb")
      .get(dmStoreUrl + "/documents/#{Document_ID5}/binary")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("accept","*/*")
      .header("user-id", "#{Username}")
      .header("user-roles", "caseworker"))

  val API_DocDownload100mb =

    exec(http("API_DocDownloadProcess100mb")
      .get(dmStoreUrl + "/documents/#{Document_ID6}/binary")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("accept","*/*")
      .header("user-id", "#{Username}")
      .header("user-roles", "caseworker"))

  val API_DocDownload250mb =

    exec(http("API_DocDownloadProcess250mb")
      .get(dmStoreUrl + "/documents/#{Document_ID7}/binary")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("accept","*/*")
      .header("user-id", "#{Username}")
      .header("user-roles", "caseworker"))
  
  val API_DocDownload500mb =

    exec(http("API_DocDownloadProcess500mb")
      .get(dmStoreUrl + "/documents/#{Document_ID8}/binary")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("accept","*/*")
      .header("user-id", "#{Username}")
      .header("user-roles", "caseworker"))

  val API_DocDownload1000mb =

    exec(http("API_DocDownloadProcess1000mb")
      .get(dmStoreUrl + "/documents/#{Document_ID9}/binary")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("accept","*/*")
      .header("user-id", "#{Username}")
      .header("user-roles", "caseworker"))
}