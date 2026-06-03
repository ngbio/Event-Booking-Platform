
package com.group3.repository;

import com.group3.pojo.StatusUser;


public interface StatusUserRepository {

    StatusUser getStatusUserById(Integer id);

    boolean changeStatusUser(Integer userId, Integer statusId);
}
