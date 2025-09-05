package com.white.cloud_drive.service;

import com.white.cloud_drive.dto.CreateFileRequest;
import com.white.cloud_drive.dto.FileDTO;
import com.white.cloud_drive.model.File;
import com.white.cloud_drive.model.Folder;
import com.white.cloud_drive.model.User;
import com.white.cloud_drive.repository.FileRepository;
import com.white.cloud_drive.repository.FolderRepository;
import com.white.cloud_drive.repository.UserRepository;
import com.white.cloud_drive.security.SecurityUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    private final Path rootPath = Paths.get("storage");

    public FileService(FileRepository fileRepository,
                       FolderRepository folderRepository,
                       UserRepository userRepository,
                       SecurityUtils securityUtils) {
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    public FileDTO saveFile(MultipartFile file, Long parentId) {
        try {
            Long userId = securityUtils.getCurrentUserId();
            User owner = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Folder parentFolder = null;
            if (parentId != null) {
                parentFolder = folderRepository.findById(parentId)
                        .orElseThrow(() -> new RuntimeException("Parent folder not found"));

                if (!parentFolder.getOwner().getId().equals(owner.getId())) {
                    throw new RuntimeException("You can't add files to someone else's folder");
                }
            }

            boolean exists = parentFolder == null
                    ? fileRepository.existsByNameAndFolderIsNull(file.getOriginalFilename())
                    : fileRepository.existsByNameAndFolderId(file.getOriginalFilename(), parentFolder.getId());

            if (exists) {
                throw new RuntimeException("File with the same name already exists");
            }

            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String uniqueName = UUID.randomUUID() + (extension != null ? "." + extension : "");

            Path userPath = rootPath.resolve(userId.toString());

            Files.createDirectories(userPath);

            Path targetPath = userPath.resolve(uniqueName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            File fileEntity = new File();
            fileEntity.setName(file.getOriginalFilename());
            fileEntity.setStoredName(uniqueName);
            fileEntity.setOwner(owner);
            fileEntity.setFolder(parentFolder);
            fileEntity.setSize(file.getSize());
            fileEntity.setPath(buildFilePath(parentFolder, file.getOriginalFilename()));

            fileRepository.save(fileEntity);

            return new FileDTO(fileEntity);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public ResponseEntity<Resource> downloadFile(Long id) {
        Long userId = securityUtils.getCurrentUserId();

        File file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!file.getOwner().getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        Path filePath = rootPath
                .resolve(userId.toString())
                .resolve(file.getStoredName());

        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    public Resource getFileResource(String path) {
        Long userId = securityUtils.getCurrentUserId();

        File file = fileRepository.findByPathAndOwnerId(path, userId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        Path filePath = rootPath
                .resolve(userId.toString())
                .resolve(file.getStoredName().replaceFirst("^/", ""));

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("File not found on disk");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid file path");
        }
    }

    public Optional<FileDTO> getFileByPath(String path) {
        Long userId = securityUtils.getCurrentUserId();
        return fileRepository.findByPathAndOwnerId(path, userId)
                .map(this::mapToDTO);
    }

    private String buildFilePath(Folder parentFolder, String fileName) {
        if (parentFolder == null) return "/" + fileName;
        return buildFolderPath(parentFolder) + "/" + fileName;
    }

    private String buildFolderPath(Folder folder) {
        if (folder.getParentFolder() == null) {
            return folder.getName();
        }
        return buildFolderPath(folder.getParentFolder()) + "/" + folder.getName();
    }

    private FileDTO mapToDTO(File file) {
        return new FileDTO(file);
    }
}

