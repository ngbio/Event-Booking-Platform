package com.group3.utils;

import com.group3.exceptions.BusinessException;
import org.springframework.web.multipart.MultipartFile;

public final class MediaFileValidator {

    private static final long MAX_MEDIA_FILE_SIZE = 20L * 1024 * 1024;

    private MediaFileValidator() {
    }

    public static void validateEventMedia(MultipartFile image, MultipartFile video) {
        validateFileSize(image, "Anh");
        validateFileSize(video, "Video");
    }

    private static void validateFileSize(MultipartFile file, String label) {
        if (file != null && !file.isEmpty() && file.getSize() > MAX_MEDIA_FILE_SIZE) {
            throw new BusinessException(label + " toi da 20MB!");
        }
    }
}
