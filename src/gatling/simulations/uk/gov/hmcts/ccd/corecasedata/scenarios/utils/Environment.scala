package scenarios.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

  val idamAPI = "https://idam-api.#{env}.platform.hmcts.net"
  val ccdEnvurl = "https://www-ccd.#{env}.platform.hmcts.net"
  val ccdDataStoreUrl = "http://ccd-data-store-api-#{env}.service.core-compute-#{env}.internal"
  val baseURL = "https://gateway-ccd.#{env}.platform.hmcts.net"
  val s2sUrl = "http://rpe-service-auth-provider-#{env}.service.core-compute-#{env}.internal"
  val dmStore = "http://dm-store-#{env}.service.core-compute-#{env}.internal"
  val caseDocUrl = "http://ccd-case-document-am-api-#{env}.service.core-compute-#{env}.internal"
  val userProfileUrl = "http://ccd-user-profile-api-#{env}.service.core-compute-#{env}.internal"
  val aacUrl = "http://aac-manage-case-assignment-#{env}.service.core-compute-#{env}.internal"
  val aacHost = "aac-manage-case-assignment-#{env}.service.core-compute-#{env}.internal"

  val constantthinkTime = 7 //7

  val minWaitForNextIteration = 60 //60
  val maxWaitForNextIteration = 80 //80

  val HttpProtocol = http

}
