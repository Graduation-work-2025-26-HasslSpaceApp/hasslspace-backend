package ru.hse.hasslspace.chatservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.chatservice.model.Message
import java.util.UUID

@Repository
interface MessageRepository : CrudRepository<Message, UUID> {

}