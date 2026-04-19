package ru.hse.hasslspace.chatservice.controller

import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.hse.hasslspace.chatservice.service.S3Service

@RestController
@RequestMapping(CHAT_SERVICE_BASE_PATH_URL)
class S3Controller(
    private val s3Service: S3Service
)  {

    @PutMapping(UPLOAD_FILE_URL, consumes = [MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(
        @RequestParam file: MultipartFile,
        @RequestParam fileType: String,
        @RequestParam(required = false) fileUrl: String?
    ): String =
        s3Service.uploadFile(file, fileType, fileUrl)

    @GetMapping(DOWNLOAD_FILE_URL)
    fun downloadFile(@RequestParam key: String): ResponseEntity<ByteArray> =
        s3Service.downloadFile(key)
}
