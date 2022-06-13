package domain1

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class DomainTest {
    @Test
    fun `signup`() = runBlocking<Unit> {
        // given
        val firstName: String = "Stefan"
        val lastName: String = "Sevelda"
        val email = null
        val phoneNumber = null
        val birthday = LocalDate.now().minusYears(30)

        // when
        val actual = signup(CreateCustomerRequest(firstName, lastName, birthday, email, phoneNumber))

        // then
        actual.payload.customer shouldBe Customer(
            firstName,
            lastName,
            birthday,
            email,
            phoneNumber
        )
    }
}