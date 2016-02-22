package hu.nevermind.demo.controller

import hu.nevermind.demo.data.Account
import hu.nevermind.demo.data.AccountRepository
import hu.nevermind.demo.data.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*

@Secured(Role.Admin)
@RestController
open class AccountScreenController
@Autowired
constructor(private val accountRepository: AccountRepository) {

    @RequestMapping("/getAllAccounts")
    open fun getAccounts(): Iterable<Account> {
        return accountRepository.findAll()
    }

    @RequestMapping("/getAccount/{id}")
    open fun getAccount(@PathVariable id: String): Account? {
        return accountRepository.findOne(id)
    }

    @RequestMapping("/saveAccount", method = arrayOf(RequestMethod.POST))
    open fun putAccount(@RequestBody entity: Account): Account? {
        val authentication = SecurityContextHolder.getContext().authentication;

        val modifiedEntity = accountRepository.findByUsername(entity.username)
        val plainPassword = entity.passwordHash
        return if (modifiedEntity != null) {
            modifiedEntity.disabled = entity.disabled
            modifiedEntity.role = entity.role
            if (plainPassword.isNotEmpty()) {
                modifiedEntity.passwordHash = BCryptPasswordEncoder().encode(plainPassword)
            }
            if ((authentication.principal as User).username != entity.username) {
                modifiedEntity.username = entity.username
            }
            accountRepository.save(modifiedEntity)
        } else {
            val newEntity = Account()
            newEntity.username = entity.username
            newEntity.disabled = false
            newEntity.role = entity.role
            newEntity.passwordHash = BCryptPasswordEncoder().encode(plainPassword)
            accountRepository.save(newEntity)
        }
    }
}