package ru.hse.hasslspace.serverservice.service

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory
import ru.hse.hasslspace.serverservice.dto.*
import ru.hse.hasslspace.serverservice.dto.converter.ChannelToChannelDtoConverter
import ru.hse.hasslspace.serverservice.model.Channel
import ru.hse.hasslspace.serverservice.model.converter.UpdateChannelDtoToChannelConverter
import ru.hse.hasslspace.serverservice.repository.*
import java.util.*

@Service
class ChannelService(
    private val serverRepository: ServerRepository,
    private val serverMemberRepository: ServerMemberRepository,
    private val channelRepository: ChannelRepository,
    private val channelToChannelDtoConverter: ChannelToChannelDtoConverter,
    private val updateChannelDtoToChannelConverter: UpdateChannelDtoToChannelConverter,
) {

    @Transactional
    fun createChannel(
        userId: UUID,
        serverId: UUID,
        request: CreateChannelRequest
    ): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            // TODO: исправить, пока только владелец может создавать каналы
            if (server.ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Только владелец сервера может создавать каналы")
            }

            val savedChannel = channelRepository.save(
                Channel(
                    serverId = serverId,
                    name = request.name,
                    type = Channel.ChannelType.valueOf(request.type),
                    position = request.position,
                    maxMembers = request.maxMembers,
                    isPrivate = request.isPrivate
                )
            )

            logger.info("Channel '${request.name}' created in server $serverId by user $userId")

            ResponseEntity.status(HttpStatus.CREATED).body("Канал успешно создан")
        } catch (e: Exception) {
            logger.error("Error while creating channel", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при создании канала")
        }
    }

    @Transactional
    fun getChannelInfo(userId: UUID, serverId: UUID, channelId: UUID): ResponseEntity<ChannelDto> {
        return try {
            serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

            val channel = channelRepository.findById(channelId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

            serverMemberRepository.findByServerIdAndUserId(channel.serverId, userId)
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

            // TODO: добавить проверку прав доступа для приватного канала

            ResponseEntity.ok(channelToChannelDtoConverter.convert(channel))
        } catch (e: Exception) {
            logger.error("Error while getting channel info", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @Transactional
    fun deleteChannel(userId: UUID, serverId: UUID, channelId: UUID): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            //TODO: исправить, пока только владелец может удалять каналы
            if (server.ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Только владелец сервера может удалять каналы")
            }

            val channel = channelRepository.findById(channelId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Канал не найден")

            if (channel.serverId != serverId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Канал не принадлежит этому серверу")
            }

            // TODO: пока не создаем никаких доступов
            //channelPermissionRepository.deleteByChannelId(channelId)

            channelRepository.deleteById(channelId)

            logger.info("Channel $channelId deleted from server $serverId by user $userId")

            ResponseEntity.ok("Канал успешно удален")
        } catch (e: Exception) {
            logger.error("Error while deleting channel", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при удалении канала")
        }
    }

    @Transactional
    fun updateChannel(userId: UUID, serverId: UUID, channelId: UUID, request: UpdateChannelDto): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            //TODO: исправить, пока только владелец может обновлять каналы
            if (server.ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Только владелец сервера может изменять каналы")
            }

            val channel = channelRepository.findById(channelId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Канал не найден")

            if (channel.serverId != serverId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Канал не принадлежит этому серверу")
            }

            channelRepository.save(updateChannelDtoToChannelConverter.convert(channel, request))

            logger.info("Channel $channelId is updated in server $serverId by user $userId")

            ResponseEntity.ok("Канал успешно обновлен")
        } catch (e: Exception) {
            logger.error("Error while updating channel", e)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении канала")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ChannelService::class.java)
    }
}