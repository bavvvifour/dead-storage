package com.storage.bavifour.app.dto.file;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDeleteRequest {
    private String fileName;
    private String path;
}