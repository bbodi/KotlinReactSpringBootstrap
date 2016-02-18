object Path {

    val root: String = "#"

    object keyValue {
        val root = "#config/"
        val keyValueWithOpenedEditorModal =  { confId: String -> "$root$confId/"}
    }
}