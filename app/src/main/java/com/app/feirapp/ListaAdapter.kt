package com.app.feirapp

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat
import android.content.Context.LAYOUT_INFLATER_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.main.*

class ListaAdapter(private val produtos: ArrayList<Produto>, private val activity: MainActivity) : RecyclerView.Adapter<ListaAdapter.ListaViewHolder>() {

    class ListaViewHolder(val listaView: View) : RecyclerView.ViewHolder(listaView) {

        init {
        }

        fun setChecks(produtos: ArrayList<Produto>, position: Int, activity: MainActivity) {
            val checkbox2 = listaView.findViewById(R.id.checkbox2) as CheckBox

            checkbox2.setOnClickListener {
                if (checkbox2.isChecked) {
                    val vi = activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val dialog_add = AlertDialog.Builder(itemView.context).create()
                    val inflate = vi.inflate(R.layout.check_dialog, null)
                    val editpreco = inflate.findViewById(R.id.editpreco) as EditText
                    var editqtde = inflate.findViewById(R.id.editqtde) as EditText
                    val butcancel = inflate.findViewById(R.id.button1) as Button
                    val butadd = inflate.findViewById(R.id.button2) as Button
                    val addbutton = inflate.findViewById(R.id.adicionarButton) as Button
                    val subbutton = inflate.findViewById(R.id.subtrairButton) as Button

                    var qtde = 1

                    editqtde.setText(qtde.toString())

                    editpreco.addTextChangedListener(activity.NumberTextWatcher(editpreco))

                    dialog_add.setView(inflate)

                    editpreco.isFocusableInTouchMode = true
                    editpreco.isFocusable = true
                    editpreco.requestFocus()
                    editpreco.setSelection(0)

                    dialog_add.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

                    editpreco.setText("R$0,00")
                    editpreco.setSelection(editpreco.text.length)

                    butadd.setOnClickListener {
                        activity.addProdutoLista(
                                position,
                                editpreco.text.toString(),
                                editqtde.text.toString()
                        )
                        produtos[position].isSelected = true
                        dialog_add.dismiss()
                    }

                    butcancel.setOnClickListener {
                        produtos[position].isSelected = false
                        checkbox2.isChecked = false
                        dialog_add.dismiss()
                    }

                    addbutton.setOnClickListener {
                        qtde += 1
                        editqtde.setText(qtde.toString())
                    }

                    subbutton.setOnClickListener {
                        qtde -= 1
                        editqtde.setText(qtde.toString())
                    }

                    dialog_add.show()

                } else {
                    produtos[position].isSelected = false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ListaViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.shoplist, parent, false)
        return ListaViewHolder(v)
    }

    override fun onBindViewHolder(holder: ListaViewHolder, position: Int) {
        val produtoModel = produtos[position]

        (holder.listaView.findViewById(R.id.ord2) as TextView).text = "%s.".format(position.toString())
        (holder.listaView.findViewById(R.id.name2) as TextView).text = produtoModel.nome
        (holder.listaView.findViewById(R.id.checkbox2) as CheckBox).isChecked = produtoModel.isSelected

        holder.setChecks(produtos, position, activity)
    }

    override fun getItemCount(): Int {
        return produtos.size
    }
}