package hu.nevermind.app.screen

import com.github.andrewoma.react.*
import hu.nevermind.app.*
import hu.nevermind.app.store.*
import hu.nevermind.common.*
import hu.nevermind.reakt.bootstrap.*
import hu.nevermind.reakt.bootstrap.table.*
import hu.nevermind.reakt.jqext.find
import hu.nevermind.reakt.jqext.size
import jquery.jq
import org.junit.Test
import kotlin.browser.window

private @native val accounting: dynamic = noImpl

private object KeyValueScreenIds {
    val screenId = "keyValueScreen"
    val addButton = "${screenId}_addButton"
    object table {
        val id = "${screenId}_table"
        object row {
            val editButton: (Int)->String = {rowIndex -> "${id}_editButton_$rowIndex"}
            val deleteButton: (Int)->String = {rowIndex -> "${id}_deleteButton_$rowIndex"}
        }
    }
    object modal {
        val id = "${screenId}_modal"
        object inputs {
            val key = "${id}_key"
            val value = "${id}_value"
        }
        object buttons {
            val save = "${id}_saveButton"
            val close = "${id}_closeButton"
        }
    }
}

class KeyValueScreen : ComponentSpec<Unit, Unit>() {

    companion object {
        val factory = react.createFactory(KeyValueScreen())
    }

    override fun componentDidMount() {
        KeyValueStore.addChangeListener(this) {
            forceUpdate()
        }
    }

    override fun componentWillUnmount() {
        KeyValueStore.removeListener(this)
    }

    override fun Component.render() {
        val keyValues = KeyValueStore.keyValues()
        val editingKeyValue = KeyValueStore.editingKeyValue
        var rowIndex = 0
        bsGrid({ id = KeyValueScreenIds.screenId }) {
            bsButton ({
                id = KeyValueScreenIds.addButton
                bsStyle = BsStyle.Primary
                onClick = { Actions.setEditingKeyValue(globalDispatcher, KeyValue("", "")) }
            }) { text("Hozzáadás") }
            bsRow {
                bsCol ({ md = 10 }) {
                    bootstrapTable<KeyValue>({
                        data = keyValues.toTypedArray()
                        hover = true
                        selectRow = SelectRowProp(SelectionMode.radio, clickToSelect = true, hideSelectColumn = true)
                        search = true
                    }) {
                        tableHeaderColumn<KeyValue, Unit>({
                            width = "50"
                            dataFormat = { cell, keyValue ->
                                createReactElement {
                                    bsButtonGroup ({ bsSize = BsSize.ExtraSmall }) {
                                        bsButton ({
                                            id = KeyValueScreenIds.table.row.editButton(rowIndex)
                                            bsStyle = BsStyle.Primary
                                            onClick = {
                                                window.location.hash = Path.keyValue.withOpenedEditorModal(keyValue.key)
                                            }
                                        }) { text("Szerkesztés") }
                                        bsButton ({
                                            id = KeyValueScreenIds.table.row.deleteButton(rowIndex++)
                                            bsStyle = BsStyle.Danger
                                            onClick = {
                                                Actions.deleteKeyValue(globalDispatcher, keyValue)
                                            }
                                        }) { text("Törlés") }
                                    }
                                }
                            }
                        })
                        tableHeaderColumn<KeyValue, String>({
                            isKey = true
                            dataField = "key"
                            width = "100"
                        }) { text("Key") }
                        tableHeaderColumn<KeyValue, String>({
                            dataField = "value"
                            dataAlign = DataAlign.Right
                            width = "75"
                            dataFormat = { cell, keyValue -> accounting.formatNumber(cell, 3, ' ') }
                        }) { text("Value") }
                    }
                }
            }
            if (editingKeyValue != null) {
                fun closeModal(result: ModalResult, entity: KeyValue?) {
                    if (result == ModalResult.Save) {
                        Actions.modifyKeyValue(globalDispatcher, entity!!)
                    }
                    Actions.setEditingKeyValue(globalDispatcher, null)
                    window.location.hash = Path.keyValue.root
                }
                editorDialog(KeyValueEditorDialogProps(editingKeyValue, ::closeModal))
            }
        }
    }
}


data class KeyValueEditorDialogProps(val editedKeyValue: KeyValue, val close: (ModalResult, KeyValue?) -> Unit)
data class KeyValueEditorDialogState(val editedKeyValue: KeyValue)

class KeyValueEditorDialog() : ComponentSpec<KeyValueEditorDialogProps, KeyValueEditorDialogState>() {

    companion object {
        val factory = react.createFactory(KeyValueEditorDialog())
    }

    override fun componentDidMount() {
        KeyValueStore.addChangeListener(this) {
            val newKeyValue = KeyValueStore.editingKeyValue
            if (newKeyValue != null) {
                state = KeyValueEditorDialogState(newKeyValue.copy())
            }
        }
    }

    override fun componentWillUnmount() {
        KeyValueStore.removeListener(this)
    }

    override fun initialState(): KeyValueEditorDialogState? {
        return KeyValueEditorDialogState(props.editedKeyValue)
    }

