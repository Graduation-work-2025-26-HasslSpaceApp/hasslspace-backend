package ru.hse.hasslspace.serverservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.hse.hasslspace.serverservice.model.Server.Companion.TABLE_NAME
import java.time.LocalDateTime
import java.util.*

@Table(TABLE_NAME)
data class Server(
    @Id
    @Column(ID_COLUMN_NAME)
    val id: UUID? = null,

    @Column(NAME_COLUMN_NAME)
    var name: String,

    @Column(OWNER_ID_COLUMN_NAME)
    val ownerId: UUID,

    @Column(ICON_URL_COLUMN_NAME)
    var iconUrl: String? = null,

    @Column(CREATED_AT_COLUMN_NAME)
    val createdAt: LocalDateTime?
) {

    companion object {
        const val TABLE_NAME = "server"

        const val ID_COLUMN_NAME = "id"
        const val NAME_COLUMN_NAME = "name"
        const val OWNER_ID_COLUMN_NAME = "owner_id"
        const val ICON_URL_COLUMN_NAME = "icon_url"
        const val CREATED_AT_COLUMN_NAME = "created_at"
    }
}