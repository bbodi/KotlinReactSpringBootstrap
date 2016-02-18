package hu.nevermind.demo

import hu.nevermind.demo.data.Account
import hu.nevermind.demo.data.AccountRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan
open class Main {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(Main::class.java, *args)
        }
    }

    @Bean
    open fun init(accountRepository: AccountRepository): CommandLineRunner {
        return CommandLineRunner {
            accountRepository.save(Account("admin", "admin", "ADMIN"));
        }
    }
}
