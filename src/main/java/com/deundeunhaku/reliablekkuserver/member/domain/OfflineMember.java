package com.deundeunhaku.reliablekkuserver.member.domain;

import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.google.firebase.database.annotations.NotNull;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class OfflineMember {

    @Id @GeneratedValue
    private Long id;

    @NotNull
    private String phoneNumber;

}
