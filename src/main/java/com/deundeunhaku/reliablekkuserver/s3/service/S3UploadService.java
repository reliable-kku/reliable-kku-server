package com.deundeunhaku.reliablekkuserver.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.deundeunhaku.reliablekkuserver.s3.dto.S3Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String s3Bucket = "https://deundeunhaku-bucket.s3.ap-northeast-2.amazonaws.com";

    public S3Response saveFileWithUUID(MultipartFile multipartFile) {
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            ObjectMetadata metadata = setMetadata(multipartFile);

            String uuid = UUID.randomUUID().toString();

            saveImageWithUUID(multipartFile, metadata, uuid);
            return S3Response.of(originalFilename, uuid, amazonS3.getUrl(bucket, uuid).toString());
        } catch (IOException e) {
            log.error("s3에 file 저장중 에러 발생", e);
            throw new RuntimeException(e);
        }

    }

    public void deleteFile(String image) {
        amazonS3.deleteObject(bucket, image);
    }

    private PutObjectResult saveImageWithUUID(MultipartFile multipartFile, ObjectMetadata metadata, String uuid) throws IOException {
        return amazonS3.putObject(bucket, uuid, multipartFile.getInputStream(), metadata);
    }

    private static ObjectMetadata setMetadata(MultipartFile multipartFile) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        return metadata;
    }
}
