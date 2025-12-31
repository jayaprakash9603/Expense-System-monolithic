package com.jaya.friendshipservice.dto;

import com.jaya.friendshipservice.models.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInviteRequestDTO {
    private Integer userId;
    private GroupRole role;
    private String message;
}