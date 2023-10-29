package com.deundeunhaku.reliablekkuserver.member.controller;

import com.deundeunhaku.reliablekkuserver.member.dto.AdminMemberManagementResponse;
import com.deundeunhaku.reliablekkuserver.member.service.AdminMemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/member")
public class AdminMemberController {

  private final AdminMemberService adminMemberService;

  @GetMapping
  public ResponseEntity<List<AdminMemberManagementResponse>> getMemberList(
      @RequestParam(defaultValue = "") String searchKeyword) {
    return ResponseEntity.ok(adminMemberService.getMemberList(searchKeyword));
  }

}
