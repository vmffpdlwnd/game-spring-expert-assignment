package com.gameexpert.player.service;

import com.gameexpert.player.repository.PlayerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gameexpert.common.ConflictException;
import com.gameexpert.player.dto.CreatePlayerRequest;
import com.gameexpert.player.entity.Player;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Transactional
    public void createPlayer(CreatePlayerRequest request) {
        // Lv 3: 저장, 중복은 savePlayer의 제약 위반 처리로 걸러짐
        if (playerRepository.existsByNickname(request.getNickname()))
            throw new ConflictException("DUPLICATE_NICKNAME");
        savePlayer(new Player(request.getNickname()));
    }

    private void savePlayer(Player player) {
        try {
            playerRepository.saveAndFlush(player);
        } catch (org.springframework.dao.DataIntegrityViolationException failure) {
            throw new ConflictException("DUPLICATE_NICKNAME");
        }
    }
}
