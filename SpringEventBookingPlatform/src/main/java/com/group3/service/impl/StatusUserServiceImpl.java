package com.group3.service.impl;

import com.group3.pojo.StatusUser;
import com.group3.pojo.User;
import com.group3.repository.StatusUserRepository;
import com.group3.repository.UserRepository;
import com.group3.service.StatusUserService;
import com.group3.service.TicketEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusUserServiceImpl implements StatusUserService{
    @Autowired
    private StatusUserRepository statusUserRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TicketEmailService ticketEmailService;

    private static final int ROLE_ORGANIZER = 2;
    private static final int STATUS_PENDING = 1;
    private static final int STATUS_ACTIVE = 2;
    
    @Override
    public boolean changeStatusUser(Integer userId, Integer statusId){
        User user = this.userRepo.findUserById(userId);
        Integer oldStatusId = user != null && user.getStatusId() != null ? user.getStatusId().getId() : null;
        Integer roleId = user != null && user.getRoleId() != null ? user.getRoleId().getId() : null;

        boolean success = this.statusUserRepo.changeStatusUser(userId, statusId);
        if (success
                && Integer.valueOf(ROLE_ORGANIZER).equals(roleId)
                && Integer.valueOf(STATUS_PENDING).equals(oldStatusId)
                && Integer.valueOf(STATUS_ACTIVE).equals(statusId)) {
            ticketEmailService.sendOrganizerApprovedEmail(user.getEmail(), user.getFullName());
        }

        return success;
    }
    
    @Override
    public StatusUser getStatusUserById(Integer id){
        return this.statusUserRepo.getStatusUserById(id);
    }
    
}
