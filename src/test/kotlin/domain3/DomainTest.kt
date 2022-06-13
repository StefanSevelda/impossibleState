package domain3

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
    inner class PhoneTest {

        @Test
        fun `create invalid phone number`() = runBlocking<Unit> {
            val actual = PhoneNumber.build("not a phone number")
            actual shouldBeLeft Error(ErrorType.InvalidPhoneNumber)
        }

        @Test
        fun `create valid phone number`() = runBlocking<Unit> {
            val phoneNumber = "+43 650 500 50 55"
            val actual = PhoneNumber.build(phoneNumber)
            actual shouldBeRight PhoneNumber(phoneNumber)
        }
    }

    @Nested
    inner class EmailTest {

        @Test
        fun `create invalid email`() = runBlocking<Unit> {
            val actual = Email.build("not_a_email")
            actual shouldBeLeft Error(ErrorType.InvalidEmail)
        }

        @Test
        fun `create valid email`() = runBlocking<Unit> {
            val email = "stefan.sevelda@gmail.com"
            val actual = Email.build(email)
            actual shouldBeRight Email(email)
        }
    }

    @Nested
    inner class AggregatedContactInfoTest {

        @Test
        fun `no contact information provided`() = runBlocking<Unit> {
            val email = null
            val phoneNumber = null
            val actual = ContactInfo.build(email, phoneNumber)
            actual shouldBeLeft Error(ErrorType.NoContactData)
        }

        @Test
        fun `email and phone number provided`() = runBlocking<Unit> {
            val email = "stefan.sevelda@gmail.com"
            val phoneNumber = "+43 650 500 50 50"
            val actual = ContactInfo.build(email, phoneNumber)
            actual shouldBeRight PhoneAndEmail(Email(email), PhoneNumber(phoneNumber))
        }
    }

    @Nested
    inner class Signup {

        @Test
        fun `signup domain model final`() = runBlocking<Unit> {
            // given
            val firstName: String = "Stefan"
            val lastName: String = "Sevelda"
            val email = "stefan.sevelda@gmail.com"
            val phoneNumber = "+43 650 500 55 55"
            val birthday = LocalDate.now().minusYears(30)

            // when
            val actual = signup(CreateCustomerRequest(firstName, lastName, birthday, email, phoneNumber)).bind()

            // then
            actual.payload.customer shouldBe Customer(
                firstName,
                lastName,
                FullLegalAge(birthday),
                PhoneAndEmail(Email(email), PhoneNumber(phoneNumber))
            )
        }

    }
}