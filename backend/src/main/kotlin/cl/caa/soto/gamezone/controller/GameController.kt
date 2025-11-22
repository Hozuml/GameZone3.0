package cl.caa.soto.gamezone.controller

import cl.caa.soto.gamezone.model.Game
import cl.caa.soto.gamezone.service.GameService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/games") // La ruta base será /api/games
class GameController(private val gameService: GameService) {

    // RUTA 1: GET /api/games (Listar todos)
    @GetMapping
    fun getAll(): List<Game> {
        return gameService.getAllGames()
    }

    // RUTA 2: POST /api/games (Crear un juego)
    @PostMapping
    fun create(@RequestBody game: Game): ResponseEntity<Game> {
        val newGame = gameService.createGame(game)
        return ResponseEntity.ok(newGame)
    }

    // RUTA 3: DELETE /api/games/{id} (Eliminar un juego)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<String> {
        gameService.deleteGame(id)
        return ResponseEntity.ok("Juego eliminado con éxito")
    }
}