package hu.nevermind.demo.app

import hu.nevermind.demo.data.AccountRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
open class AuthenticationConfig : GlobalAuthenticationConfigurerAdapter() {

    @Autowired
    private lateinit var accountRepository: AccountRepository;

    override public fun init(auth: AuthenticationManagerBuilder) {
        auth
                .userDetailsService(userDetailsService())
                .passwordEncoder(BCryptPasswordEncoder())
    }

    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            val account = accountRepository.findByUsername(username);
            if (account != null) {
                User(
                        account.username,
                        account.passwordHash,
                        true, // enabled
                        true, // accountNonExpired
                        true, // credentialsNonExpired
                        true, // accountNonLocked
                        AuthorityUtils.commaSeparatedStringToAuthorityList(account.roles) // authorities
                )
            } else {
                throw UsernameNotFoundException("could not find the user '$username'");
            }
        }
    }


}