package ru.alx

val states = listOf(
    State(
        name = "start",
        action = { null },
        nextState = { _, _ -> "welcome" }
    ),
    State(
        name = "welcome",
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
    State(
        name = "companion",
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
    State(
        name = "test-block-1",
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
    State(
        name = "test-block-2",
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
    State(
        name = "event",
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
    State(
        name = "good-event",
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
    State(
        name = "bad-event",
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
    State(
        name = "random-event",
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
    State(
        name = "restart",
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