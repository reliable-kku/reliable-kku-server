package com.deundeunhaku.reliablekkuserver.fcm.service;

import com.deundeunhaku.reliablekkuserver.fcm.dto.FcmBaseRequest;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcmService {

  private final FirebaseMessaging firebaseMessaging;
  private final MemberService memberService;

  public void sendNotificationByOrderId(FcmBaseRequest request) {

    Member member = memberService.findMemberById(request.targetUserId());

    if (member.getFirebaseToken() != null) {
      Notification notification = Notification.builder()
          .setTitle(request.title())
          .setBody(request.body())
          .setImage(request.imageUrl())
          .build();

      Message message = Message.builder()
          .setToken(member.getFirebaseToken())
          .setNotification(notification)
//                         .putAllData(requestDto.getData())
          .build();

      try {
        firebaseMessaging.send(message);
      } catch (FirebaseMessagingException e) {
        log.warn("알림 보내기를 실패하였습니다. targetUserId={}", request.targetUserId());
//                    e.printStackTrace();
//                    throw new RuntimeException("알림 보내기를 실패하였습니다. targetUserId=" + request.targetUserId());
      }
    } else {
      log.warn("서버에 저장된 해당 유저의 FirebaseToken이 존재하지 않습니다. targetUserId={}", request.targetUserId());
//                throw new RuntimeException("서버에 저장된 해당 유저의 FirebaseToken이 존재하지 않습니다. targetUserId="
//                    + request.targetUserId());
    }
  }

  public void sendNotificationToAdmin(String adminFcmToken, String title, String body) {
    Notification notification = Notification.builder()
        .setTitle(title)
        .setBody(body)
        .build();

    Message message = Message.builder()
        .setToken(adminFcmToken)
        .setNotification(notification)
        .build();

    try {
      firebaseMessaging.send(message);
    } catch (FirebaseMessagingException e) {
      log.warn("알림 보내기를 실패하였습니다. adminFcmToken={}", adminFcmToken);

    }
  }

}
