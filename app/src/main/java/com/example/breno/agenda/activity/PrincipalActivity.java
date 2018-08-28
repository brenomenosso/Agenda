package com.example.breno.agenda.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.widget.AbsListView.MultiChoiceModeListener;

import com.example.breno.agenda.model.Aluno;
import com.example.breno.agenda.adapter.AlunoAdapter;
import com.example.breno.agenda.R;
import com.example.breno.agenda.dao.AlunoDAO;


public class PrincipalActivity extends AppCompatActivity {

    private final int ALUNO_CADASTRO_REQUEST_CODE = 122;

    private AlunoDAO dao;
    private FloatingActionButton btnAdicionar;
    private ListView lista;
    private AlunoAdapter alunoAdapter;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        instanceMethods();
        implementsMethods();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initMethods();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (actionMode != null) {
            actionMode.finish();
            lista.clearChoices();
        } else {
            super.onBackPressed();
        }
    }

    //Filtro Pesquisa
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pesquisa_item, menu);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setQueryHint("Pesquisar...");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                alunoAdapter.getFilter().filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s)) {
                    alunoAdapter.getFilter().filter(s);
                    return true;
                }
                return false;
            }
        });

        return true;
    }

    //Valido se a requisição foi aprovada e atualizo a lista
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ALUNO_CADASTRO_REQUEST_CODE && resultCode == RESULT_OK) {
            alunoAdapter.updateList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void instanceMethods() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        lista = findViewById(R.id.list);
        btnAdicionar = findViewById(R.id.btnAdicionar);
        alunoAdapter = new AlunoAdapter(this);
        lista.setAdapter(alunoAdapter);

    }

    private void implementsMethods() {

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                lista.setItemChecked(i, lista.isItemChecked(i));
                lista.setSelection(i);
                return true;
            }
        });


        //Para quando clica em 1 item (Editar, mudar de formulario e transmitir info entre elas)
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (alunoAdapter.getListaSelecao().size() > 0) {
                    lista.setItemChecked(position, !lista.isItemChecked(position));
                    lista.setSelection(position);
                } else {
                    Aluno aluno = (Aluno) lista.getItemAtPosition(position);
                    Intent intentCadastro = new Intent(PrincipalActivity.this, CadastroActivity.class);
                    intentCadastro.putExtra("aluno", aluno);
                    startActivityForResult(intentCadastro, ALUNO_CADASTRO_REQUEST_CODE);
                }
            }
        });


        // Capture ListView item click
        lista.setMultiChoiceModeListener(new MultiChoiceModeListener() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_excluir, menu);
                actionMode = mode;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                alunoAdapter.getListaSelecao().clear();
                actionMode = null;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    if (!alunoAdapter.getListaSelecao().contains(position))
                        alunoAdapter.getListaSelecao().add(position);
                } else {
                    Integer index = alunoAdapter.getListaSelecao().indexOf(position);
                    if (alunoAdapter.getListaSelecao().contains(position))
                        alunoAdapter.getListaSelecao().remove(index);
                }

                mode.setTitle(alunoAdapter.getListaSelecao().size() + " Selected");
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_excluir:
                        new AlertDialog.Builder(PrincipalActivity.this)
                                .setTitle("Excluir")
                                .setMessage("Deseja excluir o registro?")
                                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        for (int position : alunoAdapter.getListaSelecao()) {
                                            Aluno aluno = alunoAdapter.getItem(position);
                                            dao.deletar(aluno);
                                            dao.close();
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        alunoAdapter.updateList();
                        mode.finish();
                        return true;
                    case R.id.setting_all:

                    default:
                        return false;
                }
            }


        });

        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrincipalActivity.this, CadastroActivity.class);
                startActivityForResult(intent, ALUNO_CADASTRO_REQUEST_CODE);

            }
        });

    }

    private void initMethods() {
        alunoAdapter.updateList();
    }

}









