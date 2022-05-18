package ru.alx

val states = mapOf(
    "start" to State(
        action = { null },
        nextState = { _, _ -> "welcome" }
    ),
    "welcome" to State(
        action = { ctx: Context ->
            ctx.user.event = -1
            ctx.user.testResult = ""

            sendMessage(
                OutTextMessage(
                    chat_id = ctx.chatId,
                    text = "Привет ${ctx.user.firstName ?: ctx.user.username}",
                    reply_markup =  Keyboard(listOf(listOf(
                        KeyboardButton("Найти компаньена"),
                        KeyboardButton("Выбрать сообытие"),
                    )))
                )
            )
            null
        },
        nextState = { action, ctx ->
            when (action.message?.text) {
                "Найти компаньена" -> "companion"
                "Выбрать сообытие" -> when (ctx.user.testResult.isEmpty()) {
                    true -> "test-block-1"
                    else -> "event"
                }
                else -> sendDefaultMessage(ctx)
            }
        }
    ),
    "companion" to State(
        action = { ctx ->
            sendMessage(OutTextMessage(
                text = "companion",
                chat_id = ctx.chatId
            ))
            null
        },
        nextState = { _, _ ->
            "start"
        }
    ),
    "test-block-1" to State(
        action = { ctx ->
            sendMessage(OutTextMessage(
                text = "Вам необходимо пройти тест",
                chat_id = ctx.chatId
            ))

            sendPhoto(OutPhotoMessage(
                chat_id = ctx.chatId,
                photo = getPhoto("download.jpeg"),
                caption = "Вам нравится это?",
                reply_markup = Keyboard(listOf(listOf(
                    KeyboardButton("Да"),
                    KeyboardButton("Нет"),
                )))
            ))

            null
        },
        nextState = { action, ctx ->
            when (action.message?.text) {
                "Да" -> {
                    ctx.user.testResult += "Y"
                    "test-block-2"
                }
                "Нет" -> {
                    ctx.user.testResult += "N"
                    "test-block-2"
                }
                else -> sendDefaultMessage(ctx)
            }
        }
    ),
    "test-block-2" to State(
        action = { ctx ->
            sendPhoto(OutPhotoMessage(
                chat_id = ctx.chatId,
                photo = getPhoto("download.jpeg"),
                caption = "А это?",
                reply_markup = Keyboard(listOf(listOf(
                    KeyboardButton("Да"),
                    KeyboardButton("Нет"),
                )))
            ))

            null
        },
        nextState = { action, ctx ->
            when (action.message?.text) {
                "Да" -> {
                    ctx.user.testResult += "Y"
                    "event"
                }
                "Нет" -> {
                    ctx.user.testResult += "N"
                    "event"
                }
                else -> sendDefaultMessage(ctx)
            }
        }
    ),
    "event" to State(
        action = { ctx ->
            sendMessage(OutTextMessage(
                text = "Какое событие подобрать",
                reply_markup = Keyboard(listOf(listOf(
                    KeyboardButton("Классное! \uD83D\uDC4D" ),
                    KeyboardButton("Отвратительное \uD83D\uDCA9"),
                    KeyboardButton("Рискну! \uD83E\uDDD0")
                ))),
                chat_id = ctx.chatId
            ))

            null
        },
        nextState = { action, ctx ->
            when (action.message?.text) {
                "Классное! \uD83D\uDC4D" -> "good-event"
                "Отвратительное \uD83D\uDCA9" -> "bad-event"
                "Рискну! \uD83E\uDDD0" -> "random-event"
                else -> sendDefaultMessage(ctx)
            }
        }
    ),
    "good-event" to State(
        action = { ctx ->
            sendMessage(OutTextMessage(
                chat_id = ctx.chatId,
                text = "Поздравляем вам досталось - полет на воздушном шаре над Питером",
                reply_markup = RemoveKeyboard()
            ))

            sendPhoto(OutPhotoMessage(
                chat_id = ctx.chatId,
                photo = getPhoto("event.jpg"),
                reply_markup = InlineKeyboard(listOf(listOf(
                    KeyboardButton(text = "Перейти на сайт", url = "https://www.afisha.ru/")
                )))
            ))

            "restart"
        },
        nextState = { _, _ ->
            "restart"
        }
    ),
    "bad-event" to State(
        action = { ctx ->
            sendMessage(OutTextMessage(
                chat_id = ctx.chatId,
                text = "Поздравляем вам досталось - один час кормления голубей на крестовском",
                reply_markup = RemoveKeyboard()
            ))

            sendPhoto(OutPhotoMessage(
                chat_id = ctx.chatId,
                photo = getPhoto("event.jpg"),
                reply_markup = InlineKeyboard(listOf(listOf(
                    KeyboardButton(text = "Перейти на сайт", url = "https://www.afisha.ru/")
                )))
            ))

            "restart"
        },
        nextState = { _, _ ->
            "restart"
        }
    ),
    "random-event" to State(
        action = { ctx ->
            sendDice(ctx.chatId)

            sendMessage(OutTextMessage(
                chat_id = ctx.chatId,
                text = "Поздравляем вам досталось - мастер класс по таксидермии",
                reply_markup = RemoveKeyboard()
            ))

            sendPhoto(OutPhotoMessage(
                chat_id = ctx.chatId,
                photo = getPhoto("event.jpg"),
                reply_markup = InlineKeyboard(listOf(listOf(
                    KeyboardButton(text = "Перейти на сайт", url = "https://www.afisha.ru/")
                )))
            ))

            "restart"
        },
        nextState = { _, _ ->
            "restart"
        }
    ),
    "restart" to State(
        action = { ctx ->
            sendMessage(OutTextMessage(
                chat_id = ctx.chatId,
                text = "Подходит?",
                reply_markup = Keyboard(listOf(listOf(
                    KeyboardButton("Да"),
                    KeyboardButton("Нет, хочу другое"),
                    KeyboardButton("Пройти тест заново")
                )))
            ))

            null
        },
        nextState = { action, ctx ->
            when (action.message?.text) {
                "Да" -> "welcome"
                "Нет, хочу другое" -> "event"
                "Пройти тест заново" -> {
                    ctx.user.testResult = ""
                    "test-block-1"
                }
                else -> sendDefaultMessage(ctx)
            }
        }
    )
)