package cl.caa.soto.gamezone.service

import cl.caa.soto.gamezone.model.User
import cl.caa.soto.gamezone.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class UserService(private val userRepository: UserRepository) {

    // Regex: Mayúscula, minúscula, número, símbolo, min 10 caracteres
    private val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\S+\$).{10,}$"

    fun registerUser(user: User): User {
        // 1. Validación DUOC
        if (!user.email.endsWith("@duoc.cl")) {
            throw RuntimeException("El correo debe ser institucional (@duoc.cl)")
        }

        // 2. Validación Correo Único
        if (userRepository.existsByEmail(user.email)) {
            throw RuntimeException("El correo ya está registrado")
        }

        // 3. Validación Contraseña Segura
        if (!Pattern.matches(PASSWORD_PATTERN, user.password)) {
            throw RuntimeException("Contraseña muy débil. Usa: Mayúscula, minúscula, número, símbolo y 10 caracteres.")
        }

        // 4. Validación Géneros
        if (user.generos.isEmpty()) {
            throw RuntimeException("Debes seleccionar al menos un género favorito")
        }

        return userRepository.save(user)
    }

    fun login(email: String, passwordInput: String): User {
        val usuario = userRepository.findByEmail(email)
            ?: throw RuntimeException("Credenciales inválidas") // Mensaje genérico por seguridad

        if (usuario.password != passwordInput) {
            throw RuntimeException("Credenciales inválidas")
        }
        return usuario
    }

    fun listarTodos(): List<User> = userRepository.findAll()
}