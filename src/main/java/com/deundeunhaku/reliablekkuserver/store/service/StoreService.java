package com.deundeunhaku.reliablekkuserver.store.service;

import com.deundeunhaku.reliablekkuserver.store.constant.STORE_ID;
import com.deundeunhaku.reliablekkuserver.store.domain.Store;
import com.deundeunhaku.reliablekkuserver.store.repository.StoreRepository;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StoreService {

  private final StoreRepository storeRepository;

  @PostConstruct
  public void init() {
    storeRepository.save(new Store(false));
  }

  @Transactional
  public Store setStoreOpenOrClosed(STORE_ID storeId) {
    Store store = storeRepository.findById(storeId.getStoreId())
        .orElseThrow(() -> new IllegalArgumentException("해당하는 가게가 없습니다."));

    store.setIsOpened(!store.getIsOpened());
    return store;
  }

  public Store getStoreOpenOrClosed(STORE_ID storeId) {
    return storeRepository.findById(storeId.getStoreId())
        .orElseThrow(() -> new IllegalArgumentException("해당하는 가게가 없습니다."));
  }
}
