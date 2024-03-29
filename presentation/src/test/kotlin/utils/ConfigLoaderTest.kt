package utils

import com.j0k3r6017.exception.ConfigLoaderException
import com.j0k3r6017.utils.ConfigLoader
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.File

class ConfigLoaderTest {

    private lateinit var configLoader: ConfigLoader

    @Test
    fun givenConfigLoader_whenGetConfigByProperties_thenReturnConfig() {
        configLoader =
            ConfigLoader(
                pathResource =
                "${System.getProperty("user.dir")}${File.separator}src${File.separator}test${File.separator}resources${File.separator}properties${File.separator}default"
            )
        assertDoesNotThrow { configLoader.getConfig() }
    }

    @Test
    fun givenConfigLoader_whenGetConfigByYaml_thenReturnConfig() {
        configLoader =
            ConfigLoader(
                pathResource =
                "${System.getProperty("user.dir")}${File.separator}src${File.separator}test${File.separator}resources${File.separator}yaml${File.separator}default"
            )
        assertDoesNotThrow { configLoader.getConfig() }
    }

    @Test
    fun givenConfigLoader_whenGetConfigByProperties_thenThrowConfigLoaderException() {
        configLoader =
            ConfigLoader(
                pathResource =
                "${System.getProperty("user.dir")}${File.separator}src${File.separator}test${File.separator}resources${File.separator}properties${File.separator}throw"
            )
        assertThrows<ConfigLoaderException> { configLoader.getConfig() }
    }

    @Test
    fun givenConfigLoader_whenGetConfigByYaml_thenThrowConfigLoaderException() {
        configLoader =
            ConfigLoader(
                pathResource =
                "${System.getProperty("user.dir")}${File.separator}src${File.separator}test${File.separator}resources${File.separator}yaml${File.separator}throw"
            )
        assertThrows<ConfigLoaderException> { configLoader.getConfig() }
    }
}