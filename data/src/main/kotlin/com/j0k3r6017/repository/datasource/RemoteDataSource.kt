package com.j0k3r6017.repository.datasource

import com.j0k3r6017.model.Config
import java.sql.Connection
import java.sql.DriverManager

class RemoteDataSource(private val config: Config) {

    private var connection: Connection? = null

    fun getConnection(): Connection {
        Class.forName(config.driver)
        connection = DriverManager.getConnection(
            config.url,
            config.username,
            config.password
        )
        return connection ?: throw Exception("Connection is null")
    }
}