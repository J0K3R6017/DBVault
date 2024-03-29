package model

data class ConfigModel(
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

        fun build() = ConfigModel(driver, url, username, password)
    }
}
