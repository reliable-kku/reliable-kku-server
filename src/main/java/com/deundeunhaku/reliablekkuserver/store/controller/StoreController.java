package com.deundeunhaku.reliablekkuserver.store.controller;

import com.deundeunhaku.reliablekkuserver.store.constant.STORE_ID;
import com.deundeunhaku.reliablekkuserver.store.domain.Store;
import com.deundeunhaku.reliablekkuserver.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {

  private final StoreService storeService;

  @GetMapping("/open-closed")
  public ResponseEntity<Store> isOpenedOrClosed() {
    Store store = storeService.getStoreOpenOrClosed(STORE_ID.DEUNDEUN_HAKU);
    return ResponseEntity.ok(store);
  }


}
