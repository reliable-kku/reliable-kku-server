package com.deundeunhaku.reliablekkuserver.member.repository;

import static com.deundeunhaku.reliablekkuserver.member.domain.QMember.member;

import com.deundeunhaku.reliablekkuserver.member.constant.Role;
import com.deundeunhaku.reliablekkuserver.member.dto.AdminMemberManagementResponse;
import com.deundeunhaku.reliablekkuserver.member.dto.QAdminMemberManagementResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Slice<AdminMemberManagementResponse> findMemberBySearchKeyword(String searchKeyword,
      Pageable pageable) {
    List<AdminMemberManagementResponse> content = jpaQueryFactory.select(
            new QAdminMemberManagementResponse(
                member.id,
                member.realName,
                member.level,
                member.phoneNumber
            )
        ).from(member)
        .where(member.role.eq(Role.USER),
            member.realName.contains(searchKeyword).or(member.phoneNumber.contains(searchKeyword))
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize() + 1)
        .orderBy(member.id.asc())
        .fetch();

    boolean hasNext = false;
    if (content.size() > pageable.getPageSize()) {
      content.remove(pageable.getPageSize());
      hasNext = true;
    }

    return new SliceImpl<>(content, pageable, hasNext);
  }
}

