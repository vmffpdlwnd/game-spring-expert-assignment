package com.gameexpert.chat.service;

import com.gameexpert.chat.entity.ChatMessage;
import com.gameexpert.chat.event.ChatSavedEvent;
import org.springframework.context.ApplicationEventPublisher;
import com.gameexpert.chat.repository.ChatMessageRepository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gameexpert.chat.dto.ChatMessageResponse;
import com.gameexpert.common.NotFoundException;
import com.gameexpert.world.repository.WorldRepository;
import com.gameexpert.world.entity.World;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final int MAX_LIMIT = 100;

    private final ChatMessageRepository chatMessageRepository;
    private final WorldRepository worldRepository;
    private final ApplicationEventPublisher events;

    @Transactional
    public ChatMessageResponse saveMessage(Long worldId, String sender, String content) {
        // Lv 5: 월드 조회 후 채팅 저장
        World world = worldRepository.findById(worldId)
                .orElseThrow(() -> new NotFoundException("WORLD_NOT_FOUND"));
        ChatMessage saved = chatMessageRepository.save(new ChatMessage(world,sender,content));
        return savedResponse(worldId, saved);
        }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getRecentMessages(Long worldId, int limit) {
        if (!worldRepository.existsById(worldId)) {
            throw new NotFoundException("WORLD_NOT_FOUND");
        }

        int capped = Math.min(Math.max(limit, 1), MAX_LIMIT);

        List<ChatMessage> recent = chatMessageRepository
                .findByWorldIdOrderByCreatedAtDescIdDesc(worldId, PageRequest.of(0, capped));

        // Lv 5: 최신순으로 가져온 결과를 오래된 순서로 뒤집어 응답 변환
        List<ChatMessage> ascending = new java.util.ArrayList<>(recent);
        java.util.Collections.reverse(ascending);

        return ascending.stream()
                .map(m -> new ChatMessageResponse(m.getSenderNickname(), m.getContent(), m.getCreatedAt()))
                .toList();

    }

    private ChatMessageResponse savedResponse(Long worldId, ChatMessage saved) {
        events.publishEvent(new ChatSavedEvent(
                worldId,
                saved.getSenderNickname(),
                saved.getContent(),
                saved.getCreatedAt()
        ));
        return new ChatMessageResponse(
                saved.getSenderNickname(),
                saved.getContent(),
                saved.getCreatedAt()
        );
    }
}
