package repository.datasource

import com.j0k3r6017.model.Config
import com.j0k3r6017.repository.datasource.RemoteDataSource
import com.j0k3r6017.utils.ConfigLoader
import org.junit.jupiter.api.Test

class RemoteDataSourceTest {

    private lateinit var remoteDataSource: RemoteDataSource

    @Test
    fun givenRemoteDataSource_whenGetConnection_thenShouldReturnConnection() {
        val configLoader = ConfigLoader()
        val configModel = configLoader.getConfig()
        val config = Config.Builder
            .driver(configModel.driver)
            .url(configModel.url)
            .username(configModel.username)
            .password(configModel.password)
            .build()
        remoteDataSource = RemoteDataSource(config)
        val connection = remoteDataSource.getConnection()
        assert(connection != null)
    }
}