package com.gameexpert.player.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreatePlayerRequest {

    // Lv 3: 닉네임 검증 (2~12자, 영문/숫자/밑줄)
    @NotBlank
    @Size(min=2, max=12)
    @Pattern(regexp="^[a-zA-Z0-9_]+$")
    private final String nickname;

    public CreatePlayerRequest(String nickname) {
        this.nickname = nickname;
    }
}
