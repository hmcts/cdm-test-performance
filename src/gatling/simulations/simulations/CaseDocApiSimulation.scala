package simulations

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import scenarios.utils._
import scenarios.api.casedocapi
import scala.concurrent.duration._
import io.gatling.core.controller.inject.open.{AtOnceOpenInjection, OpenInjectionStep}
import io.gatling.commons.stats.assertion.Assertion
import io.gatling.core.pause.PauseType

class CaseDocApiSimulation extends Simulation  {

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

	/* PERFORMANCE TEST CONFIGURATION */
	val oneMbStoreTargetPerHour:Double = 10760
  val fiveMbStoreTargetPerHour:Double = 1200
  val tenMbStoreTargetPerHour:Double = 450
  val twentyMbStoreTargetPerHour:Double = 306
  val fiftyMbStoreTargetPerHour:Double = 104
  val oneHundredMbStoreTargetPerHour:Double = 51
  val twofiftyMbStoreTargetPerHour:Double = 22
  val fiveHundredMbStoreTargetPerHour:Double = 4
  val oneGbStoreTargetPerHour:Double = 1
  val fiveHundredDownloadTarget:Double = 10

	val rampUpDurationMins = 10
	val rampDownDurationMins = 10
	val testDurationMins = 30 //60

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

  //Gatling specific configs, required for perf testing
  val BaseURL = Environment.baseURL
  val config: Config = ConfigFactory.load()
  val caseDocUsers = csv("CMCUserData.csv").circular
  val feedProbateUserDataPerftest = csv("ProbateUserData.csv").circular
  val feed500mbfiles = csv("500mbfiles.csv").random

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(Environment.baseURL.replace("#{env}", s"${env}"))
    .doNotTrackHeader("1")
    .disableCaching

  val onembfilescenario = scenario("CDAM 1mb file upload & download")
    .exec(_.set("env", s"${env}"))
    .exitBlockOnFail {
      feed(feedProbateUserDataPerftest)
      .exec(S2S.s2s("ccd_data"))
      .exec(IdamLogin.GetIdamToken)
      .exec(casedocapi.CCDAPI_ProbateCreate)
      .exec(casedocapi.S2SLogin)
      .exec(casedocapi.idamLogin)
      .exec(_.set("filename", "1MB.pdf"))
      .exec(casedocapi.caseDocUpload)
      .exec(casedocapi.addDocToCase)
    }
    .doIf("#{Document_ID.exists()}") {
      repeat(4) {
        exec(casedocapi.caseDocDownload)
      }
    }

  val fivembfilescenario = scenario("CDAM 5mb file upload & download")
    .exec(_.set("env", s"${env}"))
    .exitBlockOnFail {
      feed(feedProbateUserDataPerftest)
      .exec(S2S.s2s("ccd_data"))
      .exec(IdamLogin.GetIdamToken)
      .exec(casedocapi.CCDAPI_ProbateCreate)
      .exec(casedocapi.S2SLogin)
      .exec(casedocapi.idamLogin)
      .exec(_.set("filename", "5MB.pdf"))
      .exec(casedocapi.caseDocUpload)
      .exec(casedocapi.addDocToCase)
    }
    .doIf("#{Document_ID.exists()}") {
      repeat(4) {
        exec(casedocapi.caseDocDownload)
      }
    }

  val tenmbfilescenario = scenario("CDAM 10mb file upload & download")
    .exec(_.set("env", s"${env}"))
    .exitBlockOnFail {
      feed(feedProbateUserDataPerftest)
      .exec(S2S.s2s("ccd_data"))
      .exec(IdamLogin.GetIdamToken)
      .exec(casedocapi.CCDAPI_ProbateCreate)
      .exec(casedocapi.S2SLogin)
      .exec(casedocapi.idamLogin)
      .exec(_.set("filename", "10MB.pdf"))
      .exec(casedocapi.caseDocUpload)
      .exec(casedocapi.addDocToCase)
    }
    .doIf("#{Document_ID.exists()}") {
      repeat(4) {
        exec(casedocapi.caseDocDownload)
      }
    }

  val twentymbfilescenario = scenario("CDAM 20mb file upload & download")
    .exec(_.set("env", s"${env}"))
    .exitBlockOnFail {
      feed(feedProbateUserDataPerftest)
      .exec(S2S.s2s("ccd_data"))
      .exec(IdamLogin.GetIdamToken)
      .exec(casedocapi.CCDAPI_ProbateCreate)
      .exec(casedocapi.S2SLogin)
      .exec(casedocapi.idamLogin)
      .exec(_.set("filename", "20MB.pdf"))
      .exec(casedocapi.caseDocUpload)
      .exec(casedocapi.addDocToCase)
    }
    .doIf("#{Document_ID.exists()}") {
      repeat(4) {
        exec(casedocapi.caseDocDownload)
      }
    }

