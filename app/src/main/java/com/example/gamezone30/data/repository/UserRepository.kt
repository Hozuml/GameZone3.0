package com.example.gamezone30.data.repository

import com.example.gamezone30.data.local.dao.UserDao
import com.example.gamezone30.data.local.dao.entity.User


class UserRepository(
    // El repositorio "pide" el control remoto (DAO) para poder trabajar
    private val userDao: UserDao
) {

    /**
     * Función para registrar un nuevo usuario.
     */
    suspend fun registerUser(user: User) {
        userDao.insertUser(user)
    }

    /**
     * Función para encontrar un usuario por su email.
     */
    suspend fun findUserByEmail(email: String): User? {
        return userDao.findUserByEmail(email)
    }

    /**
     * Función rápida para comprobar si un email YA existe.
     */
    suspend fun isEmailRegistered(email: String): Boolean {
        return findUserByEmail(email) != null
    }

}
