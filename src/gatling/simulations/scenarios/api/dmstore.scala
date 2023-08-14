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

  val API_DocUpload = 

    exec(_.setAll(
      "FileName1" -> "1MB.pdf",
      "FileName2" -> "2MB.pdf",
      "FileName3" -> "3MB.pdf",
      "FileName4" -> "5MB.pdf",
      "FileName5" -> "10MB.pdf"))

    .exec(http("API_DocUploadProcess1mb")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .bodyPart(RawFileBodyPart("files", "#{FileName1}")
        .fileName("#{FileName1}")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID1")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_DocUploadProcess2mb")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .bodyPart(RawFileBodyPart("files", "#{FileName2}")
        .fileName("#{FileName1}")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID2")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_DocUploadProcess3mb")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .bodyPart(RawFileBodyPart("files", "#{FileName3}")
        .fileName("#{FileName1}")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID3")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_DocUploadProcess5mb")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .bodyPart(RawFileBodyPart("files", "#{FileName4}")
        .fileName("#{FileName1}")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID4")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_DocUploadProcess10mb")
      .post(dmStoreUrl + "/documents")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .bodyPart(RawFileBodyPart("files", "#{FileName5}")
        .fileName("#{FileName1}")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID5")))

    .pause(Environment.constantthinkTime.seconds)

  val API_DocDownload = 

    repeat(2) {

      exec(http("API_DocDownloadProcess1mb")
        .get(dmStoreUrl + "/documents/#{Document_ID1}/binary")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("accept","*/*")
        .header("user-id", "#{ProbateUserName}")
        .header("user-roles", "caseworker"))

      .pause(Environment.constantthinkTime.seconds)

      .exec(http("API_DocDownloadProcess2mb")
        .get(dmStoreUrl + "/documents/#{Document_ID2}/binary")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("accept","*/*")
        .header("user-id", "#{ProbateUserName}")
        .header("user-roles", "caseworker"))

      .pause(Environment.constantthinkTime.seconds)

      .exec(http("API_DocDownloadProcess3mb")
        .get(dmStoreUrl + "/documents/#{Document_ID3}/binary")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("accept","*/*")
        .header("user-id", "#{ProbateUserName}")
        .header("user-roles", "caseworker"))

      .pause(Environment.constantthinkTime.seconds)

      .exec(http("API_DocDownloadProcess5mb")
        .get(dmStoreUrl + "/documents/#{Document_ID4}/binary")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("accept","*/*")
        .header("user-id", "#{ProbateUserName}")
        .header("user-roles", "caseworker"))

      .pause(Environment.constantthinkTime.seconds)

      .exec(http("API_DocDownloadProcess10mb")
        .get(dmStoreUrl + "/documents/#{Document_ID5}/binary")
        .header("ServiceAuthorization", "Bearer #{bearerToken}")
        .header("accept","*/*")
        .header("user-id", "#{ProbateUserName}")
        .header("user-roles", "caseworker"))

      .pause(Environment.constantthinkTime.seconds)
    }
}