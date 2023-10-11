package com.deundeunhaku.reliablekkuserver.order.domain;


import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.domain.OfflineMember;
import com.google.firebase.database.annotations.NotNull;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    @Id
    @CreatedDate
    private LocalDate orderDatetime;

    @NotNull
    @ColumnDefault("0")
    private Integer orderPrice;

    @NotNull
    private LocalDate expectedWaitDatetime;

    @NotNull
    private Boolean isOfflineOrder;

    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @OneToOne
    @JoinColumn(name="offlineMember_id")
    private OfflineMember OfflineMember;
}
