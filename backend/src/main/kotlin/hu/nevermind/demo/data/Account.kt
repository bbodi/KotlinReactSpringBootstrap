package hu.nevermind.demo.data

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.Entity
import javax.persistence.Id


@Entity
class Account(@Id var username: String = "",
              var passwordHash: String = "",
              var disabled: Boolean = false,
              var role: String = "") : AbstractEntity()

@Repository
interface AccountRepository : CrudRepository<Account, String> {
    fun findByUsername(username: String): Account?
}