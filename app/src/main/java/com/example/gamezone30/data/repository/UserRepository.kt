package com.example.gamezone30.data.repository

import com.example.gamezone30.data.local.dao.UserDao
import com.example.gamezone30.data.local.dao.entity.User
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun registerUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun findUserByEmail(email: String): User? {
        return userDao.findUserByEmail(email)
    }

    suspend fun isEmailRegistered(email: String): Boolean {
        return findUserByEmail(email) != null
    }

    fun getUser(): Flow<User?> {
        return userDao.getUser()
    }

    suspend fun updateUser(fullName: String, phone: String) {
        userDao.updateUser(fullName, phone)
    }
}