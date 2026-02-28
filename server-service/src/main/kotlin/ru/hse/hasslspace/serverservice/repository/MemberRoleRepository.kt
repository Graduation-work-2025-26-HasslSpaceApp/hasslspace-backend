package ru.hse.hasslspace.serverservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.serverservice.model.MemberRole
import java.util.*

@Repository
interface MemberRoleRepository : CrudRepository<MemberRole, Map<String, UUID>> {
    //todo: надо подумать над ключом, мб такой вариант работать не будет
    // на редите нашел это https://spring.io/blog/2025/07/22/spring-data-jdbc-composite-id
}
