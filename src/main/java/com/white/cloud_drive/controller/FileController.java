package com.white.cloud_drive.controller;

import com.white.cloud_drive.dto.FileDTO;
import com.white.cloud_drive.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "parentId", required = false) Long parentId) {

        FileDTO savedFile = fileService.saveFile(file, parentId);
        return ResponseEntity.ok(savedFile);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        return fileService.downloadFile(id);
    }

    @GetMapping("/path")
    public ResponseEntity<FileDTO> getFileByPath(@RequestParam String path) {
        return fileService.getFileByPath(path)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

