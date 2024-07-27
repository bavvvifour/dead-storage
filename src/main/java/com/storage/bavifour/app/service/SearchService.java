package com.storage.bavifour.app.service;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    @Autowired
    private MinioClient minioClient;

    public List<String> searchFilesAndFolders(String bucketName, String searchQuery) throws Exception {
        List<String> results = new ArrayList<>();

        Iterable<Result<Item>> objects = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(true)
                        .build());

        for (Result<Item> result : objects) {
            Item item = result.get();
            String objectName = item.objectName();
            if (objectName.contains(searchQuery)) {
                results.add(objectName);
            }
        }

        return results;
    }
}
