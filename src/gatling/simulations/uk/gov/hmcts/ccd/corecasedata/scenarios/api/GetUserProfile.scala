package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment
import scala.concurrent.duration._

object GetUserProfile {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val feedUserData = csv("UserProfileJurisdictions.csv").random

  val headers_0 = Map(
    "Content-Type" -> "application/json",
    "Authorization" -> "Bearer eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiOHAyaWo4NktKU3hDSnhnL3lKL1dsN043MTFzPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjY2RpbXBvcnRkb21haW5AZ21haWwuY29tIiwiY3RzIjoiT0FVVEgyX1NUQVRFTEVTU19HUkFOVCIsImF1dGhfbGV2ZWwiOjAsImF1ZGl0VHJhY2tpbmdJZCI6IjU1YzRiYWU3LTQ4NTYtNDJlNy1hNjMzLWNiZjY1YTc3OTY1My0yOTQxMDQwMCIsImlzcyI6Imh0dHBzOi8vZm9yZ2Vyb2NrLWFtLnNlcnZpY2UuY29yZS1jb21wdXRlLWlkYW0tcGVyZnRlc3QuaW50ZXJuYWw6ODQ0My9vcGVuYW0vb2F1dGgyL3JlYWxtcy9yb290L3JlYWxtcy9obWN0cyIsInRva2VuTmFtZSI6ImFjY2Vzc190b2tlbiIsInRva2VuX3R5cGUiOiJCZWFyZXIiLCJhdXRoR3JhbnRJZCI6IlV5NkR5UkJ2Y3hwa2NZem95OVlCaVJzb0FCQSIsImF1ZCI6ImNjZF9hZG1pbiIsIm5iZiI6MTU4NDYwNjcyNCwiZ3JhbnRfdHlwZSI6ImF1dGhvcml6YXRpb25fY29kZSIsInNjb3BlIjpbIm9wZW5pZCIsInByb2ZpbGUiLCJyb2xlcyJdLCJhdXRoX3RpbWUiOjE1ODQ2MDY3MjQsInJlYWxtIjoiL2htY3RzIiwiZXhwIjoxNTg0NjM1NTI0LCJpYXQiOjE1ODQ2MDY3MjQsImV4cGlyZXNfaW4iOjI4ODAwLCJqdGkiOiJHWlZ3ME1lSkZMRVgwek5HNlp2b0xYcHhlb28ifQ.hST35xfdOjpuSgkm83DnRUasOjPwIUJQu6Ow--F91bU7mC-WRlvl7dxSbpa8BSZM7krRmg6nQ6kNRUCQvDHPd7wpV-HKm-AOc1vUSxO6AtVM06eLqQgP7FPto5uVnywn1S-P4jjZG0CcDYNcQKTNKeCF6GV59TH4FSMyshaU1RVn34LdXP1kRtgCBFZEAPjERIAZuq5yvXV1bPKaVVfyuCbsvOT5U2qUc6_hqQ0amUvqncTIx7jDz0qHpncI09bZc9RoeAdsGZpi2cvfYtyUiMmG1jYBHLMatKqNoi-ng4kXyPV98rPnhdXD4cxlO10ZpgBc_DPFzWcjZQK5QTPL9A",
    "ServiceAuthorization" -> "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjY2RfZGF0YSIsImV4cCI6MTU4NDYyMTE1OX0.HFGhRbVctFoI3RFjY0WatFRVTxuAHp-lmhvcpSATee0FuqGu96PqxNElPWX9QLYWl6-OoerbPzWYujCBoQGDVA")

  val SearchJurisdiction = feed(feedUserData)
    .exec(http("CUP_GetJurisdiction")
    .get("http://ccd-user-profile-api-perftest.service.core-compute-perftest.internal/users?jurisdiction=${UPJurisdiction}")
      .headers(headers_0))

    .exec {
      session =>
        println("Selected jurisdiction is ")
        println(session("UPJurisdiction").as[String])
        session}

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val SearchAllUsers = exec(http("CUP_GetAllUsers")
    .get("http://ccd-user-profile-api-perftest.service.core-compute-perftest.internal/users")
      .headers(headers_0))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

}