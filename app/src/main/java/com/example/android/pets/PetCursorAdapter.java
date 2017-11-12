package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;

//Empieza a hacer el Cursor Adapter para desplegar los registros de las mascotas de manera dinamica
public class PetCursorAdapter extends CursorAdapter {

    // Contructor de la clase
    public PetCursorAdapter(Context context, Cursor cursor){
        super(context, cursor,0);
    }

    // Este metodo se usa para inflar (crear) las nuevas views vacias que apareceran en la pantalla
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    // Ahora si se le asigna los datos del cursor correspondientes a los views inflados con newView
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Encuentra los campos a llenar por elementos que sacaremos del cursor
        TextView petName = (TextView) view.findViewById(R.id.list_item_pet_name);
        TextView petSummary = (TextView) view.findViewById(R.id.list_item_pet_summary);

        // Extrae las propiedades registros que nos interesan del cursor
        int nombreColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
        int breedColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
        String nombre = cursor.getString(nombreColumnIndex);
        String breed = cursor.getString(breedColumnIndex);

        // Hace las asignaciones de texto a los textviews basados en los datos del cursor
        petName.setText(nombre);
        petSummary.setText(breed);

    }
}
