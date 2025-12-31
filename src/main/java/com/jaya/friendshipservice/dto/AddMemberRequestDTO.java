package com.jaya.friendshipservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberRequestDTO {
    @NotNull(message = "User ID is required")
    private Integer userId;
}