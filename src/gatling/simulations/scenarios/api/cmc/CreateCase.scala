package scenarios.api.cmc

import ccd._
import io.gatling.core.Predef._

object CreateCase {

	val feedCMCUserData = csv("CMCUserData.csv").circular

	val execute =

		feed(feedCMCUserData)
		.exec(CcdHelper.createCase("#{email}", "#{password}", CcdCaseTypes.CMC_MoneyClaimCase, "CreateClaim", "bodies/cmc/CMC_CreateCase.json"))
		.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.CMC_MoneyClaimCase, "#{caseId}", "StayClaim", "bodies/cmc/CMC_StayClaim.json"))
		.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.CMC_MoneyClaimCase, "#{caseId}", "ClaimNotes", "bodies/cmc/CMC_ClaimNotes.json"))
		.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.CMC_MoneyClaimCase, "#{caseId}", "LiftStay", "bodies/cmc/CMC_LiftStay.json"))
		.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.CMC_MoneyClaimCase, "#{caseId}", "SupportUpdate", "bodies/cmc/CMC_SupportUpdate.json"))

}
