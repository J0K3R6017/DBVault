package com.j0k3r6017.presentation

import com.google.gson.Gson
import com.j0k3r6017.data.repository.SchemaVersionRemoteRepository
import com.j0k3r6017.data.repository.datasource.RemoteDataSource
import com.j0k3r6017.domain.repository.SchemaVersionRepository
import com.j0k3r6017.presentation.model.Migration
import com.j0k3r6017.presentation.utils.ConfigLoader
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DBVault(pathBase: String = System.getProperty("user.dir")) {

    private val schemaVersionRepository: SchemaVersionRepository
    private val remoteDataSource: RemoteDataSource
    private val dir: File = File("${pathBase}${File.separator}migrations")

    init {
        val configLoader = ConfigLoader(pathBase = pathBase)
        remoteDataSource = RemoteDataSource(configLoader.getConfig())
        schemaVersionRepository = SchemaVersionRemoteRepository(remoteDataSource)
        schemaVersionRepository.createSchemaVersionTable()
    }

    private fun getDate(): String {
        val format = DateTimeFormatter.ofPattern("yyyy_MM_dd")
        val date = LocalDateTime.now()
        return date.format(format)
    }

    private fun getConsecutive(): Int {
        dir.listFiles()?.let {
            if(it.isNotEmpty()) {
                val file = it.sorted().last { file -> file.name.startsWith(getDate()) }
                val consecutive = file.name.substring(11,15).toInt()
                return consecutive.plus(1)
            } else {
                return 1000
            }
        }
        return 1000
    }

    private fun getFilenameMigrated(): List<String> {
        val connection = remoteDataSource.getConnection()
        val statement = connection.createStatement()
        val query = "SELECT migration FROM public.schema_version;"
        val resultSet = statement?.executeQuery(query)
        val files = mutableListOf<String>()
        while (resultSet?.next() == true) {
            files.add(resultSet.getString("migration"))
        }
        statement?.close()
        connection.close()
        return files
    }

    fun getFilesExcluding(exclude: List<String>): List<File> {
        return dir.listFiles()?.sorted()?.filter { file -> !exclude.contains(file.name) } ?: emptyList()
    }

    fun getFilesRollback(exclude: List<String>): List<File> {
        return dir.listFiles()?.sortedDescending()?.filter { file -> exclude.contains(file.name) } ?: emptyList()
    }

    fun makeMigration(name: String) {
        if(dir.exists()) {
            val consecutive = getConsecutive()
            val fileName = "${getDate()}_${consecutive}_$name.json"
            val file = File("${dir.absolutePath}${File.separator}$fileName")
            file.createNewFile()
            val writer = FileWriter(file)
            writer.write("{\n\t\"up\": [\n\t\t\"\"\n\t],\n\t\"down\": [\n\t\t\"\"\n\t]\n}")
            writer.close()
            println("Migración creada con éxito")
        } else {
            println("No existe el directorio de migraciones")
        }
    }

    fun migrate() {
        val files = getFilesExcluding(getFilenameMigrated())
        if(files.isNotEmpty()) {
            val step = getStep()
            files.forEach { file ->
                val migration = file.readText()
                val gson = Gson()
                val migrations = gson.fromJson(migration, Migration::class.java)
                val connection = remoteDataSource.getConnection()
                val statement = connection.createStatement()
                connection.autoCommit = false
                try {
                    migrations.up.forEach { query ->
                        statement?.executeUpdate(query)
                    }
                    val query = "INSERT INTO public.schema_version (migration, step) VALUES ('${file.name}', ${step.plus(1)});"
                    statement?.executeUpdate(query)
                    connection.commit()
                    println("Migración ${file.name} aplicada con éxito")
                } catch (e: Exception) {
                    connection.rollback()
                    println("Error al aplicar la migración ${file.name}")
                    e.printStackTrace()
                } finally {
                    statement.close()
                    connection.close()
                }
            }
        } else {
            println("No hay migraciones pendientes")
        }
    }

    fun getFilesForStep(step: String): List<String> {
        val connection = remoteDataSource.getConnection()
        val statement = connection.createStatement()
        val query = "SELECT * FROM public.schema_version ${if(step != "-1") "WHERE step = $step" else ""} ORDER BY migration;"
        val resultSet = statement?.executeQuery(query)
        val files = mutableListOf<String>()
        while (resultSet?.next() == true) {
            files.add(
                resultSet.getString("migration"),
            )
        }
        statement?.close()
        connection.close()
        return files
    }

    fun rollback(stepp: Int?) {
        val step = stepp ?: getStep()
        if(step != 0) {
            val files = getFilesForStep(step.toString())
            val filesRollback = getFilesRollback(files)
            val connection = remoteDataSource.getConnection()
            val statement = connection.createStatement()
            connection.autoCommit = false
            try {
                filesRollback.forEach { file ->
                    val migration = file.readText()
                    val gson = Gson()
                    val migrations = gson.fromJson(migration, Migration::class.java)
                    migrations.down.forEach { query ->
                        statement?.executeUpdate(query)
                    }
                    val query = "DELETE FROM public.schema_version WHERE migration = '${file.name}';"
                    statement?.executeUpdate(query)
                    connection.commit()
                    println("Migración ${file.name} revertida con éxito")
                }
            } catch (e: Exception) {
                connection.rollback()
                println("Error al revertir la migración")
                e.printStackTrace()
            } finally {
                statement?.close()
                connection.close()
            }
        } else {
            println("No hay migraciones para revertir")
        }
    }


    fun getStep(): Int {
        val connection = remoteDataSource.getConnection()
        val statement = connection.createStatement()
        val query = "SELECT step FROM public.schema_version ORDER BY id DESC LIMIT 1;"
        val resultSet = statement?.executeQuery(query)
        var step = 0
        while (resultSet?.next() == true) {
            step = resultSet.getInt("step")
        }
        statement?.close()
        connection.close()
        return step
    }
}