object Path {

    val root: String = "#"
    val login = "#login/"
    object keyValue {
        val root = "#config/"
        val keyValueWithOpenedEditorModal =  { confId: String -> "$root$confId/"}
    }
}