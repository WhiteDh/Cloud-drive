package com.white.cloud_drive.service;

import com.white.cloud_drive.dto.CreateFolderRequest;
import com.white.cloud_drive.dto.FolderDTO;
import com.white.cloud_drive.model.Folder;
import com.white.cloud_drive.model.User;
import com.white.cloud_drive.repository.FolderRepository;
import com.white.cloud_drive.repository.UserRepository;
import com.white.cloud_drive.security.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public FolderService(FolderRepository folderRepository, UserRepository userRepository,  SecurityUtils securityUtils) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    public Folder createFolder(CreateFolderRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Folder folder = new Folder();
        folder.setName(request.getName());
        folder.setOwner(owner);
        Folder parent = null;
        if (request.getParentId() != null) {
            parent = folderRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent folder not found"));

            if (!parent.getOwner().getId().equals(owner.getId())) {
                throw new RuntimeException("You can't create folders in someone else's directory");
            }

            folder.setParentFolder(parent);
            folder.setPath(parent.getPath() + "/" + request.getName());
        } else {

            folder.setPath("/" + request.getName());
        }

        boolean exists = folderRepository.existsByNameAndParentFolderAndOwner(request.getName(), parent, owner);
        if (exists) {
            throw new RuntimeException("Folder with this name already exists in this directory");
        }
        return folderRepository.save(folder);
    }


    public List<FolderDTO> getAllFoldersByUser(Long ownerId) {
        List<Folder> folders = folderRepository.findByOwnerId(ownerId);
        return folders.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<FolderDTO> getFolderById(Long id) {
        return folderRepository.findById(id).map(this::mapToDTO);
    }

    private FolderDTO mapToDTO(Folder folder) {
        Long parentId = folder.getParentFolder() != null ? folder.getParentFolder().getId() : null;
        Long ownerId = folder.getOwner() != null ? folder.getOwner().getId() : null;
        return new FolderDTO(folder.getId(), folder.getName(), folder.getPath(), parentId, ownerId);
    }
}
