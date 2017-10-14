package pl.cyganki.tournament.service

import org.springframework.stereotype.Service
import pl.cyganki.tournament.model.Tournament
import pl.cyganki.tournament.repository.TournamentRepository

@Service
class TournamentManagementService(private val tournamentRepository: TournamentRepository) {

    fun saveTournament(userId: Long, tournament: Tournament): Tournament {
        tournament.ownerId = userId
        return this.tournamentRepository.save(tournament)
    }

    fun findTournamentById(tournamentId: String): Tournament = this.tournamentRepository.findOne(tournamentId)

    fun findTournamentByIdAndJoinedUserId(tournamentId: String, userId: Long): Tournament? {
        val tournament: Tournament = findTournamentById(tournamentId)
        val userIsEnrolled: Boolean = tournament.joinedUsersIds.contains(userId)

        return if (userIsEnrolled) tournament else null
    }
}