package hu.nevermind.demo.app

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@EnableWebSecurity
@Configuration
open class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                    .antMatchers("/", "/js/**", "/css/**", "/**/favicon.ico").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                        .loginPage("/#login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/#home")
                        .permitAll()
                .and()
                    .logout()
                        .logoutSuccessUrl("/")
                        .logoutUrl("/logout") // POST only
                .and().csrf().disable()
    }

}