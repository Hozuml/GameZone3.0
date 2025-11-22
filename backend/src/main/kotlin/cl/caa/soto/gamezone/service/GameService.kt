package cl.caa.soto.gamezone.service

import cl.caa.soto.gamezone.model.Game
import cl.caa.soto.gamezone.repository.GameRepository
import org.springframework.stereotype.Service

@Service
class GameService(private val gameRepository: GameRepository) {

    // Función para obtener todos los juegos
    fun getAllGames(): List<Game> {
        return gameRepository.findAll()
    }

    // Función para crear un nuevo juego (POST)
    fun createGame(game: Game): Game {
        return gameRepository.save(game)
    }

    // Función para eliminar un juego por ID (DELETE)
    fun deleteGame(id: Long) {
        if (!gameRepository.existsById(id)) {
            throw RuntimeException("Juego con ID $id no encontrado")
        }
        gameRepository.deleteById(id)
    }
}