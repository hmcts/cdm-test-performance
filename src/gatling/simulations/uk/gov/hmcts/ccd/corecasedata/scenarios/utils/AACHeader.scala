package uk.gov.hmcts.ccd.corecasedata.scenarios.utils

import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment._

object AACHeader {

  /*Manage Case -  Get Assignment Header*/
  val manageCaseGetAssignentsHeader = Map(
    "ServiceAuthorization" -> "Bearer ${ccd_dataBearerToken}",
    "Authorization" -> "Bearer ${access_token}",
    "Accept" -> "*/*",
    "Host" -> aacHost,
    "Accept-Encoding" -> "gzip, deflate, br",
    "Connection" -> "keep-alive"
  )
  /*Manage Case -  Remove Assignment Header*/
  val manageCaseRemoveAssignmentHeader = Map(
    "ServiceAuthorization" -> "Bearer ${ccd_dataBearerToken}",
    "Authorization" -> "Bearer ${access_token}",
    "Accept" -> "*/*",
    "Host" -> aacHost,
    "Accept-Encoding" -> "gzip, deflate, br",
    "Connection" -> "keep-alive"
  )

  /*Manage Case -  Post Assignment Header*/
  val manageCasePostAssignentsHeader = Map(
    "ServiceAuthorization" -> "Bearer ${ccd_dataBearerToken}",
    "Authorization" -> "Bearer ${access_token}",
    "Accept" -> "*/*",
    "Host" -> aacHost,
    "Accept-Encoding" -> "gzip, deflate, br",
    "Connection" -> "keep-alive"
  )

  /*NOC -  Get Questions Header*/
  val nocGetQuestionsHeader = Map(
    "ServiceAuthorization" -> "Bearer ${ccd_dataBearerToken}",
    "Authorization" -> "Bearer ${access_token}",
    "Accept" -> "*/*",
    "Host" -> aacHost,
    "Accept-Encoding" -> "gzip, deflate, br",
    "Connection" -> "keep-alive"
  )

  /*NOC -  Post Questions Header*/
  val nocPostQuestionsHeader = Map(
    "ServiceAuthorization" -> "Bearer ${ccd_dataBearerToken}",
    "Authorization" -> "Bearer ${access_token}",
    "Accept" -> "*/*",
    "Host" -> aacHost,
    "Accept-Encoding" -> "gzip, deflate, br",
    "Connection" -> "keep-alive"
  )

}
