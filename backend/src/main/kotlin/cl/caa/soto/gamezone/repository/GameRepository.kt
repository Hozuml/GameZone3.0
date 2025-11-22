package cl.caa.soto.gamezone.repository

import cl.caa.soto.gamezone.model.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : JpaRepository<Game, Long> {
    // Los métodos vienen incluidos.
}