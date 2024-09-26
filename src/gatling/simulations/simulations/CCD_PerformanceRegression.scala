package simulations

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import scenarios.api._
import scenarios.utils._
import scala.concurrent.duration._
import io.gatling.core.controller.inject.open.{AtOnceOpenInjection, OpenInjectionStep}
import io.gatling.commons.stats.assertion.Assertion
import io.gatling.core.pause.PauseType

class CCD_PerformanceRegression extends Simulation  {

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
	val probateTargetPerHour:Double = 800
	val cmcTargetPerHour:Double = 800
  val divorceTargetPerHour:Double = 800
  val iacTargetPerHour:Double = 800
  val fplTargetPerHour:Double = 800
  val nfdTargetPerHour:Double = 800
  val caseActivityTargetPerHour:Double = 850000
  val caseActivityRepeatsPerUser = 8500 //850
  val caseActivityListTargetPerHour:Double = 90000
  val caseActivityListRepeatsPerUser = 1800 //180
  val searchTargetPerHour:Double = 8000
  val searchRepeatsPerUser = 400 //40
  val elasticSearchTargetPerHour:Double = 200000
  val esRepeatsPerUser = 120 //120
  val caseFileViewTargetPerHour:Double = 5363
  val caseActivityUsers:Double = 500
  val searchUsers:Double = 200
  val esUsers:Double = 300
  val definitionStoreUsers:Double = 300

  val caseActivityIteration = 900
  val caseActivityListIteration = 120
  val ccdSearchIteration = 40
  val elasticSearchIteration = 370

	val rampUpDurationMins = 10
	val rampDownDurationMins = 10
	val testDurationMins = 60 // 60

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

  //Perftest Data
  val feedSSCSUserData = csv("SSCSUserData.csv").circular
  val feedProbateUserDataPerftest = csv("ProbateUserData.csv").circular
  val feedCMCUserData = csv("CMCUserData.csv").circular
  val feedDivorceUserData = csv("DivorceSolUserData.csv").circular
  val feedIACUserData = csv("IACUserData.csv").circular
  val feedFPLUserData = csv("FPLUserData.csv").circular
  val feedEthosUserData = csv("EthosUserData.csv").circular
  val feedNFDUserData = csv("NFDUserData.csv").circular
  val feedCMCCaseData = csv("CMCCaseData.csv").circular
  val feedJurisdictions = csv("Jurisdictions.csv").random

  //AAT Data
  val feedProbateUserDataAAT = csv("AATProbateUserData.csv").circular

  //Gatling specific configs, required for perf testing
  val BaseURL = Environment.baseURL
  val config: Config = ConfigFactory.load()

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(Environment.baseURL.replace("#{env}", s"${env}"))
    .doNotTrackHeader("1")
    .disableCaching

  /*================================================================================================

  The below scenarios are required for CCD Regression Performance Testing

  ================================================================================================*/

