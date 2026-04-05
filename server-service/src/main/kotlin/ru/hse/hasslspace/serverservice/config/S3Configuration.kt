package ru.hse.hasslspace.serverservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Configuration
class S3Configuration (
    @Value("\${app.s3.access-key}") private val accessKey: String,
    @Value("\${app.s3.secret-key}") private val secretKey: String,
    @Value("\${app.s3.region}") private val region: String,
    @Value("\${app.s3.endpoint}") private val endpoint: String
) {

    @Bean
    fun s3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        return S3Client.builder()
            .httpClient(ApacheHttpClient.create())
            .region(Region.of(region))
            .endpointOverride(URI.create(endpoint))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()
    }
}
