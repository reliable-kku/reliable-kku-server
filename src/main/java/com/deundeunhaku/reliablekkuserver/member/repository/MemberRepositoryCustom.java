package com.deundeunhaku.reliablekkuserver.member.repository;

import com.deundeunhaku.reliablekkuserver.member.dto.AdminMemberManagementResponse;
import java.util.List;

public interface MemberRepositoryCustom {

  List<AdminMemberManagementResponse> findMemberBySearchKeyword(String searchKeyword);
}
