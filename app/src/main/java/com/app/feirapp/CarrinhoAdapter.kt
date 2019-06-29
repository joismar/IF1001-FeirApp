package com.app.feirapp

import android.app.AlertDialog
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class CarrinhoAdapter(private val produtos: ArrayList<Produto>, private val activity: MainActivity) : RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder>() {

    class CarrinhoViewHolder(val carrinhoView: View) : RecyclerView.ViewHolder(carrinhoView) {

        init {
        }

        fun dialog(produtos: ArrayList<Produto>, position: Int, adapter: CarrinhoAdapter, activity: MainActivity) {
            carrinhoView.setOnClickListener {
                val dialog = AlertDialog.Builder(itemView.context)

                dialog.setTitle("O que deseja fazer?")
                dialog.setPositiveButton("Deletar Item") { _, _ ->
                    Log.d("Produtos: ", produtos.toString())
                    activity.deletaProduto(position, adapter)
                }
                dialog.setNegativeButton("Cancelar") { _, _ -> }
                dialog.create().show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): CarrinhoViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.carrinholista, parent, false)
        return CarrinhoViewHolder(v)
    }

    override fun onBindViewHolder(holder: CarrinhoViewHolder, position: Int) {
        val produtoModel = produtos[position]

        (holder.carrinhoView.findViewById(R.id.produto_nome) as TextView).text = produtoModel.nome
        (holder.carrinhoView.findViewById(R.id.produto_qtde) as TextView).text = "(%s)".format(produtoModel.qtde)
        (holder.carrinhoView.findViewById(R.id.produto_preco) as TextView).text = DecimalFormat("#,##0,00").format(produtoModel.preco * 100)
        (holder.carrinhoView.findViewById(R.id.produto_preco_total) as TextView).text = DecimalFormat("#,##0,00").format(produtoModel.getTotal() * 100)

        holder.dialog(produtos, position, this, activity)
    }

    override fun getItemCount(): Int {
        return produtos.size
    }
}