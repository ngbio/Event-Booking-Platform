package com.group3.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class UserUpdateRequest {
    private String fullName;

    @Pattern(regexp = "^0[35789]\\d{8}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Pattern(regexp = "^(\\d{12})$", message = "CCCD phải có đúng 12 chữ số")
    private String identityCard;

    @Size(max = 100, message = "Tên tổ chức/doanh nghiệp không được vượt quá 100 ký tự")
    private String organizationName;

    @Pattern(regexp = "^\\d{10}(\\-\\d{3})?$", message = "Mã số thuế phải là 10 chữ số, hoặc 13 chữ số có gạch nối")
    private String taxCode;
    
    @Past(message = "Ngày sinh không hợp lệ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
    
    @Pattern(regexp = "^(male|female)$", message = "Giới tính không hợp lệ (male, female)")
    private String gender;

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

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender != null ? gender.trim() : null;
    }
    
    
    
}
