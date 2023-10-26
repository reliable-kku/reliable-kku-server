package com.deundeunhaku.reliablekkuserver.member.domain;

import com.google.firebase.database.annotations.NotNull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class OfflineMember {

    @Id @GeneratedValue
    private Long id;

    @NotNull
    private String phoneNumber;

    @Builder
    public OfflineMember(Long id, String phoneNumber) {
        this.id = id;
        this.phoneNumber = phoneNumber;
    }
}
