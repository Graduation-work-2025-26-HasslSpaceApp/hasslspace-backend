package ru.hse.hasslspace.userservice.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.*

@Service
class PhotoService(
    private val s3Client: S3Client,
    @Value("\${app.s3.bucket}")
    private val bucket: String,
    @Value("\${app.s3.endpoint}")
    private val endpoint: String,
) {

    fun uploadFile(photo: MultipartFile, photoUrl: String? = null): String {
        val key = photoUrl?.substringAfter("$bucket/")
            ?: "$USER_PHOTO_TYPE/${UUID.randomUUID()}"

        PutObjectRequest.builder().apply {
            bucket(bucket)
            key(key)
            contentType(photo.contentType)
            acl(ObjectCannedACL.PUBLIC_READ)
        }.build().let { request ->
            RequestBody.fromBytes(photo.bytes).let { body ->
                s3Client.putObject(request, body)
            }
        }

        return "$endpoint/$bucket/$key"
    }

    fun downloadFile(key: String): ResponseEntity<ByteArray> {
        val objectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()

        s3Client.getObject(objectRequest).use { inputStream ->
            val data = inputStream.readAllBytes()

            val headers = HttpHeaders().apply {
                add(HttpHeaders.CONTENT_DISPOSITION, "inline")
                add(HttpHeaders.CONTENT_TYPE, inputStream.response().contentType())
            }

            return ResponseEntity.ok()
                .headers(headers)
                .body(data)
        }
    }

    companion object {
        const val USER_PHOTO_TYPE = "user"
    }
}