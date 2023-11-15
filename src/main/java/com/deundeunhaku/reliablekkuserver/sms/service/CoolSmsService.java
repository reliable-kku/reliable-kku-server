package com.deundeunhaku.reliablekkuserver.sms.service;

import com.deundeunhaku.reliablekkuserver.sms.dto.SmsCertificationNumber;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CoolSmsService {

  @Value("${coolsms.api.key}")
  private String apiKey;
  @Value("${coolsms.api.secret}")
  private String apiSecretKey;
  @Value("${coolsms.senderPhone}")
  private String senderPhone;


  private DefaultMessageService messageService;

  @PostConstruct
  private void init() {
    this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey,
        "https://api.coolsms.co.kr");
  }

  public void sendMessage(String to, String content) {
    try {
      Message message = new Message();

      message.setFrom(senderPhone);
      message.setTo(to);
      message.setText(content);

      this.messageService.sendOne(new SingleMessageSendingRequest(message));
    } catch (Exception e) {
      log.warn("SMS 전송 실패 번호 {}", to);
    }
  }


  public SmsCertificationNumber sendCertificationNumberToPhoneNumber(String phoneNumber) {

    int randomNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);
    String content = "안녕하세요! 든붕이 입니다. \n인증번호는 " + randomNumber + " 입니다.";

    sendMessage(phoneNumber, content);
    return SmsCertificationNumber.of(randomNumber);
  }

  public void sendNewPasswordToPhoneNumber(String phoneNumber, String newPassword) {
    String content = "안녕하세요! 든붕이 입니다. \n새로운 비밀번호는 " + newPassword + " 입니다.";

    sendMessage(phoneNumber, content);
  }

  public void sendOrderCompleteMessage(String phoneNumber, Long leftMinutes) {
    String content = "안녕하세요! 든붕이 입니다. \n주문이 완료되었습니다. \n" + leftMinutes + "분 후에 완료될 예정입니다.";

    sendMessage(phoneNumber, content);
  }

  public void sendOrderCancelMessage(String phoneNumber) {
    String content = "안녕하세요! 든붕이 입니다.\n가게의 사정으로 인해 주문이 취소되었습니다.\n다음에 이용해주세요.";

    sendMessage(phoneNumber, content);
  }


  public void sendOrderPickupMessage(String phoneNumber) {
    String content = "안녕하세요! 든붕이 입니다.\n붕어빵이 완성되었습니다!\n30분 내로 매장에서 붕어빵을 수령해주세요.";

    sendMessage(phoneNumber, content);
  }

  public void sendOrderNotTakeMessage(String phoneNumber) {
    String content = "안녕하세요! 든붕이 입니다.\n붕어빵을 시간내에 수령하지 않아 미수령 처리하였습니다.";

    sendMessage(phoneNumber, content);
  }


  public void sendOrderFinishMessage(String phoneNumber) {
    String content = "안녕하세요! 든붕이 입니다.\n붕어빵 맛있게 드세요! :>";

    sendMessage(phoneNumber, content);
  }

}
