package com.deundeunhaku.reliablekkuserver.sms.dto;

public record RecipientList(
        String recipientNo
) {
    public static RecipientList of(String recipientNo) {
        return new RecipientList(recipientNo);
    }
}
