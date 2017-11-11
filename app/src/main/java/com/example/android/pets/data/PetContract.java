package com.example.android.pets.data;

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

}
