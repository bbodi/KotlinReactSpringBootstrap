# KotlinReactSpringBootstrap
A web application framework and structure template using Kotlin, React and Spring. It makes easier to start new projects from scratch.

Kotlin 1.0.0-beta-4589

## Features:
- Type safe React (thanks to [Reakt](https://github.com/andrewoma/reakt)) and [React Bootstrap](https://react-bootstrap.github.io/) builders

   ```
bsModalBody ({ closeButton = true }) {
    bsRow {
      bsCol({ md = 12 }) {
          form {
              bsRow {
                  bsCol({ md = 6 }) {
                      bsInput(){}
                  }
              }
          }
      }
    }
}
```
- Typesafe HTML enums

 ```kotlin
bsInput({type = InputType.Text}){...}
bsButton ({bsStyle = BsStyle.Success}){}
```
- Typesafe element ID-s

 ```kotlin
bsButton ({id = KeyValueScreenIds.addButton}) { text("Add") }
```
- ... and helper methods for readable unit tests

 ```kotlin
assertTrue(KeyValueScreenIds.addButton.appearOnScreen())
KeyValueScreenIds.addButton.simulateClick()
```
- Typesafe hash URL-s

 ```
href = "#${Path.account.root}"
```
- BDD frontend testing (inspired by [Spek](https://github.com/JetBrains/spek))

 ```kotlin
given("in any state") {
    on("routing to the KeyValue screen") {
      window.location.hash = Path.keyValue.root // these initialization code runs before every "it" invocations
      it("should render KeyValue screen") { assertTrue(jq("#${KeyValueScreenIds.screenId}").size() == 1) }
      it("should make the KeyValue menupoint active") { assertTrue(jq("#${NavMenuIds.keyValue}").parent().hasClass("active")) }
    }
}
```
- Router

 ```kotlin
RouterStore.match(
    "${Path.login}" to { params ->
        ...
    },
    "${Path.keyValue.root}:id" to { params ->
        assert(params["id"] != null)
    },
    "${Path.account.root}?id" to { params ->
        // params["id"] can be null
    },
    otherwise = {
        ...
    }
  )
```
- Frontend unit testing without any backend

 ```
file:///path_to/web_framework/frontend/tests.html?tests
```
- Authentication

 ```
admin, admin, ROLE_ADMIN
user, user, ROLE_USER
```
## Caveats / Hacks / Challanges
- All the frontend dependency is included in the project, because the Kotlin Javascript and IntelliJ is not ready yet for conveniently handling dependencies.
- At the frontend, asynchronous invocation is not rare, so unit tests must take into account that some state changes occur and become testable only after some time.   
   It is solved by the special BDD syntax and QUnit's assert.async() call.  
   
 ```kotlin
 private fun later(assert: dynamic, body: () -> Unit) {  
    val done = assert.async()  
    window.setTimeout({  
        body()  
        done()  
    }, 100)  
 }
```  
   The BDD builder collects all the "given", "on" and "it" bodies, and runs them synchronously, but with some delay between the state initialization ("given" and "on" bodies) and test ("it" bodies) codes.
- Currently Kotlin generates QUnit tests from methods annotated with @Test. But in the @Test methods you cannot use the assert parameter of the QUnit test, which is necessary for asyncronous tests.  
So there is only one @Test method per class, which is necessary to run the test class automatically on startup, but in that test we call an other QUnit test method, in which we run our own BDD-style builders. This call only build a tree-like structure from the builder invocations, but we have to run these tests. We can do that by calling the runFirstGiven(assert) method.

 ```kotlin
@native("QUnit.test") fun qunitTest(name: String, body: (assert: dynamic)->Unit)
```

 ```kotlin
    @Test
    fun hack() {
        kotlin.test.assertTrue(true) // qunit hack, at least one assert must be present
        qunitTest("KeyValueScreenTest") { assert: dynamic ->
            tests() // it contains our given(...){} calls
            runFirstGiven(assert)
        }
    }
```
