package com.deundeunhaku.reliablekkuserver.payment.domain;

import com.deundeunhaku.reliablekkuserver.common.domain.BaseEntity;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.payment.constants.PayType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idx_payment_paymentKey", columnList = "paymentKey" )
})
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PayType payType;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String tossOrderId;

    @Column(nullable = false)
    private String orderName;

    private boolean paySuccessYn;

    @OneToOne
    @JoinColumn(name = "order_tb_id")
    private Order order;

    @Column
    private String paymentKey;

    @Column
    private String failReason;

    @Column
    private boolean cancelYN;

    @Column
    private String cancelReason;

}
