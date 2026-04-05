package ru.hse.hasslspace.serverservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.hse.hasslspace.serverservice.dto.CreateServerRequest
import ru.hse.hasslspace.serverservice.service.ServerService
import java.util.*

@RestController
@RequestMapping(SERVER_SERVICE_BASE_PATH_URL)
class ServerController(
    private val serverService: ServerService
) {

    @PostMapping(SERVERS_URL)
    fun createServer(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestBody request: CreateServerRequest
    ): ResponseEntity<String> =
        serverService.createServer(userId, request)

    @GetMapping(SERVERS_URL)
    fun getServer(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam(required = false) serverId: UUID?
    ): ResponseEntity<*> {
        return if (serverId != null) {
            serverService.getServer(userId, serverId)
        } else {
            serverService.getAllUserServers(userId)
        }
    }

    @DeleteMapping(SERVERS_URL)
    fun deleteServer(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID
    ): ResponseEntity<String> =
        serverService.deleteServer(userId, serverId)
}