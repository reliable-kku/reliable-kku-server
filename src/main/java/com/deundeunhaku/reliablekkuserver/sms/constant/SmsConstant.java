package com.deundeunhaku.reliablekkuserver.sms.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SmsConstant {

    @Value("${nhn-cloud-sms.appKey}")
    public String appKey;

    @Value("${nhn-cloud-sms.secretKey}")
    public String secretKey;

    @Value("${nhn-cloud-sms.senderPhone}")
    public String phone;

}
