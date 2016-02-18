package hu.nevermind.reakt.bootstrap.table

import com.github.andrewoma.react.*
import hu.nevermind.reakt.bootstrap.externalReactClass


object SelectionMode {
    val radio: SelectionMode = "radio".asDynamic()
    val checkbox: SelectionMode = "checkbox".asDynamic()
}

data class SelectRowProp<T>(val mode: SelectionMode,
                            val clickToSelect: Boolean = false,
                            val clickToSelectAndEditCell: Boolean = false,
                            val bgColor: String = "rgb(038, 93, 13)",
                            val onSelect: ((row: T, isSelected: Boolean) -> Unit)? = null,
                            val onSelectAll: ((isSelected: Boolean) -> Unit)? = null,
                            val selected: Array<Any> = emptyArray(),
                            val hideSelectColumn: Boolean = false,
                            val showOnlySelected: Boolean = false
)

object CellEditMode {
    val click: CellEditMode = "click".asDynamic()
    val dbClick: CellEditMode = "dbclick".asDynamic()
}

data class CellEditProp<T>(val mode: CellEditMode,
                           val blurToSave: Boolean = false,
                           val afterSaveCell: ((row: T, cellName: String, cellValue: Any) -> Unit)? = null)

class TableProperties<T> {
    var data: Array<T> by Property()
    var striped: Boolean by Property()
    var hover: Boolean by Property()
    var condensed: Boolean by Property()
    var height: Int by Property()
    var pagination: Boolean by Property()
    var columnFilter: Boolean by Property()
    var search: Boolean by Property()
    var searchPlaceholder: String by Property()
    var selectRow: SelectRowProp<T> by Property()
    var cellEdit: CellEditProp<T> by Property()
}

object DataAlign {
    @native val Right: DataAlign = js("'right'")
    @native val Left: DataAlign = js("'left'")
    @native val Center: DataAlign = js("'center'")
}


class TableHeaderColumnProperties<T, C> {
    var dataField: String by Property()
    var isKey: Boolean by Property()
    var width: String by Property()
    var dataAlign: DataAlign by Property()
    var dataSort: Boolean by Property()
    var hidden: Boolean by Property()
    var editable: Boolean by Property()
    var dataFormat: (cell: C, row: T) -> Any by Property()
}


fun <T, C> Component.tableHeaderColumn(
        properties: TableHeaderColumnProperties<T, C>.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass(
            js("ReactBootstrapTable.TableHeaderColumn"),
            TableHeaderColumnProperties<T, C>(), properties,
            init)
}

fun <T> Component.bootstrapTable(
        properties: TableProperties<T>.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass(
            js("ReactBootstrapTable.BootstrapTable"),
            TableProperties(), properties,
            init)
}