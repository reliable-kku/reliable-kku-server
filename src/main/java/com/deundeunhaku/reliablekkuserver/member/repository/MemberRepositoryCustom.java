package com.deundeunhaku.reliablekkuserver.member.repository;

import com.deundeunhaku.reliablekkuserver.member.dto.AdminMemberManagementResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MemberRepositoryCustom {

  Slice<AdminMemberManagementResponse> findMemberBySearchKeyword(String searchKeyword,
      Pageable pageable);
}
