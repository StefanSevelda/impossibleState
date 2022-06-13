package domain2

import arrow.core.computations.ResultEffect.bind
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class DomainTest {

    @Nested
    inner class AgeTest {

        @Test
        fun `create invalid age`() = runBlocking<Unit> {
            val birthDateInFuture = LocalDate.now().plusYears(20)
            val actual = FullLegalAge.build(birthDateInFuture)
            actual.shouldBeLeft(Error(ErrorType.InvalidBirthDate))
        }

        @Test
        fun `create valid age`() = runBlocking<Unit> {
            val birthday = LocalDate.now().minusYears(30)
            val actual = FullLegalAge.build(birthday)
            actual shouldBeRight FullLegalAge(birthday)
        }
    }

    @Nested
    inner class Signup {

        @Test
        fun `Invalid signup`() = runBlocking<Unit> {
            // given
            val firstName: String = "Stefan"
            val lastName: String = "Sevelda"
            val email = null
            val phoneNumber = null
            val birthday = LocalDate.now().plusYears(30)

            // when
            val actual = signup(CreateCustomerRequest(firstName, lastName, birthday, email, phoneNumber))

            // then
            actual shouldBeLeft Error(ErrorType.InvalidBirthDate)
        }

        @Test
        fun `Success signup`() = runBlocking<Unit> {
            // given
            val firstName: String = "Stefan"
            val lastName: String = "Sevelda"
            val email = null
            val phoneNumber = null
            val birthday = LocalDate.now().minusYears(30)

            // when
            val actual = signup(CreateCustomerRequest(firstName, lastName, birthday, email, phoneNumber)).bind()

            // then
            actual.payload.customer shouldBe Customer(
                firstName,
                lastName,
                FullLegalAge(birthday),
                email,
                phoneNumber
            )
        }

    }
}