package hu.nevermind.app

import com.github.andrewoma.flux.ActionDef
import hu.nevermind.app.keyvalue.KeyValue

object Actions {
    val setKeyValues = ActionDef<Array<KeyValue>>()
    val modifyKeyValue = ActionDef<KeyValue>()
    val setEditingKeyValue = ActionDef<KeyValue?>()
}
