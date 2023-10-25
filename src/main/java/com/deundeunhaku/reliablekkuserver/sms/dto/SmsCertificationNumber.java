package com.deundeunhaku.reliablekkuserver.sms.dto;

public record SmsCertificationNumber(
        int certificationNumber
) {

        public static SmsCertificationNumber of(int certificationNumber) {
            return new SmsCertificationNumber(certificationNumber);
        }
}
