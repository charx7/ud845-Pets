/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDBHelper;
import com.example.android.pets.data.PetProvider;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the pet data loader */
     private static final int EXISTING_PET_LOADER = 0;

    // Crea el objeto para sacar el Uri del intent
    private Uri mCurrentPetUri;

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        // Examinar si la actividad fue abierta mediante un intent de pet o no y modificar el comportamiento
        // de manera adecuada mediante el valor que fue pasado de la uri

        Intent intent = getIntent();
        Uri currentPetUri = intent.getData();

        if (currentPetUri == null){
            setTitle("Añadir una mascota");
        } else {
            setTitle("Editar una mascota");
            // Inicializacion del background thread para el metodo query
            mCurrentPetUri = currentPetUri;
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetContract.PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetContract.PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetContract.PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    /**
     * Jala los datos que puso el usuario y salva la mascota en la BDD
     */
    private  void insertPet(){
        //Llama al objeto creado que hace referencia a la view y llama al metodo .gettext y
        //.tostring para sacar los datos y transformarlos a texto el trim elimina los blancos adicionales
        String nombreAInsertar = mNameEditText.getText().toString().trim();
        Integer generoAInsertar = mGender;
        String razaAInsertar = mBreedEditText.getText().toString().trim();
        Integer pesoAInsertar = Integer.parseInt(mWeightEditText.getText().toString().trim());
        //Crea una lista de key pairs de datos dummy con los que se va a llenar un registro de la
        //talba pets
        ContentValues valoresInsertar = new ContentValues();
        valoresInsertar.put(PetContract.PetEntry.COLUMN_PET_NAME, nombreAInsertar);
        valoresInsertar.put(PetContract.PetEntry.COLUMN_PET_BREED, razaAInsertar);
        valoresInsertar.put(PetContract.PetEntry.COLUMN_PET_GENDER, generoAInsertar);
        valoresInsertar.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, pesoAInsertar);

        //Codigo viejo que inserta el la mascota a la BDD usando query directo
        //Constructor del objeto PetDBHelper
        //PetDBHelper mDbHelper = new PetDBHelper(this);
        //Establece conexicon con la BDD
        //SQLiteDatabase db = mDbHelper.getReadableDatabase();
        //Llama al metodo nuevo onInsert definido en el helper
        //mDbHelper.onInsert(db, valoresInsertar);
        //long newRowId = db.insert(PetContract.PetEntry.TABLE_NAME,null,valoresInsertar);

        //if (newRowId == -1) {
        //    Toast.makeText(this,"Error Salvando a la mascota", Toast.LENGTH_SHORT).show();
        //} else {
        //    Toast.makeText(this,"Mascota salvada con el ID: "+ newRowId, Toast.LENGTH_SHORT).show();
        //}

        // Determinar si esta es una nueva o mascota existente verificando el mCurrentPetUri si es nulo
        // o no

        if ( mCurrentPetUri == null){
            // Entonces esta es una nueva mascota, llamamos al metod de insercion del Data Provider
            // Regresa el contenido URI de la nueva mascota
            Uri newUri = getContentResolver().insert(PetContract.CONTENT_URI,valoresInsertar);

            // Manda un mensaje Toast dependiendo si fue exitoso o no la insercion de los datos
            if (newUri == null){
                Toast.makeText(this,"No se pudo insertar la mascota a la BDD",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,"Insercion exitosa a la BDD",Toast.LENGTH_SHORT).show();
            }
        }   else {

            // Entonces es una nueva mascota por lo tanto llamamos a los metodos del provider
            // relacionados con el update con la URI de la mascota que estamos mostrando para
            // que solo sea un registro

            int filasAfectadas = getContentResolver().update(mCurrentPetUri,
                    valoresInsertar,null,null);

            // Mensaje de Toast con el update bien hecho (depende del filas afectadas)
            if (filasAfectadas > 0){
                Toast.makeText(this,"Actualizacion Exitosa",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,"Fallo en la actualizacion",Toast.LENGTH_SHORT).show();
            }

        }

        /**
         * Llamado de insercion al metodo que usa el nuevo data provider, regresa una uri con el contenido
         * de direccion del nuevo registro de mascota en la BDD
         + Codigo viejo de los mensajes Toast
        Uri nuevaUri = getContentResolver().insert(PetContract.CONTENT_URI,valoresInsertar);
        // El mensaje Toast segun el valor de nuevaURI
        if (nuevaUri==null){
            Toast.makeText(this, getString(R.string.insertar_mascota_fallido)
            ,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,getString(R.string.insertar_mascota_exito),
                    Toast.LENGTH_SHORT).show();
        }
         **/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Llama al metodo para insertar la moscota a la BDD con los datos vaciados por el
                //usuario
                insertPet();
                //Salir de la Actividad
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //Metodos que usan una background thread para hacer los querys a la BDD
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Definida una projeccion sobre la que se va a hacer el query (las columnas que jalaremos)
        String[] projeccion = {PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUMN_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_WEIGHT
        };


        // Este loader ejecuta el Content Provider metodo de hacer un query usando la projeccion en
        //una background thread
        return new CursorLoader(this, // Contexto de la Parent Activity
                mCurrentPetUri, // Uri con la que se harà el query al content provider
                projeccion,// Columnas a incluir en el cursor
                null, // No hay clausula de seleccion
                null, // No hay argumentos de la clausula de seleccion
                null); // No hay orden definido
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (data== null || data.getCount() < 1) {
            return;
        }
        // Moverse a la primera fila de los datos para empezar a extraer la info
        if (data.moveToFirst()) {
            // Encuentra todas las columnas de los atributos que estamos interesados en el cursor
            int nameColumnIndex = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT);

            // Extraer la info del cursor
            String name = data.getString(nameColumnIndex);
            String breed = data.getString(breedColumnIndex);
            int gender = data.getInt(genderColumnIndex);
            int weight = data.getInt(weightColumnIndex);

            // Modifica los Views segun la info de la Mascota Seleccionada
            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(Integer.toString(weight));

            // Para el spinner se usa el metodo .setseleccion usando los casos que corresponden al
            // genero
            switch (gender) {
                case PetContract.PetEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case PetContract.PetEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}