package pl.cyganki.tournament.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.cyganki.tournament.EArbiterTournamentApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EArbiterTournamentApplication.class, loader = SpringBootContextLoader.class)
@ActiveProfiles("test")
public class HashingServiceTest {

    @Autowired
    private HashingService hashingService;

    @Test
    public void shouldMatchHashedPassword() {
        // given
        String hashedPassword;

        // when
        hashedPassword = hashingService.getSecurePassword(TestData.PASSWORD, TestData.SALT);

        // then
        assertEquals(hashingService.getSecurePassword(TestData.PASSWORD, TestData.SALT), hashedPassword);
    }

    @Test
    public void shouldNotMatchDifferentHashedPassword() {
        // given
        String hashedPassword;
        String difference = "diff";

        // when
        hashedPassword = hashingService.getSecurePassword(TestData.PASSWORD, TestData.SALT);

        // then
        assertNotEquals(hashingService.getSecurePassword(TestData.PASSWORD + difference, TestData.SALT), hashedPassword);
    }

    @Test
    public void shouldCheckPassword() {
        // given
        String hashedPassword;

        // when
        hashedPassword = hashingService.getSecurePassword(TestData.PASSWORD, TestData.SALT);

        // then
        assertEquals(hashingService.checkPassword(TestData.PASSWORD, hashedPassword, TestData.SALT), true);
    }

    @Test
    public void shouldCheckDifferentPassword() {
        // given
        String hashedPassword;
        String difference = "diff";

        // when
        hashedPassword = hashingService.getSecurePassword(TestData.PASSWORD, TestData.SALT);

        // then
        assertEquals(hashingService.checkPassword(TestData.PASSWORD + difference, hashedPassword, TestData.SALT), false);
    }

    @Test
    public void shouldMatchTwoSameHashes() {
        // given
        String firstHash;
        String secondHash;

        // when
        firstHash = hashingService.getSecurePassword(TestData.PASSWORD, TestData.SALT);
        secondHash = hashingService.getSecurePassword(TestData.PASSWORD, TestData.SALT);

        // then
        assertEquals(firstHash, secondHash);
    }

    private static class TestData {
        final static String PASSWORD = "Test123";
        final static byte[] SALT = new byte[] {1, 2, 3, 4};
    }
}
