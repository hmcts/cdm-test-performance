package uk.gov.hmcts.ccd.corecasedata.scenarios.manageCaseAssignments

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.AACHeader._
import uk.gov.hmcts.ccd.corecasedata.scenarios.IdamLogin

object noticeOfChangeControllerService {

  /* GET /noc/noc-questions request to get challenge questions for the process of making a notice of change.
      The request requires an S2SToken and Idam so these services should be called prior to running this request.
      The S2SToken and Idam is sent within the nocGetQuestionsHeader.  ${reference} variable is assigned from the feeder file.
      The feeder is defined in the Simulation.  It is expected that a list of questions will be returned and they are stored in session.
   */

  val noticeOfChangeGetQuestions =

    group("NoticeOfChange") {
      exec(http("GET_NOC_Questions")
        .get(aacUrl + "/noc/noc-questions")
        .headers(nocGetQuestionsHeader)
        .queryParam("case_id", "${caseId}")
        .check(jsonPath("$.questions[*].question_id").findAll.optional.saveAs("questionIds"))
        .check(jsonPath("$.questions[*].question_text").findAll.optional.saveAs("questionText"))
        .check(jsonPath("$.questions[-1:].order").optional.saveAs("questionOrder")))
    }

  /* POST /noc/verify-noc-answers request to answer the challenge questions for the process of making a notice of change.
     The outcome messages can be potentially
     http 200 - Notice of Change answers verified successfully
     http 400 - The requestor has answered questions uniquely identifying a litigant that they are already representing
     If the the 400 is returned and it is that the case is already allocated to the user, then switch to a different user.
     The request is not replayed but the user should then work for the create event POST
     The request requires an S2SToken and Idam so these services should be called prior to running this request.
     The S2SToken and Idam is sent within the nocPostQuestionsHeader
     The feeder is defined in the Simulation.
   */
  val noticeOfChangePostAnswers =

    group("NoticeOfChange") {
      doIf("${questionOrder.exists()}") {
        exec(nocQuestionGenerator.nocQuestionJSONBuilder())
        .exec(http("POST_NOC_Questions")
          .post(aacUrl + "/noc/verify-noc-answers")
          .headers(nocPostQuestionsHeader)
          .body(StringBody("${documentJSON}")).asJson
          .check(jsonPath("$.status_message").optional.saveAs("successMessage"))
          .check(jsonPath("$.message").optional.saveAs("unsuccessfulMessage"))
          .check(status in (200,400)))
        //check to see if the unsuccessful message is because the noc case is already allocated to this user.
        .doIf("${unsuccessfulMessage.exists()}") {
          doIf(session => session("unsuccessfulMessage").as[String].equals("The requestor has answered questions uniquely identifying a litigant that they are already representing")) {
            //if the case is already allocated then change the credentials to another organisation so that the next post create event can still be submitted
            exec(session => {session.set("Username", session("alternativeUserName").as[String])})
            //get new Idam token for the user so that the event can be created in the next API call
            .exec(IdamLogin.GetIdamToken)
            //remove the unsuccessful message and documentJSON from session as it may not appear in next iteration but still be stored in session
            .exec(session => {session.removeAll("unsuccessfulMessage", "documentJSON")})
          }
        }
    }
  }

  /* POST /noc/noc-requests request to submit the creation of a noc event and ultimately.  This request automatically creates
      a call to POST /noc/check-noc-approval and POST /noc/apply-decision, so 3 requests from NOC are simulated.
      The request requires an S2SToken and Idam so these services should be called prior to running this request.
      The variable ${documentJSON} is created via the nocQuestionJSONBuilder function
      The S2SToken and Idam is sent within the nocPostQuestionsHeader.  The feeder is defined in the Simulation.
      A specific string is expected in the json response that indicates a successful noc creation.
   */
  val noticeOfChangePostCreateNOCEvent =

    group("NoticeOfChange") {
        doIf("${questionOrder.exists()}") {
          exec(nocQuestionGenerator.nocQuestionJSONBuilder())
          .exec(http("POST_NOC_CreateEvent")
              .post(aacUrl + "/noc/noc-requests")
              .headers(nocPostQuestionsHeader)
              .body(StringBody("${documentJSON}")).asJson
              .check(status is 201)
              .check(jsonPath("$.status_message").is("The Notice of Change request has been successfully submitted.")))
        }
    }


}



