package scenarios.api.iac

import ccd._
import io.gatling.core.Predef._
import utilities.{DateUtils, StringUtils}

object CreateCase {

  val feedIACUserData = csv("IACUserData.csv").circular

  val execute =

    exec(_.setAll("randomString" -> StringUtils.randomString(5),
      "dob" -> DateUtils.getDatePastRandom("yyyy-MM-dd", minYears = 20, maxYears = 50),
      "todayDate" -> DateUtils.getDateNow("yyyy-MM-dd")))

    .feed(feedIACUserData)
    .exec(CcdHelper.uploadDocumentToCdam("#{email}", "#{password}", CcdCaseTypes.IA_Asylum.copy(microservice = "xui_webapp"), "1MB.pdf"))
    .exec(CcdHelper.createCase("#{email}", "#{password}", CcdCaseTypes.IA_Asylum, "startAppeal", "bodies/iac/IACCreateCase.json"))
    .exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.IA_Asylum, "#{caseId}", "submitAppeal", "bodies/iac/IACSubmitAppeal.json"))
}