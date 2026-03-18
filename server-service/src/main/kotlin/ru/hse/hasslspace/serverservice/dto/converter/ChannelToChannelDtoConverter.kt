package ru.hse.hasslspace.serverservice.dto.converter

import org.springframework.stereotype.Component
import ru.hse.hasslspace.serverservice.dto.ChannelDto
import ru.hse.hasslspace.serverservice.model.Channel

@Component
class ChannelToChannelDtoConverter {

    fun convert(channel: Channel): ChannelDto {
        return ChannelDto(
            id = channel.id.toString(),
            name = channel.name,
            type = channel.type.name,
            position = channel.position,
            maxMembers = channel.maxMembers,
            isPrivate = channel.isPrivate
        )
    }
}