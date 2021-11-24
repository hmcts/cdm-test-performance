package uk.gov.hmcts.ccd.corecasedata.simulations

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._ //comment out for VM runs, only required for proxy
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
import scala.concurrent.duration._

class CCD_StressTest extends Simulation  {

  //Iteration Settings
  val api_probateIteration = 1600 //40
  val api_sscsIteration = 1600 //40
  val api_divorceIteration = 1600 //40
  val api_iacIteration = 1600 //40
  val api_fplIteration = 1600 //40
  val api_frIteration = 1600 //40
  val api_cmcIteration = 1600 //40

  val ui_PBiteration = 600 //15
  val ui_SSCSiteration = 600 //15
  val ui_CMCiteration = 600 //15

  val caseActivityTotalRepeat = 200 //5
  val caseActivityIteration = 120
  val caseActivityListIteration = 12

  val ccdSearchIteration = 1400 //35
  val elasticSearchIteration = 3600 //90

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

  The below scenarios are required for CCD Stress Performance Testing

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

  val API_DivorceSolicitorCreateCase = scenario("Divorce Solicitor Case Create")
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

  //CCD UI Requests
  val UI_CCDProbateScenario = scenario("CCDPB")
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
        .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
  }

  val UI_CCDSSCSScenario = scenario("CCDSSCS")
    .repeat(1) {
     exec(Browse.Homepage)
      .exec(SSCS.SSCSLogin)
      .repeat(ui_SSCSiteration) {
        exec(SSCS.SSCSCreateCase)
        .exec(SSCS.SSCSDocUpload)
        .exec(SSCS.SSCSSearchAndView)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
    }

  val UI_CCDCMCScenario = scenario("CCDCMC")
    .repeat(1) {
      exec(Browse.Homepage)
      .exec(CMC.CMCLogin)
      .repeat(ui_CMCiteration) {
        exec(CMC.CMCCreateCase)
        .exec(CMC.CMCStayCase)
        // .exec(CMC.CMCAttachScannedDocs)
        .exec(CMC.CMCSupportUpdate)
        .exec(CMC.CMCSearchAndView)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
  }

  //CCD Case Activity Requests
  val CaseActivityScn = scenario("CCD Case Activity Requests")
    .repeat(1) {
      exec(ccdcaseactivity.CDSGetRequest)
      .repeat(caseActivityTotalRepeat) {
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

  setUp(
    API_DivorceSolicitorCreateCase.inject(
      incrementConcurrentUsers(100)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),
    API_CMCCreateCase.inject(
      incrementConcurrentUsers(50)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),
    API_ProbateCreateCase.inject(
      incrementConcurrentUsers(50)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),
    API_SSCSCreateCase.inject(
      incrementConcurrentUsers(50)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),
    // API_IACCreateCase.inject(
    //   incrementConcurrentUsers(1)
    //     .times(40)
    //     .eachLevelLasting(10.minutes)
    //     .separatedByRampsLasting(2.minutes)
    //     .startingFrom(10)),
    // API_FRCreateCase.inject(
    //   incrementConcurrentUsers(1)
    //     .times(40)
    //     .eachLevelLasting(10.minutes)
    //     .separatedByRampsLasting(2.minutes)
    //     .startingFrom(10)),
    // API_FPLCreateCase.inject(
    //   incrementConcurrentUsers(1)
    //     .times(40)
    //     .eachLevelLasting(10.minutes)
    //     .separatedByRampsLasting(2.minutes)
    //     .startingFrom(10)),
    

    UI_CCDProbateScenario.inject(
      incrementConcurrentUsers(50)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),
    // UI_CCDSSCSScenario.inject(
    //   incrementConcurrentUsers(1)
    //     .times(40)
    //     .eachLevelLasting(10.minutes)
    //     .separatedByRampsLasting(2.minutes)
    //     .startingFrom(10)),
    // UI_CCDCMCScenario.inject(
    //   incrementConcurrentUsers(1)
    //     .times(40)
    //     .eachLevelLasting(10.minutes)
    //     .separatedByRampsLasting(2.minutes)
    //     .startingFrom(10)),
    
    CaseActivityScn.inject(
      incrementConcurrentUsers(50)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),

    CCDSearchView.inject(
      incrementConcurrentUsers(50)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),
    CCDElasticSearch.inject(
      incrementConcurrentUsers(50)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)))

  //This used for debugging only
  /*setUp(
    
    API_ProbateSolicitorCreate.inject(rampUsers(1) during (1 minutes))
  )*/
  .protocols(httpProtocol)
  .maxDuration(360 minutes)
}