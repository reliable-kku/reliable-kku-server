package com.deundeunhaku.reliablekkuserver;

import com.deundeunhaku.reliablekkuserver.config.TestConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TestConfig.class)
public class BaseRepositoryTest extends BaseDisplayNameConfig {
}
