package io.github.amame04.android.simplehttpserver

enum class Status(private val text: String) {
    OK("200 OK"),
    NOT_FOUND("404 Not Found"),
    MOVED_PERMANENTLY("301 Moved Permanently"),
    BAD_REQUEST("400 Bad Request"),
    METHOD_NOT_ALLOWED("405 Method Not Allowed"),
    ;

    override fun toString() : String {
        return this.text
    }
}