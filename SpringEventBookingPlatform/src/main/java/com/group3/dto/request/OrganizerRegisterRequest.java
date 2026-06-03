/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 *
 * @author THUAN
 */
public class OrganizerRegisterRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Pattern(regexp = ".+@.+\\..+", message = "Email phải có đuôi domain (ví dụ: .com, .vn)")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải từ 6 ký tự trở lên")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?!.*\\s)[A-Za-z\\d@$!%*#?&]{6,}$",
            message = "Mật khẩu phải từ 6 ký tự, gồm ít nhất một chữ cái và một số")
    private String password;

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0[35789]\\d{8}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    //Cho nha to chuc
    @NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "^(\\d{12})$", message = "CCCD phải có đúng 12 chữ số")
    private String identityCard;

    @NotBlank(message = "Tên tổ chức/ doanh nghiệp không được để trống")
    @Size(max = 100, message = "Tên tổ chức/doanh nghiệp không được vượt quá 100 ký tự")
    private String organizationName;

    @NotBlank(message = "Mã số thuế doanh nghiệp không được để trống")
    @Pattern(regexp = "^\\d{10}(\\-\\d{3})?$", message = "Mã số thuế phải là 10 chữ số, hoặc 13 chữ số có gạch nối")
    private String taxCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

}
