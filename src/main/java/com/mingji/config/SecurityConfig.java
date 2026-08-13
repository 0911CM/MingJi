package com.mingji.config;

import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 整站密码门禁。
 *
 * <p>站点只有一个固定的内部账号，访问者只需要在密码页输入密码。
 * 密码哈希和 Remember-Me 签名密钥只能由服务器环境变量提供。</p>
 */
@Configuration
public class SecurityConfig {

    public static final String SITE_USERNAME = "owner";
    public static final String REMEMBER_COOKIE_NAME = "MINGJI_REMEMBER";
    public static final int ONE_YEAR_SECONDS = 365 * 24 * 60 * 60;
    private static final Pattern BCRYPT_HASH = Pattern.compile(
            "^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

    @Bean
    public UserDetailsService siteUserDetailsService(
            @Value("${mingji.security.password-hash:}") String passwordHash) {
        if (!StringUtils.hasText(passwordHash)) {
            throw new IllegalStateException(
                    "必须配置 MINGJI_ACCESS_PASSWORD_HASH，且不能在源码中保存站点密码");
        }

        String rawBcryptHash = passwordHash.startsWith("{bcrypt}")
                ? passwordHash.substring("{bcrypt}".length())
                : passwordHash;
        if (!BCRYPT_HASH.matcher(rawBcryptHash).matches()) {
            throw new IllegalStateException(
                    "MINGJI_ACCESS_PASSWORD_HASH 必须是 BCrypt 哈希，不能填写明文密码");
        }

        return new InMemoryUserDetailsManager(User.withUsername(SITE_USERNAME)
                .password("{bcrypt}" + rawBcryptHash)
                .roles("OWNER")
                .build());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UserDetailsService userDetailsService,
            @Value("${mingji.security.remember-key:}") String rememberKey,
            @Value("${mingji.security.secure-cookie:false}") boolean secureCookie) throws Exception {
        if (!StringUtils.hasText(rememberKey) || rememberKey.length() < 32) {
            throw new IllegalStateException(
                    "必须配置至少 32 个字符的 MINGJI_REMEMBER_KEY");
        }

        CookieCsrfTokenRepository csrfRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfRepository.setCookiePath("/");
        csrfRepository.setSecure(secureCookie);

        AuthenticationEntryPoint apiUnauthorized = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
        AntPathRequestMatcher apiRequest = new AntPathRequestMatcher("/api/**");
        AuthenticationEntryPoint pageLogin = new LoginUrlAuthenticationEntryPoint("/access");

        http
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/access", "/css/access.css", "/error").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                apiUnauthorized, apiRequest)
                        .defaultAuthenticationEntryPointFor(
                                pageLogin, new NegatedRequestMatcher(apiRequest))
                        .accessDeniedHandler((request, response, exception) -> {
                            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                            boolean anonymous = authentication == null
                                    || authentication instanceof AnonymousAuthenticationToken;
                            if (anonymous && request.getRequestURI().startsWith("/api/")) {
                                response.sendError(HttpStatus.UNAUTHORIZED.value());
                            } else {
                                response.sendError(HttpStatus.FORBIDDEN.value());
                            }
                        }))
                .formLogin(form -> form
                        .loginPage("/access")
                        .loginProcessingUrl("/access")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/access?error")
                        .permitAll())
                .rememberMe(remember -> remember
                        .alwaysRemember(true)
                        .key(rememberKey)
                        .rememberMeCookieName(REMEMBER_COOKIE_NAME)
                        .tokenValiditySeconds(ONE_YEAR_SECONDS)
                        .useSecureCookie(secureCookie)
                        .userDetailsService(userDetailsService))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/access?logout")
                        .deleteCookies("JSESSIONID", REMEMBER_COOKIE_NAME))
                .csrf(csrf -> csrf.csrfTokenRepository(csrfRepository));

        return http.build();
    }

    @Bean
    public CookieSameSiteSupplier securityCookieSameSiteSupplier() {
        return CookieSameSiteSupplier.ofStrict()
                .whenHasNameMatching("^(JSESSIONID|XSRF-TOKEN|" + REMEMBER_COOKIE_NAME + ")$");
    }
}
