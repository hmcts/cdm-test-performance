package scenarios.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utilities.AzureKeyVault

object IdamLogin {

  val IdamAPI = Environment.idamAPI
  val ccdScope = "openid profile authorities acr roles openid profile roles"
  val clientSecret = AzureKeyVault.loadClientSecret("ccd-perftest", "ccd-api-gateway-oauth2-client-secret", "CCD_CLIENT_SECRET")

  val GetIdamToken =

    exec(http("GetIdamToken")
      .post(IdamAPI + "/o/token?client_id=ccd_gateway&client_secret=" + clientSecret + "&grant_type=password&scope=" + ccdScope + "&username=#{Username}&password=#{Password}")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status.is(200))
      .check(jsonPath("$.access_token").saveAs("access_token")))
}