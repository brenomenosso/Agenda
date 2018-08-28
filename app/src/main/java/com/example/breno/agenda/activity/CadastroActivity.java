package com.example.breno.agenda.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.breno.agenda.model.Aluno;
import com.example.breno.agenda.R;
import com.example.breno.agenda.dao.AlunoDAO;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

public class CadastroActivity extends AppCompatActivity {

    private Aluno aluno;
    private EditText editNome;
    private EditText editEndereco;
    private EditText editTelefone;
    private EditText editIdade;
    private EditText editSite;
    private RatingBar ratingNota;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        instanceMethods();
        implementsMethods();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initMethods();
    }

    private void instanceMethods() {

        //Setando os valores no edit
        editNome = findViewById(R.id.edtNome);
        editEndereco = findViewById(R.id.edtEndereco);
        editTelefone = findViewById(R.id.edtTelefone);
        editIdade = findViewById(R.id.edtIdade);
        editSite = findViewById(R.id.edtSite);
        ratingNota = findViewById(R.id.rtgNota);
    }

    private void implementsMethods() {
        //Mascara para campo Telefone
        editTelefone.addTextChangedListener(new MaskTextWatcher(editTelefone, new SimpleMaskFormatter("(NN)NNNN-NNNN")));
    }


    private void initMethods() {
        //Vamos recuperar o dados em tempo de exec
        aluno = (Aluno) getIntent().getSerializableExtra("aluno");
        if (aluno != null) {
            editNome.setText(aluno.getNome());
            editEndereco.setText(aluno.getEndereco());
            editTelefone.setText(aluno.getTelefone());
            editIdade.setText(String.valueOf(aluno.getIdade()));
            editSite.setText(aluno.getSite());
            ratingNota.setRating(aluno.getNota().intValue());
        } else {
            aluno = new Aluno();
        }

    }

    //Menu para o Icone
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuformulario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Quando for clicada na tela de alterar, habilitar botao de excluir
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null && aluno != null && aluno.getId() != null) {
            menu.findItem(R.id.item_excluir).setVisible(true);
            finish();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    //Funcao para confirmar o cadastro (V) no toolbar superior
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_ok:
                if (validaCampos()) {
                    aluno.setNome(editNome.getText().toString());
                    aluno.setEndereco(editEndereco.getText().toString());
                    aluno.setTelefone(editTelefone.getText().toString());
                    if (aluno.getIdade() != null) {
                        aluno.setIdade(Integer.valueOf(editIdade.getText().toString()));
                    }
                    aluno.setSite(editSite.getText().toString());
                }

                aluno.setNota((double) ratingNota.getProgress());
                AlunoDAO dao = new AlunoDAO(this);
                if (aluno.getId() != null && validaCampos()) {
                    dao.alterar(aluno);
                } else if (aluno.getId() == null && validaCampos()) {
                    dao.insere(aluno);
                }
                if(validaCampos()) {
                    dao.close();
                    Toast.makeText(CadastroActivity.this, "Aluno salvo!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    break;
                }
                break;

            case (R.id.item_excluir):
                AlunoDAO daoo = new AlunoDAO(this);
                daoo.deletar(aluno);
                daoo.close();

                Toast.makeText(CadastroActivity.this, " Usuário deletado ", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(CadastroActivity.this, PrincipalActivity.class);
                setResult(RESULT_OK);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validaCampos() {

        editNome.setError(null);
        editEndereco.setError(null);
        editTelefone.setError(null);
        editIdade.setError(null);
        editSite.setError(null);

        if (TextUtils.isEmpty(editNome.getText())) {
            editNome.setError("Informe o campo \"Nome\"");
            editNome.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(editEndereco.getText())) {
            editEndereco.setError("Informe o campo \"Endereço\"");
            editEndereco.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(editTelefone.getText())) {
            editTelefone.setError("Informe o campo \"Telefone\"");
            editTelefone.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(editIdade.getText())) {
            editIdade.setError("Informe o campo \"Idade\"");
            editIdade.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(editSite.getText())) {
            editSite.setError("Informe o campo \"Site\"");
            editSite.requestFocus();
            return false;
        }
        return true;
    }

}
