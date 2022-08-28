package bssm.db.bssmgit.global.config.security;

import bssm.db.bssmgit.global.auth.CustomUserDetailService;
import bssm.db.bssmgit.global.jwt.JwtAuthenticationFilter;
import bssm.db.bssmgit.global.jwt.JwtTokenProvider;
import bssm.db.bssmgit.global.jwt.JwtValidateService;
import bssm.db.bssmgit.global.jwt.exception.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService customUserDetailService;
    private final JwtValidateService jwtValidateService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(a -> a
                        .antMatchers("/", "/error", "/webjars/**").permitAll()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .oauth2Login()
                .and()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/users/**").permitAll()
                .antMatchers("/api/users/refresh").permitAll()
                .antMatchers("/api/free/find/**").permitAll()
                .antMatchers("/api/free/comments/find/**").permitAll()
                .antMatchers("/api/project/find/**").permitAll()
                .antMatchers("/api/project/comments/find/**").permitAll()
                .antMatchers("/api/major/find/**").permitAll()
                .antMatchers("/api/major/comments/find/**").permitAll()
                .antMatchers("/api/manager/find/**").permitAll()
                .antMatchers("/api/manager/**")
                .access("hasRole('MANAGER') or hasRole('ADMIN')")
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailService, jwtValidateService),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class);
    }

}
