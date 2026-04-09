package ru.hse.hasslspace.serverservice.model.converter

import org.springframework.stereotype.Component
import ru.hse.hasslspace.serverservice.dto.UpdateChannelDto
import ru.hse.hasslspace.serverservice.model.Channel

@Component
class UpdateChannelDtoToChannelConverter {

    fun convert(channel: Channel, updateChannelDto: UpdateChannelDto): Channel {
        return Channel(
            id = channel.id,
            serverId = channel.serverId,
            name = updateChannelDto.name ?: channel.name,
            type = Channel.ChannelType.valueOf(updateChannelDto.type?: channel.type.toString()),
            position = updateChannelDto.position ?: channel.position,
            maxMembers = updateChannelDto.maxMembers ?: channel.maxMembers,
            isPrivate = updateChannelDto.isPrivate ?: channel.isPrivate,
        )
    }
}