package com.j0k3r6017

import com.j0k3r6017.utils.ConfigLoader

fun main() {
    val configLoader = ConfigLoader()
    val config = configLoader.getConfig()
    println(config.url)
}