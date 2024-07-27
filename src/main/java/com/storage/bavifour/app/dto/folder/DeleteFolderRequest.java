package com.storage.bavifour.app.dto.folder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteFolderRequest {
    private String folderName;
    private String path;
}