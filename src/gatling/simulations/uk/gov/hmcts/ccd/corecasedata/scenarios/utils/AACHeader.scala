package uk.gov.hmcts.ccd.corecasedata.scenarios.utils

import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment._

object AACHeader {

  /*AAC Header*/
  val aacHeader = Map(
    "ServiceAuthorization" -> "Bearer ${ccd_dataBearerToken}",
    "Authorization" -> "Bearer ${access_token}",
    "Accept" -> "*/*",
    "Host" -> aacHost,
    "Accept-Encoding" -> "gzip, deflate, br",
    "Connection" -> "keep-alive"
  )

}
