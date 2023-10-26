package com.deundeunhaku.reliablekkuserver.order.repository;

import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderEachMenuResponse;
import java.util.List;

public interface MenuOrderRepositoryCustom {

  List<OrderEachMenuResponse> findByOrderToOrderEachMenuResponse(Order order);
}
