package uk.gov.hmcts.ccd.corecasedata.simulations

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._ //comment out for VM runs, only required for proxy
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
import scala.concurrent.duration._

class CCD_PerformanceRegression extends Simulation  {

  //Iteration Settings
  val api_probateIteration = 60 //60
  val api_sscsIteration = 50 //50
  val api_divorceIteration = 60 //60
  val api_iacIteration = 40 //40
  val api_fplIteration = 40 //40
  val api_frIteration = 40 //40
  val api_cmcIteration = 45 //45

  val ui_PBiteration = 12
  val ui_SSCSiteration = 14
  val ui_CMCiteration = 14
  val ui_Diviteration = 14

  val caseActivityIteration = 120
  val caseActivityListIteration = 12
  val ccdSearchIteration = 40
  val elasticSearchIteration = 300

  //Gatling specific configs, required for perf testing
  val BaseURL = Environment.baseURL
  val config: Config = ConfigFactory.load()

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    // .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080)) //Comment out for VM runs
    .doNotTrackHeader("1")

  /*================================================================================================

  The below scenarios are required for CCD Regression Performance Testing

  ================================================================================================*/

  //CCD API - Create & Case Event Journeys
  val API_ProbateCreateCase = scenario("Probate Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_Probate)
      .repeat(api_probateIteration) { //api_probateIteration
        exec(ccddatastore.CCDAPI_ProbateCreate)
        .exec(ccddatastore.CCDAPI_ProbateCaseEvents)
        // .exec(ccddatastore.CCDAPI_ProbateDocUpload) //10/05/2021 - not currently working
        // .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_ProbateSolicitorCreate = scenario("Probate Solicitor Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_ProbateSolicitor)
      .repeat(api_probateIteration) {
        exec(ccddatastore.CCDAPI_ProbateSolicitorCreate)
        .exec(ccddatastore.CCDAPI_ProbateSolicitorCaseEvents)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_SSCSCreateCase = scenario("SSCS Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_SSCS)
      .repeat(api_sscsIteration) { //api_sscsIteration
        exec(ccddatastore.CCDAPI_SSCSCreate)
        .exec(ccddatastore.CCDAPI_SSCSCaseEvents)
        // .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_DivorceCreateCase = scenario("Divorce Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_Divorce)
      .repeat(api_divorceIteration) { //api_divorceIteration
        exec(ccddatastore.CCDAPI_DivorceSolicitorCreate)
        .exec(ccddatastore.CCDAPI_DivorceSolicitorCaseEvents)
        // .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_IACCreateCase = scenario("IAC Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_IAC)
      .repeat(api_iacIteration) { //api_iacIteration
        exec(ccddatastore.CCDAPI_IACCreate)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_FPLCreateCase = scenario("FPL Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_FPL)
      .repeat(api_fplIteration) { //api_fplIteration
        exec(ccddatastore.CCDAPI_FPLCreate)
        .exec(ccddatastore.CCDAPI_FPLCaseEvents)
        // .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_FRCreateCase = scenario("FR Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_FR)
      .repeat(api_frIteration) { //api_frIteration
        exec(ccddatastore.CCDAPI_FRCreate)
        .exec(ccddatastore.CCDAPI_FRCaseEvents)
        // .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_CMCCreateCase = scenario("CMC Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_CMC)
      .repeat(api_cmcIteration) { //api_cmcIteration
        exec(ccddatastore.CCDAPI_CMCCreate)
        .exec(ccddatastore.CCDAPI_CMCCaseEvents)
        // .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  //CCD UI Requests
  val UI_CCDProbateScenario = scenario("CCD UI Probate")
    .repeat(1) {
      exec(Browse.Homepage)
      .exec(PBGoR.submitLogin)
      .repeat(ui_PBiteration) {
        exec(PBGoR.PBCreateCase)
        .exec(PBGoR.PBPaymentSuccessful)
        .exec(PBGoR.PBDocUpload)
        .exec(PBGoR.PBStopCase)
        .exec(PBGoR.PBSearch)
        .exec(PBGoR.PBView)
        // .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
  }

  val UI_CCDSSCSScenario = scenario("CCD UI SSCS")
    .repeat(1) {
     exec(Browse.Homepage)
      .exec(SSCS.SSCSLogin)
      .repeat(ui_SSCSiteration) {
        exec(SSCS.SSCSCreateCase)
        .exec(SSCS.SSCSDocUpload)
        .exec(SSCS.SSCSSearchAndView)
        // .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
    }

  val UI_CCDCMCScenario = scenario("CCD UI CMC")
    .repeat(1) {
      exec(Browse.Homepage)
      .exec(CMC.CMCLogin)
      .repeat(ui_CMCiteration) {
        exec(CMC.CMCCreateCase)
        .exec(CMC.CMCStayCase)
        // .exec(CMC.CMCAttachScannedDocs)// Not currently working 10/05/2021
        .exec(CMC.CMCSupportUpdate)
        .exec(CMC.CMCSearchAndView)
        // .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
  }

  val UI_CCDDivScenario = scenario("CCD UI Divorce")
    .repeat(1) {
      exec(Browse.Homepage)
        .exec(DVExcep.submitLogin)
        .repeat(ui_Diviteration) {
          exec(DVExcep.DVCreateCase)
          .exec(DVExcep.DVDocUpload)
          .exec(DVExcep.DVSearch)
          .exec(DVExcep.DVView)
          // .exec(WaitforNextIteration.waitforNextIteration)
        }
        .exec(Logout.ccdLogout)
    }

  //CCD Case Activity Requests
  val CaseActivityScn = scenario("CCD Case Activity Requests")
    .repeat(1) {
      exec(ccdcaseactivity.CDSGetRequest)
      .repeat(5) {
        repeat(caseActivityListIteration) {
          exec(ccdcaseactivity.CaseActivityList)
        }
        .repeat(caseActivityIteration) {
          exec(ccdcaseactivity.CaseActivityRequest)
        }
      }
    }

  //CCD Search Requests (non-Elastic Search)
  val CCDSearchView = scenario("CCD Search and View Cases")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_Ethos)
      .repeat(ccdSearchIteration) {
        exec(ccddatastore.CCDAPI_EthosJourney)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  //CCD Elastic Search Requests
  val CCDElasticSearch = scenario("CCD - Elastic Search")
    .repeat(1) {
      exec(elasticsearch.CDSGetRequest)
      .repeat(elasticSearchIteration) {
        exec(elasticsearch.ElasticSearchWorkbasket)
      }
    }

  setUp(
    //CCD API scenarios
    API_ProbateCreateCase.inject(rampUsers(100) during (10 minutes)), //50 during 10
    API_SSCSCreateCase.inject(rampUsers(100) during (10 minutes)), //50 during 10
    API_DivorceCreateCase.inject(rampUsers(100) during (10 minutes)), //50 during 10
    API_IACCreateCase.inject(rampUsers(100) during (10 minutes)), //50 during 10
    API_FPLCreateCase.inject(rampUsers(100) during (10 minutes)), //50 during 10
    API_FRCreateCase.inject(rampUsers(100) during (10 minutes)), //50 during 10
    API_CMCCreateCase.inject(rampUsers(100) during (10 minutes)), //50 during 10

    //CCD UI scenarios
    UI_CCDProbateScenario.inject(rampUsers(40) during (10 minutes)),
    UI_CCDSSCSScenario.inject(rampUsers(40) during (10 minutes)),
    UI_CCDCMCScenario.inject(rampUsers(40) during (10 minutes)),
    // UI_CCDDivScenario.inject(rampUsers(15) during (10 minutes)),

    //Case Activity Requests
    CaseActivityScn.inject(rampUsers(1000) during (10 minutes)),

    //CCD Searches
    CCDSearchView.inject(rampUsers(100) during (10 minutes)),
    CCDElasticSearch.inject(rampUsers(200) during (10 minutes))
    
    //Debugging requests (leave commented out for test runs please)
    // API_FPLCreateCase.inject(atOnceUsers(1)).disablePauses
    )
  .protocols(httpProtocol)
}