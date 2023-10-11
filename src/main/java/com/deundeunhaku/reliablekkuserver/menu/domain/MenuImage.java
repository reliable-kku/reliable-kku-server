package com.deundeunhaku.reliablekkuserver.menu.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class MenuImage {

        @Id
        @GeneratedValue
        private Long id;

        private String imageUrl;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "menu_id")
        private Menu menu;

        @Builder
        public MenuImage(String imageUrl, Menu menu) {
            this.imageUrl = imageUrl;
            this.menu = menu;
        }
}
