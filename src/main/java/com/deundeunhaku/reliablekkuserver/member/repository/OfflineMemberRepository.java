package com.deundeunhaku.reliablekkuserver.member.repository;

import com.deundeunhaku.reliablekkuserver.member.domain.OfflineMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfflineMemberRepository extends JpaRepository<OfflineMember, Long> {

}
