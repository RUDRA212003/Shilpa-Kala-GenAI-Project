package com.example.shilpakalashowcase.data

data class User(

    val uid: String = "",

    val name: String = "",

    val email: String = "",

    val bio: String = "",

    val location: String = "",

    val specialization: String = "",

    val portfolioCount: Int = 0,

    val role: UserRole = UserRole.BUYER

)