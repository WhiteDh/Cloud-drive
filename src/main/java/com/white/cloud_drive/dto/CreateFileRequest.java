package com.white.cloud_drive.dto;

import lombok.Data;

@Data
public class CreateFileRequest {
    private String name;
    private Long parentId;
    private Long size;
}