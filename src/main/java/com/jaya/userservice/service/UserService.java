package com.jaya.userservice.service;



import com.jaya.userservice.modal.User;
import com.jaya.userservice.request.SignupRequest;
import com.jaya.userservice.request.UserUpdateRequest;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface UserService {

    public User getUserProfile(String jwt);

    User findUserById( Integer id) throws Exception;

    public List<User> getAllUsers();

    public User getUserByEmail(String email);

    public User updateUserProfile(String jwt, UserUpdateRequest updateRequest);

    void deleteUser(Integer id) throws AccessDeniedException;

    public boolean checkEmailAvailability(String email);

    public User findByEmail(String email);

    public void updatePassword(User user, String newPassword);

    // Register a new user, handling roles and password hashing
    public User signup(SignupRequest signupRequest);

    // Switch user mode between USER and ADMIN
    public User switchUserMode(String jwt, String newMode);

}
