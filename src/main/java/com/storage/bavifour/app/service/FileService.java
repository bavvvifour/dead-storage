package com.storage.bavifour.app.service;

import io.minio.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
    @Autowired
    private MinioClient minioClient;

    public List<String> listFiles(String bucketName, String path) throws Exception {
        String prefix = path.isEmpty() ? "" : path + "/";
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .build());

        List<String> files = new ArrayList<>();
        for (Result<Item> result : results) {
            Item item = result.get();
            String objectName = item.objectName();
            if (!objectName.equals(path)) {
                files.add(objectName);
            }
        }
        return files;
    }

    public void uploadFile(String bucketName, String path, MultipartFile file) throws Exception {
        String objectName = path.isEmpty() ? file.getOriginalFilename() : path + "/" + file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
    }

    public void renameFile(String bucketName, String path, String fileName, String newName) throws Exception {
        String oldObjectName = path.isEmpty() ? fileName : path + "/" + fileName;
        String newObjectName = path.isEmpty() ? newName : path + "/" + newName;

        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(bucketName)
                        .object(newObjectName)
                        .source(CopySource.builder()
                                .bucket(bucketName)
                                .object(oldObjectName)
                                .build())
                        .build());
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(oldObjectName)
                        .build());
    }

    public void deleteFile(String bucketName, String path, String fileName) throws Exception {
        String objectName = path.isEmpty() ? fileName : path + "/" + fileName;
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }
}
