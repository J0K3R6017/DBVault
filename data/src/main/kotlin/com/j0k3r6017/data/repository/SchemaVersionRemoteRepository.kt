package com.j0k3r6017.data.repository

import com.j0k3r6017.data.repository.datasource.RemoteDataSource
import com.j0k3r6017.domain.repository.SchemaVersionRepository

class SchemaVersionRemoteRepository(private val remoteDataSource: RemoteDataSource): SchemaVersionRepository {

    override fun createSchemaVersionTable() {
        val connection = remoteDataSource.getConnection()
        val statement = connection.createStatement()
        statement.executeUpdate(
            """
            CREATE TABLE IF NOT EXISTS schema_version (
                id SERIAL PRIMARY KEY,
                migration VARCHAR(255) NOT NULL,
                step INTEGER NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
            
            CREATE UNIQUE INDEX IF NOT EXISTS schema_version_version_uindex ON schema_version (id);
            """.trimIndent()
        )
        statement.close()
        connection.close()
    }
}