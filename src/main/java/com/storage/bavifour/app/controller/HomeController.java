package com.storage.bavifour.app.controller;

import com.storage.bavifour.app.exception.file.FileListingException;
import com.storage.bavifour.app.service.BucketService;
import com.storage.bavifour.app.service.FileService;
import com.storage.bavifour.utils.BreadcrumbsUtil.Breadcrumb;
import com.storage.bavifour.utils.BreadcrumbsUtil.BreadcrumbsUtil;
import com.storage.bavifour.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private FileService fileService;

    @Autowired
    private BucketService bucketService;

    @GetMapping("/")
    public String listObjects(@RequestParam(value = "path", required = false, defaultValue = "") String path,
                              Model model) {

        String bucketName = SecurityUtils.getCurrentUsername();
        bucketService.createBucket(bucketName);

        try {
            List<String> fileNames = fileService.listFiles(bucketName, path);

            List<Breadcrumb> breadcrumbs = BreadcrumbsUtil.generateBreadcrumbs(path);

            model.addAttribute("files", fileNames);
            model.addAttribute("path", path);
            model.addAttribute("breadcrumbs", breadcrumbs);
        } catch (Exception e) {
            logger.error("Error listing files for bucket: {}", bucketName, e);
            throw new FileListingException("Error listing files: " + e.getMessage(), e);
        }
        return "index";
    }
}
