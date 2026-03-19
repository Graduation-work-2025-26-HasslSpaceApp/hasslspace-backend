package ru.hse.hasslspace.serverservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.hse.hasslspace.serverservice.dto.CreateRoleRequest
import ru.hse.hasslspace.serverservice.dto.RoleInfoDto

import ru.hse.hasslspace.serverservice.service.ServerRoleService
import java.util.*

@RestController
@RequestMapping(SERVER_SERVICE_BASE_PATH_URL)
class ServerRoleController(
    private val serverRoleService: ServerRoleService
) {

    @PostMapping(SERVERS_ROLES_URL)
    fun createRole(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID,
        @RequestBody request: CreateRoleRequest
    ): ResponseEntity<String> {
        return serverRoleService.createRole(userId, serverId, request)
    }

    @GetMapping(SERVERS_ROLES_URL)
    fun getRoles(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID
    ): ResponseEntity<List<RoleInfoDto>> {
        return serverRoleService.getRoles(userId, serverId)
    }

    @DeleteMapping(SERVERS_ROLES_URL)
    fun deleteRole(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID,
        @RequestParam roleId: UUID
    ): ResponseEntity<String> {
        return serverRoleService.deleteRole(userId, serverId, roleId)
    }

    @PostMapping(SERVERS_MEMBERS_ROLES_URL)
    fun assignRole(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID,
        @RequestParam targetUserId: UUID,
        @RequestParam roleId: UUID
    ): ResponseEntity<String> {
        return serverRoleService.assignRole(userId, serverId, targetUserId, roleId)
    }

    @DeleteMapping(SERVERS_MEMBERS_ROLES_URL)
    fun removeRole(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID,
        @RequestParam targetUserId: UUID,
        @RequestParam roleId: UUID
    ): ResponseEntity<String> {
        return serverRoleService.removeRole(userId, serverId, targetUserId, roleId)
    }
}