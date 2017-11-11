
package com.example.android.pets;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDBHelper;
import com.example.android.pets.data.PetDBHelper;

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

        //Llama al constructor de la clase PetDBHelper para poder hacer metodos sobre el
        mDbHelper = new PetDBHelper(this);
        displayDatabaseInfo();
    }

    //Override al metodo onStart para que cuando la actividad empiece de nuevo haga una llamada
    //al metodo displayDatabaseInfo que muestra la info de la BDD
    @Override
    protected void onStart(){
        super.onStart();
        displayDatabaseInfo();
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
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Nuevo metodo de hacer el query de manera de no usar un "raw" SQL statement usando un cursor
        //ESta parte selecciona las columnas que quiero que se desplieguen en el query
        String[] projeccion = {PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
                };
        
        Cursor c = db.query(PetEntry.TABLE_NAME, projeccion, null, null,
                null, null, null);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("Number of rows in pets database table: " + c.getCount());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            c.close();
        }
    }

    //Metodo que inserta una mascota
    private void insertPet(){
        //Crea una lista de key pairs de datos dummy con los que se va a llenar un registro de la
        //talba pets
        ContentValues valoresInsertar = new ContentValues();
        valoresInsertar.put(PetEntry.COLUMN_PET_NAME, "Toto");
        valoresInsertar.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        valoresInsertar.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        valoresInsertar.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        //Establece conexicon con la BDD
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Llama al metodo nuevo onInsert definido en el helper
        mDbHelper.onInsert(db, valoresInsertar);
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
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}