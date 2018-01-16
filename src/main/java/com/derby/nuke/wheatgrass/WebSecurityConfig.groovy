package com.derby.nuke.wheatgrass;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Value('${security.user}')
	def String user;
	@Value('${security.password}')
	def String password;
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
        http
            .authorizeRequests()
                .antMatchers("/api/**").authenticated()
//                .antMatchers("/wechat.ci").authenticated()
                .antMatchers("/**").permitAll()
				.and()
			.httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser(user).password(password).roles("ADMIN");
    }
}
