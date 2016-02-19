package hu.nevermind.demo.controller

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AppController() {

    @RequestMapping("/user")
    fun user(): Any {
        val context = SecurityContextHolder.getContext();
        val authentication = context.authentication;
        return object {
            val name = (authentication.principal as User).username
            val roles = authentication.authorities.map { it.authority }.toTypedArray()
        }
    }

}