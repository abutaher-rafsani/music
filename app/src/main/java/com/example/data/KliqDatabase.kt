package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.*
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        UserPhoneContactEntity::class,
        PostEntity::class,
        PostCommentEntity::class,
        ReelEntity::class,
        ScoutGigEntity::class,
        SkillSessionEntity::class,
        WalletEntity::class,
        WalletTransactionEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        UserTaskEntity::class,
        SocialInteractionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class KliqDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun reelDao(): ReelDao
    abstract fun scoutGigDao(): ScoutGigDao
    abstract fun skillSessionDao(): SkillSessionDao
    abstract fun walletDao(): WalletDao
    abstract fun chatDao(): ChatDao
    abstract fun contactsDao(): ContactsDao
    abstract fun userTaskDao(): UserTaskDao
    abstract fun socialInteractionDao(): SocialInteractionDao

    companion object {
        @Volatile
        private var INSTANCE: KliqDatabase? = null

        fun getDatabase(context: Context): KliqDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KliqDatabase::class.java,
                    "kliq_platform_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
