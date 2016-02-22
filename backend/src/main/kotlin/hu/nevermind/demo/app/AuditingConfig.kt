package hu.nevermind.demo.app

import hu.nevermind.demo.Profiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User


@Configuration
@EnableJpaAuditing
open class AuditingConfig {

    @Autowired
    private lateinit var environment: Environment

    @Bean open fun auditorProvider(): AuditorAware<String> {
        return AuditorAware<kotlin.String> {
            val authentication = SecurityContextHolder.getContext().authentication;

            if ((authentication == null || !authentication.isAuthenticated)) {
                if (environment.activeProfiles.contains(Profiles.dev)) {
                    "anonymous"
                } else {
                    null
                }
            } else {
                (authentication.principal as User).username
            }
        }
    }
}