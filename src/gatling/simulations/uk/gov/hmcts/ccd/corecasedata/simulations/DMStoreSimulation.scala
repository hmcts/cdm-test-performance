package uk.gov.hmcts.ccd.corecasedata.simulations

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._ //comment out for VM runs, only required for proxy
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
import scala.concurrent.duration._

class DMStoreSimulation extends Simulation  {

  //Gatling specific configs, required for perf testing
  val BaseURL = Environment.baseURL
  val config: Config = ConfigFactory.load()

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")

  val tenfilesimulation = scenario("Dm Store Upload & Download")
    .repeat(1) {
      exec(dmstore.S2SLogin)
      .repeat(22) { //22
        exec(dmstore.API_DocUpload)
        .exec(dmstore.API_DocDownload)
      }
    }

    

  setUp(
    tenfilesimulation.inject(rampUsers(60) during (10 minutes)) //60 during 10
  )
  .protocols(httpProtocol)

}