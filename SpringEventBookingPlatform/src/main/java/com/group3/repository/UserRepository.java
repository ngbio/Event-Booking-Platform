
package com.group3.repository;
import com.group3.pojo.User;
import java.util.List;
import java.util.Map;


public interface UserRepository {
    List<User> getUsers(Map<String, String> params);

    Long countUsers(Map<String, String> params);

    User findUserById(Integer id);

    User findUserByEmail(String email);

    boolean existEmail(String email);

    User updateUser(User user);

    Long count();

    User addUser(User u);
        
    boolean authenticate(String email, String password);
    
    boolean changePassword(Integer userId, String newEncryptedPassword);
}
