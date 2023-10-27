package com.deundeunhaku.reliablekkuserver.payment.domain;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.payment.constants.PayType;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentResponse;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Auditable;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idx_payment_member", columnList = "member"),
        @Index(name = "idx_payment_paymentKey", columnList = "paymentKey" ),
})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false, unique = true)
    private Long paymentId;

    @Column(nullable = false, name="pay_type")
    @Enumerated(EnumType.STRING)
    private PayType payType;

    @Column(nullable = false, name="pay_amount")
    private Long amount;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String orderName;

    private boolean paySuccessYn;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member")
    private Member member;

    @Column
    private String paymentKey;

    @Column
    private String failReason;

    @Column
    private boolean cancelYN;

    @Column
    private String cancelReason;

    @Column(nullable = false)
    private LocalDate createdAt;

    public PaymentResponse toPaymentResDto() {
        return PaymentResponse.builder()
                .payType(payType.getPayType())
                .amount(amount)
                .orderName(orderName)
                .orderId(orderId)
                .customerName(member.getRealName())
                .createAt(String.valueOf(getCreatedAt()))
                .cancelYn(cancelYN)
                .failResponse(failReason)
                .build();
    }
}
