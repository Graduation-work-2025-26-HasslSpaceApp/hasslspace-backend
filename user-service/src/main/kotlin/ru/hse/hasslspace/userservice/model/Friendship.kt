package ru.hse.hasslspace.userservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.hse.hasslspace.userservice.model.Friendship.Companion.TABLE_NAME
import java.time.LocalDateTime
import java.util.*

@Table(name = TABLE_NAME)
data class Friendship(
    @Id
    @Column(ID_COLUMN_NAME)
    val id: UUID? = null,

    @Column(REQUESTER_ID_COLUMN_NAME)
    val requesterId: UUID,

    @Column(ADDRESSEE_ID_COLUMN_NAME)
    val addresseeId: UUID,

    @Column(STATUS_COLUMN_NAME)
    var status: FriendshipStatus? = FriendshipStatus.PENDING,

    @Column(CREATED_AT_COLUMN_NAME)
    val createdAt: LocalDateTime?
) {

    enum class FriendshipStatus {
        PENDING,
        ACCEPTED,
        DECLINED,
        BLOCKED
    }

    companion object {
        const val TABLE_NAME = "friendship"

        const val ID_COLUMN_NAME = "id"
        const val REQUESTER_ID_COLUMN_NAME = "requester_id"
        const val ADDRESSEE_ID_COLUMN_NAME = "addressee_id"
        const val STATUS_COLUMN_NAME = "status"
        const val CREATED_AT_COLUMN_NAME = "created_at"
    }
}
