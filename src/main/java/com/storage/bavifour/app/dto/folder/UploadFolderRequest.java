package com.storage.bavifour.app.dto.folder;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadFolderRequest {
    private MultipartFile[] files;
    private String path;
}