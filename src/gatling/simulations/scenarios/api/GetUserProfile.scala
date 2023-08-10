package scenarios.api

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios.utils._
import scala.concurrent.duration._

object GetUserProfile {

  val feedUserData = csv("UserProfileJurisdictions.csv").random

  val SearchJurisdiction = 
  
    feed(feedUserData)
    
    .exec(http("CUP_GetJurisdiction")
      .get(Environment.userProfileUrl + "/users?jurisdiction=#{UPJurisdiction}")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer #{access_token}")
      .header("ServiceAuthorization", "Bearer #{bearerToken}"))

    .pause(Environment.constantthinkTime.seconds)

  val SearchAllUsers = 
  
    exec(http("CUP_GetAllUsers")
      .get(Environment.userProfileUrl + "/users")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer #{access_token}")
      .header("ServiceAuthorization", "Bearer #{bearerToken}"))

    .pause(Environment.constantthinkTime.seconds)

}