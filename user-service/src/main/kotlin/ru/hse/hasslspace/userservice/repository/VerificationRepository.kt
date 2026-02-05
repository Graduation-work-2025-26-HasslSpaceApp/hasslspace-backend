package ru.hse.hasslspace.userservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.userservice.model.Verification
import java.util.UUID

@Repository
interface VerificationRepository : CrudRepository<Verification, UUID> {

    @Query(
        """
            select *
            from public."verification"
            where user_id = :userId
        """
    )
    fun findByUserId(userId: UUID): Verification?

}
