package com.group3.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

public class EventRequest {
    @NotBlank(message = "Tiêu đề sự kiện không được để trống")
    @Size(max = 255, message = "Tiêu đề không được quá 255 ký tự")
    private String title;

    private String description;
    
    private MultipartFile imageFile;

    private MultipartFile videoFile;
    
    @NotNull(message = "Thời gian bắt đầu không được trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") 
    private Date startTime;

    @NotNull(message = "Thời gian kết thúc không được trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date endTime;

    @NotBlank(message = "Địa điểm tổ chức không được trống")
    private String location;

    @Min(value = 1, message = "Số lượng vé ít nhất phải là 1")
    private int totalTickets;

    @NotNull(message = "Giá vé không được trống")
    @DecimalMin(value = "0.0", message = "Giá vé không được nhỏ hơn 0")
    private BigDecimal price;

    @NotBlank(message = "Danh mục không được để trống")
    private String categoryIds; 

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    public MultipartFile getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(MultipartFile videoFile) {
        this.videoFile = videoFile;
    }
    
    
}
