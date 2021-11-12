package uk.gov.hmcts.ccd.corecasedata.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.http.Predef._ //comment out for VM runs, only required for proxy
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
import scala.concurrent.duration._

class CCDUIPTSimulation extends Simulation  {

  val config: Config = ConfigFactory.load()
  val BaseURL = Environment.baseURL

  val PBiteration = 7 //7
  val SSCSiteration = 14 //14
  val CMCiteration = 8 //8
  val Diviteration = 15 //15
  val Fpliteration = 10 //10

  val Ethositeration = 26 //26
  val LFUiteration = 10 //8
  val csIterationLarge = 200
  val csIterationSmall = 200
  val caseActivityIteration = 120
  val caseActivityListIteration = 12
  val xuiCaseListActivityIteration = 12 //12
  val xuiCaseActivityIteration = 120 //120

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .inferHtmlResources().silentResources
    // .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080)) //Comment out for VM runs
    .doNotTrackHeader("1")

  val CCDProbateScenario = scenario("CCDPB")
    .repeat(1) {
      exec(Browse.Homepage)
      .exec(PBGoR.submitLogin)
      .repeat(PBiteration) {
        exec(PBGoR.PBCreateCase)
        .exec(PBGoR.PBCaseActivity)
        .exec(PBGoR.PBPaymentSuccessful)
        .exec(PBGoR.PBCaseActivity)
        .exec(PBGoR.PBDocUpload)
        .exec(PBGoR.PBCaseActivity)
        // .exec(PBGoR.PBStopCase)
        // .exec(PBGoR.PBCaseActivity)
        .exec(PBGoR.PBSearch)
        .exec(PBGoR.PBView)
        .exec(PBGoR.PBCaseActivity)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
  }

  val CCDSSCSScenario = scenario("CCDSSCS")
    .repeat(1) {
     exec(Browse.Homepage)
      .exec(SSCS.SSCSLogin)
      .repeat(SSCSiteration) {
        exec(SSCS.SSCSCreateCase)
        .exec(SSCS.SSCSCaseActivity)
        .exec(SSCS.SSCSDocUpload)
        .exec(SSCS.SSCSCaseActivity)
        .exec(SSCS.SSCSSearchAndView)
        .exec(SSCS.SSCSCaseActivity)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
    }

  val CCDCMCScenario = scenario("CCDCMC")
    .repeat(1) {
      exec(Browse.Homepage)
      .exec(CMC.CMCLogin)
      .repeat(CMCiteration) {
        exec(CMC.CMCCreateCase)
        .exec(CMC.CMCCaseActivity)
        .exec(CMC.CMCStayCase)
        .exec(CMC.CMCCaseActivity)
        .exec(CMC.CMCWaitingTransfer)
        .exec(CMC.CMCCaseActivity)
        //.exec(CMC.CMCTransfer)
        // .exec(CMC.CMCAttachScannedDocs)
        .exec(CMC.CMCCaseActivity)
        .exec(CMC.CMCSupportUpdate)
        .exec(CMC.CMCCaseActivity)
        .exec(CMC.CMCSearchAndView)
        .exec(CMC.CMCCaseActivity)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
  }

  val CCDDivScenario = scenario("CCDDIV")
    .repeat(1) {
      exec(Browse.Homepage)
        .exec(DVExcep.submitLogin)
        .repeat(Diviteration) {
          exec(DVExcep.DVCreateCase)
          .exec(DVExcep.DVDocUpload)
          .exec(DVExcep.DVUpdateContactDetails)
          .exec(DVExcep.DVSearch)
          .exec(DVExcep.DVView)
          .exec(WaitforNextIteration.waitforNextIteration)
        }
        .exec(Logout.ccdLogout)
    }

  val CCDFPLScenario = scenario("CCDFPL")
    .repeat(1) {
      exec(Browse.Homepage)
        .exec(FPL.FPLLogin)
        .repeat(Fpliteration) {
          exec(FPL.FPLCreateCase)
          .exec(FPL.FPLDocumentUpload)
          .exec(WaitforNextIteration.waitforNextIteration)
        }
        .exec(Logout.ccdLogout)
    }

  val CCDEthosScenario = scenario("CCDEthos")
    .repeat(1) {
      exec(Browse.Homepage)
      .exec(EthosSearchView.submitLogin)
      .repeat(Ethositeration) {
        exec(EthosSearchView.Search)
        .exec(EthosSearchView.OpenCase)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
  }

  val CCDLargeFileUpload = scenario("CCDLFU")
      .repeat(1) {
        exec(Browse.Homepage)
        .exec(LargeFileUpload.LFULogin)
        .repeat(LFUiteration) {
          exec(LargeFileUpload.LFU_CreateCase)
          .exec(LargeFileUpload.LFUDocUpload)
          .exec(LargeFileUpload.LFUSearchAndView)
          .exec(WaitforNextIteration.waitforNextIteration)
          }
          .exec(Logout.ccdLogout)
      }

  val UserProfileSearch = scenario("CCDUP")
      .repeat(20) {
        exec(GetUserProfile.SearchJurisdiction)
        .exec(GetUserProfile.SearchAllUsers)
        .exec(WaitforNextIteration.waitforNextIteration)
      }

  val ProbateSearch = scenario("Probate CW Search")
      .repeat(1) {
        exec(Browse.Homepage)
        .exec(PBGoR.submitLogin)
        .repeat(Ethositeration) {
          exec(PBGoR.PBSearch)
          .exec(WaitforNextIteration.waitforNextIteration)
        }
      }

  val DivorceSearch = scenario("Divorce CW Search")
      .repeat(1) {
        exec(Browse.Homepage)
        .exec(DVExcep.submitLogin)
        .repeat(Ethositeration) {
          exec(DVExcep.DVSearch)
          .exec(WaitforNextIteration.waitforNextIteration)
        }
      }

  val XUIAdminScn = scenario("XUI Admin Org Login")
    .repeat(1){
      exec(ExuiView.XUIAdminOrg)
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

  val CaseActivityListScn = scenario("CCD Case Activity List Requests")
    .repeat(1) {
      exec(ccdcaseactivity.CDSGetRequest)
      .repeat(caseActivityListIteration) {
        exec(ccdcaseactivity.CaseActivityList)
      }
    }

  val XUICaseActivityScn = scenario("XUI - Case Activity Requests")
    .repeat(1) {
      exec(ExuiView.manageCasesHomePage)
      .exec(ExuiView.manageCaseslogin)
      .repeat(15) { //15
        repeat(xuiCaseListActivityIteration) {
          exec(ExuiView.CaseActivityList)
        }
        // .exec(ExuiView.CaseActivityOpenCase)
        .repeat(xuiCaseActivityIteration) {
          exec(ExuiView.CaseActivitySingle)
        }
      }
      .exec(ExuiView.XUILogout)
    }

  val CCDCaseActivityScn = scenario("CCD - UI Case Activity Requests")
    .repeat(1)
    {
      exec(Browse.Homepage)
      .exec(CaseActivity.submitLogin)
      .repeat(15){
        repeat(xuiCaseListActivityIteration) {
          exec(CaseActivity.CaseActivityList)
        }
        .repeat(xuiCaseActivityIteration) {
          exec(CaseActivity.CaseActivitySingle)
        }
      }
      .exec(Logout.ccdLogout)
    }


  //CCD Regression UI Scenario
  setUp(
    //These 5 scenarios required for CCD regression testing (case activity added 28/01/2021)
    // CCDProbateScenario.inject(rampUsers(150) during (20 minutes)), //150
    // CCDSSCSScenario.inject(rampUsers(150) during (20 minutes)), //150
    // CCDEthosScenario.inject(rampUsers(400) during (20 minutes)), //400
    // CCDCMCScenario.inject(rampUsers(150) during (20 minutes)), //150
    // CCDDivScenario.inject(rampUsers(150) during (20 minutes)), //150
    // CaseActivityScn.inject(rampUsers(100) during (20 minutes)) //100

    // CaseActivityScn.inject(rampUsers(1000) during (20 minutes)) //100
    // XUICaseActivityScn.inject(rampUsers(600) during (20 minutes)), //1000
    CCDDivScenario.inject(rampUsers(1) during (20 minutes)).disablePauses //400

    // CCDCMCScenario.inject(rampUsers(1) during (1 minutes)), //150
    // CCDSSCSScenario.inject(rampUsers(1) during (1 minutes)),
  )
    .protocols(httpProtocol)
    .maxDuration(120 minutes)
}