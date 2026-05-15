package com.example.shilpakalashowcase.data.network

import com.example.shilpakalashowcase.data.Product
import com.example.shilpakalashowcase.data.UserProfile
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface for MongoDB Atlas Data API or a custom middleware backend.
 * The MONGODB_URI provided is used for connection, but usually accessed via an API Key.
 */
interface MongoDBService {
    
    // User endpoints
    @GET("users/{uid}")
    suspend fun getUserProfile(@Path("uid") uid: String): Response<UserProfile>

    @POST("users/upsert")
    suspend fun upsertUserProfile(@Body profile: UserProfile): Response<Unit>

    // Product endpoints
    @GET("products")
    suspend fun getAllProducts(): Response<List<Product>>

    @GET("products/seller/{sellerId}")
    suspend fun getProductsBySeller(@Path("sellerId") sellerId: String): Response<List<Product>>

    @POST("products/add")
    suspend fun addProduct(@Body product: Product): Response<Unit>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): Response<Unit>
}
