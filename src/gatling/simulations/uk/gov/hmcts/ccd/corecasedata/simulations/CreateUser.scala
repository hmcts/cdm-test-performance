package simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import scenarios.api.CreateUser
import scenarios.utils._
import scala.concurrent.duration._

class CreateUser extends Simulation  {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")

  val GrantRole = scenario("Grant idam role")
    .repeat(1) {
      exec(CreateUser.IdamAdminLogin)
      .repeat(1) { //Set this value to the number of users you need to update (RolesForUsers.csv)
        exec(CreateUser.IdamUser)
        .repeat(1) { //Set this value to the number of roles you need to add per user (RolesToAdd.csv)
          exec(CreateUser.GetAndApplyRole)
        }
      }
    }

  val DeleteRole = scenario("Remove idam roles")
    .repeat(1) {
      exec(CreateUser.IdamAdminLogin)
      .repeat(49) { //Set this value to the number of users you need to update (RolesForUsers.csv)
        exec(CreateUser.IdamUser)
        .repeat(1) { //Set this value to the number of roles you need to remove per user (RolesToAdd.csv)
          exec(CreateUser.GetAndRemoveRole)
      }
      }
    }

  val GetUserID = scenario ("Get idam ID for user by email")
  .repeat(1) {
      exec(CreateUser.IdamAdminLogin)
      .repeat(2) { //Set this value to the number of users you need to update (RolesForUsers.csv)
        exec(CreateUser.IdamUser)
      }
  }

  val CreateUserTestingSupport = scenario("Create User in Idam")
    .repeat(200) {
      exec(CreateUser.CreateUserInIdam)
    }

  val DeleteUserTestingSupport = scenario("Delete User in Idam")
    .repeat(39) {
      exec(CreateUser.DeleteUserInIdam)
    }

  setUp(
    // GrantRole.inject(rampUsers(1) during (5 minutes)))
    // DeleteRole.inject(rampUsers(1) during (1 minutes)))
    CreateUserTestingSupport.inject(rampUsers(1) during (1.minutes)))
    // DeleteUserTestingSupport.inject(rampUsers(1) during (1 minutes)))
    // GetUserID.inject(rampUsers(1) during (1 minutes)))
    .protocols(httpProtocol)
}