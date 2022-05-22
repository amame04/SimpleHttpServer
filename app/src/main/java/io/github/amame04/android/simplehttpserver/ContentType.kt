package io.github.amame04.android.simplehttpserver

enum class ContentType(private val text: String, private val extensions: String) {
    TEXT_PLAIN("text/plain", "txt"),
    TEXT_HTML("text/html", "html,htm"),
    TEXT_CSS("text/css", "css"),
    TEXT_XML("text/xml", "xml"),
    APPLICATION_JAVASCRIPT("application/javascript", "js"),
    APPLICATION_JSON("application/json", "json"),
    APPLICATION_PDF("application/pdf", "pdf"),
    APPLICATION_ZIP("application/zip", "zip"),
    APPLICATION_X_GTAR("application/x-gtar", "gtar"),
    APPLICATION_X_GZIP("application/x-gzip", "gz"),
    IMAGE_JPEG("image/jpeg", "jpg,jpeg"),
    IMAGE_PNG("image/png", "png"),
    IMAGE_GIF("image/gif", "gif"),
    IMAGE_VND_MICROSOFT_ICON("image/vnd.microsoft.icon", "ico"),
    ;


    override fun toString() : String {
        return this.text
    }

    fun toContentType(ext: String) : ContentType {
        val contentTypeMap = HashMap<String, ContentType>()
        values().forEach {
            if(it.extensions.indexOf(",") != -1){
                val extensionsList = it.extensions.split(",")
                extensionsList.forEach { extension -> contentTypeMap[extension.uppercase()] = it }
            } else {
                contentTypeMap[it.extensions.uppercase()] = it
            }
        }
        return contentTypeMap.getOrDefault(ext.uppercase(), TEXT_PLAIN)
    }
}