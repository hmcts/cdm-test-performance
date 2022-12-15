package uk.gov.hmcts.ccd.corecasedata.simulations

import io.gatling.commons.stats.assertion.Assertion
import io.gatling.core.Predef._
import io.gatling.core.controller.inject.open.OpenInjectionStep
import io.gatling.core.pause.PauseType
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.ccd.corecasedata.scenarios.IdamLogin
import uk.gov.hmcts.ccd.corecasedata.scenarios.manageCaseAssignments._
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment._

import scala.concurrent.duration._
import scala.language.postfixOps


class AAC_API extends Simulation {

  val dmBaseURL = aacUrl

  /* TEST TYPE DEFINITION */
  /* pipeline = nightly pipeline against the AAT environment (see the Jenkins_nightly file) */
  /* perftest (default) = performance test against the perftest environment */
  val testType = scala.util.Properties.envOrElse("TEST_TYPE", "perftest")

  //set the environment based on the test type
  val environment = testType match{
    case "perftest" => "perftest"
    case "pipeline" => "perftest" //updated pipeline to run against perftest - change to aat to run against AAT
    case _ => "**INVALID**"
  }
  /* ******************************** */

  /* ADDITIONAL COMMAND LINE ARGUMENT OPTIONS */
  val debugMode = System.getProperty("debug", "off") //runs a single user e.g. ./gradle gatlingRun -Ddebug=on (default: off)
  val env = System.getProperty("env", environment) //manually override the environment aat|perftest e.g. ./gradle gatlingRun -Denv=aat
  /* ******************************** */

  /* PERFORMANCE TEST CONFIGURATION */
  val rampUpDurationMins = 5
  val rampDownDurationMins = 5
  val testDurationMins = 60
  /*Hourly Volumes for Share Case requests*/
  val AssignmentsHourlyTarget:Double = 2000
  /*Hourly Volumes for Notice of Change requests*/
  val NOCHourlyTarget: Double = 300

  /*Rate Per Second Volume for Share Case Requests */
  val AssignmentsRatePerSec = AssignmentsHourlyTarget / 3600
  /*Rate Per Second Volume for Share Case Requests */
  val NOCRatePerSec = NOCHourlyTarget / 3600
   /* PIPELINE CONFIGURATION */
  val numberOfPipelineUsers = 1



  /* SIMULATION FEEDER FILES */
  val ManageCaseAPIFeeder = csv("caseAssignmentShareCaseAPI.csv").circular
  val NOCAPIFeeder =  csv("noticeOfChangeAPI.csv")

  //If running in debug mode, disable pauses between steps
  val pauseOption:PauseType = debugMode match{
    case "off" => constantPauses
    case _ => disabledPauses
  }
  /* ******************************** */

  //  /* PIPELINE CONFIGURATION */
  //  val numberOfPipelineUsersSole:Double = 5
  //  val numberOfPipelineUsersJoint:Double = 5
  /* ******************************** */

  val httpProtocol = HttpProtocol
    .baseUrl(dmBaseURL)
    .doNotTrackHeader("1")
    .inferHtmlResources()
    .silentResources

  before{
    println(s"Test Type: ${testType}")
    println(s"Test Environment: ${env}")
    println(s"Debug Mode: ${debugMode}")
  }


  //defines the Gatling simulation model, based on the inputs
  def simulationProfile(simulationType: String, userPerSecRate: Double, numberOfPipelineUsers: Double): Seq[OpenInjectionStep] = {
    simulationType match {
      case "perftest" =>
        if (debugMode == "off") {
          Seq(
            rampUsersPerSec(0.00) to (userPerSecRate) during (rampUpDurationMins minutes),
            constantUsersPerSec(userPerSecRate) during (testDurationMins minutes),
            rampUsersPerSec(userPerSecRate) to (0.00) during (rampDownDurationMins minutes)
          )
        }
        else{
          Seq(atOnceUsers(1))
        }
      case "pipeline" =>
        Seq(rampUsers(numberOfPipelineUsers.toInt) during (2 minutes))
      case _ =>
        Seq(nothingFor(0))
    }
  }

  //defines the test assertions, based on the test type
  def assertions(simulationType: String): Seq[Assertion] = {
    simulationType match {
      case "perftest" =>
        Seq(global.successfulRequests.percent.gte(95))
      case "pipeline" =>
        Seq(global.successfulRequests.percent.gte(95))
      case _ =>
        Seq()
    }
  }

  /* Share Case API Scenarios*/

  //scenario for Share Case - Get Assignment, Delete Assignment, Create Assignment
  val ScnCaseAssignments = scenario("ShareCase_CaseAssignments")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .exec(S2S.s2s("ccd_data"))
        .feed(ManageCaseAPIFeeder)
        .exec(IdamLogin.GetIdamToken)
        .exec(caseAssignmentControllerService.caseAssignmentGetAssignments)
        .exec(caseAssignmentControllerService.caseAssignmentRemoveAssignments)
        .exec(caseAssignmentControllerService.caseAssignmentPostAssignment)
    }


  //scenario for Share Case - Get Assignment, Delete Assignment, Create Assignment
  val ScnNoticeOfChange = scenario("NoticeOfChange")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .exec(S2S.s2s("ccd_data"))
        .feed(NOCAPIFeeder)
        .exec(IdamLogin.GetIdamToken)
        .exec(noticeOfChangeControllerService.noticeOfChangeGetQuestions)
        .exec(noticeOfChangeControllerService.noticeOfChangePostAnswers)
        .exec(noticeOfChangeControllerService.noticeOfChangePostCreateNOCEvent)
    }

  /*Manage Case Simulations */
  setUp(
    ScnCaseAssignments.inject(simulationProfile(testType,AssignmentsRatePerSec, numberOfPipelineUsers)).pauses(pauseOption),
    ScnNoticeOfChange.inject(simulationProfile(testType,NOCRatePerSec, numberOfPipelineUsers)).pauses(pauseOption)
  ).protocols(httpProtocol)
    .assertions(assertions(testType))

}
