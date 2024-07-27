package com.storage.bavifour.app.dto.folder;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameFolderRequest {
    private String folderName;
    private String newName;
    private String path;
}