import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Requirements:
 * - customer needs to have a firstName and a lastName
 * - customer needs to be older than 18 years
 * - customer needs to provide either a valid email or phone number or both
 * - email or phone number needs to be verified
 */

enum class ErrorType {

    InvalidBirthDate
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

data class Customer(
    val firstName: String,
    val lastName: String,
    val birthDate: FullLegalAge,
    val email: String?,
    val phoneNumber: String?
)

//// Wednesday, March 21, 2040 9:41:37 AM
val birthdate = LocalDate.of(2040, 3, 26)
//// Thursday, March 21, 1991 9:41:37 AM - 669548497000
//val birthdate = LocalDate.of(1991, 3, 26)

suspend fun validate(): Either<Error, Customer> = either {
    val birthDated = FullLegalAge.validate(birthdate).bind()
    Customer(
        firstName = "Max",
        lastName = "Mustermann",
        birthDate = birthDated,
        email = null,
        phoneNumber = null
    )
}

runBlocking {
    val result = validate()
    println(result)
}
