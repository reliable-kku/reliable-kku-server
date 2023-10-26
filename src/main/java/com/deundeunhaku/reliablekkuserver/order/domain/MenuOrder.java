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
@IdClass(MenuOrderId.class)
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
  private Order order;

  @ColumnDefault("0")
  private Integer count;


  public MenuOrder(Menu menu, Order order, Integer count) {
    this.menu = menu;
    this.order = order;
    this.count = count;
  }

  public void updateCount(Integer count) {
    this.count = count;
  }

  public static MenuOrder createMenuOrder(Menu menu, Order order, Integer count) {
    return new MenuOrder(menu, order, count);
  }
}
