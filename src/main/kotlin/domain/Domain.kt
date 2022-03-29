package domain

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID


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

data class PhoneNumber(val phoneNumber: String) : ContactInfo() {
    companion object {

        private val regex = Regex("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*\$")
        fun validate(phoneNumber: String): Either<Error, PhoneNumber> =
            if (regex.matches(phoneNumber))
                PhoneNumber(phoneNumber).right()
            else
                Error(ErrorType.InvalidPhoneNumber).left()
    }
}

data class Email(val email: String) : ContactInfo() {
    companion object {

        private val regex = Regex("(^[A-Z0-9a-z._%+-]+)(@[A-Za-z0-9.-]+\\.[A-Za-z]+)")
        fun validate(email: String): Either<Error, Email> = if (regex.matches(email))
            Email(email).right()
        else
            Error(ErrorType.InvalidEmail).left()
    }
}

data class EmailLink(val hash: String)

data class PhoneAndEmail(val email: Email, val phoneNumber: PhoneNumber) : ContactInfo()

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


data class CreateCustomerRequest(
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val email: String?,
    val phoneNumber: String?
)

object RiskModel

fun getRiskModel(): Either<Error, RiskModel> = RiskModel.right()

enum class RiskScore {
    HIGH, MEDIUM, LOW
}

suspend fun validate(createCustomerRequest: CreateCustomerRequest): Either<Error, Customer> = either {
    with(createCustomerRequest) {
        val birthDated = FullLegalAge.validate(birthDate).bind()
        val contactInfo = validateContactInfo(email, phoneNumber).bind()
        Customer(
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDated,
            contactInfo = contactInfo
        )
    }
}



fun getExpiryTime(): LocalDate = LocalDate.now().plusDays(1)


data class EventInformation(
    val partionKey: UUID,
//    val metaInformation: MetaInformation
)

fun publishEvent(customer: Customer, riskScore: RiskScore, emailLink: EmailLink): Either<Error, EventInformation> {
    // publish to event stream
    return EventInformation(UUID.randomUUID()).right()
}
