package ru.hse.hasslspace.serverservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.hse.hasslspace.serverservice.dto.FriendsListDto
import ru.hse.hasslspace.serverservice.dto.ServerMemberDto
import ru.hse.hasslspace.serverservice.service.ServerMemberService
import java.util.*

@RestController
@RequestMapping(SERVER_SERVICE_BASE_PATH_URL)
class ServerMemberController(
    private val serverMemberService: ServerMemberService
) {

    @PostMapping(INVITES_JOIN_URL)
    fun joinServer(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam code: String
    ): ResponseEntity<String> {
        return serverMemberService.joinServerByInvite(userId, code)
    }

    @DeleteMapping(SERVERS_MEMBERS_ME_URL)
    fun leaveServer(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID
    ): ResponseEntity<String> {
        return serverMemberService.leaveServer(userId, serverId)
    }

    @GetMapping(SERVERS_MEMBERS_URL)
    fun getServerMembers(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID
    ): ResponseEntity<List<ServerMemberDto>> {
        return serverMemberService.getServerMembers(userId, serverId)
    }

    @DeleteMapping(SERVERS_MEMBERS_URL)
    fun kickMember(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID,
        @RequestParam targetUserId: UUID
    ): ResponseEntity<String> {
        return serverMemberService.kickMember(userId, serverId, targetUserId)
    }

    @GetMapping(SERVERS_MEMBERS_NOT_IN_SERVER_URL)
    fun getFriendsNotInServer(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID
    ): ResponseEntity<List<FriendsListDto>> {
        return serverMemberService.getFriendsNotInServer(userId, serverId)
    }
}