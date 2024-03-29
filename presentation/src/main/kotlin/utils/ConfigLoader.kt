package utils

import model.ConfigModel
import exception.ApplicationPropertiesException
import exception.ApplicationYamlException
import exception.ConfigFileException
import exception.ConfigLoaderException
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.util.*

class ConfigLoader(
    private val pathBase: String = System.getProperty("user.dir"),
    private val pathResource: String = "$pathBase${File.separator}src${File.separator}main${File.separator}resources"
) {

    @Throws(ConfigLoaderException::class)
    fun getConfig(): ConfigModel {
        var configModel: ConfigModel? = null
        try {
            val configFile = getConfigFile()
            val profile =
                if (configFile.extension == "properties") {
                    getProfileFromProperties()
                } else {
                    getProfileFromYaml(configFile.name)
                }
            val configFileByProfile = getConfigFile("-$profile")
            configModel =
                if (configFileByProfile.extension == "properties") {
                    getConfigModelFromProperties(profile)
                } else {
                    getConfigModelFromYaml(configFileByProfile.name)
                }
        } catch (e: Exception) {
            when (e) {
                is ConfigFileException,
                is ApplicationPropertiesException,
                is ApplicationYamlException -> {
                    println("\u001B[31m${e.message}\u001B[0m")
                }
            }
        }

        return configModel ?: throw ConfigLoaderException("")
    }

    @Throws(ApplicationPropertiesException::class)
    private fun getProfileFromProperties(): String {
        val properties = Properties()
        val applicationProperties = FileInputStream("${pathResource}${File.separator}application.properties")
        properties.load(applicationProperties)
        applicationProperties.close()

        return properties.getProperty("spring.profiles.active")
            ?: throw ApplicationPropertiesException("spring.profiles.active not found in application.properties")
    }

    @Throws(ApplicationPropertiesException::class)
    private fun getConfigModelFromProperties(profile: String): ConfigModel {
        val properties = Properties()
        val applicationProperties = FileInputStream("${pathResource}${File.separator}application-$profile.properties")
        properties.load(applicationProperties)
        applicationProperties.close()
        val configModel = ConfigModel.Builder
            .driver(
                properties.getProperty("dbvault.driver")
                    ?: throw ApplicationPropertiesException("dbvault.driver not found in application.properties")
            )
            .url(
                properties.getProperty("dbvault.url")
                    ?: throw ApplicationPropertiesException("dbvault.url not found in application.properties")
            )
            .username(
                properties.getProperty("dbvault.username")
                    ?: throw ApplicationPropertiesException("dbvault.username not found in application.properties")
            )
            .password(
                properties.getProperty("dbvault.password")
                    ?: throw ApplicationPropertiesException("dbvault.password not found in application.properties")
            )

        return configModel.build()
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(ApplicationYamlException::class)
    private fun getProfileFromYaml(fileName: String): String {
        val yaml = Yaml()
        val applicationYaml = FileInputStream("${pathResource}${File.separator}$fileName")
        val data: Map<String, Objects> =
            yaml.load(applicationYaml) ?: throw ApplicationYamlException("$fileName is empty")
        val spring: Map<String, Objects> =
            if (data.containsKey("spring")) {
                data["spring"] as Map<String, Objects>
            } else {
                throw ApplicationYamlException("spring not found in $fileName")
            }
        val profile: Map<String, Objects> =
            if (spring.containsKey("profiles")) {
                spring["profiles"] as Map<String, Objects>
            } else {
                throw ApplicationYamlException("spring:profiles not found in $fileName")
            }
        val active: String =
            if (profile.containsKey("active")) {
                profile["active"].toString()
            } else {
                throw ApplicationYamlException("spring:profiles:active not found in $fileName")
            }
        return active
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(ApplicationYamlException::class)
    private fun getConfigModelFromYaml(fileName: String): ConfigModel {
        val yaml = Yaml()
        val applicationYaml = FileInputStream("${pathResource}${File.separator}$fileName")
        val data: Map<String, Objects> =
            yaml.load(applicationYaml) ?: throw ApplicationYamlException("$fileName is empty")
        val dbvault: Map<String, Objects> =
            if (data.containsKey("dbvault")) {
                data["dbvault"] as Map<String, Objects>
            } else {
                throw ApplicationYamlException("dbvault not found in $fileName")
            }
        val driver: String =
            if (dbvault.containsKey("driver")) {
                dbvault["driver"].toString()
            } else {
                throw ApplicationYamlException("dbvault:driver not found in $fileName")
            }
        val url: String =
            if (dbvault.containsKey("url")) {
                dbvault["url"].toString()
            } else {
                throw ApplicationYamlException("dbvault:url not found in $fileName")
            }
        val username: String =
            if (dbvault.containsKey("username")) {
                dbvault["username"].toString()
            } else {
                throw ApplicationYamlException("dbvault:username not found in $fileName")
            }
        val password: String =
            if (dbvault.containsKey("password")) {
                dbvault["password"].toString()
            } else {
                throw ApplicationYamlException("dbvault:password not found in $fileName")
            }

        return ConfigModel.Builder
            .driver(driver)
            .url(url)
            .username(username)
            .password(password)
            .build()
    }

    @Throws(ConfigFileException::class)
    private fun getConfigFile(profile: String = ""): File {
        var configFilePath: File? = null
        val configFiles =
            listOf("application$profile.properties", "application$profile.yaml", "application$profile.yml")
        configFiles.forEach {
            val file = File("${pathResource}${File.separator}$it")
            if (file.exists()) {
                configFilePath = file
            }
        }
        return configFilePath
            ?: throw ConfigFileException("${configFiles.joinToString(", ")} not found in $pathResource")
    }
}