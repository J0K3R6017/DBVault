package com.j0k3r6017.domain.model

data class Config(
    val driver: String,
    val url: String,
    val username: String,
    val password: String,
) {
    object Builder {
        private var driver: String = ""
        private var url: String = ""
        private var username: String = ""
        private var password: String = ""

        fun driver(driver: String) = apply { Builder.driver = driver }
        fun url(url: String) = apply { Builder.url = url }
        fun username(username: String) = apply { Builder.username = username }
        fun password(password: String) = apply { Builder.password = password }

        fun build() = Config(driver, url, username, password)
    }
}