package com.white.cloud_drive.dto;

import com.white.cloud_drive.model.Folder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolderDTO {
    private Long id;
    private String name;
    private String path;
    private Long parentId;
    private Long ownerId;


    public  FolderDTO(Folder folder) {
        this.id = folder.getId();
        this.name = folder.getName();
        this.path = folder.getPath();
        this.parentId = folder.getParentFolder() != null ? folder.getParentFolder().getId() : null;
        this.ownerId = folder.getOwner() != null ? folder.getOwner().getId() : null;

    }
}
