package com.deundeunhaku.reliablekkuserver.menu.domain;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.google.firebase.database.annotations.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String phoneNumber;

    @NotNull
    @Column(unique = true)
    private String name;

    private String description;

    @ColumnDefault("0")
    private Integer pricePerOne;

    @ColumnDefault("0")
    private Integer pricePerThree;

    @Setter
    @ColumnDefault("false")
    private boolean isSale;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member onlineMember;


    private String menuImageUrl;

    @Builder
    public Menu(Long id, String phoneNumber, String name, String description, Integer pricePerOne,
        Integer pricePerThree, boolean isSale, Member onlineMember, String menuImageUrl) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.description = description;
        this.pricePerOne = pricePerOne;
        this.pricePerThree = pricePerThree;
        this.isSale = isSale;
        this.onlineMember = onlineMember;
        this.menuImageUrl = menuImageUrl;
    }
}
