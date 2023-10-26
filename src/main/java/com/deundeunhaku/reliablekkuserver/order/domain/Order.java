package com.deundeunhaku.reliablekkuserver.order.domain;


import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.domain.OfflineMember;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderRegisterRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;

@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Order implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private Long todayOrderCount;

  @CreatedDate
  private LocalDateTime orderDatetime;

  @NotNull
  @ColumnDefault("0")
  private Integer orderPrice;

  @NotNull
  private LocalDateTime expectedWaitDatetime;

  @NotNull
  private Boolean isOfflineOrder;

  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @CreatedDate
  private LocalDate createdAt;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @OneToOne
  @JoinColumn(name = "offlineMember_id")
  private OfflineMember offlineMember;

  public void updateOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }


  public Order(Long todayOrderCount, LocalDateTime orderDatetime, Integer orderPrice,
      LocalDateTime expectedWaitDatetime, Boolean isOfflineOrder, LocalDate createdAt,
      Member member, OrderStatus orderStatus
  ) {
    this.todayOrderCount = todayOrderCount;
    this.orderDatetime = orderDatetime;
    this.orderPrice = orderPrice;
    this.expectedWaitDatetime = expectedWaitDatetime;
    this.isOfflineOrder = isOfflineOrder;
    this.createdAt = createdAt;
    this.member = member;
    this.orderStatus = orderStatus;
  }

  public static Order createOfflineOrder(Long todayOrderCount, OrderRegisterRequest request,
      Member member) {
    return new Order(
        todayOrderCount,
        LocalDateTime.now(),
        request.orderPrice(),
        LocalDateTime.now(),
        true,
        LocalDate.now(),
        member,
        OrderStatus.WAIT);
  }
}
