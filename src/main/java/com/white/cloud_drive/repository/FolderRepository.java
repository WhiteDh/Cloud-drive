package com.white.cloud_drive.repository;

import com.white.cloud_drive.model.Folder;
import com.white.cloud_drive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findByOwnerId(Long ownerId);
    boolean existsByNameAndParentFolderAndOwner(String name, Folder parentFolder, User owner);

}
