package net.myFile_Upload.upload.task2

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageUploadAPI {

    @Multipart
    @POST("api/v1/upload/")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("desc") desc: RequestBody
    ): Call<ImageUploadResponse>

    companion object {
        operator fun invoke(): ImageUploadAPI {
            return Retrofit.Builder()
                .baseUrl("https://darot-image-upload-service.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ImageUploadAPI::class.java)
        }
    }
}