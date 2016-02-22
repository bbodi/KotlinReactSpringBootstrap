package hu.nevermind.demo.data

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class AbstractEntity {
    @Column(name = "created_by", nullable = false)
    @CreatedBy
    var createdByUser: String = ""

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    var created: Date = Date()

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    var updated: Date = Date()

    @Column(name = "modified_by", nullable = false)
    @LastModifiedBy
    var modifiedByUser: String = ""
}