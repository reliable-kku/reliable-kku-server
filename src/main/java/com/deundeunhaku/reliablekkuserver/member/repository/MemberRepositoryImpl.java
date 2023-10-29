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

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Page<AdminMemberManagementResponse> findMemberBySearchKeyword(String searchKeyword,
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
        .limit(pageable.getPageSize())
        .orderBy(member.id.asc())
        .fetch();

    Long count = jpaQueryFactory.select(
            member.count()
        ).from(member)
        .where(member.role.eq(Role.USER),
            member.realName.contains(searchKeyword).or(member.phoneNumber.contains(searchKeyword))
        )
        .fetchOne();

    return new PageImpl<>(content, pageable, count);
  }

}
