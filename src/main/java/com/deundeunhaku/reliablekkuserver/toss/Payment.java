//package com.deundeunhaku.reliablekkuserver.toss;
//
//import com.google.firebase.database.annotations.NotNull;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.ColumnDefault;
//import org.hibernate.annotations.DynamicInsert;
//
//@DynamicInsert
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Getter
//@Entity
//public class Payment {
//
//  @Id
//  private String orderId;
//
//  @NotNull
//  @ColumnDefault("0")
//  private int amount;
//
//  private PaymentType paymentType;
//
//  @ColumnDefault("false")
//  private Boolean isPaid;
//
//  public void setPaid(){
//    this.isPaid = true;
//  }
//
//  @Builder
//  public Payment(String orderId, int amount, PaymentType paymentType, Boolean isPaid) {
//    this.orderId = orderId;
//    this.amount = amount;
//    this.paymentType = paymentType;
//    this.isPaid = isPaid;
//  }
//}
