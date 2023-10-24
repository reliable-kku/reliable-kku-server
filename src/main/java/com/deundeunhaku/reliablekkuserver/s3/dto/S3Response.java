package com.deundeunhaku.reliablekkuserver.s3.dto;

public record S3Response (
        String originalFileName,
        String fileS3Key,
        String s3ImageUrl
) {

    public static S3Response of (String originalFileName, String fileS3Key, String s3ImageUrl) {
        return new S3Response(originalFileName, fileS3Key, s3ImageUrl);
    }
}
