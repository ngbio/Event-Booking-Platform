/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.group3.dto.request.AttendeeRegisterRequest;
import com.group3.dto.request.LoginRequest;
import com.group3.dto.request.OrganizerRegisterRequest;
import com.group3.dto.request.UserUpdateRequest;
import com.group3.dto.request.ChangePasswordRequest;
import com.group3.pojo.User;
import com.group3.dto.response.UserResponse;
import com.group3.exceptions.BusinessException;
import com.group3.exceptions.DuplicateResourceException;
import com.group3.exceptions.ResourceNotFoundException;
import com.group3.exceptions.UnauthorizedException;
import com.group3.pojo.Attendee;
import com.group3.pojo.Organizer;
import com.group3.pojo.Role;
import com.group3.pojo.StatusUser;
import com.group3.repository.UserRepository;
import com.group3.repository.RoleRepository;
import com.group3.repository.StatusUserRepository;

import com.group3.service.UserService;
import com.group3.utils.DTOMapper;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
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

    private static final int ROLE_ATTENDEE = 3;
    private static final int ROLE_ORGANIZER = 2;
    private static final Integer PENDING= 1;
    private static final Integer ACTIVE= 2;
    private static final Integer REJECTED= 3;

    private void validateAvatar(MultipartFile avatar) {
        if (avatar == null || avatar.isEmpty()) {
            throw new BusinessException("Avatar bắt buộc không được để trống");
        }
        //Kiem tra dung luong avatar
        if (avatar.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException("Avatar tối đa 2MB!");
        }
    }
    
    private User validateAndGetCurrentUser(Principal principal) {
    if (principal == null) {
        throw new UnauthorizedException("Chưa đăng nhập hoặc token hết hạn");
    }
    User user = this.userRepo.findUserByEmail(principal.getName());
    if (user == null) {
        throw new ResourceNotFoundException("Không tìm thấy thông tin tài khoản người dùng!");
    }
    return user;
}

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
    public User getUserEntityByEmail(String email) {
        return this.userRepo.findUserByEmail(email);
    }

    @Override
    public boolean checkExistEmail(String email) {
        return this.userRepo.existEmail(email);
    }

    @Override
    public Long countUsers() {
        return this.userRepo.count();
    }

    @Override
    public UserResponse addUser(OrganizerRegisterRequest request, MultipartFile avatar) {
        if (this.userRepo.existEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email này đã có người đăng ký!");
        }
        validateAvatar(avatar);

        User user = DTOMapper.toUserEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Date now = new Date();
        user.setCreatedDate(now);
        user.setUpdatedDate(now);

        Role userRole = roleRepo.findById(ROLE_ORGANIZER);
        user.setRoleId(userRole);

        int statusId = PENDING;
        StatusUser statusUser = statusUserRepo.getStatusUserById(statusId);
        user.setStatusId(statusUser);

        user.setOrganizer(new Organizer(user, request.getIdentityCard(), request.getOrganizationName(), request.getTaxCode()));
        
        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(avatar.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                user.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                throw new BusinessException("Cập nhật avatar thất bại!");
            }
        }

        return DTOMapper.toUserResponse(this.userRepo.addUser(user));
    }
    
    @Override
    public UserResponse addUser(AttendeeRegisterRequest request, MultipartFile avatar) {
        if (this.userRepo.existEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email này đã có người đăng ký!");
        }
        validateAvatar(avatar);

        User user = DTOMapper.toUserEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Date now = new Date();
        user.setCreatedDate(now);
        user.setUpdatedDate(now);

        Role userRole = roleRepo.findById(ROLE_ATTENDEE);
        user.setRoleId(userRole);

        int statusId = ACTIVE;
        StatusUser statusUser = statusUserRepo.getStatusUserById(statusId);
        user.setStatusId(statusUser);

        user.setAttendee(new Attendee(user));

        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(avatar.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                user.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                throw new BusinessException("Cập nhật avatar thất bại!");
            }
        }

        return DTOMapper.toUserResponse(this.userRepo.addUser(user));
    }

    @Override
    public UserResponse updateProfile(Principal principal, UserUpdateRequest request, MultipartFile avatar) {
        User user = validateAndGetCurrentUser(principal);
        if (user.getRoleId().getId() == ROLE_ATTENDEE) {
            if ((request.getIdentityCard() != null && !request.getIdentityCard().isEmpty())
                    || (request.getOrganizationName() != null && !request.getOrganizationName().isEmpty())
                    || (request.getTaxCode() != null && !request.getTaxCode().isEmpty())) {
                throw new BusinessException("Lỗi dữ liệu: Người mua vé không có các thông tin của Nhà tổ chức");
            }
            if (request.getBirthDate()==null)
                throw new BusinessException("Ngày sinh không được để trống");
            if (request.getGender()==null||request.getGender().isEmpty())
                throw new BusinessException("Giới tính không được để trống");
        } else if (user.getRoleId().getId() == ROLE_ORGANIZER) {
            if (request.getIdentityCard() == null || request.getIdentityCard().isEmpty()) {
                throw new BusinessException("Người tổ chức bắt buộc phải cung cấp CCCD");
            }
            if (request.getOrganizationName() == null || request.getOrganizationName().isEmpty()) {
                throw new BusinessException("Bắt buộc phải cung cấp Tên tổ chức/doanh nghiệp");
            }
            if (request.getTaxCode() == null || request.getTaxCode().isEmpty()) {
                throw new BusinessException("Bắt buộc phải cung cấp Mã số thuế của doanh nghiệp");
            }
        }

        //Kiem tra 3 cot cua nha to chuc  co thay doi hay khong
        boolean isLegalDocumentChanged = false;

        if (user.getRoleId() != null && user.getRoleId().getId() == ROLE_ORGANIZER) {
            Organizer organizer = user.getOrganizer();
            if (organizer == null) {
                organizer = new Organizer(user, request.getIdentityCard(), request.getOrganizationName(), request.getTaxCode());
                user.setOrganizer(organizer);
                isLegalDocumentChanged = true;
            }
            if (request.getIdentityCard() != null && !request.getIdentityCard().equals(organizer.getIdentityCard())) {
                isLegalDocumentChanged = true;
            }
            if (request.getOrganizationName() != null && !request.getOrganizationName().equals(organizer.getOrganizationName())) {
                isLegalDocumentChanged = true;
            }
            if (request.getTaxCode() != null && !request.getTaxCode().equals(organizer.getTaxCode())) {
                isLegalDocumentChanged = true;
            }
        }
        user = DTOMapper.toUserEntity(request, user);

        if (avatar != null && !avatar.isEmpty()) {
            validateAvatar(avatar);
            try {
                Map res = this.cloudinary.uploader().upload(avatar.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                user.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                throw new BusinessException("Cập nhật avatar thất bại!");
            }
        }
        //Neu 3 truong co thay doi thi chuyen sang Pending
        if (isLegalDocumentChanged) {
            StatusUser pendingStatus = statusUserRepo.getStatusUserById(PENDING);
            user.setStatusId(pendingStatus);
        }

        User savedUser = this.userRepo.updateUser(user);
        return DTOMapper.toUserResponse(savedUser);

    }

    @Override
    public UserResponse authenticate(LoginRequest request
    ) {
        boolean isAuthenticated = this.userRepo.authenticate(request.getEmail(), request.getPassword());

        if (isAuthenticated) {
            User user = this.userRepo.findUserByEmail(request.getEmail());
            return DTOMapper.toUserResponse(user);
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userRepo.findUserByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Không tồn tại người dùng với email: " + email);
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRoleId().getName()));

        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(), authorities);
    }

    @Override
    public void changePassword(Principal principal, ChangePasswordRequest request) {
        User user = validateAndGetCurrentUser(principal);
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Mật khẩu xác nhận không trùng khớp!");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Mật khẩu cũ không chính xác!");
        }
        String newEncryptedPassword = passwordEncoder.encode(request.getNewPassword());
        boolean isSuccess = userRepo.changePassword(user.getId(), newEncryptedPassword);

        if (!isSuccess) {
            throw new BusinessException("Cập nhật thất bại, vui lòng thử lại sau!");
        }
    }
    
    @Override
    public UserResponse getCurrentUserProfile(Principal principal){
        User user = validateAndGetCurrentUser(principal);
        return DTOMapper.toUserResponse(user);
    }

}
