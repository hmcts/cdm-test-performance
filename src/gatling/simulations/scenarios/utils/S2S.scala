package scenarios.utils

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object S2S {

  val config: Config = ConfigFactory.load()
  val s2sUrl = Environment.s2sUrl

  //microservice is a string defined in the Simulation and passed into the body below
  def s2s(microservice: String) = {

    exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody(s"""{"microservice":"${microservice}"}"""))
      .check(bodyString.saveAs(s"${microservice}BearerToken")))
      .exitHereIfFailed

  }
}