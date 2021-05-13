package com.tasksweeper.service

import com.tasksweeper.entities.AccountDTO
import com.tasksweeper.entities.StatusDTO
import com.tasksweeper.exceptions.InvalidCredentialsException
import com.tasksweeper.exceptions.InvalidEmailException
import com.tasksweeper.exceptions.InvalidUsernameException
import com.tasksweeper.repository.AccountRepository
import com.tasksweeper.repository.Account_StatusRepository
import com.tasksweeper.repository.StatusRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mindrot.jbcrypt.BCrypt
import java.util.regex.Pattern

class AccountService : KoinComponent {

    private val accountRepository: AccountRepository by inject()
    private val account_statusRepository: Account_StatusRepository by inject()

    val HP: Pair<String, Int> = Pair("Health", 5)
    val GOLD: Pair<String, Int> = Pair("Gold", 0)
    val EXP: Pair<String, Int> = Pair("Experience", 0)

    val statusAndValue: List<Pair<String, Int>> = listOf(HP, GOLD, EXP)


    private val usernamePattern = Pattern.compile(
        "^(?=[a-zA-Z0-9._]{4,20}\$)(?!.*[_.]{2})[^_.].*[^_.]\$"
    )
    private val emailPattern = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    suspend fun registerAccount(
        accountUsername: String,
        accountEmail: String,
        accountPassword: String,
        level: Int
    ): AccountDTO {
        if (!usernamePattern.matcher(accountUsername).matches()) throw InvalidUsernameException(accountUsername)
        if (!emailPattern.matcher(accountEmail).matches()) throw InvalidEmailException(accountEmail)

        val account: AccountDTO = accountRepository.insertAccount(
            accountUsername,
            accountEmail,
            BCrypt.hashpw(accountPassword, BCrypt.gensalt()),
            level
        )

        for (status in statusAndValue) {
            account_statusRepository.insertAccount_Status(accountUsername, status.first, status.second)
        }

        return account
    }

    suspend fun checkAccount(accountUsername: String, accountPassword: String): AccountDTO =
        getAccount(accountUsername).also {
            if (!BCrypt.checkpw(accountPassword, it.password))
                throw InvalidCredentialsException()
        }

    suspend fun getAccount(accountUsername: String): AccountDTO =
        accountRepository.selectAccount(accountUsername)
}
