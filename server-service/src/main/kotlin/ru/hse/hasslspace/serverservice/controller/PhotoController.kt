package ru.hse.hasslspace.serverservice.controller

import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.hse.hasslspace.serverservice.service.PhotoService


@RestController
@RequestMapping(SERVER_SERVICE_BASE_PATH_URL)
class PhotoController(
    private val photoService: PhotoService
)  {

    @PutMapping(UPLOAD_PHOTO_URL, consumes = [MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(
        @RequestParam photo: MultipartFile,
        @RequestParam(required = false) photoUrl: String?
    ): String =
        photoService.uploadFile(photo, photoUrl)

    @GetMapping(DOWNLOAD_PHOTO_URL)
    fun downloadFile(@RequestParam key: String): ResponseEntity<ByteArray> =
        photoService.downloadFile(key)
}
