package com.deundeunhaku.reliablekkuserver.fcm.dto;

public record FcmBaseRequest(
    Long targetUserId,
    String title,
    String body,
    String imageUrl

) {

    public FcmBaseRequest(Long targetUserId, String title, String body) {
        this(targetUserId, title, body, "https://deundeunhaku-bucket.s3.ap-northeast-2.amazonaws.com/push_image.jpg");
    }

    public static FcmBaseRequest of(Long targetUserId, String title, String body) {
        return new FcmBaseRequest(targetUserId, title, body);
    }
}
