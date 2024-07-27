package com.storage.bavifour.app.controller;

import com.storage.bavifour.app.dto.folder.CreateFolderRequest;
import com.storage.bavifour.app.dto.folder.DeleteFolderRequest;
import com.storage.bavifour.app.dto.folder.RenameFolderRequest;
import com.storage.bavifour.app.dto.folder.UploadFolderRequest;
import com.storage.bavifour.app.exception.folder.FolderCreationException;
import com.storage.bavifour.app.exception.folder.FolderDeletionException;
import com.storage.bavifour.app.exception.folder.FolderRenameException;
import com.storage.bavifour.app.exception.folder.FolderUploadException;
import com.storage.bavifour.app.service.FileService;
import com.storage.bavifour.app.service.FolderService;
import com.storage.bavifour.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class FolderController {

    private static final Logger logger = LoggerFactory.getLogger(FolderController.class);

    @Autowired
    private FolderService folderService;

    @Autowired
    private FileService fileService;

    @PostMapping("/create-folder")
    public String createFolder(@ModelAttribute CreateFolderRequest createFolderRequest,
                               Model model) {
        String bucketName = SecurityUtils.getCurrentUsername();
        try {
            folderService.createFolder(bucketName,  createFolderRequest.getPath(), createFolderRequest.getFolderName());
            return "redirect:/?path=" + encodeURL(createFolderRequest.getPath());
        } catch (Exception e) {
            logger.error("Error creating folder: ", e);
            throw new FolderCreationException("Error creating folder: " + e.getMessage(), e);
        }
    }

    @PatchMapping("/rename-folder")
    public String renameFolder(@ModelAttribute RenameFolderRequest renameFolderRequest,
                               Model model) {
        String bucketName = SecurityUtils.getCurrentUsername();
        try {
            String folderName = trimTrailingSlash(renameFolderRequest.getFolderName());
            folderName = extractLastSegment(folderName);
            folderService.renameFolder(bucketName, renameFolderRequest.getPath(), folderName, renameFolderRequest.getNewName());
            return "redirect:/?path=" + encodeURL(renameFolderRequest.getPath());
        } catch (Exception e) {
            logger.error("Error renaming folder: ", e);
            throw new FolderRenameException("Error renaming folder: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/delete-folder")
    public String deleteFolder(@ModelAttribute DeleteFolderRequest deleteFolderRequest,
                               Model model) {
        String bucketName = SecurityUtils.getCurrentUsername();
        try {
            String folderName = trimTrailingSlash(deleteFolderRequest.getFolderName());
            folderName = extractLastSegment(folderName);
            folderService.deleteFolder(bucketName, deleteFolderRequest.getPath(), folderName);
            return "redirect:/?path=" + encodeURL(deleteFolderRequest.getPath());
        } catch (Exception e) {
            logger.error("Error deleting folder: ", e);
            throw new FolderDeletionException("Error deleting folder: " + e.getMessage(), e);
        }
    }

    @PostMapping("/upload-folder")
    public String uploadFolder(@ModelAttribute UploadFolderRequest uploadFolderRequest,
                               Model model) {
        String bucketName = SecurityUtils.getCurrentUsername();
        try {
            folderService.uploadFolder(bucketName, uploadFolderRequest.getPath(), uploadFolderRequest.getFiles());
            return "redirect:/?path=" + encodeURL(uploadFolderRequest.getPath());
        } catch (Exception e) {
            logger.error("Error uploading folder: ", e);
            throw new FolderUploadException("Error uploading folder: " + e.getMessage(), e);
        }
    }

    private String encodeURL(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String trimTrailingSlash(String value) {
        if (value == null) {
            return value;
        }
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }


    private String extractLastSegment(String value) {
        if (value == null) {
            return value;
        }
        int lastSlashIndex = value.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            return value.substring(lastSlashIndex + 1);
        }
        return value;
    }

}
