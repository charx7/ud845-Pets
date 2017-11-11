package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class PetContract {

    //Constructor vacio
    private PetContract(){};

    //Metodo que ayuda a definir los parametros de contracto de la BDD
    public static abstract class PetEntry implements BaseColumns{
        //Definimos el nombre de la tabla en el contrato
        public static final String TABLE_NAME = "pets";
        //Definimos el nombre de las columnas de la tabla pets
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";

        /**
         * Valores Posibles de genero para los pets y su entero asignado
         */
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_UNKNOWN= 0;
    }
    /**
     * Empiezan los valores constantes de URI para el content provider
     */
    //La contante de authority
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    //La contante del contenido base de las URIs
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //La contante de contenido de la tabla pets
    public static final String PATH_PETS = "pets";
    //Ahora all junto para hacer la direccion URI con un objeto Uri
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);
}
