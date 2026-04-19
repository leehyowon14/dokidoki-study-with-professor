package com.animalleague.april.integration.support;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostgresIntegrationSmokeIntegrationTest extends PostgresIntegrationTest {

    @Test
    void postgresIntegrationBaseProvidesDataSource() {
        assertThat(dataSource).isNotNull();
    }
}

