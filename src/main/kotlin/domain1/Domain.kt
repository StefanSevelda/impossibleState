package domain1

import java.time.LocalDate
import java.util.UUID

data class Customer(
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
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

fun getRiskModel(): RiskModel = RiskModel

enum class RiskScore {
    HIGH, MEDIUM, LOW
}

fun CreateCustomerRequest.build(): Customer {
    return Customer(
        firstName = firstName,
        lastName = lastName,
        birthDate = birthDate,
        email = email,
        phoneNumber = phoneNumber
    )
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

fun publishEvent(customer: Customer, riskScore: RiskScore, emailLink: VerificationLink): EventMetaInformation {
    // publish to event stream
    return EventMetaInformation(UUID.randomUUID(), Payload(customer, riskScore, emailLink))
}

fun signup(request: CreateCustomerRequest): EventMetaInformation {
    val customer = request.build()
    try {
        val linkExpiryTime = getExpiryTime()
        val riskModel = getRiskModel()
        val emailLink = createVerificationLink(customer, linkExpiryTime)
        val riskScore = calculateRiskProfile(customer, riskModel)
        return publishEvent(customer, riskScore, emailLink)
            .also { println(it) }
    } catch (e: Throwable) {
       throw e;
    }
}
