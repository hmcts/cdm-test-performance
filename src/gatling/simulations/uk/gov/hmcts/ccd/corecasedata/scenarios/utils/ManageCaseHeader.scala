package uk.gov.hmcts.ccd.corecasedata.scenarios.utils

import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment._

object ManageCaseHeader {

  /*Manage Case -  Get Assignment Header*/
  val manageCaseGetAssignentsHeader = Map(
    "ServiceAuthorization" -> "Bearer ${ccd_dataBearerToken}",
    "Authorization" -> "Bearer ${access_token}",
    "Accept" -> "*/*",
    "Host" -> manageCaseHost,
    "Accept-Encoding" -> "gzip, deflate, br",
    "Connection" -> "keep-alive",
  )
  /*Manage Case -  Remove Assignment Header*/
  val manageCaseRemoveAssignmentHeader = Map(
    "ServiceAuthorization" -> "Bearer ${ccd_dataBearerToken}",
    "Authorization" -> "Bearer ${access_token}",
    "Accept" -> "*/*",
    "Host" -> manageCaseHost,
    "Accept-Encoding" -> "gzip, deflate, br",
    "Connection" -> "keep-alive",
  )

  /*Manage Case -  Post Assignment Header*/
  val manageCasePostAssignentsHeader = Map(
    "ServiceAuthorization" -> "Bearer ${ccd_dataBearerToken}",
    "Authorization" -> "Bearer ${access_token}",
    "Accept" -> "*/*",
    "Host" -> manageCaseHost,
    "Accept-Encoding" -> "gzip, deflate, br",
    "Connection" -> "keep-alive",
    //"user-roles" -> "#{userRole}",
    //"user-id" -> "#{userId}",
  )

}
