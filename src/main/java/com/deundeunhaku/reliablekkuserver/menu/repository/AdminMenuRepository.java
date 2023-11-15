package com.deundeunhaku.reliablekkuserver.menu.repository;

import com.deundeunhaku.reliablekkuserver.menu.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminMenuRepository extends JpaRepository<Menu, Long> {


}
