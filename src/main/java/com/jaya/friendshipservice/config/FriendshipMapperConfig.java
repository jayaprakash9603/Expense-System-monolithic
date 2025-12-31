
package com.jaya.friendshipservice.config;


import com.jaya.friendshipservice.util.FriendshipMapper;
import com.jaya.userservice.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FriendshipMapperConfig {

    @Autowired
    private UserService userService;

    @PostConstruct
    public void initFriendshipMapper() {
        FriendshipMapper.setUserService(userService);
    }
}