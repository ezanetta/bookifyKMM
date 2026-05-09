package com.ezanetta.bookifykmm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform