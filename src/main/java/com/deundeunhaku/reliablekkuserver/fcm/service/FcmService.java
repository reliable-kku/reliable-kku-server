package com.deundeunhaku.reliablekkuserver.fcm.service;

import com.deundeunhaku.reliablekkuserver.fcm.FcmTokenRepository;
import com.deundeunhaku.reliablekkuserver.fcm.domain.FcmToken;
import com.deundeunhaku.reliablekkuserver.fcm.dto.FcmBaseRequest;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcmService {

  private final FirebaseMessaging firebaseMessaging;
  private final MemberService memberService;
  private final FcmTokenRepository fcmTokenRepository;

  public void sendNotificationByOrderId(FcmBaseRequest request) {

    Member member = memberService.findMemberById(request.targetUserId());

    List<FcmToken> fcmTokenList = fcmTokenRepository.findByMember(member);

    for (FcmToken fcmToken : fcmTokenList) {
      Notification notification = Notification.builder()
          .setTitle(request.title())
          .setBody(request.body())
          .setImage(request.imageUrl())
          .build();

      Message message = Message.builder()
          .setToken(fcmToken.getToken())
          .setNotification(notification)
          .build();

      try {
        firebaseMessaging.send(message);
      } catch (FirebaseMessagingException e) {
        log.warn("알림 보내기를 실패하였습니다. targetUserId={}", request.targetUserId());
      }
    }
  }

  public void sendNotificationToAdmin(String title, String body) {

    Member admin = memberService.findMemberById(1L);

    List<FcmToken> fcmTokenList = fcmTokenRepository.findByMember(admin);

    for (FcmToken fcmToken : fcmTokenList) {
      Notification notification = Notification.builder()
          .setTitle(title)
          .setBody(body)
          .build();

      Message message = Message.builder()
          .setToken(fcmToken.getToken())
          .setNotification(notification)
          .build();

      try {
        firebaseMessaging.send(message);
      } catch (FirebaseMessagingException e) {
        log.warn("알림 보내기를 실패하였습니다. fcmToken={}", fcmToken.getToken());

      }
    }


  }

}
