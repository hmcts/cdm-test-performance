package uk.gov.hmcts.ccd.corecasedata.scenarios.manageCaseAssignments

import io.gatling.core.Predef._
import io.gatling.http.Predef._


object nocQuestionGenerator {

  var jsonNOCTop =
  """{
    |  "answers": [""".stripMargin

  var jsonNOCAnswer =
  """{
    |      "question_id": "nocQuestion",
    |      "value": "nocAnswer"
    |    }""".stripMargin

  var jsonNOCBottom =
  """],
    |  "case_id": "nocCaseId"
    |}""".stripMargin


  //  /* function to create a dynamic JSON request containing the answers and add it within the JSON payload for notice of change
  //   The full payload is then saved to session as documentJSON */
  //
  def nocQuestionJSONBuilder() = {
    var jsonNOCQuestionBuilder = ""
    var questionIndexCounter = 0
    //repeat loop the question json builder based on the number of questions returned in the GET questions request.
    repeat(session => session("questionOrder").as[Int], "questionIndex") {
      //get the question values (Id and Text value) from the existing saved jsonpath responses from GET questions request and store in session
      exec(session => {
        val questionIdItem = session("questionIds").as[Vector[String]]
        questionIndexCounter = session("questionIndex").as[Int]
        val questionTextItem = session("questionText").as[Vector[String]]
        val questionIdString = questionIdItem(questionIndexCounter)
        val questionTextString = questionTextItem(questionIndexCounter)
        session.setAll("questionIdString" -> questionIdString, "questionTextString" -> questionTextString)
      })
      //use the setQuestionAnswer function to get a possible answer from a list of known questions
      .exec(setQuestionAnswer())
      //build the question json now that the value for the question is defined.  Replace hard coded values in the json
      .exec(session => {
        jsonNOCQuestionBuilder = jsonNOCQuestionBuilder + jsonNOCAnswer
        jsonNOCQuestionBuilder = jsonNOCQuestionBuilder.replace("nocQuestion", session("questionIdString").as[String])
        jsonNOCQuestionBuilder = jsonNOCQuestionBuilder.replace("nocAnswer", session("questionVariable").as[String])
        //if the json is not in the final loop then we need to add a comma between json questions in the payload
        if (session("questionOrder").as[Int] != questionIndexCounter + 1) {
          jsonNOCQuestionBuilder = jsonNOCQuestionBuilder + ","
        }
        session
      })
    } // end of repeat
   //now that the repeat loop has completed, build the full json payload and store in session to be used in the POST request
   .exec(session => {
      val caseJSON = jsonNOCBottom.replace("nocCaseId", session("caseId").as[String])
      val completeJSON = jsonNOCTop + jsonNOCQuestionBuilder + caseJSON
      jsonNOCQuestionBuilder = ""
      session.setAll("documentJSON" -> completeJSON)
   })
  }  //end of def


 /* function to set a session variable based on the list of known answers.  This list can be increased to accommodate new questions when required*/

  def setQuestionAnswer() = {
    //check for appellant first name and add it to session from the data file
    doIf(session => session("questionTextString").as[String].equals("First Name")) {
      exec(session => {
        session.set("questionVariable", session("appellantFirstname").as[String])
      })
    }
    //check for appellant last name and add it to session from the data file
    .doIf(session => session("questionTextString").as[String].equals("Last Name")) {
      exec(session => {
        session.set("questionVariable", session("appellantSurname").as[String])
      })
    }
  }

}





