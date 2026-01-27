package ru.hse.hasslspace.userservice.repository

import org.springframework.data.repository.CrudRepository
import ru.hse.hasslspace.userservice.model.Verification
import java.util.UUID

interface VerificationRepository : CrudRepository<Verification, UUID> {

}
