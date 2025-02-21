package com.securefilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing file metadata in API responses.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponse {

    private String fileName;

    private String s3Url;

    private Long fileSize;

    private String contentType;

    private String uploadedBy;

    private String uploadedAt;
}
