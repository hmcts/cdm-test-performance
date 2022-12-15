package uk.gov.hmcts.ccd.corecasedata.scenarios.manageCaseAssignments

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.AACHeader._

object caseAssignmentControllerService {


  /* GET /case-assignments request to get all assignments from a case.
      The request requires an S2SToken and Idam so these services should be called prior to running this request.
      The S2SToken and Idam is sent within the manageCaseGetAssignmentHeader
      ${Reference} variable is assigned from the caseAssignmentShareCaseAPI.csv feeder file.  The feeder is defined in the Simulation.
      If an assignment is found then the required variables are captured in the response.  A successful request can still return no assignment,
      therefore the jsonpath is optional.
   */

  val caseAssignmentGetAssignments =

    group("CaseAssignment") {
      exec(http("GET_Case_Assignments")
        .get(aacUrl + "/case-assignments")
        .headers(aacHeader)
        .queryParam("case_ids", "${caseId}")
        .check(jsonPath("$.case_assignments[0].shared_with[0].idam_id").optional.saveAs("assignmentAssigneeId"))
        .check(jsonPath("$.case_assignments[0].case_id").optional.saveAs("assignmentCaseId"))
        .check(jsonPath("$.case_assignments[0].shared_with[0].case_roles[0]").optional.saveAs("assignmentCaseRoles")))
    }

  /* DELETE /case-users request to remove a case assignment from a case.  Only executes if a assignment case reference exists (from the Get request).
      The request requires an S2SToken and Idam so these services should be called prior to running this request.
      The S2SToken and Idam is sent within the manageCaseRemoveAssignmentHeader
      If the unassignment is successful then a specific message is expected
   */

  val caseAssignmentRemoveAssignments =

    group("CaseAssignment") {
      doIf("${assignmentCaseId.exists()}")
      {
        exec(http("DELETE_Case_Assignments")
          .delete(aacUrl + "/case-assignments")
          .headers(aacHeader)
          .body(ElFileBody("bodies/caseManagement/unassignCase.json")).asJson
          .check(jsonPath("$.status_message").is("Unassignment(s) performed successfully.")))
      }
    }


  /* POST /case-users request to assign person from the same organisation to a case.
     The request requires an S2SToken and Idam so these services should be called prior to running this request.
       The S2SToken and Idam is sent within the manageCasePostAssignmentHeader
       ${AssignmentRole} variable is assigned from the caseAssignmentShareCaseAPI.csv feeder file.  The feeder is defined in the Simulation file.
       If the assignment is successful then a specific message is expected.
    */


  val caseAssignmentPostAssignment =
    group("CaseAssignment") {
      exec(http("POST_Case_Assignments")
        .post(aacUrl + "/case-assignments")
        .headers(aacHeader)
        .body(ElFileBody("bodies/caseManagement/assignCase.json")).asJson
        .check(jsonPath("$.status_message").is("Roles ${assignmentRole} from the organisation policies successfully assigned to the assignee.")))
    }

}
