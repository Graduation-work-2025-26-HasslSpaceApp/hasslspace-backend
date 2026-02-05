package ru.hse.hasslspace.userservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.hse.hasslspace.userservice.model.Verification.Companion.TABLE_NAME
import java.time.LocalDateTime
import java.util.UUID

@Table(name = TABLE_NAME)
data class Verification(
    @Id
    @Column(ID_COLUMN_NAME)
    val id: UUID? = null,

    @Column(USER_ID_COLUMN_NAME)
    val userId: UUID,

    @Column(CODE_COLUMN_NAME)
    val code: String,

    @Column(CREATED_AT_COLUMN_NAME)
    val createdAt: LocalDateTime?
) {

    companion object {
        const val TABLE_NAME = "verification"

        const val ID_COLUMN_NAME = "id"
        const val USER_ID_COLUMN_NAME = "user_id"
        const val CODE_COLUMN_NAME = "code"
        const val CREATED_AT_COLUMN_NAME = "created_at"
    }
}
