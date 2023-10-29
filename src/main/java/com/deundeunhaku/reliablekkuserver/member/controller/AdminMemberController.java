package com.deundeunhaku.reliablekkuserver.member.controller;

import com.deundeunhaku.reliablekkuserver.member.dto.AdminMemberManagementResponse;
import com.deundeunhaku.reliablekkuserver.member.service.AdminMemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
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
  public ResponseEntity<Slice<AdminMemberManagementResponse>> getMemberList(
      @RequestParam(defaultValue = "") String searchKeyword,
      @PageableDefault(sort = "id", direction = Direction.ASC) Pageable pageable) {
    return ResponseEntity.ok(adminMemberService.getMemberList(searchKeyword, pageable));
  }

}
