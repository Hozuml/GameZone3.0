package cl.caa.soto.gamezone.model

import jakarta.persistence.*

@Entity
@Table(name = "games")
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 150)
    val title: String,

    @Column(nullable = false, length = 50)
    val genre: String,

    @Column(nullable = false)
    val price: Double,

    @Column(nullable = true, length = 500)
    val description: String?
)