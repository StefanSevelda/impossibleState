import java.util.Date

/** Requirements:
 * - customer needs to have a firstName and a lastName
 * - customer needs to be older than 18 years
 * - customer needs to provide either a valid email or phone number or both
 * - email or phone number needs to be verified
  */


data class Customer(
    val firstName: String,
    val lastName: String,
    val birthDate: Date,
    val email: String?,
    val phoneNumber: String?
)

//// Wednesday, March 21, 2040 9:41:37 AM
val birthdate = Date(2215935697000)

Customer(
    firstName = "Max",
    lastName = "Mustermann",
    birthDate = birthdate,
    email = null,
    phoneNumber = null
)
