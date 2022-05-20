package ru.alx

val TEST_YES_BTN = "Это мне близко и понятно"
val TEST_NO_BTN = "Это мне не нравится"
val TEST_KEYS = Keyboard(listOf(listOf(
    KeyboardButton(TEST_YES_BTN),
    KeyboardButton(TEST_NO_BTN),
)))

fun testState(name: String, img: String, nextState: String): State {
    return State(
        name = name,
        action = { ctx ->
            sendPhoto(OutPhotoMessage(
                chat_id = ctx.chatId,
                photo = getPhoto(img),
                caption = "Как вам это?",
                reply_markup = TEST_KEYS
            ))

            null
        },
        nextState = { action, ctx ->
            when (action.message?.text) {
                TEST_YES_BTN -> {
                    ctx.user.testResult += "Y"
                    nextState
                }
                TEST_NO_BTN -> {
                    ctx.user.testResult += "N"
                    nextState
                }
                else -> sendDefaultMessage(ctx)
            }
        }
    )
}

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
                    text = "Привет.\n" +
                            "Я бот мобильного приложения Art & Freedom. Оно позволяет легко находить выставки современного искусства, которые подойдут именно тебе, а еще помогает найти компанию для похода на выставку. \n" +
                            "Пока мои неспешные разработчики готовят к выходу приложение, я в меру своих возможностей, проведу тебя через лабиринты современного искусства к событиям, которые всколыхнут твое сердечко. ",
                    reply_markup = Keyboard(listOf(listOf(
                        KeyboardButton("Найти компанию"),
                        KeyboardButton("Выбрать событие"),
                    )))
                )
            )
            null
        },
        nextState = { action, ctx ->
            when (action.message?.text) {
                "Найти компанию" -> "companion"
                "Выбрать событие" -> when (ctx.user.testResult.isEmpty()) {
                    true -> "start-test"
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
        name = "start-test",
        action = { ctx ->
            sendMessage(OutTextMessage(
                text = "Мы знаем, что современное искусство бывает непонятным и сложным для восприятия. Но также оно позволяет быть порталом в мир личных чувств и переживаний. Ты совсем не обязан чувствовать, как все, когда соприкасаешься с ним. \n" +
                        "Искусство обучает внутренней свободе. Диалогу с собой. Помогает задавать себе неудобные вопросы. \n" +
                        "\n" +
                        "Чтобы мне было проще подобрать события, которые подойдут именно тебе, пожалуйста, пройди тест, выбирая картинки, которые тебе откликаются. Это наш прообраз искусственного интеллекта, который в мобильном приложении будет находить для тебя события еще точнее. \n" +
                        "\n" +
                        "«Ныряем в искусство?» ",
                chat_id = ctx.chatId,
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
                    "test-block-1"
                }
                "Нет" -> {
                    "welcome"
                }
                else -> sendDefaultMessage(ctx)
            }
        }
    ),
    testState("test-block-1", "test_1.jpg", "test-block-2"),
    testState("test-block-2", "test_2.jpg", "test-block-3"),
    testState("test-block-3", "test_3.jpg", "test-block-4"),
    testState("test-block-4", "test_4.jpg", "event"),
    State(
        name = "event",
        action = { ctx ->
            sendMessage(OutTextMessage(
                text = "Теперь я стал понимать тебя немного лучше и хочу предложить тебе некоторые события, которые в ближайшие дни будут проходить в Москве. Я предлагаю тебе градацию. Нажимая кнопку — точно да, ты увидишь три события, которые  скорее всего тебя порадуют. \n" +
                        "Нажимая кнопку — точно нет, ты увидишь события, которые могут разочаровать и еще можешь проверить мою работу, и посмотреть вдруг я ошибся.\n" +
                        "Нажимая кнопку — рискнуть, ты получишь рандомный список событий ",
                reply_markup = Keyboard(listOf(listOf(
                    KeyboardButton("Точно да" ),
                    KeyboardButton("Точно нет"),
                    KeyboardButton("Рискнуть")
                ))),
                chat_id = ctx.chatId
            ))

            null
        },
        nextState = { action, ctx ->
            when (action.message?.text) {
                "Точно да" -> "good-event"
                "Точно нет" -> "bad-event"
                "Рискнуть" -> "random-event"
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
                    "start-test"
                }
                else -> sendDefaultMessage(ctx)
            }
        }
    )
)