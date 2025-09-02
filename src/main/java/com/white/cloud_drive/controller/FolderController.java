package com.white.cloud_drive.controller;

import com.white.cloud_drive.dto.CreateFolderRequest;
import com.white.cloud_drive.dto.FolderDTO;
import com.white.cloud_drive.model.Folder;
import com.white.cloud_drive.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping
    public ResponseEntity<FolderDTO> createFolder(@RequestBody CreateFolderRequest request) {
        Folder folder = folderService.createFolder(request);
        return ResponseEntity.ok(new FolderDTO(folder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FolderDTO> getFolder(@PathVariable Long id) {
        return folderService.getFolderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{ownerId}")
    public ResponseEntity<List<FolderDTO>> getFoldersByUser(@PathVariable Long ownerId) {
        List<FolderDTO> folders = folderService.getAllFoldersByUser(ownerId);
        return ResponseEntity.ok(folders);
    }
}
