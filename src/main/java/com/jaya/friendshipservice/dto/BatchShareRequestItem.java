package com.jaya.friendshipservice.dto;

import lombok.Data;

@Data
public class BatchShareRequestItem {
    private Integer userId;
    private String accessLevel;
}