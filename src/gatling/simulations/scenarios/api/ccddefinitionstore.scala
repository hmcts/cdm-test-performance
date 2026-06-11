package scenarios.api

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios.utils._
import scala.concurrent.duration._

object ccddefinitionstore {

val CCD_DefinitionStoreJurisdictions = 

  exec(http("API_DefinitionStore_GetJurisdiction_#{jurisdiction}")
    .get(Environment.definitionStoreUrl + "/api/data/jurisdictions?ids=#{jurisdiction}")
    .header("ServiceAuthorization", "Bearer #{ccd_gwBearerToken}")
    .header("Authorization", "Bearer #{access_token}")
    .header("Content-Type","application/json"))

  .pause(Environment.constantthinkTime.seconds)

  .exec(http("API_DefinitionStore_GetJurisdictions")
    .get(Environment.definitionStoreUrl + "/api/data/jurisdictions?ids=PROBATE,CIVIL,IA,DIVORCE,SSCS")
    .header("ServiceAuthorization", "Bearer #{ccd_gwBearerToken}")
    .header("Authorization", "Bearer #{access_token}")
    .header("Content-Type","application/json"))

  .pause(Environment.constantthinkTime.seconds)

  .exec(http("API_DefinitionStore_GetJurisdictions")
    .get(Environment.definitionStoreUrl + "/api/data/jurisdictions?ids=EMPLOYMENT,PRIVATELAW,SSCS,PUBLICLAW,CMC")
    .header("ServiceAuthorization", "Bearer #{ccd_gwBearerToken}")
    .header("Authorization", "Bearer #{access_token}")
    .header("Content-Type","application/json"))

  .pause(Environment.constantthinkTime.seconds)

val CCD_DefinitionStoreGetUserRole = 

  exec(http("API_DefinitionStore_GetUserRole")
    .get(Environment.definitionStoreUrl + "/api/user-role?role=Y2FzZXdvcmtlci1wdWJsaWNsYXctc3lzdGVtdXBkYXRl")
    .header("ServiceAuthorization", "Bearer #{ccd_gwBearerToken}")
    .header("Authorization", "Bearer #{access_token}")
    .header("Content-Type","application/json"))

  .pause(Environment.constantthinkTime.seconds)

}