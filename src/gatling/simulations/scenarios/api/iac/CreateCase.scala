package scenarios.api.iac

import ccd._
import io.gatling.core.Predef._
import utilities.DateUtils

import scala.util.Random

object CreateCase {

  val feedIACUserData = csv("IACUserData.csv").circular

  val rnd = new Random()

  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  //Used to override the microservice for this case type set within the common-performance repo
  val IA_Asylum = CcdCaseTypes.IA_Asylum.copy(microservice = "iac")

  val execute =

    exec(_.setAll("randomString" -> randomString(5),
      "dob" -> DateUtils.getDatePastRandom("yyyy-MM-dd", minYears = 20, maxYears = 50),
      "todayDate" -> DateUtils.getDateNow("yyyy-MM-dd")))

      .feed(feedIACUserData)
      .exec(CcdHelper.uploadDocumentToCdam("#{email}", "#{password}", IA_Asylum, "1MB.pdf"))
      .exec(CcdHelper.createCase("#{email}", "#{password}", CcdCaseTypes.IA_Asylum, "startAppeal", "bodies/iac/IACCreateCase.json"))
      .exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.IA_Asylum, "#{caseId}", "submitAppeal", "bodies/iac/IACSubmitAppeal.json"))
}