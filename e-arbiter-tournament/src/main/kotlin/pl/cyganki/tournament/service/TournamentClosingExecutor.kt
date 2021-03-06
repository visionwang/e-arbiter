package pl.cyganki.tournament.service

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import mu.KLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pl.cyganki.tournament.model.TournamentStatus
import pl.cyganki.tournament.repository.TournamentRepository
import java.time.LocalDateTime

/**
 * Executor responsible for closing active tournaments where tournament end date is before current date
 * Task is scheduled and tournaments in database are checked every minute
 * There is a dedicated query sent to MongoDB so there is no problem with performance
 */
@Service
class TournamentClosingExecutor(
        private val tournamentRepository: TournamentRepository,
        private val mailService: MailService
) {

    @Scheduled(fixedRate = oneMinuteTimeInMs)
    fun closeTournamentsAfterEndDate() {
        val currentDate = LocalDateTime.now()
        logger.debug { "Run closeTournamentsAfterEndDate task" }
        with(tournamentRepository.findActiveTournamentsWhereEndDateIsEarlierThan(currentDate)) {
            forEach {
                it.finish()
                val savedTournament = tournamentRepository.save(it)

                if (TournamentStatus.FINISHED == savedTournament.status) {
                    logger.info { "Closed tournament with id ${it.id}" }
                    launch(CommonPool) { mailService.sendFinishedTournamentEmail(it.id) }
                } else {
                    throw RuntimeException("Tournament ${it.id} was not finished by executor")
                }
            }
        }
    }

    companion object : KLogging() {
        private const val oneMinuteTimeInMs: Long = 1 * 60 * 1000
    }
}