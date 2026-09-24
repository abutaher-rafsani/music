package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class AddressData(
    @Json(name = "division") val division: String = "ঢাকা",
    @Json(name = "district") val district: String = "ঢাকা",
    @Json(name = "upazila") val upazila: String? = null,
    @Json(name = "unionCity") val unionCity: String? = null,
    @Json(name = "villageWard") val villageWard: String? = null,
    @Json(name = "details") val details: String? = null
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    // Step 1: Mandatory Fields
    @Json(name = "fullName") val fullName: String,
    @Json(name = "username") val username: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "email") val email: String = "",
    @Json(name = "password") val password: String,

    // Step 2: Address Information
    @Json(name = "presentAddress") val presentAddress: AddressData? = null,
    @Json(name = "permanentAddress") val permanentAddress: AddressData? = null,
    @Json(name = "sameAddress") val sameAddress: Boolean = true,

    // Step 3: Education, Career & Personal
    @Json(name = "jobInfo") val jobInfo: String = "",
    @Json(name = "university") val university: String = "",
    @Json(name = "college") val college: String = "",
    @Json(name = "school") val school: String = "",
    @Json(name = "dob") val dob: String? = null,
    @Json(name = "gender") val gender: String? = null,
    @Json(name = "interests") val interests: List<String> = emptyList(),

    // Step 4: Profile, Settings & Refer
    @Json(name = "profilePhotoUrl") val profilePhotoUrl: String = "",
    @Json(name = "bio") val bio: String = "",
    @Json(name = "userType") val userType: String = "ছাত্র / শিক্ষার্থী",
    @Json(name = "contactSyncEnabled") val contactSyncEnabled: Boolean = true,
    @Json(name = "refCode") val refCode: String = "",
    @Json(name = "acceptedTerms") val acceptedTerms: Boolean = true
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "identifier") val identifier: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class CheckUsernameResponse(
    @Json(name = "available") val available: Boolean,
    @Json(name = "message") val message: String
)

@JsonClass(generateAdapter = true)
data class AuthUserData(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "fullName") val fullName: String? = null,
    @Json(name = "username") val username: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "balance") val balance: Double? = 50.0,
    @Json(name = "coin") val coin: Int? = 0,
    @Json(name = "profilePhotoUrl") val profilePhotoUrl: String? = null,
    @Json(name = "myReferralCode") val myReferralCode: String? = null
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String,
    @Json(name = "user") val user: AuthUserData? = null
)

/**
 * Retrofit Interface for api_auth.php
 */
interface AuthApiService {
    @GET("api_auth.php")
    suspend fun checkUsername(
        @Query("action") action: String = "check_username",
        @Query("username") username: String
    ): Response<CheckUsernameResponse>

    @POST("api_auth.php?action=register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("api_auth.php?action=login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
}
