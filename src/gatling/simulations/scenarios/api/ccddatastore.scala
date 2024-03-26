package scenarios.api

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios.utils._
import scala.concurrent.duration._
import java.io.{BufferedWriter, FileWriter}
import scala.util.Random

object ccddatastore {

  val config: Config = ConfigFactory.load()

  val ccdDataStoreUrl = "http://ccd-data-store-api-#{env}.service.core-compute-#{env}.internal"
  val CaseDocAPI = Environment.caseDocUrl
  val ccdScope = "openid profile authorities acr roles openid profile roles"
  val feedCaseSearchData = csv("caseSearchData.csv").random
  val feedEthosSearchData = csv("EthosSearchData.csv").random

  val constantThinkTime = Environment.constantthinkTime

  val patternDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val now = LocalDate.now()

  private val rng: Random = new Random()
  private def niNumber(): String = rng.alphanumeric.filter(_.isDigit).take(8).mkString
  private def firstName(): String = rng.alphanumeric.filter(_.isLetter).take(10).mkString
  private def lastName(): String = rng.alphanumeric.filter(_.isLetter).take(10).mkString

  val headers_0 = Map( //Authorization token needs to be generated with idam login
    "Authorization" -> "AdminApiAuthToken ",
    "Content-Type" -> "application/json")

  val CCDAPI_ProbateCreate = 

    exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/event-triggers/applyForGrant/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Provate_ValidateCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_Probate_CreateCase.json")))

