package com.example.jefferson.listadecompas;

import java.io.Serializable;

public class Produto implements Serializable {
    String id;
    String produto;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }



}
