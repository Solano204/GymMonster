package com.monster.gateway.Entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class TokenTest {

    @Test
    void allArgsConstructor_setsEveryField() {
        Instant expiresAt = Instant.now().plusSeconds(3600);
        Instant refreshExpiresAt = Instant.now().plusSeconds(7200);

        Token token = new Token("carlos", "access-tok", "refresh-tok", expiresAt, refreshExpiresAt);

        assertThat(token.getUsername()).isEqualTo("carlos");
        assertThat(token.getAccessToken()).isEqualTo("access-tok");
        assertThat(token.getRefreshToken()).isEqualTo("refresh-tok");
        assertThat(token.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(token.getRefreshExpiresAt()).isEqualTo(refreshExpiresAt);
    }

    @Test
    void settersMutateEveryField() {
        Token token = new Token("a", "b", "c", Instant.now(), Instant.now());

        Instant newExpiry = Instant.now().plusSeconds(60);
        token.setUsername("carlos2");
        token.setAccessToken("new-access");
        token.setRefreshToken("new-refresh");
        token.setExpiresAt(newExpiry);

        assertThat(token.getUsername()).isEqualTo("carlos2");
        assertThat(token.getAccessToken()).isEqualTo("new-access");
        assertThat(token.getRefreshToken()).isEqualTo("new-refresh");
        assertThat(token.getExpiresAt()).isEqualTo(newExpiry);
    }

    @Test
    void equalsAndHashCode_matchForIdenticalFieldValues() {
        Instant expiresAt = Instant.parse("2026-01-01T00:00:00Z");
        Instant refreshExpiresAt = Instant.parse("2026-01-02T00:00:00Z");

        Token a = new Token("carlos", "acc", "ref", expiresAt, refreshExpiresAt);
        Token b = new Token("carlos", "acc", "ref", expiresAt, refreshExpiresAt);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equals_isFalse_whenAccessTokenDiffers() {
        Instant now = Instant.now();
        Token a = new Token("carlos", "acc-1", "ref", now, now);
        Token b = new Token("carlos", "acc-2", "ref", now, now);

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void toString_includesFieldNamesForDebuggability() {
        Token token = new Token("carlos", "acc", "ref", Instant.now(), Instant.now());

        assertThat(token.toString()).contains("username=carlos");
    }
}
