package com.deundeunhaku.reliablekkuserver.payment.controller;

import com.deundeunhaku.reliablekkuserver.common.config.TossPaymentConfig;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.payment.dto.*;
import com.deundeunhaku.reliablekkuserver.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final TossPaymentConfig tossPaymentConfig;


    @PostMapping("/requestPayment")
    public ResponseEntity<PaymentResponse> requestPayments(@AuthenticationPrincipal Member member, @RequestBody @Valid PaymentRequest paymentReq) {
        PaymentResponse paymentRes = paymentService.requestPayment(paymentReq.toEntity(), member.getId()).toPaymentResDto();
        paymentRes.setSuccessUrl(paymentReq.getSuccessUrl() == null ? tossPaymentConfig.getSuccessUrl() : paymentReq.getSuccessUrl());
        paymentRes.setFailUrl(paymentReq.getFailUrl() == null ? tossPaymentConfig.getFailUrl() : paymentReq.getSuccessUrl());
        return ResponseEntity.ok(paymentRes);
    }

    @GetMapping("/requestPayment/success")
    public ResponseEntity paymentSuccess(@RequestParam String paymentKey, @RequestParam String orderId, @RequestParam Long amount){
        return ResponseEntity.ok().body(paymentService.paymentSuccess(paymentKey, orderId, amount));

    }

    @GetMapping("/requestPayment/fail")
    public ResponseEntity paymentFail(@RequestParam String code, @RequestParam String message, @RequestParam String orderId){
        paymentService.paymentFail(code, message, orderId);
        return ResponseEntity.ok().body(PaymentFail.builder()
                .errorCode(code)
                .errorMessage(message)
                .orderId(orderId)
                .build()
        );
    }

    @PostMapping("/{paymentKey}/cancel")
    public ResponseEntity<PaymentCancelResponse> cancelPayment(@PathVariable String paymentKey,
                                                               @RequestBody PaymentCancelRequest cancelRequest) {
        PaymentCancelResponse response = paymentService.cancelPayment(paymentKey, cancelRequest);
        return ResponseEntity.ok(response);

    }

    /*@GetMapping("/success")
    public ResponseEntity<Payment> requestFinalPayments(
        @RequestParam(value = "orderId") String orderId,
        @RequestParam(value = "amount") Integer amount,
        @RequestParam(value = "paymentKey") String paymentKey) throws Exception {
    ) {
        try {
            paymentService.verifyRequest(paymentKey, orderId, amount);
            Payment result = paymentService.requestFinalPayment(paymentKey, orderId, amount);

            return responseService.getSingleResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BussinessException(e.getMessage());
        }
    }
        }*/

    //client로부터 결제 요청 성공 받음
    //toss로 결제 요청



    /*@GetMapping("/success")
    public String paymentResult(
            Model model,
            @RequestParam(value = "orderId") String orderId,
            @RequestParam(value = "amount") Integer amount,
            @RequestParam(value = "paymentKey") String paymentKey) throws Exception {


        String secretKey = tossPaymentConfig.getTestSecretKey();

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(secretKey.getBytes("UTF-8"));
        String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);

        URL url = new URL(TossPaymentConfig.URL + paymentKey);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200 ? true : false;
        model.addAttribute("isSuccess", isSuccess);

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();
        model.addAttribute("responseStr", jsonObject.toJSONString());
        System.out.println(jsonObject.toJSONString());

        model.addAttribute("method", (String) jsonObject.get("method"));
        model.addAttribute("orderName", (String) jsonObject.get("orderName"));

        if (((String) jsonObject.get("method")) != null) {
            if (((String) jsonObject.get("method")).equals("카드")) {
                model.addAttribute("cardNumber", (String) ((JSONObject) jsonObject.get("card")).get("number"));
            } else if (((String) jsonObject.get("method")).equals("가상계좌")) {
                model.addAttribute("accountNumber", (String) ((JSONObject) jsonObject.get("virtualAccount")).get("accountNumber"));
            } else if (((String) jsonObject.get("method")).equals("계좌이체")) {
                model.addAttribute("bank", (String) ((JSONObject) jsonObject.get("transfer")).get("bank"));
            } else if (((String) jsonObject.get("method")).equals("휴대폰")) {
                model.addAttribute("customerMobilePhone", (String) ((JSONObject) jsonObject.get("mobilePhone")).get("customerMobilePhone"));
            }
        } else {
            model.addAttribute("code", (String) jsonObject.get("code"));
            model.addAttribute("message", (String) jsonObject.get("message"));
        }

        return "success";
    }

    @GetMapping("/fail")
    public String paymentResult(
            Model model,
            @RequestParam(value = "message") String message,
            @RequestParam(value = "code") Integer code
    ) throws Exception {

        model.addAttribute("code", code);
        model.addAttribute("message", message);

        return "fail";
    }*/
}
