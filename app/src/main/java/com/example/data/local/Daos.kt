package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_accounts WHERE usernameOrEmail = :usernameOrEmail LIMIT 1")
    suspend fun getUserAccount(usernameOrEmail: String): UserAccountEntity?

    @Query("UPDATE user_accounts SET password = :newPassword WHERE usernameOrEmail = :usernameOrEmail")
    suspend fun updateAccountPassword(usernameOrEmail: String, newPassword: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(account: UserAccountEntity)

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserFlow(id: String = "user_default"): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUser(id: String = "user_default"): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET points = :points, balanceBdt = :balanceBdt WHERE id = :id")
    suspend fun updateBalance(id: String = "user_default", points: Int, balanceBdt: Double)

    @Query("UPDATE users SET isLoggedIn = :isLoggedIn WHERE id = :id")
    suspend fun setLoginState(id: String = "user_default", isLoggedIn: Boolean)

    @Query("UPDATE users SET name = :name, gender = :gender, birthday = :birthday, phone = :phone WHERE id = :id")
    suspend fun updateProfileDetails(id: String = "user_default", name: String, gender: String, birthday: String, phone: String)

    @Query("UPDATE users SET friendsCount = :count WHERE id = :id")
    suspend fun updateFriendsCount(id: String = "user_default", count: Int)

    @Query("UPDATE users SET totalReferrals = :count WHERE id = :id")
    suspend fun updateTotalReferrals(id: String = "user_default", count: Int)

    @Query("DELETE FROM user_accounts")
    suspend fun clearAllAccounts()

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()
}

@Dao
interface WithdrawalDao {
    @Query("SELECT * FROM withdrawals ORDER BY timestamp DESC")
    fun getAllWithdrawalsFlow(): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserWithdrawalsFlow(userId: String = "user_default"): Flow<List<WithdrawalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity)

    @Query("UPDATE withdrawals SET status = :status WHERE id = :id")
    suspend fun updateWithdrawalStatus(id: String, status: String)

    @Query("DELETE FROM withdrawals")
    suspend fun clearAll()
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE conversationUserId = :conversationUserId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationUserId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessagesFlow(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE conversationUserId = :conversationUserId")
    suspend fun clearConversation(conversationUserId: String)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()
}

@Dao
interface FriendDao {
    @Query("SELECT * FROM friends")
    fun getAllFriendsFlow(): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends WHERE id = :id LIMIT 1")
    suspend fun getFriendById(id: String): FriendEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFriends(friends: List<FriendEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFriend(friend: FriendEntity)

    @Query("UPDATE friends SET status = :status WHERE id = :id")
    suspend fun updateFriendStatus(id: String, status: String)

    @Query("UPDATE friends SET isBlocked = :isBlocked WHERE id = :id")
    suspend fun updateFriendBlockStatus(id: String, isBlocked: Boolean)

    @Query("UPDATE friends SET lastMessage = :lastMessage, lastMessageTime = :time WHERE id = :id")
    suspend fun updateLastMessage(id: String, lastMessage: String, time: String)

    @Query("DELETE FROM friends")
    suspend fun clearAllFriends()
}
