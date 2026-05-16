/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.group3.dto.request.LoginRequest;
import com.group3.dto.request.RegisterRequest;
import com.group3.dto.request.UserUpdateRequest;
import com.group3.pojo.User;
import com.group3.dto.response.UserResponse;
import com.group3.pojo.Role;
import com.group3.pojo.StatusUser;
import com.group3.repository.UserRepository;
import com.group3.repository.RoleRepository;
import com.group3.repository.StatusUserRepository;

import com.group3.service.UserService;
import com.group3.utils.DTOMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author THUAN
 */
@Service

public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private StatusUserRepository statusUserRepo;

    @Override
    public List<UserResponse> getUsers(Map<String, String> params) {
        List<User> users = this.userRepo.getUsers(params);
        return DTOMapper.toUserResponseList(users);
    }

    @Override
    public Long countUsers(Map<String, String> params) {
        return this.userRepo.countUsers(params);
    }

    @Override
    public void deleteUser(int id) {
        this.userRepo.deleteUser(id);
    }

    @Override
    public UserResponse getUserById(int id) {
        User user = this.userRepo.findUserById(id);
        return DTOMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = this.userRepo.findUserByEmail(email);
        return DTOMapper.toUserResponse(user);
    }

    @Override
    public boolean checkExistEmail(String email) {
        return this.userRepo.existEmail(email);
    }

//    @Override
//    public boolean checkExistUsername(String username){
//        return this.userRepo.existUsername(username);
//    }
    @Override
    public Long countUsers() {
        return this.userRepo.count();
    }

//    @Override
//    public UserResponse getUserByUsername(String username) {
//        User user = this.userRepo.getUserByUsername(username);
//        return DTOMapper.toUserResponse(user);
//    }
    @Override
    public UserResponse addUser(RegisterRequest request, MultipartFile avatar, int roleId) {
        User user = DTOMapper.toUserEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role userRole = roleRepo.findById(roleId);
        user.setRoleId(userRole);

        int statusId = roleId == 2 ? 1 : 2;
        StatusUser statusUser = statusUserRepo.getStatusUserById(statusId);
        user.setStatusId(statusUser);

        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(avatar.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                user.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return DTOMapper.toUserResponse(this.userRepo.addUser(user));
    }

    @Override
    public UserResponse updateUser(Integer id, UserUpdateRequest request, MultipartFile avatar) {
        User user = this.userRepo.findUserById(id);
        if (user == null) {
            return null;
        }
        //Kiem tra 4 cot cua nha to chuc  co thay doi hay khong
        boolean isLegalDocumentChanged = false;

        if (user.getRoleId() != null && user.getRoleId().getId() == 2) {
            if (request.getIdentityCard() != null && !request.getIdentityCard().equals(user.getIdentityCard())) {
                isLegalDocumentChanged = true;
            }
            if (request.getBusinessLicense() != null && !request.getBusinessLicense().equals(user.getBusinessLicense())) {
                isLegalDocumentChanged = true;
            }
            if (request.getOrganizationName() != null && !request.getOrganizationName().equals(user.getOrganizationName())) {
                isLegalDocumentChanged = true;
            }
            if (request.getTaxCode() != null && !request.getTaxCode().equals(user.getTaxCode())) {
                isLegalDocumentChanged = true;
            }
        }
        user = DTOMapper.toUserEntity(request, user);

        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(avatar.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                user.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Neu 4 truong co thay doi thi chuyen sang Pending
        if (isLegalDocumentChanged) {
            StatusUser pendingStatus = statusUserRepo.getStatusUserById(1);
            user.setStatusId(pendingStatus);
        }
        
        User savedUser = this.userRepo.updateUser(user);
        return DTOMapper.toUserResponse(savedUser);

    }

    @Override
    public UserResponse authenticate(LoginRequest request
    ) {
        boolean isAuthenticated = this.userRepo.authenticate(request.getUsername(), request.getPassword());

        if (isAuthenticated) {
            User user = this.userRepo.findUserByEmail(request.getUsername());
            return DTOMapper.toUserResponse(user);
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepo.findUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Không tồn tại!");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRoleId().getName()));

        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(), authorities);
    }

}
