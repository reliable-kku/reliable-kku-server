package com.deundeunhaku.reliablekkuserver.sms.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ToSmsServerRequest {
    String body;
    String sendNo;
    List<RecipientList> recipientList;
}
