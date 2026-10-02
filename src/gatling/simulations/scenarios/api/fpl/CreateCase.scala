package scenarios.api.fpl

import ccd._
import io.gatling.core.Predef._

object CreateCase {

	val feedFPLUserData = csv("FPLUserData.csv").circular

	val execute =

		feed(feedFPLUserData)
		.exec(CcdHelper.uploadDocumentToCdam("#{email}", "#{password}", CcdCaseTypes.PUBLICLAW_CARE_SUPERVISION_EPO.copy(microservice = "xui_webapp"), "1MB.pdf"))
		.exec(CcdHelper.createCase("#{email}", "#{password}", CcdCaseTypes.PUBLICLAW_CARE_SUPERVISION_EPO, "openCase", "bodies/fpl/FPLCreateCase.json"))
		.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PUBLICLAW_CARE_SUPERVISION_EPO, "#{caseId}", "enterChildren", "bodies/fpl/CCD_FPL_EnterChildren.json"))
		.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PUBLICLAW_CARE_SUPERVISION_EPO, "#{caseId}", "enterRespondents", "bodies/fpl/CCD_FPL_EnterRespondents.json"))
		.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PUBLICLAW_CARE_SUPERVISION_EPO, "#{caseId}", "enterGrounds", "bodies/fpl/CCD_FPL_EnterGrounds.json"))
//		.exec(CcdHelper.uploadDocumentToCdam("#{email}", "#{password}", CcdCaseTypes.PUBLICLAW_CARE_SUPERVISION_EPO.copy(microservice = "xui_webapp"), "1MB.pdf"))
		.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PUBLICLAW_CARE_SUPERVISION_EPO, "#{caseId}", "uploadDocuments", "bodies/fpl/CCD_FPL_UploadDocuments.json"))
		.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PUBLICLAW_CARE_SUPERVISION_EPO, "#{caseId}", "ordersNeeded", "bodies/fpl/CCD_FPL_OrdersNeeded.json"))
		.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PUBLICLAW_CARE_SUPERVISION_EPO, "#{caseId}", "hearingNeeded", "bodies/fpl/CCD_FPL_HearingNeeded.json"))
		.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PUBLICLAW_CARE_SUPERVISION_EPO, "#{caseId}", "enterLocalAuthority", "bodies/fpl/CCD_FPL_LocalAuthority.json"))
		.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PUBLICLAW_CARE_SUPERVISION_EPO, "#{caseId}", "otherProposal", "bodies/fpl/CCD_FPL_OtherProposal.json"))
		.exec(CcdHelper.getCase("#{email}", "#{password}", CcdCaseTypes.PUBLICLAW_CARE_SUPERVISION_EPO, "#{caseId}"))
//		.exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PUBLICLAW_CARE_SUPERVISION_EPO, "#{caseId}", "submitApplication", "bodies/fpl/CCD_FPL_OtherProposal.json"))

}
