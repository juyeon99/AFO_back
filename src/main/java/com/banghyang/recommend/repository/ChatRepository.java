package com.banghyang.recommend.repository;

import com.banghyang.recommend.dto.ChatDto;
import com.banghyang.recommend.entity.Chat;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String> {

    @Aggregation(pipeline = {
            "{ $match: { member_id: ?0 } }",        // member_id로 직접 매칭
            "{ $sort: { timestamp: 1 } }",          // 오름차순 정렬
            "{ $group: { "
                    + "_id: '$type', "
                    + "messages: { $push: '$$ROOT' }"
                    + "} }",
            "{ $unwind: { "
                    + "path: '$messages', "
                    + "preserveNullAndEmptyArrays: true"
                    + "} }",
            "{ $replaceRoot: { newRoot: '$messages' } }"
    })
    List<Chat> findByMemberId(Long memberId);
}
