package com.group3.service;

import com.group3.pojo.User;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends UserDetailsService {
    User getUserByUsername(String username) ;
    User addUser(Map<String, String> params, MultipartFile avatar);
    boolean authenticate(String username, String password);
}
