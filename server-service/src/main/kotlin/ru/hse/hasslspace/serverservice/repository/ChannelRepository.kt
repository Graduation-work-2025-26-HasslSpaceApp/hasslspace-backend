package ru.hse.hasslspace.serverservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.serverservice.model.Channel
import java.util.*

@Repository
interface ChannelRepository : CrudRepository<Channel, UUID> {
}