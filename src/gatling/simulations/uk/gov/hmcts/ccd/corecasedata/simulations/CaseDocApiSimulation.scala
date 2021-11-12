package uk.gov.hmcts.ccd.corecasedata.simulations

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._

import scala.concurrent.duration._

class CaseDocApiSimulation extends Simulation  {

  //Gatling specific configs, required for perf testing
  val BaseURL = Environment.baseURL
  val config: Config = ConfigFactory.load()
  val caseDocUsers = csv("CMCUserData.csv").circular
  val caseDocCases = csv("casedocdata/ProbateCaseIds.csv").queue

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")

  val tenfilesimulation = scenario("Case Doc API Upload & Download")
    .repeat(1) {
      exec(casedocapi.S2SLogin)
      .exec(casedocapi.idamLogin)
      .repeat(1) { //22
        exec(casedocapi.caseDocUpload)
        // .exec(casedocapi.addDocToCase)
        .exec(casedocapi.caseDocDownload)
      }
    }

  setUp(
    tenfilesimulation.inject(rampUsers(1) during (10 minutes)) //60 during 10
  )
  .protocols(httpProtocol)

}