    .exec(http("API_Probate_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_Probate_CreateCase.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ProbateCaseEvents =

    exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/paymentSuccessApp/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_Probate_ValidatePaymentSuccessful")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_PaymentSuccess.json")))

    .exec(http("API_Probate_PaymentSuccessful")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_PaymentSuccess.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/boStopCaseForCasePrinted/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken4")))

    .exec(http("API_Probate_ValidateStopCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_Probate_StopCase.json"))
      .check(substring("boCaseStopReasonList")))

    .exec(http("API_Probate_StopCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_Probate_StopCase.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .exec {
      session =>
        println(session("caseId").as[String])
        session
    }

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/boAddCommentStop/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Probate_ValidateAddComment")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_AddComment.json")))

    .exec(http("API_Probate_AddComment")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_AddComment.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/boUploadDocumentsStop/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(session => {
      session.set("FileName1", "1MB.pdf")
    })

    .exec(http("API_Probate_DocUploadProcess")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{access_token}")
      .header("ServiceAuthorization", "#{probate_backendBearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "#{CaseType}")
      .formParam("jurisdictionId", "#{Jurisdiction}")
      .bodyPart(RawFileBodyPart("files", "#{FileName1}")
        .fileName("#{FileName1}")
        .transferEncoding("binary"))
      .check(regex("""http://(.+)/""").saveAs("DMURL"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken")))

    .exec(http("API_Probate_ValidateDocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_Probate_DocUpload.json")))

    .exec(http("API_Probate_DocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_Probate_DocUpload.json")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/boResolveStop/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken"))
      .check(jsonPath("$.case_details.case_data.boCaseStopReasonList[0].id").saveAs("caseStopId")))

    .exec(http("API_Probate_ValidateResolveStop")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_ResolveStop.json")))

    .exec(http("API_Probate_ResolveStop")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_ResolveStop.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/handleEvidence/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Probate_ValidateSupplementaryEvidence")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_SupplementaryEvidence.json")))

    .exec(http("API_Probate_SupplementaryEvidence")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_SupplementaryEvidence.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/moveToCWEscalation/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Probate_ValidateSMEReferral")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_SMEReferral.json")))

    .exec(http("API_Probate_SMEReferral")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_SMEReferral.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_Probate_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/attachScannedDocs/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Probate_ValidateAttachScannedDocs")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_AttachScannedDocs.json")))

    .exec(http("API_Probate_AttachScannedDocs")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/probate/CCD_AttachScannedDocs.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_SSCSCreate =

    exec(http("API_SSCS_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/event-triggers/validAppealCreated/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(_.setAll(
        ("NINumber", niNumber()),
        ("firstname", firstName()),
        ("lastname", lastName())
    ))

    .exec(http("API_SSCS_ValidateCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_CreateValidAppeal.json")))

    .exec(http("API_SSCS_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_CreateValidAppeal.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_SSCSCaseEvents =

    exec(http("API_SSCS_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/uploadDocument/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(session => {
      session.set("FileName1", "1MB.pdf")
    })

    .exec(http("API_SSCS_DocUploadProcess")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{access_token}")
      .header("ServiceAuthorization", "#{sscsBearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "Benefit") 
      .formParam("jurisdictionId", "SSCS") 
      .bodyPart(RawFileBodyPart("files", "#{FileName1}")
        .fileName("#{FileName1}")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken")))

    .exec(http("API_SSCS_ValidateDocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_DocUpload.json")))

    .exec(http("API_SSCS_DocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_DocUpload.json")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_SSCS_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/addHearing/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken4")))

    .exec(http("API_SSCS_ValidateAddHearing")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_AddHearing.json")))

    .exec(http("API_SSCS_AddHearing")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_AddHearing.json")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_SSCS_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/dwpActionDirection/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken5")))

    .exec(http("API_SSCS_ValidateActionDirection")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_ActionDirection.json")))

    .exec(http("API_SSCS_ActionDirection")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/sscs/CCD_SSCS_ActionDirection.json")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_CMCCreate =

    exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/event-triggers/CreateClaim/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_CMC_ValidateCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/cmc/CMC_CreateCase.json")))

    .exec(http("API_CMC_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/cmc/CMC_CreateCase.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_CMCUploadDoc = 

    exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/ReviewedPaperResponse/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("#.token").saveAs("eventToken1")))

    .exec(_.setAll(  
      "FileName1"  -> ("1MB.pdf"),
      "FileName2"  -> ("1MB2.pdf"),
      "currentDate" -> now.format(patternDate)
    ))

    .exec(http("API_CMC_DocUploadProcess1")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{access_token}")
      .header("ServiceAuthorization", "#{cmc_claim_storeBearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "MoneyClaimCase")  //#{CaseType}
      .formParam("jurisdictionId", "CMC")  //#{Jurisdiction}
      .bodyPart(RawFileBodyPart("files", "#{FileName1}")
        .fileName("#{FileName1}")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID1"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken1")))

    .exec(http("API_CMC_DocUploadProcess2")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{access_token}")
      .header("ServiceAuthorization", "#{cmc_claim_storeBearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "MoneyClaimCase")  //#{CaseType}
      .formParam("jurisdictionId", "CMC")  //#{Jurisdiction}
      .bodyPart(RawFileBodyPart("files", "#{FileName2}")
        .fileName("#{FileName2}")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID2"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken2")))

    .exec(http("API_CMC_ValidateDocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/cmc/CMC_DocUpload.json")))

    .exec(http("API_CMC_DocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/cmc/CMC_DocUpload.json")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_CMCCaseHandedToCCBC =

    exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/CaseMovedOffline/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken1")))

    .exec(http("API_CMC_ValidateHandedToCCBC")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/CMC/CMC_HandedToCCBC.json")))

    .exec(http("API_CMC_HandedToCCBC")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/CMC/CMC_HandedToCCBC.json")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_CMCCaseEvents =

    exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/StayClaim/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken1")))

    .exec(http("API_CMC_ValidateCaseStayed")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/cmc/CMC_StayClaim.json")))

    .exec(http("API_CMC_CaseStayed")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/cmc/CMC_StayClaim.json"))
      .check(jsonPath("$.state").is("stayed")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/ClaimNotes/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_CMC_ValidateClaimNotes")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/cmc/CMC_ClaimNotes.json")))

    .exec(http("API_CMC_ClaimNotes")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/cmc/CMC_ClaimNotes.json")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/LiftStay/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken5")))

    .exec(http("API_CMC_ValidateLiftStay")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/cmc/CMC_LiftStay.json")))

    .exec(http("API_CMC_LiftStay")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/cmc/CMC_LiftStay.json")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_CMC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/SupportUpdate/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken5")))

    .exec(http("API_CMC_ValidateSupportUpdate")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/cmc/CMC_SupportUpdate.json")))

    .exec(http("API_CMC_SupportUpdate")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/cmc/CMC_SupportUpdate.json")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_DivorceSolicitorCreate =

    exec(http("API_Divorce_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/event-triggers/solicitorCreate/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Divorce_ValidateSolCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/divorce/CCD_DivorceCreateSol.json")))

    .exec(http("API_Divorce_SolCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/divorce/CCD_DivorceCreateSol.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_DivorceSolicitorCaseEvents = 

    exec(http("API_Divorce_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/UpdateLanguage/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_Divorce_ValidateSolUpdateLanguage")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/divorce/CCD_Divorce_UpdateLanguage.json")))

    .exec(http("API_Divorce_SolUpdateLanguage")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/divorce/CCD_Divorce_UpdateLanguage.json")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_Divorce_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/solicitorStatementOfTruthPaySubmit/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_Divorce_ValidateCaseSubmission")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/divorce/CCD_Divorce_UpdateLanguage.json")))

    .exec(http("API_Divorce_CaseSubmission")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/divorce/CCD_Divorce_UpdateLanguage.json")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_DivorceNFDCreate = 

    exec(http("API_NFD_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/event-triggers/solicitor-create-application/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_NFD_SolCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/nfd/CCD_CreateNFDApp.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

  val CCDAPI_IACCreate =

    exec(http("API_IAC_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/event-triggers/startAppeal/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_IAC_ValidateCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/iac/IACCreateCase.json")))

    .exec(http("API_IAC_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/iac/IACCreateCase.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

  val CCDAPI_FPLCreate =

    exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/event-triggers/openCase/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_FPL_ValidateCreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("FPLCreateCase.json")))

    .exec(http("API_FPL_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("FPLCreateCase.json"))//.asJson
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_FPLCaseEvents = 

    exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/enterChildren/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken1")))

    .exec(http("API_FPL_ValidateAddChildren")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterChildren.json")))

    .exec(http("API_FPL_AddChildren")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterChildren.json")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/enterRespondents/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_FPL_ValidateEnterRespondents")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterRespondents.json")))

    .exec(http("API_FPL_EnterRespondents")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterRespondents.json")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/enterGrounds/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(http("API_FPL_ValidateEnterGrounds")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterGrounds.json")))

    .exec(http("API_FPL_EnterGrounds")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_EnterGrounds.json")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/uploadDocuments/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken4")))

     .exec(session => {
      session.set("FileName1", "1MB.pdf")
    })

    .exec(http("API_FPL_DocUploadProcess")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{access_token}")
      .header("ServiceAuthorization", "#{xui_webappBearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "#{CaseType}")
      .formParam("jurisdictionId", "#{Jurisdiction}")
      .bodyPart(RawFileBodyPart("files", "#{FileName1}")
        .fileName("#{FileName1}")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken")))

    .exec(http("API_FPL_ValidateDocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_UploadDocuments.json")))

    .exec(http("API_FPL_DocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_UploadDocuments.json")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/ordersNeeded/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken5")))
    
    .exec(http("API_FPL_ValidateOrdersNeeded")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_OrdersNeeded.json")))

    .exec(http("API_FPL_OrdersNeeded")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_OrdersNeeded.json")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/hearingNeeded/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken6")))

    .exec(http("API_FPL_ValidateHearingNeeded")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_HearingNeeded.json")))

    .exec(http("API_FPL_HearingNeeded")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_HearingNeeded.json")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/enterLocalAuthority/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken7")))

    .exec(http("API_FPL_ValidateEnterLocalAuthority")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_LocalAuthority.json")))

    .exec(http("API_FPL_EnterLocalAuthority")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_LocalAuthority.json")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/otherProposal/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken3")))

    .exec(http("API_FPL_ValidateOtherProposal")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_OtherProposal.json")))

    .exec(http("API_FPL_OtherProposal")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")      
      .body(ElFileBody("bodies/fpl/CCD_FPL_OtherProposal.json")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_FPL_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/submitApplication/token")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken8"))
      .check(jsonPath("$.case_details.case_data.draftApplicationDocument.document_url").saveAs("documentUrl"))
      .check(jsonPath("$.case_details.case_data.draftApplicationDocument.document_filename").saveAs("documentFilename")))

    .exec(http("API_FPL_ValidateSubmitApplication")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/fpl/CCD_FPL_SubmitApplication.json")))

    .exec(http("API_FPL_SubmitApplication")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/fpl/CCD_FPL_SubmitApplication.json")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_EthosJourney =

    feed(feedEthosSearchData)

    .exec(http("CCD_EthosSearch")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases?case_reference=#{EthosCaseRef}")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(status in (200)))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("CCD_EthosViewCase")
      .get(ccdDataStoreUrl + "/cases/#{EthosCaseRef}")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .header("Experimental","true")
      .check(status in (200)))

    .pause(Environment.constantthinkTime.seconds)

  val GetAssignedUsers = 

    exec(http("AAC_010_GetAssignedUsersAndRoles")
      .get(ccdDataStoreUrl + "/case-users")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("experimental","true")
      .queryParam("case_ids", "#{caseToShare}"))

    .pause(Environment.constantthinkTime.seconds)

}