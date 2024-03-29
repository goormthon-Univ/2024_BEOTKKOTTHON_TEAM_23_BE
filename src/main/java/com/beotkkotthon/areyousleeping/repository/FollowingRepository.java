package com.beotkkotthon.areyousleeping.repository;

import com.beotkkotthon.areyousleeping.domain.Following;
import com.beotkkotthon.areyousleeping.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowingRepository extends JpaRepository<Following, Long> {
    Optional<Following> findBySenderAndReceiver(User sender, User receiver);
    List<Following> findBySenderId(Long senderId);
}
