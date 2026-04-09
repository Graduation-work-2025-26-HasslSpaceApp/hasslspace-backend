package ru.hse.hasslspace.serverservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.hse.hasslspace.serverservice.dto.CreateServerRequest
import ru.hse.hasslspace.serverservice.dto.UpdateServerDto
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
        @RequestParam(required = false) serverId: UUID?,
        @RequestParam(required = false) friendId: UUID?
    ): ResponseEntity<*> {
        return if (serverId != null && friendId == null) {
            serverService.getServer(userId, serverId)
        } else if(serverId == null && friendId != null) {
            serverService.getSharedServers(userId, friendId)
        } else {
            serverService.getAllUserServers(userId)
        }
    }

    @PatchMapping(SERVERS_URL)
    fun updateServer(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID,
        @RequestBody request: UpdateServerDto
    ): ResponseEntity<String> =
        serverService.updateServer(userId, serverId, request)

    @DeleteMapping(SERVERS_URL)
    fun deleteServer(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID
    ): ResponseEntity<String> =
        serverService.deleteServer(userId, serverId)
}