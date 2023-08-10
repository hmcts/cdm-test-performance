package scenarios.utils

import Environment._

object AACHeader {

  /*AAC Header*/
  val aacHeader = Map(
    "ServiceAuthorization" -> "Bearer #{ccd_dataBearerToken}",
    "Authorization" -> "Bearer #{access_token}",
    "Accept" -> "*/*",
    "Host" -> aacHost,
    "Accept-Encoding" -> "gzip, deflate, br",
    "Connection" -> "keep-alive"
  )

}