package ua.nure.mossurd.blooddonation.request

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import ua.nure.mossurd.blooddonation.request.model.LoginResponseDto

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/auth/login")
    fun loginCall(@Body body: Map<String, String>): Call<LoginResponseDto>
}