package cl.caa.soto.gamezone.controller

import cl.caa.soto.gamezone.model.User
import cl.caa.soto.gamezone.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    // GET: http://localhost:8080/api/users
    @GetMapping
    fun listar(): List<User> = userService.listarTodos()

    // POST: http://localhost:8080/api/users/register
    @PostMapping("/register")
    fun registrar(@RequestBody user: User): ResponseEntity<Any> {
        return try {
            val nuevo = userService.registerUser(user)
            ResponseEntity.ok(nuevo)
        } catch (e: Exception) {
            // Retorna un error 400 (Bad Request) con el mensaje de la validación
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    // POST: http://localhost:8080/api/users/login
    @PostMapping("/login")
    fun login(@RequestBody data: Map<String, String>): ResponseEntity<Any> {
        return try {
            val user = userService.login(data["email"] ?: "", data["password"] ?: "")
            ResponseEntity.ok(user)
        } catch (e: Exception) {
            ResponseEntity.status(401).body(mapOf("error" to e.message))
        }
    }
}