package cc.worldmandia.database.user

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import kotlin.time.Duration.Companion.minutes

class UserRepositoryImpl : UserRepository {
    init {
        runBlocking { // TODO example users
            launch {
                suspendTransaction {
                    if (User.Table.selectAll().limit(1).empty()) {
                        repeat(5) { i ->
                            User.Table.insertAndGetId {
                                it[name] = "User $i"
                                it[age] = (1..18).random()
                                it[bio] = "Some bio for user"
                            }
                            delay(5.minutes)
                        }
                    }
                }
            }
        }
    }

    override suspend fun getAllUsersByName(userName: String) = suspendTransaction {
        User.Table.selectAll().where { User.Table.name eq userName }.toSet()
    }

    override suspend fun getAllUsersBySortType(sortType: SortType): Set<ResultRow> = suspendTransaction {
        when (sortType) {
            SortType.NEW -> User.Table.selectAll().orderBy(User.Table.createdAt, SortOrder.DESC).toSet()
        }
    }
}