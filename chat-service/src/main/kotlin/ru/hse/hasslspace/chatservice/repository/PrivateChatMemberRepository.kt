package ru.hse.hasslspace.chatservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.chatservice.model.PrivateChatMember

@Repository
interface PrivateChatMemberRepository : CrudRepository<PrivateChatMember, PrivateChatMember.PrivateChatMemberId> {

}