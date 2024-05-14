package com.example.di

import com.example.data.MessageDataSource
import com.example.data.MessageDataSourceImpl
import com.example.room.RoomController
import org.koin.dsl.module
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.coroutine.coroutine

val mainModule = module {

    single {
        KMongo.createClient()
            .coroutine
            .getDatabase(name = "message_db")
    }

    single<MessageDataSource> {
        MessageDataSourceImpl(db = get())
    }

    single {
        RoomController(messageDataSource = get())
    }
}