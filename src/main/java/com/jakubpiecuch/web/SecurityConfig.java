package com.jakubpiecuch.web;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoDefaultConfiguration;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableOAuth2Sso
@EnableZuulProxy
public class SecurityConfig extends OAuth2SsoDefaultConfiguration {

    public SecurityConfig(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.csrf().disable().antMatcher("/**").authorizeRequests()
                .antMatchers("/login**").permitAll()
                .anyRequest().authenticated();
    }
}
