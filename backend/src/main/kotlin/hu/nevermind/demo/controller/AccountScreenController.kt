package hu.nevermind.demo.controller

import hu.nevermind.demo.data.Account
import hu.nevermind.demo.data.AccountRepository
import hu.nevermind.demo.data.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
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

    @RequestMapping("/saveAccount", method=arrayOf(RequestMethod.POST))
    open fun putAccount(@RequestBody entity: Account): Account {
        accountRepository.save(entity)
        return entity
    }
}