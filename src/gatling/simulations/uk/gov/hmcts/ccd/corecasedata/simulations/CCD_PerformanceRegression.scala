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
  val api_probateIteration = 55 //55
  val api_sscsIteration = 50 //50
  val api_divorceIteration = 60 //60
  val api_iacIteration = 40 //40
  val api_fplIteration = 36 //36
  val api_frIteration = 40 //40
  val api_cmcIteration = 45 //45

  val ui_PBiteration = 9
  val ui_SSCSiteration = 17
  val ui_CMCiteration = 15
  val ui_Diviteration = 9

  val caseActivityIteration = 120
  val caseActivityListIteration = 12
  val ccdSearchIteration = 40
  val elasticSearchIteration = 370

  val feedSSCSUserData = csv("SSCSUserData.csv").circular
  val feedProbateUserData = csv("ProbateUserData.csv").circular
  val feedCMCUserData = csv("CMCUserData.csv").circular
  val feedDivorceUserData = csv("DivorceSolUserData.csv").circular
  val feedIACUserData = csv("IACUserData.csv").circular
  val feedFPLUserData = csv("FPLUserData.csv").circular
  val feedEthosUserData = csv("EthosUserData.csv").circular

  //Gatling specific configs, required for perf testing
  val BaseURL = Environment.baseURL
  val config: Config = ConfigFactory.load()

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")

  /*================================================================================================

  The below scenarios are required for CCD Regression Performance Testing

  ================================================================================================*/

  //CCD API - Create & Case Event Journeys
  val API_ProbateCreateCase = scenario("Probate Case Create")
    .repeat(1) {
      exec(S2S.s2s("ccd_data"))
      .feed(feedProbateUserData)
      .exec(IdamLogin.GetIdamToken)
      .repeat(api_probateIteration) { //api_probateIteration
        exec(ccddatastore.CCDAPI_ProbateCreate)
        .exec(ccddatastore.CCDAPI_ProbateCaseEvents)
        .exec(S2S.s2s("probate_backend")) 
        .exec(ccddatastore.CCDAPI_ProbateDocUpload) 
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_SSCSCreateCase = scenario("SSCS Case Create")
    .repeat(1) {
      exec(S2S.s2s("ccd_data"))
      .feed(feedSSCSUserData)
      .exec(IdamLogin.GetIdamToken)
      .repeat(api_sscsIteration) { //api_sscsIteration
        exec(ccddatastore.CCDAPI_SSCSCreate)
        .exec(S2S.s2s("sscs"))
        .exec(ccddatastore.CCDAPI_SSCSCaseEvents)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_CMCCreateCase = scenario("CMC Case Create")
    .repeat(1) {
      exec(S2S.s2s("ccd_data"))
      .feed(feedCMCUserData)
      .exec(IdamLogin.GetIdamToken)
      .repeat(api_cmcIteration) { //api_cmcIteration
        exec(ccddatastore.CCDAPI_CMCCreate)
        .exec(ccddatastore.CCDAPI_CMCCaseEvents)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_DivorceCreateCase = scenario("Divorce Case Create")
    .repeat(1) {
      exec(S2S.s2s("ccd_data"))
      .feed(feedDivorceUserData)
      .exec(IdamLogin.GetIdamToken)
      .repeat(api_divorceIteration) { //api_divorceIteration
        exec(ccddatastore.CCDAPI_DivorceSolicitorCreate)
        .exec(ccddatastore.CCDAPI_DivorceSolicitorCaseEvents)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_IACCreateCase = scenario("IAC Case Create")
    .repeat(1) {
      exec(S2S.s2s("ccd_data"))
      .feed(feedIACUserData)
      .exec(IdamLogin.GetIdamToken)      
      .repeat(api_iacIteration) { //api_iacIteration
        exec(ccddatastore.CCDAPI_IACCreate)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_FPLCreateCase = scenario("FPL Case Create")
    .repeat(1) {
      exec(S2S.s2s("ccd_data"))
      .feed(feedFPLUserData)
      .exec(IdamLogin.GetIdamToken) 
      .repeat(api_fplIteration) { //api_fplIteration
        exec(ccddatastore.CCDAPI_FPLCreate)
        .exec(S2S.s2s("xui_webapp"))
        .exec(ccddatastore.CCDAPI_FPLCaseEvents)
        .exec(WaitforNextIteration.waitforNextIteration)
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
        .exec(CMC.CMCSupportUpdate)
        .exec(CMC.CMCSearchAndView)
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
      exec(S2S.s2s("ccd_data"))
      .feed(feedEthosUserData)
      .exec(IdamLogin.GetIdamToken) 
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

  //Main CCD Performance Test
  
  /*
  setUp(
    //CCD API scenarios
    API_ProbateCreateCase.inject(rampUsers(180) during (10 minutes)), //50 during 10
    API_SSCSCreateCase.inject(rampUsers(180) during (10 minutes)), //50 during 10
    API_CMCCreateCase.inject(rampUsers(180) during (10 minutes)), //50 during 10
    API_DivorceCreateCase.inject(rampUsers(180) during (10 minutes)), //50 during 10
    API_IACCreateCase.inject(rampUsers(180) during (10 minutes)), //50 during 10
    // API_FPLCreateCase.inject(rampUsers(150) during (10 minutes)), //50 during 10

    //CCD UI scenarios
    UI_CCDProbateScenario.inject(rampUsers(40) during (10 minutes)),
    UI_CCDSSCSScenario.inject(rampUsers(40) during (10 minutes)),
    UI_CCDCMCScenario.inject(rampUsers(40) during (10 minutes)),

    //Case Activity Requests
    CaseActivityScn.inject(rampUsers(1200) during (10 minutes)), //1000 during 10

    //CCD Searches
    CCDSearchView.inject(rampUsers(200) during (10 minutes)), //100 during 10
    CCDElasticSearch.inject(rampUsers(300) during (10 minutes)) //200 during 10
    
    //Debugging requests (leave commented out for test runs please)
    // API_DivorceCreateCase.inject(rampUsers(1) during (1 minutes)).disablePauses
    )
  .maxDuration(60 minutes)
  .protocols(httpProtocol)
*/
  
  //Smoke Test Scenario
  setUp(
    //CCD API scenarios
    API_ProbateCreateCase.inject(rampUsers(10) during (1 minutes)), //50 during 10
    API_SSCSCreateCase.inject(rampUsers(10) during (1 minutes)), //50 during 10
    API_CMCCreateCase.inject(rampUsers(10) during (1 minutes)), //50 during 10
    API_DivorceCreateCase.inject(rampUsers(10) during (1 minutes)), //50 during 10
    API_IACCreateCase.inject(rampUsers(10) during (1 minutes)), //50 during 10

    //CCD UI scenarios
    UI_CCDProbateScenario.inject(rampUsers(10) during (1 minutes)),
    UI_CCDSSCSScenario.inject(rampUsers(10) during (1 minutes)),
    UI_CCDCMCScenario.inject(rampUsers(10) during (1 minutes)),

    // //Case Activity Requests
    CaseActivityScn.inject(rampUsers(10) during (3 minutes)), 

    // //CCD Searches
    CCDSearchView.inject(rampUsers(10) during (2 minutes)), 
    CCDElasticSearch.inject(rampUsers(10) during (2 minutes)) 
    )
  .maxDuration(10 minutes)
  .protocols(httpProtocol)
  
}