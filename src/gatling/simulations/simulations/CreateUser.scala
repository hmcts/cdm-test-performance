package simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import scenarios.api.CreateUser
import scenarios.utils._
import scala.concurrent.duration._

import io.gatling.core.controller.inject.open.{AtOnceOpenInjection, OpenInjectionStep}
import io.gatling.commons.stats.assertion.Assertion
import io.gatling.core.pause.PauseType
import com.typesafe.config.{Config, ConfigFactory}

class CreateUser extends Simulation  {

  val BaseURL = Environment.baseURL
  // val config: Config = ConfigFactory.load()

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")

  /* TEST TYPE DEFINITION */
	/* pipeline = nightly pipeline against the AAT environment (see the Jenkins_nightly file) */
	/* perftest (default) = performance test against the perftest environment */
	val testType = scala.util.Properties.envOrElse("TEST_TYPE", "perftest")

	//set the environment based on the test type
	val environment = testType match{
		case "perftest" => "perftest"
		case "pipeline" => "aat"
		case _ => "**INVALID**"
	}

	/* ******************************** */
	/* ADDITIONAL COMMAND LINE ARGUMENT OPTIONS */
	val debugMode = System.getProperty("debug", "off") //runs a single user e.g. ./gradlew gatlingRun -Ddebug=on (default: off)
	val env = System.getProperty("env", environment) //manually override the environment aat|perftest e.g. ./gradlew gatlingRun -Denv=aat
	/* ******************************** */
  
	val rampUpDurationMins = 10
	val rampDownDurationMins = 10
	val testDurationMins = 60

	val numberOfPipelineUsers = 5
	val pipelinePausesMillis:Long = 3000 //3 seconds

	//Determine the pause pattern to use:
	//Performance test = use the pauses defined in the scripts
	//Pipeline = override pauses in the script with a fixed value (pipelinePauseMillis)
	//Debug mode = disable all pauses
	val pauseOption:PauseType = debugMode match{
		case "off" if testType == "perftest" => constantPauses
		case "off" if testType == "pipeline" => customPauses(pipelinePausesMillis)
		case _ => disabledPauses
	}

  val GrantRole = scenario("Grant idam role")
    .exec(_.set("env", s"${env}"))
    .exitBlockOnFail {
      repeat(1) {
        exec(CreateUser.IdamAdminLogin)
        .repeat(1) { //Set this value to the number of users you need to update (RolesForUsers.csv)
          exec(CreateUser.IdamUser)
          .repeat(1) { //Set this value to the number of roles you need to add per user (RolesToAdd.csv)
            exec(CreateUser.GetAndApplyRole)
          }
        }
      }
    }

  val DeleteRole = scenario("Remove idam roles")
    .exec(_.set("env", s"${env}"))
    .exitBlockOnFail {
      repeat(1) {
        exec(CreateUser.IdamAdminLogin)
        .repeat(1) { //Set this value to the number of users you need to update (RolesForUsers.csv)
          exec(CreateUser.IdamUser)
          .repeat(1) { //Set this value to the number of roles you need to remove per user (RolesToAdd.csv)
            exec(CreateUser.GetAndRemoveRole)
          }
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
    .exec(_.set("env", s"${env}"))
    .repeat(1) {
      exec(CreateUser.CreateUserInIdam)
    }

  val DeleteUserTestingSupport = scenario("Delete User in Idam")
    .repeat(39) {
      exec(CreateUser.DeleteUserInIdam)
    }

  
	def simulationProfile(simulationType: String, userPerHourRate: Double, numberOfPipelineUsers: Double): Seq[OpenInjectionStep] = {
		val userPerSecRate = userPerHourRate / 3600
		simulationType match {
			case "perftest" =>
				if (debugMode == "off") {
					Seq(
						rampUsersPerSec(0.00) to (userPerSecRate) during (rampUpDurationMins.minutes),
						constantUsersPerSec(userPerSecRate) during (testDurationMins.minutes),
						rampUsersPerSec(userPerSecRate) to (0.00) during (rampDownDurationMins.minutes)
					)
				}
				else{
					Seq(atOnceUsers(1))
				}
			case "pipeline" =>
				Seq(rampUsers(numberOfPipelineUsers.toInt) during (2.minutes))
			case _ =>
				Seq(nothingFor(0))
		}
	}

  //defines the test assertions, based on the test type
  def assertions(simulationType: String): Seq[Assertion] = {
    simulationType match {
      case "perftest" =>
        if (debugMode == "off") {
          Seq(global.successfulRequests.percent.gte(95)
          )
        }
        else{
          Seq(global.successfulRequests.percent.gte(95)
          )
        }
      case "pipeline" =>
        Seq(global.successfulRequests.percent.gte(95),
            forAll.successfulRequests.percent.gte(90)
        )
      case _ =>
        Seq()
    }
  }

  setUp(
    GrantRole.inject(rampUsers(1) during (5 minutes)))
    // DeleteRole.inject(rampUsers(1) during (1 minutes)))
//     CreateUserTestingSupport.inject(rampUsers(1) during (1.minutes)))
    // DeleteUserTestingSupport.inject(rampUsers(1) during (1 minutes)))
    // GetUserID.inject(rampUsers(1) during (1 minutes)))
    .protocols(httpProtocol)
}