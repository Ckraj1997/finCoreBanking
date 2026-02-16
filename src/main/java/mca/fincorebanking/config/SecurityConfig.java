package mca.fincorebanking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import mca.fincorebanking.security.CustomAuthenticationFailureHandler;
import mca.fincorebanking.security.CustomAuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

        private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
        private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

        public SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler,
                        CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
                this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
                this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                // 🌍 Public Access
                                                .requestMatchers("/login", "/register", "/css/**", "/js/**",
                                                                "/images/**", "/error")
                                                .permitAll()

                                                // 🏠 Unified Dashboard (Router) - Accessible by ALL logged-in users
                                                .requestMatchers("/dashboard").authenticated()

                                                // 👤 Customer Access
                                                .requestMatchers("/accounts/**", "/transactions/**", "/investments/**",
                                                                "/kyc/**")
                                                .hasAnyRole("CUSTOMER", "CORPORATE")

                                                // 🏦 Branch Operations (Teller)
                                                .requestMatchers("/teller/**").hasRole("TELLER")

                                                // 👔 Manager (Business Ops - Final Approval Authority)
                                                .requestMatchers("/manager/**").hasRole("MANAGER")

                                                // 🛡️ Compliance (Enforcement - Write Access)
                                                .requestMatchers("/compliance/actions/**").hasRole("COMPLIANCE")

                                                // 📜 Audit (Read Only - Auditors & Compliance)
                                                .requestMatchers("/compliance/audit/**", "/compliance/reports/**",
                                                                "/reports/**")
                                                .hasAnyRole("COMPLIANCE", "AUDITOR")

                                                // 🔧 System Admin (Config & User Mgmt ONLY - No Business Ops)
                                                .requestMatchers("/admin/**").hasRole("ADMIN")

                                                // ⚡ Super Admin
                                                .requestMatchers("/super-admin/**").hasRole("SUPER_ADMIN").anyRequest()
                                                .authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .successHandler(customAuthenticationSuccessHandler)
                                                .failureHandler(customAuthenticationFailureHandler)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                return http.build();
        }
}