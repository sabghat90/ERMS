package com.kust.ermslibrary.models

class Message {
    var id: String = ""
    var body: String? = null
    var time: String? = null
    var senderId: String? = null

    constructor() {}

    constructor(body: String?, senderId: String?, time: String?) {
        this.body = body
        this.senderId = senderId
        this.time = time
    }
}
