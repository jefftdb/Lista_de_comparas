package com.example.jefferson.listadecompas;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

class MeuAdapter extends BaseAdapter {

    private Context mContext;
    private List<Produto> mList;

    public MeuAdapter(Context mContext, List<Produto> mList){
        this.mContext = mContext;
        this.mList = mList;

    }


    @Override
    public int getCount() {
        return this.mList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_celula,null);
        TextView produto = view.findViewById(R.id.textProduto);

        Produto p = (Produto) getItem(position);
        produto.setText(p.produto);
        return view;
    }
}

