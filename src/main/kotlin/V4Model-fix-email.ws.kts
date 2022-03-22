import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Date

/** Requirements:
 * - customer needs to have a firstName and a lastName
 * - customer needs to be older than 18 years
 * - customer needs to provide either a valid email or phone number or both
 * - email or phone number needs to be verified
 */

enum class ErrorType {
    InvalidBirthDate, InvalidEmail
}

data class Error(val errorType: ErrorType) : Throwable()

data class FullLegalAge(val birthDate: LocalDate) {
    companion object {

        fun validate(birthDate: LocalDate): Either<Error, FullLegalAge> {
            val fullLegalAgeThreshold: LocalDate = LocalDate.now().minus(18, ChronoUnit.YEARS)
            return if (birthDate.isBefore(fullLegalAgeThreshold)) {
                FullLegalAge(birthDate).right()
            } else
                Error(ErrorType.InvalidBirthDate).left()
        }
    }
}

data class Email(val email: String) {
    companion object {
        private val regex = Regex("(^[A-Z0-9a-z._%+-]+)(@[A-Za-z0-9.-]+\\.[A-Za-z]+)")
        fun validate(email: String): Either<Error, Email> = if(regex.matches(email))
            Email(email).right()
        else
            Error(ErrorType.InvalidEmail).left()
    }
}

data class Customer(
    val firstName: String,
    val lastName: String,
    val birthDate: FullLegalAge,
    val email: Email?,
    val phoneNumber: String?
)

//// Thursday, March 21, 1991 9:41:37 AM - 669548497000
val birthdate = LocalDate.of(1991, 3, 26)
val someEmail: String = "wrong_email.com"
//val someEmail = "max.musterman@gmail.com"

suspend fun validate(): Either<Error, Customer> = either {
    val birthDated = FullLegalAge.validate(birthdate).bind()
    val email  = someEmail?.let { Email.validate(it).bind() }
    Customer(
        firstName = "Max",
        lastName = "Mustermann",
        birthDate = birthDated,
        email = email,
        phoneNumber = null
    )
}

runBlocking {
    val result = validate()
    println(result)
}
