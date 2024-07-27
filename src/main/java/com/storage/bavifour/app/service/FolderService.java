package com.storage.bavifour.app.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class FolderService {
    @Autowired
    private MinioClient minioClient;

    public void createFolder(String bucketName, String path, String folderName) throws Exception {
        String folderPath = path.isEmpty() ? folderName + "/" : path + "/" + folderName + "/";
        folderPath = folderPath + ".emptyFolder";
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(folderPath)
                        .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                        .build());
    }


    public void renameFolder(String bucketName, String path, String folderName, String newName) throws Exception {
        String oldPrefix = path.isEmpty() ? folderName + "/" : path + "/" + folderName + "/";
        String newPrefix = path.isEmpty() ? newName + "/" : path + "/" + newName + "/";

        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(oldPrefix)
                .recursive(true)
                .build());

        for (Result<Item> result : results) {
            Item item = result.get();
            String newObjectName = newPrefix + item.objectName().substring(oldPrefix.length());

            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(bucketName)
                    .object(newObjectName)
                    .source(CopySource.builder()
                            .bucket(bucketName)
                            .object(item.objectName())
                            .build())
                    .build());

            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(item.objectName())
                    .build());
        }
    }

    public void deleteFolder(String bucketName, String path, String folderName) throws Exception {
        String prefix = path.isEmpty() ? folderName + "/" : path + "/" + folderName + "/";

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .recursive(true)
                        .build());

        for (Result<Item> result : results) {
            Item item = result.get();
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(item.objectName())
                            .build());
        }
    }

public void uploadFolder(String bucketName, String path, MultipartFile[] files) {
    for (MultipartFile file : files) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) continue;

        int lastIndex = originalFilename.lastIndexOf('/');
        if (lastIndex != -1) {
            String folderPath = path + "/" + originalFilename.substring(0, lastIndex + 1);

            try {
                String markerPath = folderPath + ".emptyFolder";
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(markerPath)
                                .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                                .build());
            } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        try (InputStream inputStream = file.getInputStream()) {
            String objectName = path + "/" + originalFilename;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}

}
