package com.deundeunhaku.reliablekkuserver.member.repository;

import static com.deundeunhaku.reliablekkuserver.member.domain.QMember.member;

import com.deundeunhaku.reliablekkuserver.member.constant.Role;
import com.deundeunhaku.reliablekkuserver.member.dto.AdminMemberManagementResponse;
import com.deundeunhaku.reliablekkuserver.member.dto.QAdminMemberManagementResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<AdminMemberManagementResponse> findMemberBySearchKeyword(String searchKeyword) {
    return jpaQueryFactory.select(
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
        .orderBy(member.id.asc())
        .fetch();
  }

}
