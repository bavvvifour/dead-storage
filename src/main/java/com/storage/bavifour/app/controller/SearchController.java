package com.storage.bavifour.app.controller;

import com.storage.bavifour.app.exception.SearchException;
import com.storage.bavifour.app.service.SearchService;
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
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public String search(@RequestParam("query") String query,
                         Model model) {
        String bucketName = SecurityUtils.getCurrentUsername();
        try {
            List<String> results = searchService.searchFilesAndFolders(bucketName, query);
            model.addAttribute("results", results);
            model.addAttribute("query", query);
        } catch (Exception e) {
            logger.error("Error searching files in bucket: {}", bucketName, e);
            throw new SearchException("Error searching files: " + e.getMessage(), e);
        }
        return "index";
    }
}
