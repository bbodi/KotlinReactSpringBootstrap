package hu.nevermind.demo.controller

import hu.nevermind.demo.data.KeyValuePair
import hu.nevermind.demo.data.KeyValuePairRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class KeyValuePairController {

    @Autowired
    private lateinit var keyValuePairRepository: KeyValuePairRepository


    @RequestMapping("/getKeyValues")
    fun getKeyValues(): Iterable<KeyValuePair> {
        return keyValuePairRepository.findAll()
    }

    @RequestMapping("/getKeyValue/{key}")
    fun getKeyValue(@PathVariable key: String): KeyValuePair? {
        return keyValuePairRepository.findOne(key)
    }

    @RequestMapping("/putKeyValue", method=arrayOf(RequestMethod.POST))
    fun putKeyValue(@RequestBody entity: KeyValuePair): KeyValuePair {
        keyValuePairRepository.save(entity)
        return entity
    }
}