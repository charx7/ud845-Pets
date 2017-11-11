package com.example.android.pets.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Metodo helper extendidos de SQLiteOpenHelper
public class PetDBHelper extends SQLiteOpenHelper{

    //Metodo para el desplegado del log
    public static final String LOG_TAG = PetDBHelper.class.getSimpleName();

    //Constantes que definen el nombre y la version de la BDD
    public static  final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "shelter.db";

    //Constructor de la clase que ocupa el nombre y la version de la BDD
    public PetDBHelper(Context context) {
        //super llama al contructor de la clase padre de la que se extiende, llamamos a su constructor
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    //Implementacion de los metodos sobrescritos de la claee extendida SQLiteOpenHelper
    @Override
    public void onCreate(SQLiteDatabase db) {
        //LLama la syntaxis de sql que Construye las tablas
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Destruye las tablas anteriores
        db.execSQL(SQL_DELETE_ENTRIES);
        //Llama la syntaxis de SQL que crea tablas nuevas
        onCreate(db);
    }

    //Metodo que hace inserciones a la DB
    public void onInsert(SQLiteDatabase db,ContentValues valoresaInsertar){
        //Metodo para insertar los datos en la tabla de Pets teniendo una serie de contentvalues
        //contruidos
        db.insert(PetContract.PetEntry.TABLE_NAME,null,valoresaInsertar);
    }

    //Definiciones de las sentencias de SQL
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PetContract.PetEntry.TABLE_NAME + " (" +
                    PetContract.PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PetContract.PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL," +
                    PetContract.PetEntry.COLUMN_PET_BREED + " TEXT," +
                    PetContract.PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL," +
                    PetContract.PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PetContract.PetEntry.TABLE_NAME;

}
