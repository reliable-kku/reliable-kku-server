package com.deundeunhaku.reliablekkuserver.order.domain;


import com.deundeunhaku.reliablekkuserver.common.domain.BaseEntity;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.domain.OfflineMember;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.dto.OfflineOrderRequest;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;

@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "order_tb")
public class Order extends BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
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

  private LocalDate createdDate;


  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @OneToOne
  @JoinColumn(name = "offlineMember_id")
  private OfflineMember offlineMember;

  public void addMinutesToExpectedWaitDateTime(Integer addMinutes) {
    this.expectedWaitDatetime = this.orderDatetime.plusMinutes(addMinutes);
  }

  public void updateOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  @Builder
  public Order(Long id, Long todayOrderCount, LocalDateTime orderDatetime, Integer orderPrice,
      LocalDateTime expectedWaitDatetime, Boolean isOfflineOrder, OrderStatus orderStatus,
      LocalDate createdDate, Member member, OfflineMember offlineMember) {
    this.id = id;
    this.todayOrderCount = todayOrderCount;
    this.orderDatetime = orderDatetime;
    this.orderPrice = orderPrice;
    this.expectedWaitDatetime = expectedWaitDatetime;
    this.isOfflineOrder = isOfflineOrder;
    this.orderStatus = orderStatus;
    this.createdDate = createdDate;
    this.member = member;
    this.offlineMember = offlineMember;
  }





  public static Order createOnlineOrder(Long todayOrderCount, OrderRegisterRequest request,
      Member member) {
    return Order
        .builder()
        .todayOrderCount(todayOrderCount)
        .orderDatetime(LocalDateTime.now())
        .orderPrice(request.orderPrice())
        .expectedWaitDatetime(LocalDateTime.now())
        .isOfflineOrder(false)
        .orderStatus(OrderStatus.WAIT)
        .member(member)
        .createdDate(LocalDate.now())
        .build();
  }


  public static Order createOfflineOrder(Long todayOrderCount, OfflineOrderRequest request,
      OfflineMember member) {

    return Order.builder()
        .todayOrderCount(todayOrderCount)
        .orderDatetime(LocalDateTime.now())
        .expectedWaitDatetime(LocalDateTime.now())
        .orderPrice(request.totalPrice())
        .isOfflineOrder(true)
        .offlineMember(member)
        .orderStatus(OrderStatus.WAIT)
        .createdDate(LocalDate.now())
        .build();
  }
}
