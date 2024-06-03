package com.test.storyapp.netconfig
import retrofit2.Call
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.test.storyapp.dataset.UserAddStory
import com.test.storyapp.dataset.UserRegister
import com.test.storyapp.dataset.SigninResponse
import com.test.storyapp.dataset.StoryResponse

interface ApiService {
    @GET("v1/stories")
    fun getListStory(
        @Header("Authorization") bearer: String?
    ): Call<StoryResponse>

    @Multipart
    @POST("/v1/stories")
    fun userAddStoryCall(
        @Header("Authorization") bearer: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody?,
    ): Call<UserAddStory>

    @FormUrlEncoded
    @POST("/v1/register")
    fun userSignUp(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<UserRegister>

    @FormUrlEncoded
    @POST("/v1/login")
    fun userSignIn(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<SigninResponse>
}