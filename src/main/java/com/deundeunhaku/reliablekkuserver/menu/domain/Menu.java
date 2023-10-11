package com.deundeunhaku.reliablekkuserver.menu.domain;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.google.firebase.database.annotations.NotNull;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

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


    @ColumnDefault("false")
    private boolean isSale;
//단방향 맞는지?
    @ManyToOne(fetch = FetchType.LAZY)
    private Member OnlineMember;



    @Builder
    public Menu(Long id, String phoneNumber, String name, String description, Integer pricePerOne, Integer pricePerThree, boolean isSale) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.description = description;
        this.pricePerOne = pricePerOne;
        this.pricePerThree = pricePerThree;
        this.isSale = isSale;
    }

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<MenuImage> menuImageList = new ArrayList<>();

}