  //CCD API - Create & Case Event Journeys
  val API_ProbateCreateCase = scenario("Probate Case Create")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(S2S.s2s("ccd_data"))
      .doSwitch(s"${env}") (
        "perftest" -> feed(feedProbateUserDataPerftest),
        "aat" -> feed(feedProbateUserDataAAT)
       )
      .exec(IdamLogin.GetIdamToken)
      .exec(ccddatastore.CCDAPI_ProbateCreate)
      .exec(S2S.s2s("probate_backend")) 
      .exec(ccddatastore.CCDAPI_ProbateCaseEvents)
    }

  val API_SSCSCreateCase = scenario("SSCS Case Create")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(S2S.s2s("ccd_data"))
      .feed(feedSSCSUserData)
      .exec(IdamLogin.GetIdamToken)
      .exec(ccddatastore.CCDAPI_SSCSCreate)
      .exec(S2S.s2s("sscs"))
      .exec(ccddatastore.CCDAPI_SSCSCaseEvents)
    }

  val API_CMCCreateCase = scenario("CMC Case Create")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(S2S.s2s("ccd_data"))
      .feed(feedCMCUserData)
      .exec(IdamLogin.GetIdamToken)
      .exec(ccddatastore.CCDAPI_CMCCreate)
      .exec(ccddatastore.CCDAPI_CMCCaseEvents)
    }

  val API_DivorceCreateCase = scenario("Divorce Case Create")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(S2S.s2s("ccd_data"))
      .feed(feedDivorceUserData)
      .exec(IdamLogin.GetIdamToken)
      .exec(ccddatastore.CCDAPI_DivorceSolicitorCreate)
      .exec(ccddatastore.CCDAPI_DivorceSolicitorCaseEvents)
    }

  val API_IACCreateCase = scenario("IAC Case Create")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(S2S.s2s("ccd_data"))
      .feed(feedIACUserData)
      .exec(IdamLogin.GetIdamToken)   
      .exec(S2S.s2s("xui_webapp"))   
      .exec(ccddatastore.CCDAPI_IACCreate)
    }

  val API_FPLCreateCase = scenario("FPL Case Create")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(S2S.s2s("ccd_data"))
      .feed(feedFPLUserData)
      .exec(IdamLogin.GetIdamToken) 
      .exec(ccddatastore.CCDAPI_FPLCreate)
      .exec(S2S.s2s("xui_webapp"))
      .exec(ccddatastore.CCDAPI_FPLCaseEvents)
    }

  val API_NFDCreateCase = scenario("Divorce Case Create")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(S2S.s2s("ccd_data"))
      .feed(feedNFDUserData)
      .exec(IdamLogin.GetIdamToken)
      .exec(ccddatastore.CCDAPI_DivorceNFDCreate)
    }

  //CCD Case Activity Requests
  val CaseActivityListScn = scenario("CCD Case Activity List Requests")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(ccdcaseactivity.CDSGetRequest)
      .repeat(900) {
        exec(ccdcaseactivity.CaseActivityList)
      }
    }
  
  val CaseActivityScn = scenario("CCD Case Activity Requests")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(ccdcaseactivity.CDSGetRequest)
      .repeat(8500) {
        exec(ccdcaseactivity.CaseActivityRequest)
      }
    }

  //CCD Search Requests (non-Elastic Search)
  val CCDSearchView = scenario("CCD Search and View Cases")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(S2S.s2s("ccd_data"))
      .feed(feedEthosUserData)
      .exec(IdamLogin.GetIdamToken) 
      .repeat(400) {
        exec(ccddatastore.CCDAPI_EthosJourney)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  //CCD Elastic Search Requests
  val CCDElasticSearch = scenario("CCD - Elastic Search")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(elasticsearch.CDSGetRequest)
      .repeat(120) { //esRepeatsPerUser
        exec(GetUserProfile.SearchJurisdiction)
        .exec(GetUserProfile.SearchAllUsers)
        .exec(elasticsearch.ElasticSearchGetVaryingSizes)
        .exec(elasticsearch.ElasticSearchWorkbasket)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  //CMC Add Doc to Case
  val API_CMCAddDoc = scenario("CMC Add Doc To Case")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(S2S.s2s("ccd_data"))
      .feed(feedCMCUserData)
      .feed(feedCMCCaseData)
      .exec(IdamLogin.GetIdamToken)
      .exec(S2S.s2s("cmc_claim_store"))
      .exec(ccddatastore.CCDAPI_CMCUploadDoc)
    }

  //CMC Case Events
  val API_CMCCaseEvents = scenario("CMC CaseEvents")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(S2S.s2s("ccd_data"))
      .feed(feedCMCUserData)
      .feed(feedCMCCaseData)
      .exec(IdamLogin.GetIdamToken)
      // .exec(S2S.s2s("cmc_claim_store"))
      .exec(ccddatastore.CCDAPI_CMCCaseHandedToCCBC)
    }

  val CaseFileView = scenario("Case File View scenario")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(casefileview.S2SLogin)
      .exec(casefileview.idamLogin)
      .exec(casefileview.caseDocUpload)
      .exec(casefileview.createCase)
      .exec(casefileview.caseFileViewPutCategories)
      .exec(casefileview.caseFileViewGet)
  }

  val CaseFileView5and2 = scenario("Case File View - 5 docs, 2 categories")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(casefileview.S2SLogin)
      .exec(casefileview.idamLogin)
      .exec(casefileview.caseDocUpload)
      .exec(casefileview.createCase5Doc)
      .exec(casefileview.caseFileViewPut2Categories)
      .exec(casefileview.caseFileViewGet)
  }

  val DefinitionStore = scenario("CCD Definition Store scenario")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(S2S.s2s("ccd_gw"))
      .feed(feedDivorceUserData)
      .exec(IdamLogin.GetIdamToken)
      .repeat(25) {
        feed(feedJurisdictions)
        .exec(ccddefinitionstore.CCD_DefinitionStoreJurisdictions)
      }
  }

  val DefinitionStoreUserRoles = scenario("CCD Definition Store Get Roles")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(S2S.s2s("ccd_gw"))
      .feed(feedDivorceUserData)
      .exec(IdamLogin.GetIdamToken)
      .exec(ccddefinitionstore.CCD_DefinitionStoreGetUserRole)
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
     //simulation for cdm-test-performance repo
      API_ProbateCreateCase.inject(simulationProfile(testType, probateTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
      API_CMCCreateCase.inject(simulationProfile(testType, cmcTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
      API_DivorceCreateCase.inject(simulationProfile(testType, divorceTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
      API_IACCreateCase.inject(simulationProfile(testType, iacTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
      CaseActivityListScn.inject(simulationProfile(testType, caseActivityUsers, numberOfPipelineUsers)).pauses(pauseOption),
      CaseActivityScn.inject(simulationProfile(testType, caseActivityUsers, numberOfPipelineUsers)).pauses(pauseOption),
      CCDSearchView.inject(simulationProfile(testType, searchUsers, numberOfPipelineUsers)).pauses(pauseOption),
      CCDElasticSearch.inject(simulationProfile(testType, esUsers, numberOfPipelineUsers)).pauses(pauseOption),
      CaseFileView.inject(simulationProfile(testType, caseFileViewTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
      // CaseFileView5and2.inject(simulationProfile(testType, caseFileViewTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
      DefinitionStore.inject(simulationProfile(testType, definitionStoreUsers, numberOfPipelineUsers)).pauses(pauseOption),

      // DefinitionStoreUserRoles.inject(simulationProfile(testType, definitionStoreUsers, numberOfPipelineUsers)).pauses(pauseOption)

    //  CaseActivityListScn.inject(rampUsers(500) during (10.minutes)),
		//  CaseActivityScn.inject(rampUsers(500) during (10.minutes)),
    //  CCDSearchView.inject(rampUsers(200) during (20.minutes)),
		//  CCDElasticSearch.inject(rampUsers(300) during (20.minutes)), //300 during 20
  )
    .protocols(httpProtocol)
    .assertions(assertions(testType))
    .maxDuration(85.minutes) //85

}