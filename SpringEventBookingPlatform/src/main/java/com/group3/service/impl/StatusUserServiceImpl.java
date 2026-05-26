package com.group3.service.impl;

import com.group3.pojo.StatusUser;
import com.group3.repository.StatusUserRepository;
import com.group3.service.StatusUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusUserServiceImpl implements StatusUserService{
    @Autowired
    private StatusUserRepository statusUserRepo;
    
    @Override
    public boolean changeStatusUser(Integer userId, Integer statusId){
        return this.statusUserRepo.changeStatusUser(userId, statusId);
    }
    
    @Override
    public StatusUser getStatusUserById(Integer id){
        return this.statusUserRepo.getStatusUserById(id);
    }
    
}
