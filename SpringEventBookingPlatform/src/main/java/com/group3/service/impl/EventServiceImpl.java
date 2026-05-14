/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.group3.dto.request.EventRequest;
import com.group3.dto.response.EventResponse;
import com.group3.pojo.Category;
import com.group3.pojo.Event;
import com.group3.pojo.User;
import com.group3.repository.CategoryRepository;
import com.group3.repository.EventRepository;
import com.group3.service.EventService;
import com.group3.utils.DtoMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author THUAN
 */
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private CategoryRepository cateRepo;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public List<EventResponse> getEvents(Map<String, String> params) {
        List<Event> events = this.eventRepo.findByParams(params);
        return DtoMapper.toEventResponseList(events);
    }

    @Override
    public EventResponse getEventById(Integer id) {
        return DtoMapper.toEventResponse(this.eventRepo.getEventById(id));
    }

    @Override
    public EventResponse createEvent(EventRequest request, MultipartFile image, MultipartFile video, User organizer) {
        Event event = DtoMapper.toEventEntity(request);
        //add Organizer
        event.setOrganizerId(organizer);

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
                Category cate = this.cateRepo.getCateById(Integer.parseInt(catId.trim()));
                if (cate != null) {
                    categories.add(cate);
                }
            }
            event.setCategoryCollection(categories);
        }
        return DtoMapper.toEventResponse(this.eventRepo.addEvent(event));
    }

    @Override
    public EventResponse updateEvent(Integer id, EventRequest request, MultipartFile image, MultipartFile video) {
        Event event = this.eventRepo.getEventById(id);
        if (event == null) {
            return null;
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setPrice(request.getPrice());
        event.setTotalTickets(request.getTotalTickets());
        
        //update date
        event.setUpdatedDate(new Date());

        // Upload image if provided
        if (image != null && !image.isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(image.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                event.setImageUrl(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(EventServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Upload video if provided
        if (video != null && !video.isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(video.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                event.setVideoUrl(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(EventServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Update categories if provided
        if (request.getCategoryIds() != null) {
            String[] categoryIds = request.getCategoryIds().split(",");
            Collection<Category> categories = new ArrayList<>();
            for (String catId : categoryIds) {
                Category cate = this.cateRepo.getCateById(Integer.parseInt(catId.trim()));
                if (cate != null) {
                    categories.add(cate);
                }
            }
            event.setCategoryCollection(categories);
        }

        return DtoMapper.toEventResponse(this.eventRepo.updateEvent(event));
    }

    @Override
    public boolean deleteEvent(Integer id) {
        return this.eventRepo.deleteEvent(id);
    }

    @Override
    public int getAvailableTickets(Integer eventId) {
        Event event = this.eventRepo.getEventById(eventId);
        if (event == null) {
            return 0;
        }

        // totalTickets - booked tickets
        int totalTickets = event.getTotalTickets();
        int bookedTickets = 0;

        if (event.getBookingCollection() != null) {
            for (Object booking : event.getBookingCollection()) {
                // Giả sử Booking có method getQuantity()
                // bookedTickets += ((Booking) booking).getQuantity();
                bookedTickets += 1; // Placeholder - cần adjust theo Booking POJO
            }
        }

        return totalTickets - bookedTickets;
    }

    @Override
    public boolean updateTicketsAfterBooking(Integer eventId, int quantityBooked) {
        Event event = this.eventRepo.getEventById(eventId);
        if (event == null) {
            return false;
        }

        int availableTickets = getAvailableTickets(eventId);
        if (availableTickets >= quantityBooked) {
            // Note: totalTickets không giảm, chỉ được query qua booking
            // Hoặc có thể thêm column "soldTickets" nếu cần
            return true;
        }

        return false;
    }
}
