package com.group3.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.group3.dto.request.EventRequest;
import com.group3.dto.response.EventRefundResponse;
import com.group3.dto.response.EventResponse;
import com.group3.dto.response.EventSettlementResponse;
import com.group3.exceptions.ResourceNotFoundException;
import com.group3.pojo.Category;
import com.group3.pojo.Event;
import com.group3.pojo.Organizer;
import com.group3.pojo.StatusEvent;
import com.group3.pojo.User;
import com.group3.repository.CategoryRepository;
import com.group3.repository.EventRepository;
import com.group3.repository.StatusEventRepository;
import com.group3.service.EventService;
import com.group3.utils.DTOMapper;
import com.group3.utils.MediaFileValidator;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@PropertySource("classpath:configs.properties")
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private CategoryRepository cateRepo;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private StatusEventRepository statusEventRepo;

    @Autowired
    private Environment env;

    private static final int PUBLISHED = 2;
    private static final int PENDING_REVIEW = 1;
    private static final int DRAFT = 3;
    private static final int COMPLETED = 4;

    private void refreshExpiredPublishedEvents() {
        this.eventRepo.updateExpiredPublishedEvents(PUBLISHED, COMPLETED, new Date());
    }

    private Event validateAndGetPublicEvent(Integer eventId) {
        Event event = eventRepo.getEventById(eventId);
        Integer statusId = event != null && event.getStatusId() != null ? event.getStatusId().getId() : null;
        if (event == null || PUBLISHED!=statusId) {
            throw new ResourceNotFoundException("Không tìm thấy sự kiện hoặc sự kiện chưa mở bán");
        }
        return event;
    }

    private Organizer getRequiredOrganizerProfile(User user) {
        Organizer organizer = user != null ? user.getOrganizer() : null;
        if (organizer == null) {
            throw new IllegalStateException("Tai khoan organizer chua co profile organizer");
        }
        return organizer;
    }

    @Override
    @Transactional
    public List<EventResponse> getEvents(Map<String, String> params) {
        refreshExpiredPublishedEvents();
        List<Event> events = this.eventRepo.getEvents(params);
        return DTOMapper.toEventResponseList(events);
    }

    @Override
    @Transactional
    public EventResponse getEventById(Integer eventId) {
        refreshExpiredPublishedEvents();
        Event event = validateAndGetPublicEvent(eventId);
        return DTOMapper.toEventResponse(event);
    }

    @Override
    public EventResponse createEvent(EventRequest request, MultipartFile image, MultipartFile video, User organizer) {
        MediaFileValidator.validateEventMedia(image, video);

        Event event = DTOMapper.toEventEntity(request);
        //add Organizer
        event.setOrganizerId(getRequiredOrganizerProfile(organizer));

        //add date
        Date now = new Date();
        event.setCreatedDate(now);
        event.setUpdatedDate(now);

        // Upload image
        if (image != null && !image.isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(image.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                event.setImageUrl(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(EventServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Upload video
        if (video != null && !video.isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(video.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                event.setVideoUrl(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(EventServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Set categories
        if (request.getCategoryIds() != null) {
            String[] categoryIds = request.getCategoryIds().split(",");
            Collection<Category> categories = new ArrayList<>();
            for (String catId : categoryIds) {
                Category cate = this.cateRepo.getCateById(Integer.valueOf(catId.trim()));
                if (cate != null) {
                    categories.add(cate);
                }
            }
            event.setCategoryCollection(categories);
        }

        event.setSoldTickets(0);
        event.setListingFee(BigDecimal.ZERO);
        event.setIsSettlement(false);
        event.setSettlementCode(null);

        StatusEvent statusPending = this.statusEventRepo.getStatusEventById(PENDING_REVIEW);
        event.setStatusId(statusPending);

        return DTOMapper.toEventResponse(this.eventRepo.addEvent(event));
    }

    @Override
    public EventResponse updateEvent(Integer id, EventRequest request, MultipartFile image, MultipartFile video) {
        MediaFileValidator.validateEventMedia(image, video);

        Event event = this.eventRepo.getEventById(id);
        if (event == null) {
            return null;
        }
        if (event.getStatusId() != null) {
            if (event.getStatusId().getId() == PUBLISHED) {
                throw new IllegalStateException("Sự kiện đang mở bán không được chỉnh sửa.");
            }
            event.setTitle(request.getTitle());
            event.setDescription(request.getDescription());
            event.setLocation(request.getLocation());
            event.setStartTime(request.getStartTime());
            event.setEndTime(request.getEndTime());
            event.setPrice(request.getPrice());
            event.setTotalTickets(request.getTotalTickets());

            event.setUpdatedDate(new Date());

            if (image != null && !image.isEmpty()) {
                try {
                    Map res = this.cloudinary.uploader().upload(image.getBytes(),
                            ObjectUtils.asMap("resource_type", "auto"));
                    event.setImageUrl(res.get("secure_url").toString());
                } catch (IOException ex) {
                    Logger.getLogger(EventServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (video != null && !video.isEmpty()) {
                try {
                    Map res = this.cloudinary.uploader().upload(video.getBytes(),
                            ObjectUtils.asMap("resource_type", "auto"));
                    event.setVideoUrl(res.get("secure_url").toString());
                } catch (IOException ex) {
                    Logger.getLogger(EventServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (request.getCategoryIds() != null) {
                String[] categoryIds = request.getCategoryIds().split(",");
                Collection<Category> categories = new ArrayList<>();
                for (String catId : categoryIds) {
                    Category cate = this.cateRepo.getCateById(Integer.valueOf(catId.trim()));
                    if (cate != null) {
                        categories.add(cate);
                    }
                }
                event.setCategoryCollection(categories);
            }
            if (event.getPrice().compareTo(BigDecimal.ZERO) > 0 && event.getSoldTickets() > 0) {
                BigDecimal totalRev = event.getPrice().multiply(BigDecimal.valueOf(event.getSoldTickets()));
                Double fee = env.getProperty("event.fee", Double.class);
                BigDecimal calculatedFee = totalRev.multiply(BigDecimal.valueOf(fee));
                event.setListingFee(calculatedFee);
            } else {
                event.setListingFee(BigDecimal.ZERO);
            }
            StatusEvent statusPending = this.statusEventRepo.getStatusEventById(PENDING_REVIEW);
            event.setStatusId(statusPending);
        }
        return DTOMapper.toEventResponse(this.eventRepo.updateEvent(event));
    }

    @Override
    public boolean deleteEvent(Integer eventId) {
        return this.eventRepo.deleteEvent(eventId);
    }

    @Override
    public int getAvailableTickets(Integer eventId) {
        refreshExpiredPublishedEvents();
        Event event = this.eventRepo.getEventById(eventId);
        if (event == null) {
            return 0;
        }
        if (event.getStatusId() == null || PUBLISHED!=event.getStatusId().getId()) {
            return 0;
        }
        return event.getTotalTickets() - event.getSoldTickets();
    }

    @Override
    public boolean updateTicketsAfterBooking(Integer eventId, int quantityBooked) {
        Event event = this.eventRepo.getEventById(eventId);
        if (event == null) {
            return false;
        }
        int availableTickets = event.getTotalTickets() - event.getSoldTickets();
        if (availableTickets >= quantityBooked) {
            int newSoldAmount = event.getSoldTickets() + quantityBooked;
            event.setSoldTickets(newSoldAmount);
            if (event.getPrice() != null && event.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal totalRevenue = event.getPrice().multiply(BigDecimal.valueOf(newSoldAmount));
                Double fee = env.getProperty("event.fee", Double.class);
                BigDecimal updatedListingFee = totalRevenue.multiply(BigDecimal.valueOf(fee));
                event.setListingFee(updatedListingFee);
            } else {
                event.setListingFee(BigDecimal.ZERO);
            }
            this.eventRepo.updateEvent(event);
            return true;
        }

        return false;
    }

    @Override
    public Long countEvents(Map<String, String> params) {
        return this.eventRepo.countEvents(params);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByIds(List<Integer> EventIds) {
        List<Event> events = this.eventRepo.getEventsByIds(EventIds);
        events.removeIf(event -> event.getStatusId() == null
                || event.getStatusId().getId() != PUBLISHED
                || event.getEndTime() == null
                || event.getEndTime().before(new Date()));
        return DTOMapper.toEventResponseList(events);
    }

    @Override
    public EventResponse getEventByIdForAdmin(Integer eventId) {
        Event event = this.eventRepo.getEventById(eventId);
        if (event == null) {
            throw new ResourceNotFoundException("Không tìm thấy sự kiện");
        }
        return DTOMapper.toEventResponse(event);
    }

    @Override
    public List<EventRefundResponse> getEventsForRefund() {
        return DTOMapper.toEventRefundResponseList(this.eventRepo.getEventsForRefund());
    }

    @Override
    public List<EventSettlementResponse> getEventsForSettlement() {
        return DTOMapper.toEventSettlementResponseList(this.eventRepo.getEventsForSettlement());
    }
    
    @Override
    @Transactional
    public boolean processSettlement(Integer eventId, String settlementCode) {
        Event event = this.eventRepo.getEventById(eventId);
        if (event == null || event.getStatusId() == null || event.getStatusId().getId() != 4) {
            return false;
        }
        event.setIsSettlement(true);
        event.setSettlementCode(settlementCode);
        this.eventRepo.updateEvent(event);
        
        return true;
    }
}
