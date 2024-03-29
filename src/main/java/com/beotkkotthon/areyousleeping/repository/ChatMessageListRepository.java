package com.beotkkotthon.areyousleeping.repository;

import com.beotkkotthon.areyousleeping.domain.nosql.ChatMessageList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageListRepository extends MongoRepository<ChatMessageList, String> {
}
