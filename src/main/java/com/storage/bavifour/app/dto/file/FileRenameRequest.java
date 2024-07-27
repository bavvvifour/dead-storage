package com.storage.bavifour.app.dto.file;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileRenameRequest {
    private String fileName;
    private String newName;
    private String path;
}