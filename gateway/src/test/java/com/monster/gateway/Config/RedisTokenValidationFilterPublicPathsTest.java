package com.monster.gateway.Config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

/**
 * Pins which /api/page/** requests the gateway lets through without a token.
 * Before this fix, EVERY /api/page/** request needed Authorization + username
 * headers, including POST /api/page/registerClient - meaning a brand-new
 * client (who by definition doesn't have an account/token yet) could never
 * register through the gateway, and anonymous visitors couldn't browse
 * memberships/pools/promotions/specialties/workclasses either. Mirrors
 * web-page's own SecurityConfig public-path list and gateway's SecurityConfig.
 */
class RedisTokenValidationFilterPublicPathsTest {

    private final RedisTokenValidationFilter filter =
            new RedisTokenValidationFilter(null, null, null, null, null, null);

    private static ServerWebExchange exchangeFor(HttpMethod method, String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.method(method, path).build());
    }

    @Test
    void registerClient_isPublic() {
        assertThat(filter.isPublicPageRequest(exchangeFor(HttpMethod.POST, "/api/page/registerClient"))).isTrue();
    }

    @Test
    void publicBrowsingGetEndpoints_arePublic() {
        assertThat(filter.isPublicPageRequest(exchangeFor(HttpMethod.GET, "/api/page/allMemberships"))).isTrue();
        assertThat(filter.isPublicPageRequest(exchangeFor(HttpMethod.GET, "/api/page/allPools"))).isTrue();
        assertThat(filter.isPublicPageRequest(exchangeFor(HttpMethod.GET, "/api/page/allSpecialties"))).isTrue();
        assertThat(filter.isPublicPageRequest(exchangeFor(HttpMethod.GET, "/api/page/promotions/currentPromotions/2026-01-01"))).isTrue();
        assertThat(filter.isPublicPageRequest(exchangeFor(HttpMethod.GET, "/api/page/workclasses/yoga/schedules"))).isTrue();
    }

    @Test
    void clientAccountEndpoints_stillRequireAToken() {
        assertThat(filter.isPublicPageRequest(exchangeFor(HttpMethod.PUT, "/api/page/clients/jdoe/changePassword"))).isFalse();
        assertThat(filter.isPublicPageRequest(exchangeFor(HttpMethod.GET, "/api/page/clients/jdoe/allInformation"))).isFalse();
        assertThat(filter.isPublicPageRequest(exchangeFor(HttpMethod.DELETE, "/api/page/clients/jdoe/deleteAccount"))).isFalse();
    }

    @Test
    void adminRoutes_areNeverPublic_evenIfPathHappensToMatch() {
        assertThat(filter.isPublicPageRequest(exchangeFor(HttpMethod.GET, "/api/admin/clients"))).isFalse();
    }
}
