package ru.hse.hasslspace.serverservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.hse.hasslspace.serverservice.dto.ChannelDto
import ru.hse.hasslspace.serverservice.dto.CreateChannelRequest
import ru.hse.hasslspace.serverservice.dto.RoleInfoDto
import ru.hse.hasslspace.serverservice.dto.UpdateChannelDto
import ru.hse.hasslspace.serverservice.service.ChannelService
import java.util.*

@RestController
@RequestMapping(SERVER_SERVICE_BASE_PATH_URL)
class ChannelController(
    private val channelService: ChannelService
) {

    @PostMapping(SERVERS_CHANNELS_URL)
    fun createChannel(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID,
        @RequestBody request: CreateChannelRequest
    ): ResponseEntity<String> {
        return channelService.createChannel(userId, serverId, request)
    }

    @DeleteMapping(SERVERS_CHANNELS_URL)
    fun deleteChannel(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID,
        @RequestParam channelId: UUID
    ): ResponseEntity<String> {
        return channelService.deleteChannel(userId, serverId, channelId)
    }

    @GetMapping(SERVERS_CHANNELS_URL)
    fun getChannelInfo(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID,
        @RequestParam channelId: UUID
    ): ResponseEntity<ChannelDto> {
        return channelService.getChannelInfo(userId, serverId, channelId)
    }

    @PatchMapping(SERVERS_CHANNELS_URL)
    fun updateChannel(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID,
        @RequestParam channelId: UUID,
        @RequestBody request: UpdateChannelDto
    ): ResponseEntity<String> {
        return channelService.updateChannel(userId, serverId, channelId, request)
    }

    @PostMapping(SERVERS_CHANNELS_PERMISSIONS_URL)
    fun assignRolePermission(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID,
        @RequestParam channelId: UUID,
        @RequestParam roleId: UUID,
    ): ResponseEntity<String> {
        return channelService.assignRolePermission(userId, serverId, channelId, roleId)
    }

    @GetMapping(SERVERS_CHANNELS_PERMISSIONS_URL)
    fun getChannelPermissions(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID,
        @RequestParam channelId: UUID
    ): ResponseEntity<List<RoleInfoDto>> {
        return channelService.getChannelPermissions(userId, serverId, channelId)
    }

    @DeleteMapping(SERVERS_CHANNELS_PERMISSIONS_URL)
    fun removeRolePermission(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam serverId: UUID,
        @RequestParam channelId: UUID,
        @RequestParam roleId: UUID,
    ): ResponseEntity<String> {
        return channelService.removeRolePermission(userId, serverId, channelId, roleId)
    }

}