package com.deundeunhaku.reliablekkuserver.member.repository;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
