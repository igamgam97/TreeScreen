package org.example.plotapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform