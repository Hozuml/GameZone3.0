package cl.caa.soto.gamezone.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    var fullName: String,

    @Column(nullable = false, unique = true, length = 60)
    val email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = true)
    var phone: String? = null,

    //Géneros favoritos (Lista de opciones)
    @ElementCollection
    @CollectionTable(name = "user_genres", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "genre")
    var generos: MutableList<String> = mutableListOf()
)