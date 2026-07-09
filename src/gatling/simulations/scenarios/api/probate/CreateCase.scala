package scenarios.api.probate

import ccd._
import io.gatling.core.Predef._
import utilities.DateUtils

object CreateCase {

  val feedProbateUserData = csv("ProbateUserData.csv").circular

  val execute =

  exec(_.set("todayDate", DateUtils.getDateNow("yyyy-MM-dd")))

  .feed(feedProbateUserData)
  .exec(CcdHelper.createCase("#{email}", "#{password}", CcdCaseTypes.PROBATE_GrantOfRepresentation, "applyForGrant", "bodies/probate/CCD_Probate_CreateCase.json"))
  .exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PROBATE_GrantOfRepresentation, "#{caseId}", "paymentSuccessApp", "bodies/probate/CCD_PaymentSuccess.json"))
  .exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PROBATE_GrantOfRepresentation, "#{caseId}", "boStopCaseForCasePrinted", "bodies/probate/CCD_Probate_StopCase.json"))
  .exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PROBATE_GrantOfRepresentation, "#{caseId}", "boAddCommentStop", "bodies/probate/CCD_AddComment.json"))
  .exec(CcdHelper.uploadDocumentToCdam("#{email}", "#{password}", CcdCaseTypes.PROBATE_GrantOfRepresentation.copy(microservice = "probate_backend"), "1MB.pdf"))
  .exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PROBATE_GrantOfRepresentation, "#{caseId}", "boUploadDocumentsStop", "bodies/probate/CCD_Probate_DocUpload.json"))
  .exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PROBATE_GrantOfRepresentation, "#{caseId}", "boResolveStop", "bodies/probate/CCD_ResolveStop.json"))
  .exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PROBATE_GrantOfRepresentation, "#{caseId}", "handleEvidence", "bodies/probate/CCD_SupplementaryEvidence.json"))
  .exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PROBATE_GrantOfRepresentation, "#{caseId}", "moveToCWEscalation", "bodies/probate/CCD_SMEReferral.json"))
  .exec(CcdHelper.addCaseEvent("#{email}", "#{password}", CcdCaseTypes.PROBATE_GrantOfRepresentation, "#{caseId}", "attachScannedDocs", "bodies/probate/CCD_AttachScannedDocs.json"))
}
