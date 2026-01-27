package ru.hse.hasslspace.userservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ru.hse.hasslspace.userservice.model.User.Companion.TABLE_NAME
import java.util.*
import java.util.Collections.emptyList

@Table(TABLE_NAME)
data class User(
    @Id
    @Column(ID_COLUMN_NAME)
    val id: UUID? = null,

    @Column(USERNAME_COLUMN_NAME)
    var username: String,

    @Column(NAME_COLUMN_NAME)
    var name: String,

    @Column(EMAIL_COLUMN_NAME)
    val email: String,

    @Column(PASSWORD_COLUMN_NAME)
    val password: String,

    @Column(PHOTO_URL_COLUMN_NAME)
    var photoUrl: String? = null,

    @Column(USER_INFO_COLUMN_NAME)
    var userInfo: String? = null,

    @Column(STATUS_COLUMN_NAME)
    var status: List<StatusType>? = listOf(StatusType.ONLINE),

    @Column(ROLES_COLUMN_NAME)
    val roles: List<AuthorityType>? = listOf(AuthorityType.DEFAULT),

    @Column(IS_VERIFIED_COLUMN_NAME)
    var isVerified: Boolean? = false,
) : UserDetails {

    override fun getAuthorities(): MutableList<AuthorityType> = (roles ?: emptyList()) as MutableList<AuthorityType>

    override fun getUsername() = username

    override fun getPassword() = password

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true

    enum class AuthorityType : GrantedAuthority {
        ADMIN,
        DEFAULT;

        override fun getAuthority() = this.name
    }

    enum class StatusType {
        ONLINE,
        OFFLINE
        // TODO: Add more status types if needed
    }

    companion object {
        const val TABLE_NAME = "user"

        const val ID_COLUMN_NAME = "id"
        const val USERNAME_COLUMN_NAME = "username"
        const val NAME_COLUMN_NAME = "name"
        const val EMAIL_COLUMN_NAME = "email"
        const val PASSWORD_COLUMN_NAME = "password"
        const val PHOTO_URL_COLUMN_NAME = "photo_url"
        const val USER_INFO_COLUMN_NAME = "user_info"
        const val STATUS_COLUMN_NAME = "status"
        const val ROLES_COLUMN_NAME = "roles"
        const val IS_VERIFIED_COLUMN_NAME = "is_verified"
    }
}