package com.example.doanckapi.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_table")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val Age: Float = 0f,
    val Income: Float = 0f,
    val BMI: Float = 0f,
    val HighBP: Float = 0f,
    val HighChol: Float = 0f,
    val HeartDiseaseorAttack: Float = 0f,
    val Stroke: Float = 0f,
    val DiffWalk: Float = 0f,
    val Sex: Int = -1,
    val Pregnancies: Int = 0
)
