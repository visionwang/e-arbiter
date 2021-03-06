package pl.cyganki.tournament.model.dto

import pl.cyganki.tournament.model.TournamentStatus
import java.time.LocalDateTime

data class TournamentPreview(
        val id: String,
        val ownerName: String,
        val name: String,
        val description: String,
        val publicFlag: Boolean,
        val status: TournamentStatus,
        val users: Int,
        val endDate: LocalDateTime
)