package com.group3.service;

import com.group3.pojo.StatusUser;

public interface StatusUserService {
    StatusUser getStatusUserById(Integer id);

    boolean changeStatusUser(Integer userId, Integer statusId);
}
