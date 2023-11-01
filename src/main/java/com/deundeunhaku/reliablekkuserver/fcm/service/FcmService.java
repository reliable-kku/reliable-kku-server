package com.deundeunhaku.reliablekkuserver.fcm.service;

import com.deundeunhaku.reliablekkuserver.fcm.dto.FcmBaseRequest;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                    e.printStackTrace();
                    throw new RuntimeException("알림 보내기를 실패하였습니다. targetUserId=" + request.targetUserId());
                }
            } else {
                throw new RuntimeException("서버에 저장된 해당 유저의 FirebaseToken이 존재하지 않습니다. targetUserId="
                    + request.targetUserId());
            }
    }

}
