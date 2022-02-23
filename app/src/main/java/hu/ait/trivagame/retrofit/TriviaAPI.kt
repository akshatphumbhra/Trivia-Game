package hu.ait.trivagame.retrofit

import hu.ait.trivagame.data.Questions
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaAPI {
    @GET("api.php")
    fun getResult(@Query("amount") amount : Int,
                  @Query("category") category : Int,
                  @Query("type") type : String,
                  @Query("difficulty") difficulty : String) : Call<Questions>
}