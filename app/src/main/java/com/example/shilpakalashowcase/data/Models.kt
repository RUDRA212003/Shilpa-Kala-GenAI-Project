package com.example.shilpakalashowcase.data

data class UserProfile(
    val uid: String = "",
    val name: String = "RUDRESH",
    val email: String = "",
    val role: UserRole = UserRole.BUYER,
    val bio: String = "",
    val location: String = "Karnataka, India",
    val specialization: String = "Handicrafts",
    val profileImage: String = "",
    val phone: String = "1234567890",
    val portfolioCount: Int = 0
)

data class Product(
    val id: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "", 
    val originalImageUrl: String = "",
    val price: Double = 0.0,
    val woodType: String = "",
    val material: String = "",
    val category: String = "",
    val artisanLocation: String = "Karnataka",
    val timestamp: Long = System.currentTimeMillis()
)

data class AppSettings(
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val language: String = "English"
)

data class OrderHistory(
    val id: String,
    val userId: String,
    val productId: String,
    val productName: String,
    val artisanName: String,
    val amount: Double,
    val status: String = "COMPLETED",
    val timestamp: Long = System.currentTimeMillis()
)
