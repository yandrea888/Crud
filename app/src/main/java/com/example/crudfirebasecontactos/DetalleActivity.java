package com.example.crudfirebasecontactos;

import android.content.Intent;
import android.os.Bundle;

import com.example.crudfirebasecontactos.Models.ContactoModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DetalleActivity extends AppCompatActivity {

    private TextView tv_detalle_nombres, tv_detalle_numero;
    private FloatingActionButton fab_detalle_editar, fab_detalle_eliminar;
    private ContactoModel model;

    private final String text_reference = "contactos";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference(text_reference);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        Toolbar toolbar = findViewById(R.id.toolbar_detalle);
        setSupportActionBar(toolbar);

        fab_detalle_editar = findViewById(R.id.fab_detalle_editar);
        fab_detalle_eliminar = findViewById(R.id.fab_detalle_eliminar);
        tv_detalle_nombres = findViewById(R.id.tv_detalle_nombres);
        tv_detalle_numero = findViewById(R.id.tv_detalle_numero);
        model = new ContactoModel();

        String id = getIntent().getStringExtra("id");
        if (id != null && !id.equals("")){
            reference.child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    model = dataSnapshot.getValue(ContactoModel.class);
                    if (model != null){
                        tv_detalle_nombres.setText(model.get_nombre());
                        tv_detalle_numero.setText(model.get_numero());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DetalleActivity.this, "Error con Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        }

        fab_detalle_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model != null) {
                    if (model.get_id() != null && !model.get_id().equals("")) {
                        Intent editar = new Intent(DetalleActivity.this, EditarActivity.class);
                        editar.putExtra("id", model.get_id());
                        startActivity(editar);
                    }
                }

            }
        });
        fab_detalle_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(view, "seguro que deseas eliminarlo?", Snackbar.LENGTH_LONG);
                snackbar.setAction("Estoy seguro!", new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        if (model != null) {
                            if (model.get_id() != null && !model.get_id().equals("")) {

                                reference.child(model.get_id()).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener <Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                    Intent inicio = new Intent(DetalleActivity.this, MainActivity.class);
                                                    startActivity(inicio);
                                                    finish();
                                                }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(view, "No pude eliminar, revisa la información", Snackbar.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }
                    }
                });
                snackbar.show();
            }
        });
    }

}