    private fun updateEntity(event: FormEvent, updater: (String) -> KeyValue) {
        val value = event.currentTarget.value
        state = KeyValueEditorDialogState(updater(value))
    }

    override fun Component.render() {
        val keyValue = state.editedKeyValue
        val errors = hashMapOf<String, String>()
        fillWithErrors(errors)
        bsModal ({
            id = KeyValueScreenIds.modal.id
            show = true
            onHide = { props.close(ModalResult.Close, null) }
        }) {
            bsModalHeader ({ closeButton = true }) {
                bsModalTitle { text("Szerkesztés") }
            }
            bsModalBody ({ closeButton = true }) {
                bsRow {
                    bsCol({ md = 12 }) {
                        form {
                            bsRow {
                                bsCol({ md = 6 }) {
                                    bsInput({
                                        id = KeyValueScreenIds.modal.inputs.key
                                        type = InputType.Text
                                        label = "Key"
                                        defaultValue = keyValue.key
                                        onChange = {
                                            updateEntity(it) { value -> keyValue.copy(key = value) }
                                        }
                                        errors["key"]?.let { errorMessage ->
                                            bsStyle = BsStyle.Error
                                            help = errorMessage
                                        }
                                    })
                                }
                                bsCol({ md = 6 }) {
                                    bsInput({
                                        id = KeyValueScreenIds.modal.inputs.value
                                        type = InputType.Text
                                        label = "Value"
                                        defaultValue = keyValue.value
                                        onChange = {
                                            updateEntity(it) { value -> keyValue.copy(value = value) }
                                        }
                                        errors["value"]?.let { errorMessage ->
                                            bsStyle = BsStyle.Error
                                            help = errorMessage
                                        }
                                    })
                                }
                            }
                        }
                    }
                }
            }
            bsModalFooter {
                bsButtonGroup {
                    if (errors.isEmpty()) {
                        bsButton ({
                            id = KeyValueScreenIds.modal.buttons.save
                            bsStyle = BsStyle.Success
                            onClick = { props.close(ModalResult.Save, state.editedKeyValue) }
                        }) { text("Mentés") }
                    }
                    bsButton ({
                        id = KeyValueScreenIds.modal.buttons.close
                        bsStyle = BsStyle.Danger
                        onClick = { props.close(ModalResult.Close, null) }
                    }) { text("Mégsem") }
                }
            }
        }
    }

    private fun fillWithErrors(errors: MutableMap<String, String>) {
        val conf = state.editedKeyValue
        arrayOf("key" to conf.key,
                "value" to conf.value).forEach {
            val errorMessages = validate(it.second, Min(3), Max(100))
            if (errorMessages.isNotEmpty()) {
                errors[it.first] = errorMessages.joinToString("\n")
            }
        }
    }
}

private fun Component.editorDialog(props: KeyValueEditorDialogProps): Component {
    return constructAndInsert(Component({ KeyValueEditorDialog.factory(Ref(props)) }))
}

fun Component.keyValueScreen(): Component {
    return constructAndInsert(Component({ KeyValueScreen.factory(Ref(null)) }))
}

class KeyValueScreenTest {


    @Test
    fun hack() {
        kotlin.test.assertTrue(true) // qunit hack, at least one assert must be present
        qunitTest("KeyValueScreenTest") { assert: dynamic ->
            tests()
            runFirstGiven(assert)
        }
    }

