package com.deundeunhaku.reliablekkuserver.order.repository;

import com.deundeunhaku.reliablekkuserver.order.domain.MenuOrder;
import com.deundeunhaku.reliablekkuserver.order.domain.MenuOrderId;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuOrderRepository extends JpaRepository<MenuOrder, MenuOrderId>, MenuOrderRepositoryCustom {

  List<MenuOrder> findByOrder(Order order);

}
