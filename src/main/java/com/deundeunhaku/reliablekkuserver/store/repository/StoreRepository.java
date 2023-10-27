package com.deundeunhaku.reliablekkuserver.store.repository;

import com.deundeunhaku.reliablekkuserver.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

}