  val fiftymbfilescenario = scenario("CDAM 50mb file upload & download")
    .exec(_.set("env", s"${env}"))
    .exitBlockOnFail {
      feed(feedProbateUserDataPerftest)
      .exec(S2S.s2s("ccd_data"))
      .exec(IdamLogin.GetIdamToken)
      .exec(casedocapi.CCDAPI_ProbateCreate)
      .exec(casedocapi.S2SLogin)
      .exec(casedocapi.idamLogin)
      .exec(_.set("filename", "50MB.pdf"))
      .exec(casedocapi.caseDocUpload)
      .exec(casedocapi.addDocToCase)
    }
    .doIf("#{Document_ID.exists()}") {
      repeat(4) {
        exec(casedocapi.caseDocDownload)
      }
    }

  val onehundredmbfilescenario = scenario("CDAM 100mb file upload & download")
    .exec(_.set("env", s"${env}"))
    .exitBlockOnFail {
      feed(feedProbateUserDataPerftest)
      .exec(S2S.s2s("ccd_data"))
      .exec(IdamLogin.GetIdamToken)
      .exec(casedocapi.CCDAPI_ProbateCreate)
      .exec(casedocapi.S2SLogin)
      .exec(casedocapi.idamLogin)
      .exec(_.set("filename", "100MB.pdf"))
      .exec(casedocapi.caseDocUpload)
      .exec(casedocapi.addDocToCase)
    }
    .doIf("#{Document_ID.exists()}") {
      repeat(4) {
        exec(casedocapi.caseDocDownload)
      }
    }

  val twofiftymbfilescenario = scenario("CDAM 250mb file upload & download")
    .exec(_.set("env", s"${env}"))
    .exitBlockOnFail {
      feed(feedProbateUserDataPerftest)
      .exec(S2S.s2s("ccd_data"))
      .exec(IdamLogin.GetIdamToken)
      .exec(casedocapi.CCDAPI_ProbateCreate)
      .exec(casedocapi.S2SLogin)
      .exec(casedocapi.idamLogin)
      .exec(_.set("filename", "250MB.pdf"))
      .exec(casedocapi.caseDocUpload)
      .exec(casedocapi.addDocToCase)
    }
    .doIf("#{Document_ID.exists()}") {
      repeat(4) {
        exec(casedocapi.caseDocDownload)
      }
    }

  val fivehundredmbfilescenario = scenario("CDAM 500mb file upload & download")
    .exec(_.set("env", s"${env}"))
    .exitBlockOnFail {
      feed(feedProbateUserDataPerftest)
      .exec(S2S.s2s("ccd_data"))
      .exec(IdamLogin.GetIdamToken)
      .exec(casedocapi.CCDAPI_ProbateCreate)
      .exec(casedocapi.S2SLogin)
      .exec(casedocapi.idamLogin)
      .exec(_.set("filename", "500MB.pdf"))
      .exec(casedocapi.caseDocUpload)
      .exec(casedocapi.addDocToCase)
    }
    .doIf("#{Document_ID.exists()}") {
      repeat(4) {
        exec(casedocapi.caseDocDownload)
      }
    }

  val onegbfilescenario = scenario("CDAM 1gb file upload & download")
    .exec(_.set("env", s"${env}"))
    .exitBlockOnFail {
      feed(feedProbateUserDataPerftest)
      .exec(S2S.s2s("ccd_data"))
      .exec(IdamLogin.GetIdamToken)
      .exec(casedocapi.CCDAPI_ProbateCreate)
      .exec(casedocapi.S2SLogin)
      .exec(casedocapi.idamLogin)
      .exec(_.set("filename", "1000MB.pdf"))
      .exec(casedocapi.caseDocUpload)
      .exec(casedocapi.addDocToCase)
    }
    .doIf("#{Document_ID.exists()}") {
      repeat(4) {
        exec(casedocapi.caseDocDownload)
      }
    }

  val fivehundreddownload = scenario("CDAM - download 500mb file only")
    .exec(_.set("env", s"${env}"))
    .exitBlockOnFail {
      feed(feedProbateUserDataPerftest)
      .exec(casedocapi.S2SLogin)
      .exec(casedocapi.idamLogin)
      .feed(feed500mbfiles)
      .exec(_.set("filename", "500MB.pdf"))
      .repeat(5) {
        exec(casedocapi.caseDocDownload)
      }
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
    // onembfilescenario.inject(simulationProfile(testType, oneMbStoreTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
    // fivembfilescenario.inject(simulationProfile(testType, fiveMbStoreTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
    // tenmbfilescenario.inject(simulationProfile(testType, tenMbStoreTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
    // twentymbfilescenario.inject(simulationProfile(testType, twentyMbStoreTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
    // fiftymbfilescenario.inject(simulationProfile(testType, fiftyMbStoreTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
    // onehundredmbfilescenario.inject(simulationProfile(testType, oneHundredMbStoreTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
    // twofiftymbfilescenario.inject(simulationProfile(testType, twofiftyMbStoreTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
    // fivehundredmbfilescenario.inject(simulationProfile(testType, fiveHundredMbStoreTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
    // onegbfilescenario.inject(simulationProfile(testType, oneGbStoreTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
    fivehundreddownload.inject(simulationProfile(testType, fiveHundredDownloadTarget, numberOfPipelineUsers)).pauses(pauseOption)
    )
  .protocols(httpProtocol)
  .assertions(assertions(testType))
  .maxDuration(85.minutes)
}