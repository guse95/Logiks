package com.example.logiks

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform