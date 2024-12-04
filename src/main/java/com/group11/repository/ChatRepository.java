package com.group11.repository;

import com.group11.entity.ChatEntity;
import com.group11.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    @Query("SELECT DISTINCT u " +
            "FROM ChatEntity c " +
            "JOIN UserEntity u ON c.senderID = u.userID " +
            "WHERE c.receiverID = 1")
    List<UserEntity> findDistinctSendersByReceiverId();

}
