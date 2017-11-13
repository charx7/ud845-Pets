package com.example.android.pets.data;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

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

        // Notificaciones Uri en el cursor, asi sabremos
        // si los datos en el URI cambiaron entonces sabemos que
        // debemos de actualizar el cursor
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        //Hace el llamado al metodo de match para recuperar el valor del mapeo que nos dice la
        //naturaleza de la tabla asociada URI
        final int match = sUriMatcher.match(uri);
        //Casos switch segun el valor de match dado
        switch (match){
            case PETS:
                //Llama al metodo de insercion del provedor de la informacion
                return insertPet(uri,contentValues);
            default:
                throw new IllegalArgumentException("Insertion no soportada para " + uri);
        }
    }

    /**
     *@param uri
     *@param valoresAInsertar
     * Metodo con el que se va a llamar a la inserción del registro de pets con los valores y la
     * Uri recuperada del metodo insert
     */
    private Uri insertPet(Uri uri, ContentValues valoresAInsertar){
        // Verificadores de los contenidos a insertar que tengan sentido para nuestra BDD
        // Verificador del campo no-nulo de nombre
        String name = valoresAInsertar.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("La mascota requiere un nombre");
        }
        // Verificador de peso no nulo
        int peso = valoresAInsertar.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if(peso == 0){
            throw new IllegalArgumentException("La mascota requiere un peso");
        }
        // No se Necesita verificador de la raza porque puede ser nulo sin afectar la consistencia de
        // la tabla de Pets

        // Abre la conexicon a la BDD
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // Linea que hace la insercion a la BDD y que guarda el valor del ID insertado
        long newRowId = database.insert(PetContract.PetEntry.TABLE_NAME,null,valoresAInsertar);
        // Metodo que llama a un mensaje al log y regresa null si no puedo insertar bien el registro
        if (newRowId == -1) {
            Log.e(LOG_TAG, "Fallado a insertar en la fila para "+ uri);
            return null;
        }

        // Notifica a los listeners que los datos han cambiado para el petcontent uri
        getContext().getContentResolver().notifyChange(uri,null);

        // Ya que sabemos el ID del nuevo registro insertado en la tabla este metodo nos retorna
        // Una URI con el ID appendido (autoincrementado al final
        return ContentUris.withAppendedId(uri, newRowId);
    }


    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        //Hace el match de la uri para hacer el switch segun el caso de uno o varios registros
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("No se puede actualizar para " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //Se sale antes si no hay valores a actualizar
        if (values.size() == 0) {
            return 0;
        }

        // Faltan los data validators!! para no updater valores falsos
        // Abre la conexicon a la BDD
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Hace el update de las filas y guarda el numero de filas actualizadas en la variable
        // idFilasActualizadas usando el objeto database
        int idFilasActualizadas = database.update(PetContract.PetEntry.TABLE_NAME,values,selection
                ,selectionArgs);

        // Primero verifica si el numero de filas actualizadas fue mayor a 0
        if (idFilasActualizadas> 0) {
            // Notifica a los listeners que los datos han cambiado para el petcontent uri
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Regresa el entero del numero de filas actualizadas
        return idFilasActualizadas;

    }



    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Establecer conexion con la BDD solo que ahora es writable
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Hace el match segun el caso arrojado y predefinido por la URI de argumento y hace el case
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                
                // Notifica a los listeners que los datos han cambiado para el petcontent uri
                getContext().getContentResolver().notifyChange(uri,null);

                return database.delete(PetContract.PetEntry.TABLE_NAME,
                        selection,selectionArgs);
            case PETS_ID:
                // Lineas que procesan los ids del where donde se hara el delete
                // Sobre que filas se hara la modificacion especifica de los ids
                selection = PetContract.PetEntry._ID +"=?";
                // Arroja un vector de cadenas de los ids individuales contenidos en la String
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Notifica a los listeners que los datos han cambiado para el petcontent uri
                getContext().getContentResolver().notifyChange(uri,null);

                return  database.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("No es soportado borrar para: " + uri);
        }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}