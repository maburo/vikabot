package ru.alx

data class From(
    val id: String,
    val first_name: String?,
    val last_name: String?,
    val username: String?
)

data class Message(
    val message_id: String,
    val from: From?,
    val chat: Chat?,
    val text: String?,
    val photo: List<Photo>?,
)

data class Photo(
    val file_id: String
)

data class Chat(
    val id: String,
    val first_name: String?,
    val last_name: String?,
    val username: String?,
    val type: String,
)

data class Action(
    val message: Message?,
    val callback_query: CallbackQuery?,
)

data class CallbackQuery(
    val from: From?,
    val message: Message?,
    val data: String,
)

data class InputMediaPhoto(
    val type: String,
    val media: String,
    val caption: String,
)

data class KeyboardButton(
    val text: String,
    val url: String? = null,
    val callback_data: String? = null
)

interface ReplyMarkup

data class Keyboard(
    val keyboard: List<List<KeyboardButton>>
) : ReplyMarkup

data class InlineKeyboard(
    val inline_keyboard: List<List<KeyboardButton>>
) : ReplyMarkup

data class RemoveKeyboard(
    val remove_keyboard: Boolean = true
): ReplyMarkup

interface OutMessage

data class OutTextMessage(
    val chat_id: String,
    val emoji: String? = null,
    val text: String? = null,
    val reply_markup: ReplyMarkup? = null,
) : OutMessage

data class OutPhotoMessage(
    val chat_id: String,
    val photo: String,
    val caption: String? = null,
    val reply_markup: ReplyMarkup? = null,
): OutMessage

data class Context(
    var chatId: String,
    var user: User,
)

data class State(
    val nextState: suspend (action: Action, ctx: Context) -> String,
    val conditions: Map<String, String>? = emptyMap(),
    val action: suspend (Context) -> String?,
)

data class User(
    val chatId: String,
    val username: String,
    val firstName: String? = null,
    var testResult: String = "",
    var event: Int = -1,
    var currentState: State = states["start"]!!,
    var prevState: State? = null
)