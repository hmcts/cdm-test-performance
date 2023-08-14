package simulations

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import scenarios.utils._
import scenarios.api._
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
      .repeat(1) { //22
        exec(dmstore.API_DocUpload)
        .exec(dmstore.API_DocDownload)
      }
    }

  setUp(
    tenfilesimulation.inject(rampUsers(1) during (10.minutes)) //60 during 10
  )
  .protocols(httpProtocol)

}