package com.deundeunhaku.reliablekkuserver.order.domain;

import com.deundeunhaku.reliablekkuserver.menu.domain.Menu;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class MenuOrder {

  @Id
  @NotNull
  @ManyToOne
  @JoinColumn(name = "menu_id")
  private Menu menu;

  @Id
  @NotNull
  @ManyToOne
  @JoinColumn(name = "order_id")
  @JoinColumn(name = "order_orderDateTime")
  private Order order;

  /*  @Id
    @NotNull
    private String menuId;

    @Id
    @NotNull
    private String orderId;*/

  @ColumnDefault("0")
  private Integer count;

}
