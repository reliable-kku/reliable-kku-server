package com.deundeunhaku.reliablekkuserver.member.repository;

import com.deundeunhaku.reliablekkuserver.member.domain.CertificationNumber;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface CertificationNumberRepository extends CrudRepository<CertificationNumber, Long> {

  List<CertificationNumber> findAllByPhoneNumberAndCreatedAt(String phoneNumber, LocalDateTime createdAt);

  Optional<CertificationNumber> findFirstByPhoneNumberOrderByCreatedAtDesc(
      String phoneNumber);
}
