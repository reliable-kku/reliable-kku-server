package com.deundeunhaku.reliablekkuserver.member.dto;

import com.querydsl.core.annotations.QueryProjection;

public record AdminMemberManagementResponse(
    Long id,
    String username,
    Integer level,
    String phoneNumber

) {

  public static AdminMemberManagementResponse of(Long id, String username, Integer level,
      String phoneNumber) {
    return new AdminMemberManagementResponse(id, username, level, phoneNumber);
  }

  @QueryProjection
  public AdminMemberManagementResponse(Long id, String username, Integer level,
      String phoneNumber) {
    this.id = id;
    this.username = username;
    this.level = level;
    this.phoneNumber = phoneNumber;
  }
}
