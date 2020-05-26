package mx.edu.ittepic.ladm_u4_tarea2_contentprovaider_parte1

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(context: Context?,name: String?,factory: SQLiteDatabase.CursorFactory?,version:Int) : SQLiteOpenHelper(context,name,factory,version){
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE ENTRANTES(CELULAR VARCHAR(200),MENSAJE VARCHAR(2000))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}