/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 *
 * @author thanh
 */
public class UserUpdateRequest {

    @Size(min = 6, message = "Mật khẩu phải từ 6 ký tự trở lên")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?!.*\\s)[A-Za-z\\d@$!%*#?&]{6,}$",
            message = "Mật khẩu phải từ 6 ký tự, gồm ít nhất một chữ cái và một số")
    private String password;
    
    private String fullName;

    @Pattern(regexp = "^0[35789]\\d{8}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    
    //Nha to chuc
    @Pattern(regexp = "^(\\d{12})$", message = "CCCD phải có đúng 12 chữ số")
    private String identityCard;

    @Size(max = 100, message = "Tên tổ chức/doanh nghiệp không được vượt quá 100 ký tự")
    private String organizationName;

    @Pattern(regexp = "^\\d{10}(\\-\\d{3})?$", message = "Mã số thuế phải là 10 chữ số, hoặc 13 chữ số có gạch nối")
    private String taxCode;
    
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
        this.fullName = fullName!=null?fullName.trim():null;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone!=null?phone.trim():null;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = (identityCard != null) ? identityCard.trim() : null;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = (organizationName!=null)?organizationName.trim():null;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = (taxCode!=null)?taxCode.trim():null;
    }
    
    
    
}
