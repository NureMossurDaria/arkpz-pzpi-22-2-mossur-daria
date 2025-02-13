package ua.nure.mossurd.blooddonation.request.model

data class LoginResponseDto(
    val name: String,
    val token: String,
    val bloodType: String,
    val rhesusFactor: Boolean
)