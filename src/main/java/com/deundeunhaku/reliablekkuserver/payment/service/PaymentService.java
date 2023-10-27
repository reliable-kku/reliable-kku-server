package com.deundeunhaku.reliablekkuserver.payment.service;

import com.deundeunhaku.reliablekkuserver.common.config.TossPaymentConfig;
import com.deundeunhaku.reliablekkuserver.common.exception.ExceptionCode;
import com.deundeunhaku.reliablekkuserver.common.exception.PaymentException;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import com.deundeunhaku.reliablekkuserver.payment.constants.PayType;
import com.deundeunhaku.reliablekkuserver.payment.domain.Payment;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentCancelRequest;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentCancelResponse;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentSuccess;
import com.deundeunhaku.reliablekkuserver.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpHead;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberService memberService;
    private String tossUrl = "https://api.tosspayments.com/v1/payments/";
    private String secretKey = "${payment.toss.test_secrete_api_key}";

    private String successUrl = "${payment.toss.success_url}";
    private String failUrl = "${payment.toss.fail_url}";


    //값 검증
    public Payment requestPayment(Payment paymentRequest, Long memberId){
        Member member = memberService.findMemberById(memberId);
        if(paymentRequest.getAmount() < 700){
            throw new PaymentException(ExceptionCode.INVALID_PAYMENT_AMOUNT);
        }
        paymentRequest.setMember(member);
        return paymentRepository.save(paymentRequest);
    }
    //토스 페이먼츠가 보낸 파라미터를 가지고 Service 로직에서 검증 후 반환
    @Transactional
    public PaymentSuccess paymentSuccess(String paymentKey, String orderId, Long amount) {
        Payment payment = verifyPayment(orderId, amount); // 요청가격 = 결제된 금액
        PaymentSuccess result = requestPaymentAccept(paymentKey, orderId, amount);
        payment.setPaymentKey(paymentKey);
        payment.setPaySuccessYn(true);
        return result;
    }

    //토스페이먼츠에 최종 결제 승인 요청을 보내기 위해 필요한 정보들을 담아 POST로 보내는 부분
    @Transactional
    public PaymentSuccess requestPaymentAccept(String paymentKey, String orderId, Long amount) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("orderId", orderId);
        params.put("amount", amount);

        PaymentSuccess result = null;
        try{
            result = restTemplate.postForObject(TossPaymentConfig.URL + paymentKey,
                    new HttpEntity<>(params, headers),
                    PaymentSuccess.class
                    );
        } catch(Exception e){
            throw new PaymentException(ExceptionCode.ALREADY_APPROVED);
        }
        return result;
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

    //결제 요청 금액과 실제 금액이 같은지
    private Payment verifyPayment(String orderId, Long amount) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> {
            throw new PaymentException(ExceptionCode.PAYMENT_NOT_FOUND);
        });
        if(!payment.getAmount().equals(amount)){
            throw new PaymentException(ExceptionCode.PAYMENT_AMOUNT_EXP);

        }
        return payment;
    }

    @Transactional
    public void paymentFail(String code, String message, String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() ->
                new PaymentException(ExceptionCode.PAYMENT_NOT_FOUND));
        payment.setPaySuccessYn(false);
        payment.setFailReason(message);
    }

    // 결제 취소를 요청하는 메서드
    public PaymentCancelResponse cancelPayment(String paymentKey, PaymentCancelRequest cancelRequest) {
        // 토스 페이먼츠 API로 결제 취소 요청을 보내고 응답을 받아옵니다.
        PaymentCancelResponse tossResponse = sendCancelRequestToToss(paymentKey, cancelRequest);

        // 결제 취소 성공 여부를 확인하고 결제 취소된 내역을 엔티티에 저장
        if (tossResponse.getStatus().equals("CANCELED")) {
            // 결제 취소 성공 시 로직
            // 엔티티에 결제 취소 정보 저장
        } else {
            // 결제 취소 실패 시 로직
            // 예외 처리 또는 로깅
        }

        return tossResponse;
    }

    // 토스 페이먼츠 API로 결제 취소 요청을 보내고 응답을 받는 메서드
    private PaymentCancelResponse sendCancelRequestToToss(String paymentKey, PaymentCancelRequest cancelRequest) {
        RestTemplate restTemplate = new RestTemplate();

        // 결제 취소 요청을 보낼 URL 설정
        String cancelUrl = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";
        return null;
    }

   /* @Transactional
    public Map cancelPayment(String paymentKey, String cancelReason, Integer cancelAmount) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey).orElseThrow(() -> {
            new PaymentException(ExceptionCode.PAYMENT_NOT_FOUND);
        });
        payment.setCancelYN(true);
        payment.setCancelReason(cancelReason);

        return

    }*/
}
