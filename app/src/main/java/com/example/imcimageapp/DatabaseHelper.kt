package com.example.imcimageapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "userDatabase.db"

        // Tabla de usuarios
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_APELLIDO = "apellido"
        const val COLUMN_GENERO = "genero"
        const val COLUMN_EDAD = "edad"

        // Tabla de registros de IMC
        const val TABLE_IMC_RECORDS = "imc_records"
        const val COLUMN_RECORD_ID = "record_id"
        const val COLUMN_USER = "username"
        const val COLUMN_IMC = "imc"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla de usuarios
        val createUserTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_NOMBRE TEXT,
                $COLUMN_APELLIDO TEXT,
                $COLUMN_GENERO TEXT,
                $COLUMN_EDAD INTEGER
            )
        """
        db.execSQL(createUserTable)

        // Crear tabla de registros de IMC
        val createImcTable = """
            CREATE TABLE $TABLE_IMC_RECORDS (
                $COLUMN_RECORD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER TEXT,
                $COLUMN_IMC REAL,
                $COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_USER) REFERENCES $TABLE_USERS($COLUMN_USERNAME)
            )
        """
        db.execSQL(createImcTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_IMC_RECORDS")
        onCreate(db)
    }

    // Método para registrar un usuario con datos adicionales
    fun registerUser(username: String, password: String, nombre: String, apellido: String, genero: String, edad: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_APELLIDO, apellido)
            put(COLUMN_GENERO, genero)
            put(COLUMN_EDAD, edad)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    // Método para validar el usuario (inicio de sesión)
    fun validateUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))

        val isValidUser = cursor.count > 0
        cursor.close()
        db.close()
        return isValidUser
    }

    // Método para registrar un cálculo de IMC para un usuario
    fun saveImcRecord(username: String, imc: Float) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER, username)
            put(COLUMN_IMC, imc)
        }
        db.insert(TABLE_IMC_RECORDS, null, values)
        db.close()
    }

    // Método para obtener los últimos 5 cálculos de IMC de un usuario
    fun getLastFiveImcRecords(username: String): List<Double> {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_IMC_RECORDS,
            arrayOf(COLUMN_IMC),
            "$COLUMN_USER = ?",
            arrayOf(username),
            null,
            null,
            "$COLUMN_TIMESTAMP DESC",
            "5"
        )

        val imcRecords = mutableListOf<Double>()
        if (cursor.moveToFirst()) {
            do {
                imcRecords.add(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_IMC)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return imcRecords
    }
}
