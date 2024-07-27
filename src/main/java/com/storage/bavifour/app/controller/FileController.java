package com.storage.bavifour.app.controller;

import com.storage.bavifour.app.dto.file.FileDeleteRequest;
import com.storage.bavifour.app.dto.file.FileRenameRequest;
import com.storage.bavifour.app.dto.file.FileUploadRequest;
import com.storage.bavifour.app.exception.file.FileDeletionException;
import com.storage.bavifour.app.exception.file.FileRenameException;
import com.storage.bavifour.app.exception.file.FileUploadException;
import com.storage.bavifour.app.service.FileService;
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
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public String uploadFile(@ModelAttribute FileUploadRequest fileUploadRequest,
                             Model model) {
        String bucketName = SecurityUtils.getCurrentUsername();
        try {
            fileService.uploadFile(bucketName, fileUploadRequest.getPath(), fileUploadRequest.getFile());
            return "redirect:/?path=" + encodeURL(fileUploadRequest.getPath());
        } catch (Exception e) {
            logger.error("Error uploading file: ", e);
            throw new FileUploadException("Error uploading file: " + e.getMessage(), e);

        }
    }

    @PatchMapping("/rename")
    public String renameFile(@ModelAttribute FileRenameRequest fileRenameRequest,
                             Model model) {
        String bucketName = SecurityUtils.getCurrentUsername();
        try {
            int lastSlashIndex = fileRenameRequest.getFileName().lastIndexOf('/');
            String fileName = fileRenameRequest.getFileName();
            if (lastSlashIndex != -1) {
                fileName = fileName.substring(lastSlashIndex + 1);
            }
            fileService.renameFile(bucketName, fileRenameRequest.getPath(), fileName, fileRenameRequest.getNewName());
            return "redirect:/?path=" + encodeURL(fileRenameRequest.getPath());
        } catch (Exception e) {
            logger.error("Error renaming file: ", e);
            throw new FileRenameException("Error renaming file: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/delete")
    public String deleteFile(@ModelAttribute FileDeleteRequest fileDeleteRequest,
                             Model model) {
        String bucketName = SecurityUtils.getCurrentUsername();
        try {
            int lastSlashIndex = fileDeleteRequest.getFileName().lastIndexOf('/');
            String fileName = fileDeleteRequest.getFileName();
            if (lastSlashIndex != -1) {
                fileName = fileName.substring(lastSlashIndex + 1);
            }
            fileService.deleteFile(bucketName, fileDeleteRequest.getPath(), fileName);
            return "redirect:/?path=" + encodeURL(fileDeleteRequest.getPath());
        } catch (Exception e) {
            logger.error("Error deleting file: ", e);
            throw new FileDeletionException("Error deleting file: " + e.getMessage(), e);
        }
    }

    private String encodeURL(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
