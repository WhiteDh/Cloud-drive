package com.white.cloud_drive.dto;

import com.white.cloud_drive.model.File;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {
    private Long id;
    private String name;
    private String path;
    private Long parentId;
    private Long ownerId;
    private Long size;

    public FileDTO(Long id, String name, String path, Long parentId, Long ownerId) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.parentId = parentId;
        this.ownerId = ownerId;
        this.size = null;
    }

    public FileDTO(File file) {
        this.id = file.getId();
        this.name = file.getName();
        this.path = file.getPath();
        this.parentId = file.getFolder() != null ? file.getFolder().getId() : null;
        this.ownerId = file.getOwner() != null ? file.getOwner().getId() : null;
        this.size = file.getSize();
    }
}
