import arrow.core.computations.ResultEffect.bind
import arrow.core.computations.either
import domain.CreateCustomerRequest
import domain.Customer
import domain.EmailLink
import domain.EventInformation
import domain.RiskModel
import domain.RiskScore
import domain.createExpiryTime
import domain.getRiskModel
import domain.publishEvent
import domain.validate
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

/** Requirements:
 * - customer needs to provide either a valid email or phone number or both
 */

fun calculateRiskProfile(it: Customer, riskModel: RiskModel): RiskScore {
//    ... use userinformation to validate against risk model ...
    return RiskScore.MEDIUM
}


// no either since it is pure and doesn't have any side effects
fun createEmailLink(customer: Customer, expireTime: LocalDate): EmailLink {
    val value = customer.toString() + expireTime.toString()
    // ... obfuscate ...
    return EmailLink(value)
}

runBlocking {
    either<Error, EventInformation> {
        val request: CreateCustomerRequest = CreateCustomerRequest(
            firstName = "Max",
            lastName = "Mustermann",
            birthDate = LocalDate.of(1991, 3, 26),
            email = "max.mustermann@n26.com",
            phoneNumber = "+43 650 500 33 08"
        )
        /**
         * side effects:
         *  - collect data from different source
         *  - validate user input and convert in to valid Domain model (no impossible state)
         */
        val customer = validate(request).bind()
        val linkExpiryTime = createExpiryTime()
        val riskModel = getRiskModel().bind()

        /**
         * pure domain logic without side effects
          */
        val riskScore = calculateRiskProfile(customer, riskModel)
        val emailLink = createEmailLink(customer, linkExpiryTime)

        /**
         * Side effects:
         * - persist information (by event consumer or outbox pattern)
         * - forward to other services
         * In hex- architecture referred as ports (dependency inversion)
         */
//        persistToDatabase(customer, riskScore, emailLink).bind()
        publishEvent(customer, riskScore, emailLink).bind()
            .also { println(it) }
    }
}


