package com.deundeunhaku.reliablekkuserver.member.domain;

import com.deundeunhaku.reliablekkuserver.member.constant.Role;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberMyPageResponse;
import com.google.firebase.database.annotations.NotNull;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member implements UserDetails {

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

    @Enumerated(EnumType.STRING)
    private Role role;

    @ColumnDefault("false")
    private boolean isWithdraw;

    public void changePassword(String password) {
        this.password = password;
    }

    @Builder
    public Member(Long id, String phoneNumber, String password, String realName, Integer level, String firebaseToken, Role role, boolean isWithdraw) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.realName = realName;
        this.level = level;
        this.firebaseToken = firebaseToken;
        this.role = role;
        this.isWithdraw = isWithdraw;
    }

    public void withdraw() {
        if (!this.isWithdraw) {
            this.isWithdraw = true;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public MemberMyPageResponse toMemberMyPageResponse() {
        return MemberMyPageResponse.of(this.getRealName(), this.getLevel());
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isAdmin() {
        return role.name().equals(Role.ADMIN.name());
    }

}
