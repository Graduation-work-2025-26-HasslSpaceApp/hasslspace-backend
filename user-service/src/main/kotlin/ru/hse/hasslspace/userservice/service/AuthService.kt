package ru.hse.hasslspace.userservice.service

import feign.FeignException
import io.jsonwebtoken.security.Password
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hse.hasslspace.userservice.dto.EmailRequest
import ru.hse.hasslspace.userservice.dto.LoginUserDto
import ru.hse.hasslspace.userservice.dto.RegisterUserDto
import ru.hse.hasslspace.userservice.model.User
import ru.hse.hasslspace.userservice.model.Verification
import ru.hse.hasslspace.userservice.repository.UserRepository
import ru.hse.hasslspace.userservice.repository.VerificationRepository
import ru.hse.hasslspace.userservice.service.notifier.NotifierService
import java.time.LocalDateTime
import java.util.*
import kotlin.io.encoding.ExperimentalEncodingApi

@Service
@ExperimentalEncodingApi
class AuthService(
    private val userRepository: UserRepository,
    private val verificationRepository: VerificationRepository,
    private val notifierService: NotifierService,
    private val passwordEncoder: PasswordEncoder,
    private val defaultUserDetailsService: NewUserDetailsService,
    private val jwtService: JwtService
) {

    @Transactional
    fun registerUser(registerUserDto: RegisterUserDto): ResponseEntity<String> {
        return try {
            if (userRepository.existsByEmail(registerUserDto.email)) {
                logger.error("User with email ${registerUserDto.email} already exists")
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь с таким email уже существует")
            }

            if (userRepository.existsByUsername(registerUserDto.username)) {
                logger.error("User with username ${registerUserDto.username} already exists")
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Пользователь с таким username уже существует")
            }

            val encodedPassword = passwordEncoder.encode(registerUserDto.password)

            val savedUser = userRepository.save(
                User(
                    email = registerUserDto.email,
                    username = registerUserDto.username,
                    name = registerUserDto.username, // TODO: уточнить
                    password = encodedPassword
                )
            )

            val jwt = jwtService.generateToken((savedUser ?: throw Exception("User not found")))

            logger.info("User with email ${registerUserDto.email} successfully registered")

            sendVerificationCode(savedUser.id!!, savedUser.email, savedUser.username)

            ResponseEntity.status(HttpStatus.CREATED).body(jwt)
        } catch (e: Exception) {
            logger.error("Error while registering user", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при регистрации пользователя")
        }
    }

    @Transactional
    fun loginUser(email: String, password: String): ResponseEntity<String> {
        return try {
            val userDetails = defaultUserDetailsService.loadUserByEmail(email)

            if (!passwordEncoder.matches(password, userDetails.password)) {
                logger.error("Invalid password for email $email")
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неверный пароль")
            }

            val jwt = jwtService.generateToken(userDetails as User)

            logger.info("User with email $email successfully logged in")
            ResponseEntity.status(HttpStatus.OK).body(jwt)
        } catch (e: Exception) {
            logger.error("Error while logging in user", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при входе пользователя")
        }
    }

    @Transactional
    fun sendVerificationCode(userId: UUID, email: String, name: String): ResponseEntity<String> {
        return try {
            val verificationCode = (100_000..999_999).random().toString()

            verificationRepository.save(
                Verification(
                    userId = userId,
                    code = passwordEncoder.encode(verificationCode).toString(),
                    createdAt = LocalDateTime.now()
                )
            )

            val sendEmailResponse = notifierService.send(
                EmailRequest(
                    subject = "Код подтверждения для HasslSpace",
                    targetEmail = email,
                    text = verificationCode,
                    name = name
                )
            )
            if (!sendEmailResponse.statusCode.is2xxSuccessful) {
                logger.error("Failed to send email to $email: ${sendEmailResponse.body}")
                return ResponseEntity.status(sendEmailResponse.statusCode)
                    .body(sendEmailResponse.body ?: "Ошибка при отправке письма с кодом подтверждения")
            }

            logger.info("Verification code sent to $email")
            ResponseEntity.status(HttpStatus.OK).body("Код подтверждения отправлен на $email")
        } catch (e: FeignException) {
            logger.error("Feign error while sending verification code: ${e.status()}, ${e.contentUTF8()}", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.contentUTF8())
        } catch (e: Exception) {
            logger.error("Error while sending verification code", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при отправке кода подтверждения")
        }
    }

    @Transactional
    fun verifyCode(user: User, code: String): ResponseEntity<String> {
        return try {
            val verification = verificationRepository.findByUserId(user.id!!)
                ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Код подтверждения не найден")

            if (user.isVerified == true) {
                logger.info("User with ID ${user.id} is already verified")
                return ResponseEntity.status(HttpStatus.OK).body("Пользователь уже верифицирован")
            }

            if (!passwordEncoder.matches(code, verification.code)) {
                logger.error("Invalid verification code for user ID ${user.id}")
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неверный код подтверждения")
            }

            userRepository.save(user.apply { isVerified = true })

            logger.info("User with ID ${user.id} successfully verified")
            ResponseEntity.status(HttpStatus.OK).body("Пользователь успешно верифицирован")
        } catch (e: Exception) {
            logger.error("Error while verifying code", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при проверке кода подтверждения")
        }
    }

    fun isVerified(user: User): ResponseEntity<Boolean> {
        return try {
            ResponseEntity.status(HttpStatus.OK).body(user.isVerified == true)
        } catch (e: Exception) {
            logger.error("Error while checking if user is verified", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthService::class.java)
    }
}