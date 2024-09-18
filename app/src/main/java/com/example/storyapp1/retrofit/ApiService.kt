package com.example.storyapp1.retrofit

import com.example.storyapp1.response.DetailResponse
import com.example.storyapp1.response.FileUploadResponse
import com.example.storyapp1.response.LoginResponse
import com.example.storyapp1.response.RegistResponse
import com.example.storyapp1.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): RegistResponse

    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") auth: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getDetail(
        @Header("Authorization") auth: String,
        @Path("id") id: String
    ): DetailResponse

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): FileUploadResponse

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part ("lat") latLng: RequestBody,
        @Part ("lon") long: RequestBody
    ): FileUploadResponse

    @GET("stories")
    suspend fun getStoriesLocation(
        @Header("Authorization") token: String,
        @Query("location") location : Int = 1,
    ): StoryResponse
}