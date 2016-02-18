package hu.nevermind.demo.data

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "key_value")
data class KeyValuePair(@Id var key: String = "", var value: String = "") {


}

@Repository
interface KeyValuePairRepository : CrudRepository<KeyValuePair, String> {

}