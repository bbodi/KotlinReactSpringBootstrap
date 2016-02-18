package hu.nevermind.demo.app

import org.h2.server.web.WebServlet
import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.Database
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import javax.sql.DataSource


@Configuration
open class H2DBConfig {

    @Bean open fun h2servletRegistration(): ServletRegistrationBean {
        return ServletRegistrationBean(WebServlet()).apply {
            addUrlMappings("/console/*")
        }
    }

   /* @Bean(destroyMethod = "close")
    open fun dataSource(): DataSource {
        return org.apache.tomcat.jdbc.pool.DataSource().apply {
            driverClassName = "org.h2.Driver"
            url = "jdbc:h2:tcp://localhost/~/test"
            username = "sa"
            password = ""
        }
    }

    @Bean()
    open fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        return LocalContainerEntityManagerFactoryBean().apply {
            dataSource = dataSource()
            setPackagesToScan("hu.nevermind.demo.data")
            jpaVendorAdapter = jpaVendorAdapter()
        }
    }

    open fun jpaVendorAdapter(): JpaVendorAdapter {
        return HibernateJpaVendorAdapter().apply {
            setGenerateDdl(true) // TODO csak teszteléshez!
            setDatabase(Database.H2)
        }
    }*/
}