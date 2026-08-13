package com.mingji.config;

import com.mingji.controller.AccessController;
import com.mingji.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AccessController.class,
        properties = {
                "mingji.security.password-hash=$2a$10$xFkcOE.X/cZnNthKBbvJEu.T7JVE31t1zG2t5ydTYDAGWV92zPxK2",
                "mingji.security.remember-key=0123456789abcdef0123456789abcdef",
                "mingji.security.secure-cookie=false"
        })
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    void accessPageIsPublic() throws Exception {
        mockMvc.perform(get("/access"))
                .andExpect(status().isOk());
    }

    @Test
    void pageRequestRedirectsToAccessPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/access"));
    }

    @Test
    void apiRequestReturnsUnauthorizedInsteadOfHtmlRedirect() throws Exception {
        mockMvc.perform(get("/api/content"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticatedMutatingApiRequestAlsoReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/content/publish"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void correctPasswordCreatesOneYearRememberCookie() throws Exception {
        Cookie rememberCookie = mockMvc.perform(post("/access")
                        .with(csrf())
                        .param("username", SecurityConfig.SITE_USERNAME)
                        .param("password", "correct-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().exists(SecurityConfig.REMEMBER_COOKIE_NAME))
                .andReturn().getResponse().getCookie(SecurityConfig.REMEMBER_COOKIE_NAME);

        assertThat(rememberCookie).isNotNull();
        assertThat(rememberCookie.getMaxAge()).isEqualTo(SecurityConfig.ONE_YEAR_SECONDS);
        assertThat(rememberCookie.isHttpOnly()).isTrue();
    }

    @Test
    void wrongPasswordDoesNotCreateRememberCookie() throws Exception {
        Cookie clearedCookie = mockMvc.perform(post("/access")
                        .with(csrf())
                        .param("username", SecurityConfig.SITE_USERNAME)
                        .param("password", "wrong-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access?error"))
                .andReturn().getResponse().getCookie(SecurityConfig.REMEMBER_COOKIE_NAME);

        assertThat(clearedCookie).isNotNull();
        assertThat(clearedCookie.getMaxAge()).isZero();
    }

    @Test
    @WithMockUser
    void mutatingRequestRequiresCsrfToken() throws Exception {
        mockMvc.perform(post("/api/content/publish"))
                .andExpect(status().isForbidden());
    }

    @Test
    void plaintextEncoderPrefixIsRejected() {
        SecurityConfig config = new SecurityConfig();

        assertThatThrownBy(() -> config.siteUserDetailsService("{noop}example-plaintext-password"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("BCrypt");
    }

    @Test
    void bcryptPrefixIsAcceptedWithoutBeingDuplicated() {
        SecurityConfig config = new SecurityConfig();
        UserDetailsService users = config.siteUserDetailsService(
                "{bcrypt}$2a$10$xFkcOE.X/cZnNthKBbvJEu.T7JVE31t1zG2t5ydTYDAGWV92zPxK2");

        assertThat(users.loadUserByUsername(SecurityConfig.SITE_USERNAME).getPassword())
                .isEqualTo("{bcrypt}$2a$10$xFkcOE.X/cZnNthKBbvJEu.T7JVE31t1zG2t5ydTYDAGWV92zPxK2");
    }
}
