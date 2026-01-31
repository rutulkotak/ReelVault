package com.reelvault.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform