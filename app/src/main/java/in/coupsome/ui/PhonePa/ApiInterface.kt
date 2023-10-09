package `in`.coupsome.ui.PhonePa

import `in`.coupsome.ui.PhonePa.Model.status.CheckStatusModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path

interface ApiInterface {


    @GET("apis/pg-sandbox/pg/v1/status/{merchantId}/{transactionId}")
    suspend fun checkStatus(
        @Path("merchantId") merchantId: String,
        @Path("transactionId") transactionId: String,
        @HeaderMap headers: Map<String, String>,

        ): Response<CheckStatusModel>
}