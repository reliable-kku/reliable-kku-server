package com.deundeunhaku.reliablekkuserver.member.repository;

import com.deundeunhaku.reliablekkuserver.member.domain.CertificationNumber;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface CertificationNumberRepository extends CrudRepository<CertificationNumber, Long> {

  List<CertificationNumber> findAllByCreatedAt(LocalDate createdAt);

  Optional<CertificationNumber> findTop1ByPhoneNumberOrderByCreatedAtDesc(
      String phoneNumber);
}
