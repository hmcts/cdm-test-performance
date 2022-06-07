package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment
import scala.concurrent.duration._

object GetUserProfile {

  val feedUserData = csv("UserProfileJurisdictions.csv").random

  val SearchJurisdiction = 
  
    feed(feedUserData)
    
    .exec(http("CUP_GetJurisdiction")
      .get(Environment.userProfileUrl + "/users?jurisdiction=${UPJurisdiction}")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer ${access_token}")
      .header("ServiceAuthorization", "Bearer ${bearerToken}"))

    .pause(Environment.constantthinkTime)

  val SearchAllUsers = 
  
    exec(http("CUP_GetAllUsers")
      .get(Environment.userProfileUrl + "/users")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer ${access_token}")
      .header("ServiceAuthorization", "Bearer ${bearerToken}"))

    .pause(Environment.constantthinkTime)

}