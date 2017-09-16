package pl.cyganki.tournament.service

import org.springframework.data.domain.*
import org.springframework.stereotype.Service
import pl.cyganki.tournament.model.Tournament
import pl.cyganki.tournament.model.TournamentStatus
import pl.cyganki.tournament.model.dto.TournamentPreview
import pl.cyganki.tournament.repository.TournamentRepository
import pl.cyganki.utils.modules.AuthModuleInterface

@Service
class TournamentPreviewsFetcher(
        private val tournamentRepository: TournamentRepository,
        private val authModuleInterface: AuthModuleInterface
) {

    fun getActiveTournamentsInWhichUserParticipates(userId: Long, pageable: Pageable, query: String?) = getTournamentsInWhichUserParticipatesByStatus(userId, TournamentStatus.ACTIVE, pageable, query)

    fun getFinishedTournamentsInWhichUserParticipates(userId: Long, pageable: Pageable, query: String?) = getTournamentsInWhichUserParticipatesByStatus(userId, TournamentStatus.FINISHED, pageable, query)

    fun getActiveNewestTournamentsInWhichUserDoesNotParticipate(userId: Long, pageable: Pageable): Page<TournamentPreview> {
        val newestSort = Sort(Sort.Direction.DESC, "startDate")
        val pageRequest = PageRequest(pageable.pageNumber, pageable.pageSize, newestSort)
        return getPublicTournamentsInWhichUserDoesNotParticipate(userId, pageRequest)
    }

    fun getActiveMostPopularTournamentsInWhichUserDoesNotParticipate(userId: Long, pageable: Pageable): Page<TournamentPreview> {
        val tournaments = tournamentRepository.findAllPublicActiveTournamentsInWhichUserDoesNotParticipate(userId)
        tournaments.sortByDescending { it.joinedUsersIds.size }
        val firstElement = pageable.pageNumber * pageable.pageSize
        val lastElementByPageable = (pageable.pageSize * (pageable.pageNumber + 1)) - 1         // pageable indexing from 0
        val lastElement = if (lastElementByPageable < tournaments.size - 1) lastElementByPageable else tournaments.size - 1    // we don't want to outofbound exception

        val tournamentPreviews = tournaments
                .filterIndexed { index, _ -> (firstElement..lastElement).contains(index) }  // if there is empty page, there range will be empty too
                .map {
                    TournamentPreview(
                            it.id,
                            authModuleInterface.getUserNameById(it.ownerId),
                            it.name,
                            it.description,
                            it.isPublicFlag,
                            it.status,
                            it.joinedUsersIds.size,
                            it.endDate
                    )
                }

        return PageImpl(tournamentPreviews, pageable, tournaments.size.toLong())
    }

    fun getActiveAlmostEndedTournamentsInWhichUserDoesNotParticipate(userId: Long, pageable: Pageable): Page<TournamentPreview> {
        val almostEndedSort = Sort("endDate")
        val pageRequest = PageRequest(pageable.pageNumber, pageable.pageSize, almostEndedSort)
        return getPublicTournamentsInWhichUserDoesNotParticipate(userId, pageRequest)
    }

    private fun getTournamentsInWhichUserParticipatesByStatus(userId: Long, status: TournamentStatus, pageable: Pageable, query: String?): Page<TournamentPreview> {
        val tournaments = getTournamentDependingOnQuery(userId, status, pageable, query)
        return tournaments.map {
            TournamentPreview(
                    it.id,
                    authModuleInterface.getUserNameById(it.ownerId),
                    it.name,
                    it.description,
                    it.isPublicFlag,
                    it.status,
                    it.joinedUsersIds.size,
                    it.endDate
            )
        }
    }

    private fun getPublicTournamentsInWhichUserDoesNotParticipate(userId: Long, pageable: Pageable): Page<TournamentPreview> {
        val tournaments = tournamentRepository.findPublicActiveTournamentsInWhichUserDoesNotParticipate(userId, pageable)
        return tournaments.map {
            TournamentPreview(
                    it.id,
                    authModuleInterface.getUserNameById(it.ownerId),
                    it.name,
                    it.description,
                    it.isPublicFlag,
                    it.status,
                    it.joinedUsersIds.size,
                    it.endDate
            )
        }
    }

    private fun getTournamentDependingOnQuery(userId: Long, status: TournamentStatus, pageable: Pageable, query: String?): Page<Tournament> {
        return if (query == null || query.isEmpty()) {
            tournamentRepository.findTournamentsInWhichUserParticipatesByStatus(userId, status, pageable)
        } else {
            tournamentRepository.findTournamentsInWhichUserParticipatesByStatusAndQuery(userId, status, convertQueryToSQLLikeRegex(query), pageable)
        }
    }

    private fun convertQueryToSQLLikeRegex(query: String) = ".*$query.*"
}