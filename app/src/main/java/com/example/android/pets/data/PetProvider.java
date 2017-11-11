package com.example.android.pets.data;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {

    //Valores de mapeo para ver los casos a tratar de la URIS
    private static final int PETS = 100;
    private static final int PETS_ID = 101;

    //Crea un objeto UriMatcher que llama al metodo Matcher para asignarle NO_MATCH por dafault
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    //Casos de Match para los Uri para mapeo de los valores 100 o 101
    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS + "/#", PETS_ID);
    }
    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    //Database helper object
    private PetDBHelper mDbHelper;
    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        //contuye el objeto llamando al contructor de la clase Petdbhelper
        mDbHelper = new PetDBHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        //Establecer una conexicon con la BDD
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //Nuevo objeto del tipo cursor al que vamos a almacenar el query
        Cursor cursor;

        //Ver cual caso nos arroja el URI matcher y operar segun lo debido
        int match =sUriMatcher.match(uri);
        switch (match) {
            case PETS : //Caso en que el match de la uri que le pasemos nos arroje el Integer de 100
                //Solamente hace el query sobre el objeto cursor usando las projecciones (columnas que quieres)
                cursor = database.query(PetContract.PetEntry.TABLE_NAME,
                        projection
                        ,selection
                        ,selectionArgs
                        ,null
                        ,null
                        ,sortOrder);
                break;
            case PETS_ID : //Caso en que el match de la uri que le pasemos nos arroje el Integer de 101
                //Determina cuales van a ser los elementos (columnas) de la seleccion
                selection = PetContract.PetEntry._ID + "=?";
                //Determina cuales van a ser las condiciones del matcheo el ID o el arreglo de ID "where ID = X" de SQL
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection,selection,selectionArgs
                ,null,null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("No se le puede hacer query Uri Unknown " + uri);
        }


        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}