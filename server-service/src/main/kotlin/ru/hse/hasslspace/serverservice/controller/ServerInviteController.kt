package ru.hse.hasslspace.serverservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.hse.hasslspace.serverservice.dto.ServerInviteDto
import ru.hse.hasslspace.serverservice.service.ServerInviteService
import java.util.UUID

@RestController
@RequestMapping(SERVER_SERVICE_BASE_PATH_URL)
class ServerInviteController(
    private val serverInviteService: ServerInviteService
) {

    @PostMapping(SERVER_INVITES_URL)
    fun createInvite(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID
    ): ResponseEntity<ServerInviteDto> {
        return serverInviteService.createInvite(userId, serverId)
    }

    @GetMapping(SERVER_INVITES_URL)
    fun getActiveInvites(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID
    ): ResponseEntity<List<ServerInviteDto>> {
        return serverInviteService.getActiveInvites(userId, serverId)
    }

    @DeleteMapping(SERVER_INVITES_URL)
    fun deleteInvite(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam inviteCode: String
    ): ResponseEntity<String> {
        return serverInviteService.deleteInvite(userId, inviteCode)
    }
}
