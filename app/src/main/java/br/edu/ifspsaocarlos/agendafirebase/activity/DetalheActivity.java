package br.edu.ifspsaocarlos.agendafirebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import br.edu.ifspsaocarlos.agendafirebase.R;
import br.edu.ifspsaocarlos.agendafirebase.model.Contato;


public class DetalheActivity extends AppCompatActivity {
    private Contato c;

    private DatabaseReference databaseReference;
    String FirebaseID;
    private Spinner spinnerTipoContato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        spinnerTipoContato = findViewById(R.id.spinnerTipoContato);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        final List<String> categorias = Arrays.asList("", "Amigo", "Família", "Trabalho", "Outro");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoContato.setAdapter(dataAdapter);

        if (getIntent().hasExtra("FirebaseID")) {

            FirebaseID = getIntent().getStringExtra("FirebaseID");

            databaseReference.child(FirebaseID).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    c = snapshot.getValue(Contato.class);

                    if (c != null) {
                        EditText nameText = findViewById(R.id.editTextNome);
                        nameText.setText(c.getNome());

                        EditText foneText = findViewById(R.id.editTextFone);
                        foneText.setText(c.getFone());

                        EditText emailText = findViewById(R.id.editTextEmail);
                        emailText.setText(c.getEmail());

                        spinnerTipoContato.setSelection(Integer.valueOf(c.getTipoContato()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalhe, menu);
        if (FirebaseID == null) {
            MenuItem item = menu.findItem(R.id.delContato);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.salvarContato:
                salvar();
                return true;
            case R.id.delContato:
                apagar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void apagar() {
        databaseReference.child(FirebaseID).removeValue();
        Intent resultIntent = new Intent();
        setResult(3, resultIntent);
        finish();
    }

    private void salvar() {
        String name = ((EditText) findViewById(R.id.editTextNome)).getText().toString();
        String fone = ((EditText) findViewById(R.id.editTextFone)).getText().toString();
        String email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
        Integer tipoContato = spinnerTipoContato.getSelectedItemPosition();

        if (c == null) {
            c = new Contato();
            c.setNome(name);
            c.setFone(fone);
            c.setEmail(email);
            c.setTipoContato(tipoContato.toString());
            databaseReference.push().setValue(c);
        } else {
            c.setNome(name);
            c.setFone(fone);
            c.setEmail(email);
            c.setTipoContato(tipoContato.toString());
            databaseReference.child(FirebaseID).setValue(c);
        }
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}

