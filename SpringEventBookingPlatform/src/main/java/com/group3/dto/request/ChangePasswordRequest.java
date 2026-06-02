package com.group3.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {

    @NotBlank(message = "Mật khẩu cũ không được trống")
    private String oldPassword;

    @Size(min = 6, message = "Mật khẩu phải từ 6 ký tự trở lên")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?!.*\\s)[A-Za-z\\d@$!%*#?&]{6,}$",
            message = "Mật khẩu phải từ 6 ký tự, gồm ít nhất một chữ cái và một số")
    private String newPassword;

    @NotBlank(message = "Mật khẩu mới không được trống")
    private String confirmPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}
