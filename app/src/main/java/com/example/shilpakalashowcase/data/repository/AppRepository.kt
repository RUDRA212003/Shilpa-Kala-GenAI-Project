package com.example.shilpakalashowcase.data.repository

import com.example.shilpakalashowcase.data.Product
import com.example.shilpakalashowcase.data.UserProfile
import com.example.shilpakalashowcase.data.network.MongoDBService
import retrofit2.Response

class AppRepository(private val apiService: MongoDBService) {

    suspend fun getUserProfile(uid: String): Response<UserProfile> {
        return apiService.getUserProfile(uid)
    }

    suspend fun upsertUserProfile(profile: UserProfile): Response<Unit> {
        return apiService.upsertUserProfile(profile)
    }

    suspend fun getAllProducts(): Response<List<Product>> {
        return apiService.getAllProducts()
    }

    suspend fun getProductsBySeller(sellerId: String): Response<List<Product>> {
        return apiService.getProductsBySeller(sellerId)
    }

    suspend fun addProduct(product: Product): Response<Unit> {
        return apiService.addProduct(product)
    }

    suspend fun deleteProduct(id: String): Response<Unit> {
        return apiService.deleteProduct(id)
    }
}
