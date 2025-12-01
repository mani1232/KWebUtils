package cc.worldmandia.database.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object User {
    const val MAX_VARCHAR_LENGTH = 50

    object Table : IntIdTable("Users") {
        val name = varchar("name", MAX_VARCHAR_LENGTH)
        val bio = text("bio")
        val age = integer("age")
        val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    }

    @Serializable
    data class UserDto(
        val id: Int,
        val name: String,
        val bio: String,
        val age: Int,

        @SerialName("created_at")
        val createdAt: String
    )

    fun ResultRow.toUserDto(): UserDto {
        return UserDto(
            id = this[Table.id].value,
            name = this[Table.name],
            bio = this[Table.bio],
            age = this[Table.age],
            createdAt = this[Table.createdAt].toString()
        )
    }
}