    fun tests() {
        given("in any state") {
            on("routing to the KeyValue screen") {
                window.location.hash = Path.keyValue.root
                it("should be rendered KeyValue screen") { assertTrue(jq("#${KeyValueScreenIds.screenId}").size() == 1) }
                it("should make the KeyValue menupoint active") { assertTrue(jq("#${NavMenuIds.keyValue}").parent().hasClass("active")) }
            }
        }
        given("KeyValueScreenTest in default state") {
            Actions.setLoggedInUser(globalDispatcher, Account("testUser", false, arrayListOf(Role.ROLE_ADMIN), ""))
            window.location.hash = Path.keyValue.root
            Actions.modifyKeyValue(globalDispatcher,
                    KeyValue(key = "key1", value = "100")
            )
            on("changing the URl to .../keyValue/editedkey") {
                window.location.hash = Path.keyValue.withOpenedEditorModal("key1")
                it("should open the modal") {
                    assertTrue(KeyValueScreenIds.modal.id.appearOnScreen())
                }
                it("should render the input fields") {
                    assertTrue(KeyValueScreenIds.modal.inputs.key.appearOnScreen())
                    assertTrue(KeyValueScreenIds.modal.inputs.value.appearOnScreen())
                }
                it("should fill the input fields with the values of the references KeyValue") {
                    
                    assertEquals("key1", jq("#${KeyValueScreenIds.modal.inputs.key}").`val`())
                    assertEquals("100", jq("#${KeyValueScreenIds.modal.inputs.value}").`val`())
                }
                it("should render the close button") {
                    assertTrue(KeyValueScreenIds.modal.buttons.close.appearOnScreen())
                }
            }
            on("Clicking on the Edit button") {
                KeyValueScreenIds.table.row.editButton(0).simulateClick()
                it("should open the modal") {
                    assertTrue(KeyValueScreenIds.modal.id.appearOnScreen())
                }
                it("should fill the input fields with the values of the references KeyValue") {
                    assertEquals("key1", jq("#${KeyValueScreenIds.modal.inputs.key}").`val`())
                    assertEquals("100", jq("#${KeyValueScreenIds.modal.inputs.value}").`val`())
                }
                it("should change the URL, appending the editing key to it") {
                    assertEquals(Path.keyValue.withOpenedEditorModal("key1"), RouterStore.path)
                }
            }
            on("Clicking on the Delete button") {
                KeyValueScreenIds.table.row.deleteButton(0).simulateClick()
                it("should remove the KeyValue") {
                    assertEquals(0, KeyValueStore.keyValues().size)
                }
                it("should remove the row from the table") {
                    assertEquals(0, jq("#${KeyValueScreenIds.table.id}").find("tr").size())
                }
            }
            on("Clicking on Add button") {
                KeyValueScreenIds.addButton.simulateClick()
                it("should open the modal") {
                    assertTrue(KeyValueScreenIds.modal.id.appearOnScreen())
                }
                it("should render the input values empty") {
                    assertEquals("", jq("#${KeyValueScreenIds.modal.inputs.key}").`val`())
                    assertEquals("", jq("#${KeyValueScreenIds.modal.inputs.value}").`val`())
                }
            }
        }
        given("KeyValueScreenTest new KeyValue editor is open with filled inputs") {
            window.location.hash = Path.keyValue.root
            KeyValueScreenIds.addButton.simulateClick()
            simulateChangeInput(KeyValueScreenIds.modal.inputs.key) { input ->
                input.value = "key2"
            }
            simulateChangeInput(KeyValueScreenIds.modal.inputs.value) { input ->
                input.value = "123"
            }
            on("clicking on the Close button") {
                KeyValueScreenIds.modal.buttons.close.simulateClick()
                it("should close the Modal dialog window") {
                    assertFalse(KeyValueScreenIds.modal.id.appearOnScreen())
                }
                it("should not change the URL") {
                    assertEquals(Path.keyValue.root, RouterStore.path)
                }
            }
            on("clicking on the Save button") {
                KeyValueScreenIds.modal.buttons.save.simulateClick()
                it("should close the Modal dialog window") {
                    assertFalse(KeyValueScreenIds.modal.id.appearOnScreen())
                }
                it("should not change the URL") {
                    assertEquals(Path.keyValue.root, RouterStore.path)
                }
                it("should add the new KeyValue to the Store") {
                    assertEquals(2, KeyValueStore.keyValues().size)
                }
                it("should render the new KeyValue in the table") {
                    assertTrue(jq("#${KeyValueScreenIds.screenId}").find("div:contains('123.000'):last").size() == 1)
                }
            }
        }
        given("KeyValueScreenTest: the Modal editor is open") {
            window.location.hash = Path.keyValue.root
            window.location.hash = Path.keyValue.withOpenedEditorModal("key1")
            Actions.modifyKeyValue(globalDispatcher,
                    KeyValue(key = "key1", value = "100")
            )
            on("clicking on the Close button") {
                KeyValueScreenIds.modal.buttons.close.simulateClick()
                it("should close the Modal dialog window") {
                    assertFalse(KeyValueScreenIds.modal.id.appearOnScreen())
                }
                it("should change the URL, deleting the .../key/ parts") {
                    assertEquals(Path.keyValue.root, RouterStore.path)
                }
            }
            on("clicking on the Save button") {
                KeyValueScreenIds.modal.buttons.save.simulateClick()
                it("should close the Modal dialog window") {
                    assertFalse(KeyValueScreenIds.modal.id.appearOnScreen())
                }
                it("should change the URL, deleting the .../key/ parts") {
                    assertEquals(Path.keyValue.root, RouterStore.path)
                }
            }
            on("Emptying the Key input field") {
                simulateChangeInput(KeyValueScreenIds.modal.inputs.key) { keyInput ->
                    keyInput.value = ""
                }
                it("should hide the Save button (through the validators)") {
                    assertFalse(KeyValueScreenIds.modal.buttons.save.appearOnScreen())
                }
            }
            on("Emptying the Value input field") {
                simulateChangeInput(KeyValueScreenIds.modal.inputs.value) { input ->
                    input.value = ""
                }
                it("should hide the Save button (through the validators)") {
                    assertFalse(KeyValueScreenIds.modal.buttons.save.appearOnScreen())
                }
            }
            on("Clicking on Save after changing the input value") {
                simulateChangeInput(KeyValueScreenIds.modal.inputs.value) { input ->
                    input.value = "200"
                }
                KeyValueScreenIds.modal.buttons.save.simulateClick()
                it("should save the changed value to the Store") {
                    assertEquals("200", KeyValueStore.keyValues().first().value)
                }
                it("the table should render the new, changed value") {
                    assertTrue(jq("#${KeyValueScreenIds.screenId}").find("div:contains('200.000'):last").size() == 1)
                }
            }
        }
    }
}