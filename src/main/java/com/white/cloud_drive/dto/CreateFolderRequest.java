package com.white.cloud_drive.dto;

import lombok.Data;

@Data
public class CreateFolderRequest {
    private String name;
    private Long parentId;


}
