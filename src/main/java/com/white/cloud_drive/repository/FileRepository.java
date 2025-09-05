package com.white.cloud_drive.repository;

import com.white.cloud_drive.model.File;
import com.white.cloud_drive.model.Folder;
import com.white.cloud_drive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    boolean existsByNameAndFolderId(String name, Long folderId);
    boolean existsByNameAndFolderIsNull(String name);
    List<File> findByOwnerId(Long ownerId);
    Optional<File> findByPathAndOwnerId(String path, Long ownerId);

}