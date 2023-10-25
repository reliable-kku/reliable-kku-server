package com.deundeunhaku.reliablekkuserver.member.domain;

import com.google.firebase.database.annotations.NotNull;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String phoneNumber;

    @NotNull
    private String password;

    @NotNull
    private String realName;

    @ColumnDefault("1")
    private Integer level;

    private String firebaseToken;


    @Builder
    public Member(Long id, String phoneNumber, String password, String realName, Integer level,
        String firebaseToken) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.realName = realName;
        this.level = level;
        this.firebaseToken = firebaseToken;
    }
}
