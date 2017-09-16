package pl.cyganki.tournament.service

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import pl.cyganki.tournament.EArbiterTournamentApplication
import pl.cyganki.tournament.model.TournamentStatus
import pl.cyganki.tournament.repository.TournamentRepository
import pl.cyganki.utils.modules.AuthModuleInterface
import pl.cyganki.utils.security.dto.User

/**
 * Tournaments to tests are defined in proper JSONs
 * @see /resources/db/changelog/test-data/'*'
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = arrayOf(EArbiterTournamentApplication::class), loader = SpringBootContextLoader::class)
@ActiveProfiles("test")
class TournamentPreviewsFetcherTest {

    lateinit var tournamentPreviewsFetcher: TournamentPreviewsFetcher

    lateinit var authModule: AuthModuleInterface

    @Autowired
    lateinit var tournamentRepository: TournamentRepository

    private val userId = 3L

    @Before
    fun `set up`() {
        authModule = MockAuthModule()
        tournamentPreviewsFetcher = TournamentPreviewsFetcher(tournamentRepository, authModule)
    }


    /**
     * getActiveTournamentsInWhichUserParticipates
     */

    @Test
    fun `should return all active user's tournaments in which user participates in one page`() {
        // given
        val pageRequest = PageRequest(0, 10)

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getActiveTournamentsInWhichUserParticipates(userId, pageRequest, null)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(6, totalElements)
            Assert.assertEquals(6, numberOfElements)
            Assert.assertEquals(1, totalPages)
            content.forEach { Assert.assertEquals(TournamentStatus.ACTIVE, it.status) }
        }
    }

    @Test
    fun `should return one page active user's tournament in which user participates and information about two available pages`() {
        // given
        val pageRequest = PageRequest(0, 4)

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getActiveTournamentsInWhichUserParticipates(userId, pageRequest, null)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(6, totalElements)
            Assert.assertEquals(4, numberOfElements)
            Assert.assertEquals(2, totalPages)
            Assert.assertFalse(isLast)
            content.forEach { Assert.assertEquals(TournamentStatus.ACTIVE, it.status) }
        }
    }

    @Test
    fun `should return all active user's tournaments in which user participates sorted descending by name`() {
        // given
        val pageRequest = PageRequest(0, 10, Sort(Sort.Direction.DESC, "name"))

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getActiveTournamentsInWhichUserParticipates(userId, pageRequest, null)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(6, totalElements)
            Assert.assertEquals(1, totalPages)
            for (i in 0..4) {
                for (j in i + 1..5) {
                    Assert.assertTrue(content[i].name > content[j].name)
                }
            }
            content.forEach { Assert.assertEquals(TournamentStatus.ACTIVE, it.status) }
        }
    }

    @Test
    fun `should return all active user's tournaments in which user participates in two pages sorted ascending by name`() {
        // given
        val pageRequest = PageRequest(0, 10, Sort("name"))

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getActiveTournamentsInWhichUserParticipates(userId, pageRequest, null)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(6, totalElements)
            Assert.assertEquals(1, totalPages)
            for (i in 0..4) {
                for (j in i + 1..5) {
                    Assert.assertTrue(content[i].name < content[j].name)
                }
            }
            content.forEach { Assert.assertEquals(TournamentStatus.ACTIVE, it.status) }
        }
    }

    @Test
    fun `should return one active user's tournaments in which user participates by query`() {
        // given
        val pageRequest = PageRequest(0, 10)
        val tournamentToFindName = "in est culpa tempor non consequat"
        val query = tournamentToFindName.substring(1, 11)

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getActiveTournamentsInWhichUserParticipates(userId, pageRequest, query)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(1, totalElements)
            Assert.assertEquals(1, totalPages)
            Assert.assertEquals(TournamentStatus.ACTIVE, content[0].status)
            Assert.assertEquals(tournamentToFindName, content[0].name)
        }
    }


    /**
     * getFinishedTournamentsInWhichUserParticipates
     */

    @Test
    fun `should return all finished user's tournaments in which user participates in one page`() {
        // given
        val pageRequest = PageRequest(0, 10)

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getFinishedTournamentsInWhichUserParticipates(userId, pageRequest, null)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(5, totalElements)
            Assert.assertEquals(5, numberOfElements)
            Assert.assertEquals(1, totalPages)
            content.forEach { Assert.assertEquals(TournamentStatus.FINISHED, it.status) }
        }
    }

    @Test
    fun `should return one page finished user's tournament in which user participates and information about two available pages`() {
        // given
        val pageRequest = PageRequest(1, 4)

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getFinishedTournamentsInWhichUserParticipates(userId, pageRequest, null)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(5, totalElements)
            Assert.assertEquals(1, numberOfElements)
            Assert.assertEquals(2, totalPages)
            Assert.assertFalse(isFirst)
            content.forEach { Assert.assertEquals(TournamentStatus.FINISHED, it.status) }
        }
    }

    @Test
    fun `should return all finished user's tournaments in which user participates sorted descending by name`() {
        // given
        val pageRequest = PageRequest(0, 10, Sort(Sort.Direction.DESC, "name"))

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getFinishedTournamentsInWhichUserParticipates(userId, pageRequest, null)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(5, totalElements)
            Assert.assertEquals(1, totalPages)
            for (i in 0..3) {
                for (j in i + 1..4) {
                    Assert.assertTrue(content[i].name > content[j].name)
                }
            }
            content.forEach { Assert.assertEquals(TournamentStatus.FINISHED, it.status) }
        }
    }

    @Test
    fun `should return all finished user's tournaments in which user participates in two pages sorted ascending by name`() {
        // given
        val pageRequest = PageRequest(0, 10, Sort("name"))

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getFinishedTournamentsInWhichUserParticipates(userId, pageRequest, null)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(5, totalElements)
            Assert.assertEquals(1, totalPages)
            for (i in 0..3) {
                for (j in i + 1..4) {
                    Assert.assertTrue(content[i].name < content[j].name)
                }
            }
            content.forEach { Assert.assertEquals(TournamentStatus.FINISHED, it.status) }
        }
    }

    @Test
    fun `should return one finished user's tournaments in which user participates by query`() {
        // given
        val pageRequest = PageRequest(0, 10)
        val tournamentToFindName = "dolore Lorem non commodo cupidatat non"
        val tournamentToFindDescription = "Occaecat deserunt occaecat consequat pariatur consectetur qui elit veniam nostrud veniam magna ex duis."
        val query = tournamentToFindDescription.substring(5)

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getFinishedTournamentsInWhichUserParticipates(userId, pageRequest, query)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(1, totalElements)
            Assert.assertEquals(1, totalPages)
            Assert.assertEquals(tournamentToFindName, content[0].name)
            Assert.assertEquals(tournamentToFindDescription, content[0].description)
            content.forEach { Assert.assertEquals(TournamentStatus.FINISHED, it.status) }
        }
    }


    /**
     * getActiveNewestTournamentsInWhichUserDoesNotParticipate
     */

    @Test
    fun `should return public newest active tournaments in which user does not participate (ordered by start date desc)`() {
        // given
        val pageRequest = PageRequest(0, 10)

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getActiveNewestTournamentsInWhichUserDoesNotParticipate(userId, pageRequest)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(4, totalElements)
            Assert.assertEquals(4, numberOfElements)
            Assert.assertEquals(1, totalPages)
            content.forEach { Assert.assertEquals(TournamentStatus.ACTIVE, it.status) }
        }
    }

    @Test
    fun `should return valid second (last) page with oldest active tournament in which user does not participate`() {
        // given
        val firstPageRequest = PageRequest(0, 3)
        val lastPageRequest = PageRequest(1, 3)

        // when
        val firstTournamentsPage = tournamentPreviewsFetcher.getActiveNewestTournamentsInWhichUserDoesNotParticipate(userId, firstPageRequest)
        val lastTournamentsPage = tournamentPreviewsFetcher.getActiveNewestTournamentsInWhichUserDoesNotParticipate(userId, lastPageRequest)

        // then
        firstTournamentsPage.apply {
            Assert.assertEquals(4, totalElements)
            Assert.assertEquals(3, numberOfElements)
            Assert.assertEquals(2, totalPages)
        }


        lastTournamentsPage.apply {
            Assert.assertEquals(4, totalElements)
            Assert.assertEquals(1, numberOfElements)
            Assert.assertEquals(2, totalPages)
            Assert.assertTrue(isLast)
            Assert.assertEquals(TournamentStatus.ACTIVE, content[0].status)
        }
    }

    @Test
    fun `should return empty page for 'too large' page size request for newest tournaments`() {
        // given
        val pageRequest = PageRequest(3, 20)

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getActiveNewestTournamentsInWhichUserDoesNotParticipate(userId, pageRequest)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(4, totalElements)
            Assert.assertEquals(0, numberOfElements)
            Assert.assertEquals(1, totalPages)
        }
    }

    /**
     * getActiveMostPopularTournamentsInWhichUserDoesNotParticipate
     */

    @Test
    fun `should return public most popular active tournaments in which user does not participate (ordered by joined users amount desc)`() {
        // given
        val pageRequest = PageRequest(0, 10)

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getActiveMostPopularTournamentsInWhichUserDoesNotParticipate(userId, pageRequest)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(4, totalElements)
            Assert.assertEquals(4, numberOfElements)
            Assert.assertEquals(1, totalPages)
            (0..2).forEach { i ->
                (i + 1..3).forEach { j ->
                    Assert.assertTrue(content[i].users >= content[j].users)
                }
            }
            content.forEach { Assert.assertEquals(TournamentStatus.ACTIVE, it.status) }
        }
    }

    @Test
    fun `should return valid second (last) page with the least popular active tournament in which user does not participate`() {
        // given
        val firstPageRequest = PageRequest(0, 3)
        val lastPageRequest = PageRequest(1, 3)

        // when
        val firstTournamentsPage = tournamentPreviewsFetcher.getActiveMostPopularTournamentsInWhichUserDoesNotParticipate(userId, firstPageRequest)
        val lastTournamentsPage = tournamentPreviewsFetcher.getActiveMostPopularTournamentsInWhichUserDoesNotParticipate(userId, lastPageRequest)

        // then
        firstTournamentsPage.apply {
            Assert.assertEquals(4, totalElements)
            Assert.assertEquals(3, numberOfElements)
            Assert.assertEquals(2, totalPages)
            content.forEach { Assert.assertTrue(it.users > lastTournamentsPage.content[0].users) }
        }

        lastTournamentsPage.apply {
            Assert.assertEquals(4, totalElements)
            Assert.assertEquals(1, numberOfElements)
            Assert.assertEquals(2, totalPages)
            Assert.assertTrue(isLast)

            Assert.assertEquals(TournamentStatus.ACTIVE, content[0].status)
        }
    }

    @Test
    fun `should return empty page for 'too large' page size request for the most popular tournaments`() {
        // given
        val pageRequest = PageRequest(3, 20)

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getActiveMostPopularTournamentsInWhichUserDoesNotParticipate(userId, pageRequest)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(4, totalElements)
            Assert.assertEquals(0, numberOfElements)
            Assert.assertEquals(1, totalPages)
        }
    }


    /**
     * getActiveAlmostEndedTournamentsInWhichUserDoesNotParticipate
     */

    @Test
    fun `should return public the nearest end active tournaments in which user does not participate (ordered by date asc)`() {
        // given
        val pageRequest = PageRequest(0, 10)

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getActiveAlmostEndedTournamentsInWhichUserDoesNotParticipate(userId, pageRequest)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(4, totalElements)
            Assert.assertEquals(4, numberOfElements)
            Assert.assertEquals(1, totalPages)
            (0..2).forEach { i ->
                (i + 1..3).forEach { j ->
                    Assert.assertTrue(content[i].endDate!! < content[j].endDate)
                }
            }
            content.forEach { Assert.assertEquals(TournamentStatus.ACTIVE, it.status) }
        }
    }

    @Test
    fun `should return valid second (last) page with the latest end date active tournament in which user does not participate`() {
        // given
        val firstPageRequest = PageRequest(0, 3)
        val lastPageRequest = PageRequest(1, 3)

        // when
        val firstTournamentsPage = tournamentPreviewsFetcher.getActiveAlmostEndedTournamentsInWhichUserDoesNotParticipate(userId, firstPageRequest)
        val lastTournamentsPage = tournamentPreviewsFetcher.getActiveAlmostEndedTournamentsInWhichUserDoesNotParticipate(userId, lastPageRequest)

        // then
        firstTournamentsPage.apply {
            Assert.assertEquals(4, totalElements)
            Assert.assertEquals(3, numberOfElements)
            Assert.assertEquals(2, totalPages)
            content.forEach { Assert.assertTrue(it.endDate!! < lastTournamentsPage.content[0].endDate) }
        }

        lastTournamentsPage.apply {
            Assert.assertEquals(4, totalElements)
            Assert.assertEquals(1, numberOfElements)
            Assert.assertEquals(2, totalPages)
            Assert.assertTrue(isLast)
            Assert.assertEquals(TournamentStatus.ACTIVE, content[0].status)
        }
    }

    @Test
    fun `should return empty page for 'too large' page size request for the nearest end tournaments`() {
        // given
        val pageRequest = PageRequest(3, 20)

        // when
        val foundTournamentsPage = tournamentPreviewsFetcher.getActiveAlmostEndedTournamentsInWhichUserDoesNotParticipate(userId, pageRequest)

        // then
        foundTournamentsPage.apply {
            Assert.assertEquals(4, totalElements)
            Assert.assertEquals(0, numberOfElements)
            Assert.assertEquals(1, totalPages)
        }
    }


    inner class MockAuthModule : AuthModuleInterface {
        override fun getToken() = throw UnsupportedOperationException("This mock shouldn't call this method")

        override fun getUser(token: String) = throw UnsupportedOperationException("This mock shouldn't call this method")

        override fun getUserFromRequest(user: User) = throw UnsupportedOperationException("This mock shouldn't call this method")

        override fun getUserNameById(id: Long) = "Test Owner"
    }
}