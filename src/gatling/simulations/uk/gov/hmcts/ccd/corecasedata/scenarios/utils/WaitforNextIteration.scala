package scenarios.utils

import scala.concurrent.duration._
import io.gatling.core.Predef._

object WaitforNextIteration {

  val MinWaitForNextIteration = Environment.minWaitForNextIteration
  val MaxWaitForNextIteration = Environment.maxWaitForNextIteration
    
  val waitforNextIteration = pace(MinWaitForNextIteration.seconds, MaxWaitForNextIteration.seconds)
}
