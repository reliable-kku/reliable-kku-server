package com.deundeunhaku.reliablekkuserver.payment.service;

import com.deundeunhaku.reliablekkuserver.common.config.TossPaymentConfig;
import com.deundeunhaku.reliablekkuserver.common.exception.PaymentCancelException;
import com.deundeunhaku.reliablekkuserver.common.exception.PaymentException;
import com.deundeunhaku.reliablekkuserver.payment.domain.Payment;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentCancelRequest;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentCancelResponse;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentConfirmRequest;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentErrorResponse;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentSuccess;
import com.deundeunhaku.reliablekkuserver.payment.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final ObjectMapper objectMapper;

  @Value("${payment.toss.test_secrete_api_key}")
  private String secretKey;

  @Value("${payment.toss.success_url}")
  private String successUrl;
  @Value("${payment.toss.fail_url}")
  private String failUrl;

  //토스 페이먼츠가 보낸 파라미터를 가지고 Service 로직에서 검증 후 반환
  @Transactional
  public void confirmPayment(String paymentKey, String orderId, Long amount) {

    PaymentSuccess result = requestPaymentAccept(paymentKey, orderId, amount);

    Payment payment = Payment.builder()
        .paymentKey(paymentKey)
        .tossOrderId(orderId)
        .amount(amount)
        .paySuccessYn(true)
        .payType(result.type())
        .orderName(result.orderName())
        .build();

//        Payment payment = verifyPayment(orderId, amount); // 요청가격 = 결제된 금액

    paymentRepository.save(payment);
  }

  //토스페이먼츠에 최종 결제 승인 요청을 보내기 위해 필요한 정보들을 담아 POST로 보내는 부분
  @Transactional
  public PaymentSuccess requestPaymentAccept(String paymentKey, String orderId, Long amount) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = getHeaders();
    PaymentConfirmRequest requestBody = PaymentConfirmRequest.of(paymentKey, orderId, amount);

    String result = null;
    try {
      restTemplate.getInterceptors().add(((request, body, execution) -> {
        ClientHttpResponse response = execution.execute(request, body);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response;
      }));

      result = restTemplate.exchange(TossPaymentConfig.URL + "confirm", HttpMethod.POST,
          new HttpEntity<>(requestBody, headers),
          String.class
      ).getBody();
      return objectMapper.readValue(result, PaymentSuccess.class);
    } catch (HttpClientErrorException e) {
      PaymentErrorResponse errorResponse = e.getResponseBodyAs(PaymentErrorResponse.class);
      log.warn(Objects.requireNonNull(errorResponse).message());
      throw new PaymentException(errorResponse.message());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }


  private HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    String encodeAuthKey = new String(
        Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
    headers.setBasicAuth(encodeAuthKey);
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    return headers;
  }


  // 결제 취소를 요청하는 메서드
  @Transactional
  public void cancelPayment(Long orderId, PaymentCancelRequest cancelRequest) {
    Payment payment = paymentRepository.findPaymentKeyByOrderId(orderId)
        .orElseThrow(PaymentCancelException::new);

//        Order order = orderRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);

    /*Payment payment = paymentRepository.findByPaymentKeyAndOrderId(paymentKey, order).orElseThrow(PaymentException::new);*/
    //토스 페이먼츠 API로 결제 취소 요청을 보내고 응답을 받아옵니다.
    PaymentCancelResponse tossResponse = sendCancelRequestToToss(payment.getPaymentKey(),
        cancelRequest);

    // 결제 취소 성공 여부를 확인하고 결제 취소된 내역을 엔티티에 저장
    if (tossResponse.getStatus().equals("CANCELED")) {
      // 결제 취소 성공 시 로직
      payment.setCancelYN(true);
      payment.setCancelReason(cancelRequest.cancelReason());
//            order.updateOrderStatus(OrderStatus.CANCELED);
      // 엔티티에 결제 취소 정보 저장
    } else if (tossResponse.getStatus().equals("ABORTED")) {
      // 결제 취소 실패 시 로직
      payment.setCancelYN(false);
      // 예외 처리 또는 로깅
      throw new PaymentCancelException();
    } else {
      throw new PaymentCancelException();
    }
  }

  // 토스 페이먼츠 API로 결제 취소 요청을 보내고 응답을 받는 메서드
  @Transactional
  public PaymentCancelResponse sendCancelRequestToToss(String paymentKey,
      PaymentCancelRequest cancelRequest) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = getHeaders();
    PaymentCancelRequest requestBody = PaymentCancelRequest.of(cancelRequest.cancelReason());

    String result = "";
    try {
//      result = restTemplate.postForObject(TossPaymentConfig.URL + paymentKey + "/cancel",
//          new HttpEntity<>(requestBody, headers), PaymentCancelResponse.class
//      );
      result = restTemplate.exchange(TossPaymentConfig.URL + paymentKey + "/cancel",
          HttpMethod.POST,
          new HttpEntity<>(requestBody, headers),
          String.class
      ).getBody();
      log.info("result : {}", result);

      return objectMapper.readValue(result, PaymentCancelResponse.class);

    } catch (HttpClientErrorException e) {
      PaymentErrorResponse errorResponse = e.getResponseBodyAs(PaymentErrorResponse.class);
      log.warn(Objects.requireNonNull(errorResponse).message());

      throw new PaymentCancelException(errorResponse.message());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

  }

}
