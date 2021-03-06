package pl.cyganki.tournament.testutils

import pl.cyganki.utils.modules.AuthModuleInterface
import pl.cyganki.utils.security.dto.User

class MockAuthModule : AuthModuleInterface {
    override fun getToken() = throw UnsupportedOperationException("This mock shouldn't call this method")

    override fun getUser(token: String) = throw UnsupportedOperationException("This mock shouldn't call this method")

    override fun getUserFromRequest(user: User) = throw UnsupportedOperationException("This mock shouldn't call this method")

    override fun getUserNameById(id: Long) = "Test Owner"

    override fun getAllUserNamesAndEmails()= throw UnsupportedOperationException("This mock shouldn't call this method")

    override fun getUserNamesAndEmailsByIds(usersIds: Array<Long>)= throw UnsupportedOperationException("This mock shouldn't call this method")

    override fun getUserNamesByIds(usersIds: Array<Long>) = mapOf<Long, String>()
}