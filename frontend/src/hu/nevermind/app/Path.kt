object Path {

    val root: String = "#"
    val login = "login/"
    object keyValue {
        val root = "keyValue/"
        val withOpenedEditorModal =  { key: String -> "$root$key/"}
    }
    object account {
        val root = "account/"
        val withOpenedEditorModal =  { username: String -> "$root$username/"}
    }
}