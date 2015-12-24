package com.arborsoft.platform.web.config;

import com.arborsoft.platform.web.model.User;
import com.arborsoft.platform.web.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authenticationProvider(new AuthenticationProvider() {
                @Override
                public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                    String username = authentication.getName();
                    String password = authentication.getCredentials().toString();
                    String hashed = new String(DigestUtils.sha256(password), StandardCharsets.UTF_8);

                    User user = userService.getLoggedInUser(username, hashed);

                    if (user == null)
                        user = User.builder()
                            .username("anonymous")
                            .name("Anonymous")
                            .roles(new String[]{"ROLE_GUEST"})
                            .build();

                    List<GrantedAuthority> grantedAuths = new ArrayList<>();
                    if(user.getRoles() != null){
                        for(String role : user.getRoles()){
                            grantedAuths.add(new SimpleGrantedAuthority(role));
                        }
                    }

                    return new UsernamePasswordAuthenticationToken(user, password, grantedAuths);
                }

                @Override
                public boolean supports(Class<?> aClass) {
                    return aClass.equals(UsernamePasswordAuthenticationToken.class);
                }
            })
            .authorizeRequests()
            .antMatchers("/**").authenticated()
            .antMatchers(
                "**/assets/css/**",
                "**/assets/js/**",
                "**/assets/img/**",
                "/login",
                "/logout"
            ).permitAll()
            //.anyRequest().permitAll()
            .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/plogin")
                .successHandler((request, response, authentication) -> response.sendRedirect("/"))
                .failureHandler((request, response, e) -> response.sendRedirect("/login?error=Login%20Failed"))
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/logout")
                .permitAll();
    }
}
