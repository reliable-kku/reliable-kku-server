package com.deundeunhaku.reliablekkuserver.order.repository;

import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminSalesResponse;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminOrderRepository extends JpaRepository<Order, Long> ,
    AdminOrderRepositoryCustom {
}
