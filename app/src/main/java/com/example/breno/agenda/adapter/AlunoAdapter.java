package com.example.breno.agenda.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.breno.agenda.R;
import com.example.breno.agenda.model.Aluno;
import com.example.breno.agenda.dao.AlunoDAO;

import java.util.ArrayList;
import java.util.List;

public class AlunoAdapter extends BaseAdapter implements Filterable {

    private AlunoDAO dao;
    private List<Aluno> lista;
    private List<Aluno> listaOriginal;
    private List<Integer> listaSelecao;

    public AlunoAdapter(Context context) {
        this.lista = new ArrayList<>();
        this.listaOriginal = new ArrayList<>();
        this.listaSelecao = new ArrayList<>();
        this.dao = new AlunoDAO(context);
    }

    public void updateList() {
        this.listaSelecao.clear();
        this.lista = dao.buscaAlunos();
        this.listaOriginal = lista;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Aluno getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lista.get(position).getId();
    }

    @Override
    public View getView(int position, View contentView, ViewGroup viewGroup) {
        if (contentView == null) {
            contentView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_lista_aluno, viewGroup, false);
            contentView.setTag(new ViewHolder(contentView));
        }
        ViewHolder viewHolder = (ViewHolder) contentView.getTag();
        viewHolder.textNome.setText(getItem(position).getNome());
        viewHolder.textCodigo.setText(String.valueOf(getItem(position).getId()));
        viewHolder.textTelefone.setText(getItem(position).getTelefone());
        viewHolder.textEndereco.setText(getItem(position).getEndereco());

        return contentView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Aluno> listaFiltro = new ArrayList<>();
                if (TextUtils.isEmpty(constraint)) {
                    listaFiltro = listaOriginal;
                } else {
                    String filtro = constraint.toString().toLowerCase();
                    for (Aluno aluno : listaOriginal) {
                        if (aluno.getNome().toLowerCase().contains(filtro)) {
                            listaFiltro.add(aluno);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.count = listaFiltro.size();
                results.values = listaFiltro;
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                lista = (List<Aluno>) results.values;
                listaSelecao.clear();
                notifyDataSetChanged();
            }
        };
    }

    public List<Integer> getListaSelecao() {
        return listaSelecao;
    }


    private class ViewHolder {

        private TextView textNome, textCodigo, textEndereco, textTelefone;

        ViewHolder(View contentView) {
            textNome = contentView.findViewById(R.id.textNome);
            textCodigo = contentView.findViewById(R.id.textCodigo);
            textTelefone = contentView.findViewById(R.id.textTelefone);
            textEndereco = contentView.findViewById(R.id.textEndereco);
        }

    }

}
