import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Date

/** Requirements:
 * - customer needs to provide either a valid email or phone number or both
 */

enum class ErrorType {

    InvalidBirthDate, InvalidEmail, InvalidPhoneNumber, NoContactData
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


sealed class ContactInfo

data class PhoneNumber(val phoneNumber: String): ContactInfo() {
    companion object {
        private val regex = Regex("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*\$")
        fun validate(phoneNumber: String): Either<Error, PhoneNumber> =
            if (regex.matches(phoneNumber))
                PhoneNumber(phoneNumber).right()
            else
                Error(ErrorType.InvalidPhoneNumber).left()
    }
}

data class Email(val email: String): ContactInfo() {
    companion object {
        private val regex = Regex("(^[A-Z0-9a-z._%+-]+)(@[A-Za-z0-9.-]+\\.[A-Za-z]+)")
        fun validate(email: String): Either<Error, Email> = if (regex.matches(email))
            Email(email).right()
        else
            Error(ErrorType.InvalidEmail).left()
    }
}

data class PhoneAndEmail(val email: Email, val phoneNumber: PhoneNumber): ContactInfo()

fun classifyContactData(
    phone: PhoneNumber?,
    email: Email?
): Either<Error, ContactInfo> = when {
    phone != null && email != null -> PhoneAndEmail(email, phone).right()
    phone != null -> {
        phone.right()
    }
    email != null -> {
        email.right()
    }
    else -> Error(ErrorType.NoContactData).left()
}

suspend fun validateContactInfo(emailAddress: String?, phoneNumber: String?): Either<Error, ContactInfo> = either {
    val phone: PhoneNumber? = phoneNumber?.let { PhoneNumber.validate(it).bind() }
    val email: Email? = emailAddress?.let { Email.validate(it).bind() }
    classifyContactData(phone, email).bind()
}

data class Customer(
    val firstName: String,
    val lastName: String,
    val birthDate: FullLegalAge,
    val contactInfo: ContactInfo?,
)

//// Thursday, March 21, 1991 9:41:37 AM - 669548497000
val birthdate = LocalDate.of(1991, 3, 26)
val someEmail = "max.mustermann@n26.com"
//val phoneNumber: String = "not a phone number"
val phoneNumber: String = "+43 650 500 33 08"

suspend fun validate(): Either<Error, Customer> = either {
    val birthDated = FullLegalAge.validate(birthdate).bind()
    val contactInfo = validateContactInfo(someEmail, phoneNumber).bind()
    Customer(
        firstName = "Max",
        lastName = "Mustermann",
        birthDate = birthDated,
        contactInfo = contactInfo
    )
}

runBlocking {
    val result = validate()
    println(result)
}
