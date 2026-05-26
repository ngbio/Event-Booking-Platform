package com.group3.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.group3.dto.request.EventRequest;
import com.group3.dto.response.BookingResponse;
import com.group3.dto.response.EventResponse;
import com.group3.exceptions.BusinessException;
import com.group3.exceptions.ResourceNotFoundException;
import com.group3.exceptions.UnauthorizedException;
import com.group3.pojo.Booking;
import com.group3.pojo.Category;
import com.group3.pojo.Event;
import com.group3.pojo.User;
import com.group3.repository.BookingRepository;
import com.group3.repository.EventRepository;
import com.group3.repository.StatusEventRepository;
import com.group3.repository.UserRepository;
import com.group3.service.OrganizerEventService;
import com.group3.utils.DTOMapper;
import java.util.ArrayList;

@Service
@Transactional
public class OrganizerEventServiceImpl implements OrganizerEventService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private EventRepository eventRepo;
    @Autowired
    private StatusEventRepository statusEventRepo;
    @Autowired
    private BookingRepository bookingRepo;
    @Autowired
    private Cloudinary cloudinary;

    private static final Integer PENDING = 1;
    private static final Integer PUBLISHED = 2;
    private static final Integer DRAFT = 3;
    private static final Integer COMPLETED = 4;
    private static final Integer CANCELLED = 5;

    private User validateAndGetOrganizer(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Chưa đăng nhập hoặc token hết hạn");
        }

        User user = userRepo.findUserByEmail(principal.getName());
        if (user == null || user.getRoleId() == null || user.getRoleId().getId() != 2) {
            throw new UnauthorizedException("Chỉ Nhà tổ chức mới thực hiện được thao tác này");
        }
        return user;
    }

    private Event validateEventOwnership(Integer eventId, User organizer) {
        Event event = eventRepo.getEventById(eventId);
        if (event == null) {
            throw new ResourceNotFoundException("Không tìm thấy sự kiện");
        }

        if (!organizer.getId().equals(event.getOrganizerId().getId())) {
            throw new UnauthorizedException("Bạn không có quyền thao tác trên sự kiện này");
        }
        return event;
    }

    private void handleMediaUpload(Event event, MultipartFile image, MultipartFile video) {
        try {
            if (image != null && !image.isEmpty()) {
                Map res = this.cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                event.setImageUrl(res.get("secure_url").toString());
            }
            if (video != null && !video.isEmpty()) {
                Map res = this.cloudinary.uploader().upload(video.getBytes(), ObjectUtils.asMap("resource_type", "video"));
                event.setVideoUrl(res.get("secure_url").toString());
            }
        } catch (IOException ex) {
            throw new BusinessException("Lỗi upload file media, vui lòng thử lại!");
        }
    }

    private void validateEventForPublishing(Event event) {
        // Validation khắt khe chạy trực tiếp trên dữ liệu Database
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new BusinessException("Sự kiện chưa có tên, không thể gửi duyệt!");
        }
        if (event.getImageUrl() == null || event.getImageUrl().isBlank()) {
            throw new BusinessException("Vui lòng tải lên ảnh Banner trước khi gửi duyệt!");
        }
        if (event.getStartTime() == null || event.getEndTime() == null) {
            throw new BusinessException("Vui lòng thiết lập thời gian bắt đầu và kết thúc!");
        }
        if (event.getStartTime().after(event.getEndTime())) {
            throw new BusinessException("Lỗi logic: Thời gian kết thúc đang diễn ra trước thời gian bắt đầu!");
        }
        if (event.getCategoryCollection() == null || event.getCategoryCollection().isEmpty()) {
        throw new BusinessException("Chọn ít nhất 1 thể loại cho sự kiện trước khi gửi duyệt!");
    }
    }

    private void handleCategories(Event event, String categoryIdsStr) {
        if (categoryIdsStr == null || categoryIdsStr.isBlank()) {
            return;
        }

        List<Category> categories = new ArrayList<>();
        String[] ids = categoryIdsStr.split(",");

        for (String id : ids) {
            try {
                Integer catId = Integer.valueOf(id.trim());
                Category category = new Category();
                category.setId(catId);
                categories.add(category);
            } catch (NumberFormatException ignored) {
                // Bỏ qua nếu có ID rác không phải là số
            }
        }

        // Nạp danh sách thể loại vào sự kiện
        event.setCategoryCollection(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getOrganizerEvents(Principal principal, Map<String, String> params) {
        User organizer = validateAndGetOrganizer(principal);

        Map<String, String> filters = new HashMap<>(params);
        filters.put("organizerId", organizer.getId().toString());

        List<Event> events = eventRepo.getEvents(filters);
        return DTOMapper.toEventResponseList(events);
    }

    @Override
    public EventResponse createOrganizerEvent(Principal principal, EventRequest request, MultipartFile image, MultipartFile video) {
        User organizer = validateAndGetOrganizer(principal);

        Event event = DTOMapper.toEventEntity(request);
        handleCategories(event, request.getCategoryIds());
        event.setOrganizerId(organizer);
        event.setCreatedDate(new Date());

        // LUÔN LUÔN tạo nháp để Frontend có thể gọi API nhiều lần lưu tạm
        event.setStatusId(statusEventRepo.getStatusEventById(DRAFT));

        handleMediaUpload(event, image, video);

        Event savedEvent = eventRepo.addEvent(event);
        return DTOMapper.toEventResponse(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getOrganizerEvent(Principal principal, Integer eventId) {
        User organizer = validateAndGetOrganizer(principal);
        Event event = validateEventOwnership(eventId, organizer);
        return DTOMapper.toEventResponse(event);
    }

    @Override
    public EventResponse updateOrganizerEvent(Principal principal, Integer eventId, EventRequest request, MultipartFile image, MultipartFile video) {
        User organizer = validateAndGetOrganizer(principal);
        Event existingEvent = validateEventOwnership(eventId, organizer);

        Integer currentStatus = existingEvent.getStatusId() != null ? existingEvent.getStatusId().getId() : DRAFT;

        // ĐÁNH TỤT HẠNG: Đang chờ duyệt mà sửa data -> Bắt làm lại từ Nháp
        if (currentStatus.equals(PENDING)) {
            existingEvent.setStatusId(statusEventRepo.getStatusEventById(DRAFT));
        }

        // BẢO TOÀN DỮ LIỆU: Chỉ đè data mới lên, giữ nguyên ID và các thông tin cũ
        existingEvent = DTOMapper.toEventEntity(request, existingEvent);
        handleCategories(existingEvent, request.getCategoryIds());
        handleMediaUpload(existingEvent, image, video);

        Event updatedEvent = eventRepo.updateEvent(existingEvent);
        return DTOMapper.toEventResponse(updatedEvent);
    }

    @Override
    public void changeOrganizerEventStatus(Principal principal, Integer eventId, Integer statusId) {
        User organizer = validateAndGetOrganizer(principal);
        Event existingEvent = validateEventOwnership(eventId, organizer);

        Integer currentStatus = existingEvent.getStatusId() != null ? existingEvent.getStatusId().getId() : DRAFT;

        // Ràng buộc bảo mật: Cấm tự xuất bản
        if (statusId.equals(PUBLISHED)) {
            throw new BusinessException("Nhà tổ chức không có quyền tự mở bán sự kiện. Vui lòng gửi yêu cầu duyệt!");
        }

        // LOGIC GỬI DUYỆT (PENDING)
        if (statusId.equals(PENDING)) {
            if (!currentStatus.equals(DRAFT)) {
                throw new BusinessException("Chỉ có thể gửi duyệt các sự kiện đang ở trạng thái Nháp (DRAFT)!");
            }
            // Khởi chạy bộ lọc kiểm định
            validateEventForPublishing(existingEvent);
        }

        // LOGIC HOÀN THÀNH (COMPLETED)
        if (statusId.equals(COMPLETED)) {
            if (!currentStatus.equals(PUBLISHED)) {
                throw new BusinessException("Chỉ có thể đánh dấu hoàn thành cho sự kiện đang được mở bán (PUBLISHED)!");
            }
            if (existingEvent.getEndTime() != null && new Date().before(existingEvent.getEndTime())) {
                throw new BusinessException("Sự kiện chưa kết thúc! Không thể chuyển sang trạng thái Hoàn thành.");
            }
        }

        // LOGIC HUỶ (CANCELLED)
        if (statusId.equals(CANCELLED)) {
            if (currentStatus.equals(COMPLETED)) {
                throw new BusinessException("Sự kiện đã hoàn thành, không thể hủy!");
            }
        }

        existingEvent.setStatusId(statusEventRepo.getStatusEventById(statusId));
        eventRepo.updateEvent(existingEvent);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<BookingResponse> getEventBookings(Principal principal, Integer eventId, Map<String, String> params) {
//        User organizer = validateAndGetOrganizer(principal);
//        validateEventOwnership(eventId, organizer);
//
//        List<Booking> bookings = bookingRepo.getBookingsByEventId(eventId, params);
//        return DTOMapper.toBookingResponseList(bookings);
//    }
}
