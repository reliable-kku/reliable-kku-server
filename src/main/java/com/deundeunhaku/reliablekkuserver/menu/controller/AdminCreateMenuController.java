package com.deundeunhaku.reliablekkuserver.menu.controller;

import com.deundeunhaku.reliablekkuserver.menu.dto.CreateMenuRequest;
import com.deundeunhaku.reliablekkuserver.menu.dto.CreateMenuResponse;
import com.deundeunhaku.reliablekkuserver.menu.service.AdminCreateMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminCreateMenuController {

    private final AdminCreateMenuService adminCreateMenuService;
    @PostMapping("/create-menu")
    public ResponseEntity<CreateMenuResponse> createMenu(@RequestPart(value = "file", required = false) MultipartFile multipartFile,
                                                         @RequestPart(value = "menu") CreateMenuRequest request){

        CreateMenuResponse response = adminCreateMenuService.creatAndCreateFile(multipartFile, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
