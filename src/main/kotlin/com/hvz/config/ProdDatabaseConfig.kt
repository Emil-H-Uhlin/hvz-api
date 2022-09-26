package com.hvz.config

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.net.URI
import javax.sql.DataSource

@Configuration
@Profile("prod")
class ProdDatabaseConfig

@Bean
fun dataSource(): DataSource {
    val dbUri = URI(System.getenv("DATABASE_URL"))

    var usr: String
    var pwd: String

    val dbUrl = "jdbc:postgresql://" + dbUri.host + ":" + dbUri.port + dbUri.path

    with (dbUri.userInfo.split(":")) {
        usr = this[0]
        pwd = this[1]
    }

    return with(DataSourceBuilder.create()) {
        url(dbUrl)
        username(usr)
        password(pwd)
    }.build()
}