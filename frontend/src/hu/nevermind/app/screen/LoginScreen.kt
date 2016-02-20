package hu.nevermind.app.screen

import com.github.andrewoma.react.*
import hu.nevermind.app.*
import hu.nevermind.reakt.bootstrap.*

private data class LoginScreenState(val username: String, val password: String)

private class LoginScreen : ComponentSpec<Unit, LoginScreenState>() {

    companion object {
        val factory = react.createFactory(LoginScreen())
    }

    override fun initialState(): LoginScreenState = LoginScreenState("", "")

    override fun Component.render() {
        bsGrid({ fluid = true }) {
            bsRow() {
                bsCol({ smOffset=4; sm=4; mdOffset = 4; md = 4 }) {
                    form({ action = "/login"; method = "POST" }) {
                        h1 { text("Login") }
                        bsInput({
                            type = InputType.Text
                            placeholder = "Username"
                            value = state.username
                            name = "username"
                            onChange = { event ->
                                val value = event.currentTarget.value
                                state = state.copy(username = value)
                            }
                            val errorMessage = validate(state.username, Min(3), Max(20)).joinToString("\n")
                            if (errorMessage.isNotEmpty()) {
                                bsStyle = BsStyle.Error
                                help = errorMessage
                            }
                        })
                        bsInput({
                            type = InputType.Password
                            placeholder = "Password"
                            value = state.password
                            name = "password"
                            onChange = {
                                state = state.copy(password = it.currentTarget.value)
                            }
                            val errorMessage = validate(state.password, Min(5), Max(20)).joinToString("\n")
                            if (errorMessage.isNotEmpty()) {
                                bsStyle = BsStyle.Error
                                help = errorMessage
                            }
                        })
                        bsButton ({
                            bsStyle = BsStyle.Success
                            bsSize = BsSize.Large
                            block = true
                            type = BsButtonType.Submit
                        }) { text("Login") }
                    }
                }
            }
        }
    }
}

fun Component.loginScreen(): Component {
    return constructAndInsert(Component({ LoginScreen.factory(Ref(null)) }))
}