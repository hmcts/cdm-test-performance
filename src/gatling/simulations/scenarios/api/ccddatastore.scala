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

    // .exec {
    //   session =>
    //     println(session("caseId").as[String])
    //     session
    // }

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

    .exec(session => {
      session.set("FileName1", "1MB.pdf")
    })

    .exec(http("API_IAC_DocUploadProcess")
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
      .check(regex("""http://(.+)/""").saveAs("DMURL"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken")))

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
      .body(ElFileBody("bodies/fpl/FPLCreateCase.json")))

    .exec(http("API_FPL_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases")
      .header("ServiceAuthorization", "Bearer #{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/fpl/FPLCreateCase.json"))//.asJson
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

  val CCDAPI_SpecialTribunalsCreate =

    exec(http("API_SpTribs_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/event-triggers/caseworker-create-case/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_SpTribs_ValidateCategorisation")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/st/CCD_STCategorisation.json"))
      .check(substring("cicCaseCaseCategory")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_SpTribs_ValidateDate")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/st/CCD_STDate.json"))
      .check(substring("cicCaseCaseReceivedDate")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_SpTribs_ValidateSubjects")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/st/CCD_STSubjects.json"))
      .check(substring("cicCasePartiesCIC")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_SpTribs_ValidateDetails")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/st/CCD_STDetails.json"))
      .check(substring("cicCaseDateOfBirth")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_SpTribs_ValidateApplicantDetails")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/st/CCD_STApplicantDetails.json"))
      .check(substring("cicCaseApplicantFullName")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_SpTribs_ValidateRepDetails")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/st/CCD_STRepDetails.json"))
      .check(substring("cicCaseRepresentativeFullName")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_SpTribs_ValidateContacts")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/st/CCD_STContacts.json"))
      .check(substring("cicCaseSubjectCIC")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("GetS2SToken")
      .post(Environment.s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("""{"microservice":"xui_webapp"}"""))
      .check(bodyString.saveAs("cdamS2sToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_SpTribs_CDAMDocUpload")
      .post(CaseDocAPI + "/cases/documents")
      .header("Authorization", "Bearer #{access_token}")
      .header("ServiceAuthorization", "#{cdamS2sToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "CriminalInjuriesCompensation")
      .formParam("jurisdictionId", "ST_CIC")
      .bodyPart(RawFileBodyPart("files", "1MB.pdf")
        .fileName("1MB.pdf")
        .transferEncoding("binary"))
      .check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_SpTribs_ValidateDocUpload")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/st/CCD_STDocUpload.json"))
      .check(substring("cicCaseCaseDocumentsUpload")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_SpTribs_ValidateFurtherDetails")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/st/CCD_STFurtherDetails.json"))
      .check(substring("cicCaseSchemeCic")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_SpTribs_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/st/CCD_STCreateCase.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_SpecialTribunalsBuildCaseEvent =

    exec(http("API_ST_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/caseworker-case-built/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_ST_ValidateBuildCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/validate")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/st/CCD_STBuildCaseValidate.json"))
      .check(substring("data")))

    .exec(http("API_ST_BuildCase")
      .post(ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/st/CCD_STBuildCase.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_SpecialTribunalsViewCase =

    exec(http("CCD_STViewCase")
      .get(ccdDataStoreUrl + "/cases/#{caseId}")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .header("Experimental","true")
      .check(jsonPath("$.id").is("#{caseId}")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_SpecialTribunalsGetEvents =

    exec(http("CCD_STGetEvents")
      .get(ccdDataStoreUrl + "/cases/#{caseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .header("Experimental","true")
      .check(substring("auditEvents")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ET1CaseworkerCreate =

    exec(http("API_ET_CW_GetEventToken")
      .get(ccdDataStoreUrl + "/internal/case-types/#{CaseType}/event-triggers/initiateCase?ignore-warning=false")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .check(jsonPath("$.event_token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_InitiateCase_Validate1")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=initiateCase1")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_InitiateCaseValidate1.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_InitiateCase_Validate2")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=initiateCase2")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_InitiateCaseValidate2.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_InitiateCase_Validate3")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=initiateCase3")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_InitiateCaseValidate3.json"))
      .check(jsonPath("$.data.respondentCollection[0].id").saveAs("respondentId")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_InitiateCase_Validate4")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=initiateCase4")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_InitiateCaseValidate4.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_InitiateCase_Validate7")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=initiateCase7")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_InitiateCaseValidate7.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_InitiateCase_Validate8")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=initiateCase8")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_InitiateCaseValidate8.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_InitiateCase_Validate9")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=initiateCase9")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_InitiateCaseValidate9.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_InitiateCase_Create")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/cases?ignore-warning=false")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_InitiateCase.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(session => {
      val caseId = session("caseId").asOption[String].getOrElse("NOT SET")
      println(s"ET1 Caseworker case created - caseId: $caseId")
      session
    })

  val CCDAPI_ET1SolicitorCreate =

    exec(http("API_ET_Solicitor_GetCreateToken")
      .get(ccdDataStoreUrl + "/internal/case-types/#{CaseType}/event-triggers/et1ReppedCreateCase?ignore-warning=false")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .check(jsonPath("$.event_token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_Solicitor_CreateCase")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/cases?ignore-warning=false")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_SolicitorCreateCase.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(session => {
      val caseId = session("caseId").asOption[String].getOrElse("NOT SET")
      println(s"ET1 Solicitor case created - caseId: $caseId")
      session
    })

  val CCDAPI_ET1SolicitorSectionOne =

    exec(http("API_ET_Solicitor_GetSectionOneToken")
      .get(ccdDataStoreUrl + "/internal/cases/#{caseId}/event-triggers/et1SectionOne?ignore-warning=false")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .check(jsonPath("$.event_token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_Solicitor_SectionOne")
      .post(ccdDataStoreUrl + "/cases/#{caseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_SolicitorSectionOne.json"))
      .check(status.is(201)))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ET1SolicitorSectionTwo =

    exec(http("API_ET_Solicitor_GetSectionTwoToken")
      .get(ccdDataStoreUrl + "/internal/cases/#{caseId}/event-triggers/et1SectionTwo?ignore-warning=false")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .check(jsonPath("$.event_token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_Solicitor_SectionTwo")
      .post(ccdDataStoreUrl + "/cases/#{caseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_SolicitorSectionTwo.json"))
      .check(status.is(201)))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ET1SolicitorSectionThree =

    exec(http("API_ET_Solicitor_GetSectionThreeToken")
      .get(ccdDataStoreUrl + "/internal/cases/#{caseId}/event-triggers/et1SectionThree?ignore-warning=false")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .check(jsonPath("$.event_token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_Solicitor_SectionThree")
      .post(ccdDataStoreUrl + "/cases/#{caseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_SolicitorSectionThree.json"))
      .check(status.is(201)))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ET1SolicitorSubmit =

    exec(http("API_ET_Solicitor_GetSubmitToken")
      .get(ccdDataStoreUrl + "/internal/cases/#{caseId}/event-triggers/submitEt1Draft?ignore-warning=false")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .check(jsonPath("$.event_token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_Solicitor_Submit")
      .post(ccdDataStoreUrl + "/cases/#{caseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_SolicitorSubmit.json"))
      .check(status.is(201)))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ET1CaseworkerVetting =

    exec(http("API_ET_CW_Et1Vetting_GetEventToken")
      .get(ccdDataStoreUrl + "/internal/cases/#{caseId}/event-triggers/et1Vetting?ignore-warning=false")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .check(jsonPath("$.event_token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_Et1Vetting_Validate1")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=et1Vetting1")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_Et1VettingValidate1.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_Et1Vetting_Validate2")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=et1Vetting2")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_Et1VettingValidate2.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_Et1Vetting_Validate3")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=et1Vetting3")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_Et1VettingValidate3.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_Et1Vetting_Validate4")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=et1Vetting4")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_Et1VettingValidate4.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_Et1Vetting_Validate5")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=et1Vetting5")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_Et1VettingValidate5.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_Et1Vetting_Validate6")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=et1Vetting6")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_Et1VettingValidate6.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_Et1Vetting_Validate7")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=et1Vetting7")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_Et1VettingValidate7.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_Et1Vetting_Validate8")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=et1Vetting8")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_Et1VettingValidate8.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_Et1Vetting_Validate9")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=et1Vetting9")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_Et1VettingValidate9.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_Et1Vetting_Validate10")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=et1Vetting10")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_Et1VettingValidate10.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_Et1Vetting_Validate11")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=et1Vetting11")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_Et1VettingValidate11.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_Et1Vetting_Validate12")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=et1Vetting12")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_Et1VettingValidate12.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_Et1Vetting_Validate13")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=et1Vetting13")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_Et1VettingValidate13.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_Et1Vetting_Event")
      .post(ccdDataStoreUrl + "/cases/#{caseId}/events?ignore-warning=false")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_Et1Vetting.json"))
      .check(jsonPath("$.state").is("Vetted")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ET1CaseworkerAcceptCase =

    exec(_.set("claimAcceptedDate", now.format(patternDate)))

    .exec(http("API_ET_CW_PreAcceptance_GetEventToken")
      .get(ccdDataStoreUrl + "/internal/cases/#{caseId}/event-triggers/preAcceptanceCase?ignore-warning=false")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .check(jsonPath("$.event_token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_PreAcceptance_Validate1")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=preAcceptanceCase1")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_PreAcceptanceCaseValidate1.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_PreAcceptance_Event")
      .post(ccdDataStoreUrl + "/cases/#{caseId}/events?ignore-warning=false")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_PreAcceptanceCase.json"))
      .check(jsonPath("$.state").is("Accepted")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ET1CaseworkerGenerateCorrespondence =

    exec(http("API_ET_CW_GenerateCorrespondence_GetEventToken")
      .get(ccdDataStoreUrl + "/internal/cases/#{caseId}/event-triggers/generateCorrespondence?ignore-warning=false")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .check(jsonPath("$.event_token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_GenerateCorrespondence_Validate1")
      .post(ccdDataStoreUrl + "/case-types/#{CaseType}/validate?pageId=generateCorrespondence1")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_GenerateCorrespondenceValidate1.json"))
      .check(substring("data")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_CW_GenerateCorrespondence_Event")
      .post(ccdDataStoreUrl + "/cases/#{caseId}/events?ignore-warning=false")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .body(ElFileBody("bodies/et/CCD_ET_CW_GenerateCorrespondence.json"))
      .check(jsonPath("$.state").is("Accepted"))
      .check(jsonPath("$.data.ethosCaseReference").saveAs("ethosId")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(session => {
      val ethosId = session("ethosId").asOption[String].getOrElse("NOT SET")
      val caseId  = session("caseId").asOption[String].getOrElse("NOT SET")
      println(s"ET1 case progression complete - ethosId: $ethosId, caseId: $caseId")
      session
    })

  val CCDAPI_ET1CitizenCreate =

    exec(http("API_ET_Citizen_GetInitiateToken")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/event-triggers/INITIATE_CASE_DRAFT/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_Citizen_InitiateCase")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_CitizenInitiate.json"))
      .check(jsonPath("$.id").saveAs("citizenCaseId")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ET1CitizenUpdate =

    // S1: personal info (DOB, address, phone)
    exec(http("API_ET_Citizen_GetUpdateToken_S1_PersonalInfo")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/event-triggers/UPDATE_CASE_DRAFT/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_ET_Citizen_Update_S1_PersonalInfo")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Citizen_S1_PersonalInfo.json"))
      .check(jsonPath("$.id").saveAs("citizenCaseId")))

    .pause(Environment.constantthinkTime.seconds)

    // S1: contact preferences, hearing preferences, reasonable adjustments
    .exec(http("API_ET_Citizen_GetUpdateToken_S1_ContactPrefs")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/event-triggers/UPDATE_CASE_DRAFT/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_ET_Citizen_Update_S1_ContactPrefs")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Citizen_S1_ContactPrefs.json"))
      .check(jsonPath("$.id").saveAs("citizenCaseId")))

    .pause(Environment.constantthinkTime.seconds)

    // S1: section check
    .exec(http("API_ET_Citizen_GetUpdateToken_S1_Check")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/event-triggers/UPDATE_CASE_DRAFT/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_ET_Citizen_Update_S1_Check")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Citizen_S1_Check.json"))
      .check(jsonPath("$.id").saveAs("citizenCaseId")))

    .pause(Environment.constantthinkTime.seconds)

    // S2: employment details
    .exec(http("API_ET_Citizen_GetUpdateToken_S2_Employment")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/event-triggers/UPDATE_CASE_DRAFT/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_ET_Citizen_Update_S2_Employment")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Citizen_S2_Employment.json"))
      .check(jsonPath("$.id").saveAs("citizenCaseId")))

    .pause(Environment.constantthinkTime.seconds)

    // S2: respondent details
    .exec(http("API_ET_Citizen_GetUpdateToken_S2_Respondent")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/event-triggers/UPDATE_CASE_DRAFT/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_ET_Citizen_Update_S2_Respondent")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Citizen_S2_Respondent.json"))
      .check(jsonPath("$.id").saveAs("citizenCaseId")))

    .pause(Environment.constantthinkTime.seconds)

    // S2: section check
    .exec(http("API_ET_Citizen_GetUpdateToken_S2_Check")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/event-triggers/UPDATE_CASE_DRAFT/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_ET_Citizen_Update_S2_Check")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Citizen_S2_Check.json"))
      .check(jsonPath("$.id").saveAs("citizenCaseId")))

    .pause(Environment.constantthinkTime.seconds)

    // S3: claim details
    .exec(http("API_ET_Citizen_GetUpdateToken_S3_Claim")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/event-triggers/UPDATE_CASE_DRAFT/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_ET_Citizen_Update_S3_Claim")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Citizen_S3_Claim.json"))
      .check(jsonPath("$.id").saveAs("citizenCaseId")))

    .pause(Environment.constantthinkTime.seconds)

    // S3: section check
    .exec(http("API_ET_Citizen_GetUpdateToken_S3_Check")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/event-triggers/UPDATE_CASE_DRAFT/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_ET_Citizen_Update_S3_Check")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Citizen_S3_Check.json"))
      .check(jsonPath("$.id").saveAs("citizenCaseId")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ET1CitizenSubmit =

    exec(_.set("currentDate", now.format(patternDate)))

    .exec(http("API_ET_Citizen_GetSubmitToken")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/event-triggers/SUBMIT_CASE_DRAFT/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_Citizen_SubmitCase")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{citizenCaseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_CitizenSubmit.json"))
      .check(jsonPath("$.id").saveAs("citizenCaseId")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(session => {
      val caseId  = session("citizenCaseId").asOption[String].getOrElse("NOT SET")
      println(s"ET1 case progression complete - caseId: $caseId")
      session
    })

  val CCDAPI_ET3SelfAssign =

    exec(http("API_ET_Respondent_AssignDefendantRole")
      .post(ccdDataStoreUrl + "/case-users")
      .header("ServiceAuthorization", "#{aac_manage_case_assignmentBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type", "application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Et3CaseUsers.json"))
      .check(status.is(201)))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ET3SectionOne =

    exec(http("API_ET_Respondent_GetEt3SectionOneToken")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/et3Response/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_Respondent_Et3SectionOne")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Et3SectionOne.json"))
      .check(status.is(201)))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ET3SectionTwo =

    exec(http("API_ET_Respondent_GetEt3SectionTwoToken")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/et3ResponseEmploymentDetails/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_Respondent_Et3SectionTwo")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Et3SectionTwo.json"))
      .check(status.is(201)))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ET3SectionThree =

    exec(http("API_ET_Respondent_GetEt3SectionThreeToken")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/et3ResponseDetails/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_Respondent_Et3SectionThree")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Et3SectionThree.json"))
      .check(status.is(201)))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ET3RespondentUpdate =

    exec(http("API_ET_Respondent_GetUpdateEt3Token")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/UPDATE_ET3_FORM/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken"))
      .check(jsonPath("$.case_details.case_data.respondentCollection[0].id").saveAs("respondentCollectionId")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_Respondent_UpdateEt3")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Et3Update.json"))
      .check(status.is(201)))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ET3RespondentSubmit =

    exec(http("API_ET_Respondent_GetSubmitEt3Token")
      .get(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/event-triggers/SUBMIT_ET3_FORM/token")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(Environment.constantthinkTime.seconds)

    .exec(http("API_ET_Respondent_SubmitEt3")
      .post(ccdDataStoreUrl + "/citizens/#{citizenIdamId}/jurisdictions/#{Jurisdiction}/case-types/#{CaseType}/cases/#{caseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Et3Submit.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime.seconds)

  val ETCOS_ET3Submit =

    exec(http("ETCOS_ET3_Submit")
      .post(Environment.etCosUrl + "/et3/modifyEt3Data")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/et/CCD_ET_Et3ModifySubmit.json"))
      .check(status.is(200)))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ETViewCase =

    exec(http("CCD_ET_ViewCase")
      .get(ccdDataStoreUrl + "/cases/#{caseId}")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .header("Experimental","true")
      .check(jsonPath("$.id").is("#{caseId}")))

    .pause(Environment.constantthinkTime.seconds)

  val CCDAPI_ETGetEvents =

    exec(http("CCD_ET_GetEvents")
      .get(ccdDataStoreUrl + "/cases/#{caseId}/events")
      .header("ServiceAuthorization", "#{ccd_dataBearerToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .header("Experimental","true")
      .check(substring("auditEvents")))

    .pause(Environment.constantthinkTime.seconds)

  // val CCDAPI_ETCitizenGetIdamToken =

  //   exec(http("IDAM_ET_Citizen_GetToken")
  //     .post(IdamLogin.IdamAPI + "/o/token?client_id=ccd_gateway&client_secret=" + IdamLogin.ccdGatewayClientSecret + "&grant_type=password&scope=" + ccdScope + "&username=#{citizenUsername}&password=#{citizenPassword}")
  //     .header("Content-Type", "application/x-www-form-urlencoded")
  //     .header("Content-Length", "0")
  //     .check(status.is(200))
  //     .check(jsonPath("$.access_token").saveAs("citizenAccessToken")))

  //   .pause(Environment.constantthinkTime.seconds)

}
