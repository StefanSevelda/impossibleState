package domain2

import arrow.core.Either
import arrow.core.continuations.either.invoke
import arrow.core.left
import arrow.core.right
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

enum class ErrorType {
    InvalidBirthDate
}

data class Error(val errorType: ErrorType) : Throwable()

data class FullLegalAge(val birthDate: LocalDate) {
    companion object {

        fun build(birthDate: LocalDate): Either<Error, FullLegalAge> {
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

suspend fun build(createCustomerRequest: CreateCustomerRequest): Either<Error, Customer> = invoke {
    with(createCustomerRequest) {
        val birthDated = FullLegalAge.build(birthDate).bind()
        Customer(
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDated,
            email = email,
            phoneNumber = phoneNumber
        )
    }
}

fun getExpiryTime(): LocalDate = LocalDate.now().plusDays(1)

data class EventMetaInformation(
    val partionKey: UUID,
    val payload: Payload
)

data class VerificationLink(val hash: String)

data class Payload(val customer: Customer, val riskScore: RiskScore, val link: VerificationLink)

fun calculateRiskProfile(it: Customer, riskModel: RiskModel): RiskScore {
//    ... use userinformation to validate against risk model ...
    return RiskScore.MEDIUM
}

// no either since it is pure and doesn't have any side effects
fun createVerificationLink(customer: Customer, expireTime: LocalDate): VerificationLink {
    val value = customer.toString() + expireTime.toString()
    // ... obfuscate ...
    return VerificationLink(value)
}

fun publishEvent(customer: Customer, riskScore: RiskScore, emailLink: VerificationLink): Either<Error, EventMetaInformation> {
    // publish to event stream
    return EventMetaInformation(UUID.randomUUID(), Payload(customer, riskScore, emailLink)).right()
}

suspend fun signup(request: CreateCustomerRequest): Either<Error, EventMetaInformation> = invoke {
    /**
     * side effects
     */
    val customer = build(request).bind()
    val linkExpiryTime = getExpiryTime()
    val riskModel = getRiskModel().bind()

    /**
     * pure domain logic without side effects
     */
    val emailLink = createVerificationLink(customer, linkExpiryTime)
    val riskScore = calculateRiskProfile(customer, riskModel)

    /**
     * Side effects
     */
//        persistToDatabase(customer, riskScore, emailLink).bind()
    publishEvent(customer, riskScore, emailLink).bind()
        .also { println(it) }
}
