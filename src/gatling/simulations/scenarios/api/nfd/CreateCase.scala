package scenarios.api.nfd

import ccd._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

	object CreateCase {

		val feedNFDUserData = csv("NFDUserData.csv").circular

		val execute =

			feed(feedNFDUserData)
			.exec(CcdHelper.createCase("#{email}", "#{password}", CcdCaseTypes.DIVORCE_NFD, "solicitor-create-application", "bodies/nfd/CCD_CreateNFDApp.json"))
			.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.DIVORCE_NFD, "#{caseId}", "solicitor-update-application", "bodies/nfd/CCD_UpdateNFDApp.json"))
			.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.DIVORCE_NFD, "#{caseId}", "solicitor-submit-application", "bodies/nfd/CCD_SubmitNFDApp.json"))

}
