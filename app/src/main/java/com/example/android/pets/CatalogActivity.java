
package com.example.android.pets;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDBHelper;
import com.example.android.pets.data.PetProvider;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private PetDBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        /**
         * Despliega el menu vaicio cuando no hay datos
         */
        // Find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.lista_mascotas);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        //Llama al constructor de la clase PetDBHelper para poder hacer metodos sobre el
        mDbHelper = new PetDBHelper(this);

        /**
         * displayDatabaseInfo(); comentado por ahora para probar si sirve el adapter
         */
        // displayDatabaseInfo2();
    }

    //Override al metodo onStart para que cuando la actividad empiece de nuevo haga una llamada
    //al metodo displayDatabaseInfo que muestra la info de la BDD
    @Override
    protected void onStart(){
        super.onStart();
        displayDatabaseInfo2();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        PetDBHelper mDbHelper = new PetDBHelper(this);

        // Create and/or open a database to read from it
        // SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Nuevo metodo de hacer el query de manera de no usar un "raw" SQL statement usando un cursor
        //ESta parte selecciona las columnas que quiero que se desplieguen en el query
        String[] projeccion = {PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
                };
        //Metodo de interaccion con la bdd sin usar un content provider
        //Cursor c = db.query(PetEntry.TABLE_NAME, projeccion, null, null, null, null, null);
        Cursor c = getContentResolver().query(PetContract.CONTENT_URI,projeccion, null,null,null);

        //Encuentra el ViewId de texto en que se va a desplegar la informacion
        TextView displayView = (TextView) findViewById(R.id.list_item_pet_name);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database). Usando el cursor
            displayView.setText("Number of rows in pets database table: " + c.getCount());
            //Muestra los nombres de la columnas
            displayView.append("\n\n" + PetEntry._ID + " - "
            +PetEntry.COLUMN_PET_NAME + " - " +
                    PetEntry.COLUMN_PET_BREED + " - " +
                    PetEntry.COLUMN_PET_GENDER + " - " +
                    PetEntry.COLUMN_PET_WEIGHT +
                    "\n");
            //Jala los indices de la columna del cursor "c" que obtivimos en el query (funciona
            //como una matriz n x n
            int idColumnIndex = c.getColumnIndex(PetEntry._ID);
            int nameColumnIndex = c.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = c.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = c.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = c.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            //Iteramos sobre all el cursor para ir añadiendo los datos del query a nuestra textview
            while (c.moveToNext()){// Condiciòn de loop que devuelve falso cuando ya no hay mas elementos
                //metodos get del cursor "c" para devolver el elemento del query
                int currentID = c.getInt(idColumnIndex);
                String currentName = c.getString(nameColumnIndex);
                String currentBreed = c.getString(breedColumnIndex);
                int currentGender = c.getInt(genderColumnIndex);
                int currentWeight = c.getInt(weightColumnIndex);

                //Añadiendo los elementos obtenidos del cursor a la view de text con .append
                displayView.append(("\n"+ currentID + " - " +
                        currentName + " - " +
                        currentBreed + " - " +
                        currentGender + " - " +
                        currentWeight));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            c.close();
        }
    }

    //Metodo que inserta una mascota
    private void insertPet(){
        //Crea una lista de key pairs de datos dummy con los que se va a llenar un registro de la
        //tabla pets
        ContentValues valoresInsertar = new ContentValues();
        valoresInsertar.put(PetEntry.COLUMN_PET_NAME, "Toto");
        valoresInsertar.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        valoresInsertar.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        valoresInsertar.put(PetEntry.COLUMN_PET_WEIGHT, 7);
        //Codigo viejo para hacer la insercion de manera directa a la BDD
        //Establece conexicon con la BDD
        //SQLiteDatabase db = mDbHelper.getReadableDatabase();
        //Llama al metodo nuevo onInsert definido en el helper
        //mDbHelper.onInsert(db, valoresInsertar);
        /**
         * Usando los Content providers para hacer la insercion a la BDD
         */
        Uri nuevaUri = getContentResolver().insert(PetContract.CONTENT_URI, valoresInsertar);
    }

    //Nueva manera de llamar al display ahora usando el CursorAdapter
    private void displayDatabaseInfo2(){
        //Nuevo metodo de hacer el query de manera de no usar un "raw" SQL statement usando un cursor
        //ESta parte selecciona las columnas que quiero que se desplieguen en el query
        String[] projeccion = {PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
        };
        //Metodo de interaccion con la bdd sin usar un content provider
        //Cursor c = db.query(PetEntry.TABLE_NAME, projeccion, null, null, null, null, null);
        Cursor c = getContentResolver().query(PetContract.CONTENT_URI,projeccion, null,null,null);

        // Ecuentra la listView de la actividad para empezar a llenar de views
        ListView lvItems = (ListView) findViewById(R.id.lista_mascotas);
        // Creamos un objeto de la nueva clase PetCursorAdapter que extiende Cursor Adapter
        PetCursorAdapter petAdapter = new PetCursorAdapter(this,c);
        // Unimos el Cursor adapter a la ListView que encontramos en el primer paso
        lvItems.setAdapter(petAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                //Llama al nuevo metodo que inserta una mascota
                insertPet();
                displayDatabaseInfo2();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}