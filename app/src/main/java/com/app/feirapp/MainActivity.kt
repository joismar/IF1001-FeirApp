package com.app.feirapp

import android.app.*
import android.os.*
import android.view.*
import android.widget.*
import android.graphics.*
import java.util.*
import java.text.*
import java.text.NumberFormat
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.content.SharedPreferences
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.main.*
import kotlin.collections.ArrayList
import android.view.inputmethod.EditorInfo
import android.widget.TextView



class MainActivity : Activity() {

    // Declarando variaveis de layout
    private var menu: Button? = null
    private var tab1adapter: LinearLayout? = null
    private var footab1: LinearLayout? = null
    private var imagecart: ImageView? = null
    private var tab1: TextView? = null
    private var tab2adapter: LinearLayout? = null
    private var footab2: LinearLayout? = null
    private var imagecheck: ImageView? = null
    private var tab2: TextView? = null
    private var tabadd: LinearLayout? = null
    private var tabshoplist: LinearLayout? = null
    private var warn1: TextView? = null

    private var warn2: TextView? = null
    private var add2: Button? = null
    private var add: Button? = null
    private var total: TextView? = null

    // Array de produto para ser trocado pelos array lists genéricos acima futuramente
    private var produtosArrayList = ArrayList<Produto>()
    private var listaArrayList = ArrayList<Produto>()

    // Declarando shared preferences
    private var item_dialog: AlertDialog.Builder? = null
    private var checks: SharedPreferences? = null

    private var produtosPref: SharedPreferences? = null

    // Declarando data atual
    private var date = Calendar.getInstance()

    private val displayWidthPixels: Int
        get() = resources.displayMetrics.widthPixels

    private val displayHeightPixels: Int
        get() = resources.displayMetrics.heightPixels

    private var carrinhoLista: RecyclerView? = null

    private var comprasLista: RecyclerView? = null

    // Instanciando view inicial
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        initialize()
        initializeLogic()
    }

    private fun initialize() {
        // Inicializando todas as views e construtores
        menu = findViewById<Button>(R.id.menu)
        tab1adapter = findViewById<LinearLayout>(R.id.tab1adapter)
        footab1 = findViewById<LinearLayout>(R.id.footab1)
        imagecart = findViewById<ImageView>(R.id.imagecart)
        tab1 = findViewById<TextView>(R.id.tab1)
        tab2adapter = findViewById<LinearLayout>(R.id.tab2adapter)
        footab2 = findViewById<LinearLayout>(R.id.footab2)
        imagecheck = findViewById<ImageView>(R.id.imagecheck)
        tab2 = findViewById<TextView>(R.id.tab2)
        tabadd = findViewById<LinearLayout>(R.id.tabadd)
        tabshoplist = findViewById<LinearLayout>(R.id.tabshoplist)

        warn1 = findViewById<TextView>(R.id.warn1)

        warn2 = findViewById<TextView>(R.id.warn2)
        add2 = findViewById<Button>(R.id.add2)
        add = findViewById<Button>(R.id.add)
        total = findViewById<TextView>(R.id.total)
        item_dialog = AlertDialog.Builder(this)

        // Esse método altera o preço na medida que editamos
        input_preco!!.addTextChangedListener(NumberTextWatcher(input_preco!!))

        produtosPref = getSharedPreferences("feiraPrefs", MODE_PRIVATE)

        carrinhoLista = findViewById<View>(R.id.carrinhoLista) as RecyclerView
        carrinhoLista!!.layoutManager = LinearLayoutManager(this)

        comprasLista = findViewById<View>(R.id.listaView) as RecyclerView
        comprasLista!!.layoutManager = LinearLayoutManager(this)

        input_qtde!!.setText("1")
        //input_preco.setText("R$0,00")

        input_qtde!!.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                if (input_nome!!.text.toString() == "" || input_preco!!.text.toString() == "" || input_qtde!!.text.toString() == "") {
                    showMessage("Há campos vazios!")
                } else {

                    // ADICIONA UM NOVO PRODUTO NO CARRINHO
                    produtosArrayList.add(Produto(
                            input_nome!!.text.toString(),
                            removeCurrency(input_preco!!.text.toString()).toDouble(),
                            input_qtde!!.text.toString().toInt(),
                            noCarrinho = true,
                            isSelected = false
                    ))

                    // ATUALIZA PREÇO TOTAL
                    atualizarTotal()

                    input_nome!!.setText("")
                    input_preco!!.setText("")
                    input_qtde!!.setText("")

                    // Salvando dados no arquivo
                    salvarPreferencias()

                    input_nome!!.isFocusableInTouchMode = true
                    input_nome!!.isFocusable = true
                    input_nome!!.requestFocus()
                    input_nome!!.setSelection(0)
                }
                true
            } else {
                false
            }
        }

        inputproduto!!.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                if (inputproduto!!.text.toString() == "") {
                    showMessage("Há campos vazios!")
                } else {

                    // ADICIONA UM NOVO PRODUTO NO CARRINHO
                    listaArrayList.add(Produto(
                        inputproduto!!.text.toString(),
                        0.0,
                        1,
                        noCarrinho = false,
                        isSelected = false
                    ))

                    checaListaVazia()

                    // Salvando dados no arquivo
                    salvarPreferencias()

                    inputproduto!!.setText("")

                    inputproduto!!.isFocusableInTouchMode = true
                    inputproduto!!.isFocusable = true
                    inputproduto!!.requestFocus()
                    inputproduto!!.setSelection(0)
                }
                true
            } else {
                false
            }
        }

        // Inicializando um popup menu e criando um click listener para seus itens
        menu!!.setOnClickListener {
            val popupMenu = PopupMenu(this, this.menu, 10)
            val menu = popupMenu.menu
            menu.add("Salvar Feira")
            menu.add("Limpar")
            menu.add("Sobre")
            popupMenu.setOnMenuItemClickListener { item ->
                val i = item.title.toString()

                when (i) {
                    "Salvar Feira" -> {
                        //_save_feira()
                        true
                    }
                    "Limpar" -> {
                        limparFeira()
                        true
                    }
                    "Sobre" -> true
                    else -> false
                }
            }
            // Mostrando o menu
            popupMenu.show()
        }
    }

    fun addProdutoLista(position: Int, preco: String, qtde: String) {
        produtosArrayList.add(Produto(
                listaArrayList[position].nome,
                removeCurrency(preco).toDouble(),
                qtde.toInt(),
                noCarrinho = true,
                isSelected = false
        ))
    }

    // Método para remover a moeda e deixar como uma string para se tornar float
    private fun removeCurrency(text: String): String {
        return text.replace("R", "").replace("$", "").replace(",", ".")
    }

    private fun initializeLogic() {
        checaListaVazia()

        // seta o que deve ou não está visivel inicialmente
        tabadd!!.visibility = View.VISIBLE
        tabshoplist!!.visibility = View.GONE
        footer!!.visibility = View.VISIBLE
        footer2!!.visibility = View.GONE
        tab1adapter!!.setOnClickListener { _tab1click() }
        tab2adapter!!.setOnClickListener { _tab2click() }
        carrinhoLista!!.isVerticalScrollBarEnabled = false
        comprasLista!!.isVerticalScrollBarEnabled = false

        // altera a cor da status bar
        val w = this.window
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        w.statusBarColor = Color.parseColor("#F57C00")

        // Altera cor dos botoes
        add!!.background.setColorFilter(Color.parseColor("#AD1457"), PorterDuff.Mode.MULTIPLY)
        add2!!.background.setColorFilter(Color.parseColor("#AD1457"), PorterDuff.Mode.MULTIPLY)

        title = "FeirApp"

        date = Calendar.getInstance()

        carregarPreferencias()

        //listview1!!.adapter = CarrinhoAdapter(produtosArrayList)
        //(listview1!!.adapter as BaseAdapter).notifyDataSetChanged()

        carrinhoLista?.adapter = CarrinhoAdapter(produtosArrayList, this)

        comprasLista?.adapter = ListaAdapter(listaArrayList, this)

        atualizarTotal()

    }

    // SALVA PRODUTOS EM JSON
    private fun salvarPreferencias() {
        val prefsEditor = produtosPref?.edit()
        val gson = Gson()
        val jsonCarrinho = gson.toJson(produtosArrayList)
        val jsonLista = gson.toJson(listaArrayList)
        prefsEditor?.remove("carrinho")
        prefsEditor?.remove("lista")
        prefsEditor?.putString("carrinho", jsonCarrinho)
        prefsEditor?.putString("lista", jsonLista)
        prefsEditor?.apply()
    }

    fun deletaProduto(position: Int, adapter: CarrinhoAdapter) {
        Log.d("Produtos: ", produtosArrayList.toString())
        produtosArrayList.removeAt(position)
        salvarPreferencias()
        adapter.notifyDataSetChanged()

        atualizarTotal()
    }

    // RECUPERA PRODUTOS DO ARQUIVO
    private fun carregarPreferencias() {
        val gson = Gson()
        val jsonProduto = produtosPref?.getString("carrinho", "")
        val jsonLista = produtosPref?.getString("lista", "")
        val type = object : TypeToken<ArrayList<Produto>>() {}.type
        produtosArrayList = gson.fromJson(jsonProduto, type) ?: produtosArrayList
        listaArrayList = gson.fromJson(jsonLista, type) ?: listaArrayList
    }

    // NOVO METODO CARREGAR TOTAL
    fun atualizarTotal() {
        Log.d("Produtos: ", produtosArrayList.toString())
        val precoTotal: Double = produtosArrayList.sumByDouble{ it.getTotal() }
        total!!.text = DecimalFormat("#,##0,00").format(precoTotal * 100)

        checaListaVazia()
    }
    // método pra limpar a feira
    private fun limparFeira() {
        produtosArrayList.clear()
        atualizarTotal()
    }

    /*

    // método pra salvar o carrinho
    private fun _save_feira() {
        date = Calendar.getInstance()
        filename = "feira_" + SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.US).format(date.time) + ".txt"
        for (_repeat10 in 0 until ord_list.size) {
            stringao = stringao + feira_arq!!.getString(key.toLong().toString(), "") + "\n"
            key++
        }
        //code by zTioLuh
        try {
            val myFile = File("$externalCacheDir/$filename")
            myFile.createNewFile()
            val fOut = FileOutputStream(myFile)
            val myOutWriter = OutputStreamWriter(fOut)
            myOutWriter.append(stringao)
            myOutWriter.close()
            fOut.close()
            Toast.makeText(baseContext, "Salvo em /sdcard/Android/data/br.feira/cache/$filename", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    // método pra salvar dados da lista de compras
    private fun _save_data2(_key: Double) {
        shop_arq!!.edit().putString(_key.toLong().toString(), item_list2[_key.toInt()]["item"].toString()).apply()
    }

    // método pra carregar dados para lista de compras
    private fun _load2() {
        ord2++
        if (shop_arq!!.getString((ord2 - 1).toLong().toString(), "") == "") {

        } else {
            run {
                val _item = HashMap<String, Any>()
                _item["ord"] = ord2.toLong().toString() + "."
                ord_list2.add(_item)
            }

            run {
                val _item = HashMap<String, Any>()
                _item["item"] = shop_arq!!.getString((ord2 - 1).toLong().toString(), "")
                item_list2.add(_item)
            }

            _load2()
        }
    }

    // método pra deletar itens
    private fun _del_item(_pos: Double) {
        showMessage("Item " + (item_list[_pos.toInt()]["item"].toString() + " deletado!"))
        ord_list.removeAt(ord_list.size - 1)
        item_list.removeAt(_pos.toInt())
        qtde_list.removeAt(_pos.toInt())
        price_list.removeAt(_pos.toInt())
        price_t_list.removeAt(_pos.toInt())
        (listview1!!.adapter as BaseAdapter).notifyDataSetChanged()
        feira_arq!!.edit().remove(ord_list.size.toLong().toString()).apply()
        _update(_pos)
        atualizarTotal()
    }

    // método pra adicionar da lista de compras pro carrinho
    private fun _addtocart(_pos: Double, _preco: String, _qtde: String) {
        if (_preco == "" || _qtde == "") {
            showMessage("Há campos vazios!")
        } else {
            ord = ord_list.size.toDouble()
            ord++
            run {
                val _item = HashMap<String, Any>()
                _item["ord"] = ord.toLong().toString() + "."
                ord_list.add(_item)
            }

            run {
                val _item = HashMap<String, Any>()
                _item["item"] = item_list2[_pos.toInt()]["item"].toString()
                item_list.add(_item)
            }

            run {
                val _item = HashMap<String, Any>()
                _item["qtde"] = _qtde
                qtde_list.add(_item)
            }

            run {
                val _item = HashMap<String, Any>()
                _item["preco"] = _preco
                price_list.add(_item)
            }

            run {
                val _item = HashMap<String, Any>()
                _item["preco_t"] = (java.lang.Double.parseDouble(_qtde) * java.lang.Double.parseDouble(_preco)).toString()
                price_t_list.add(_item)
            }

            atualizarTotal()
            (listview1!!.adapter as BaseAdapter).notifyDataSetChanged()
            _save_data(ord - 1)
            showMessage("Adicionado ao carrinho!")
        }
    }
    */
    // clique na tab carrinho
    private fun _tab1click() {
        tabadd!!.visibility = View.VISIBLE
        tabshoplist!!.visibility = View.GONE
        footab2!!.setBackgroundColor(-0xa8400)
        footab1!!.setBackgroundColor(-0x1)
        footer!!.visibility = View.VISIBLE
        footer2!!.visibility = View.GONE
        tab1!!.alpha = 1.toFloat()
        tab2!!.alpha = 0.6.toFloat()
        imagecart!!.alpha = 1.toFloat()
        imagecheck!!.alpha = 0.6.toFloat()
        if (produtosArrayList.size != 0) {
            totalview!!.visibility = View.VISIBLE
        }
    }

    // clique na tab lista de compras
    private fun _tab2click() {
        tabadd!!.visibility = View.GONE
        tabshoplist!!.visibility = View.VISIBLE
        footab1!!.setBackgroundColor(-0xa8400)
        footab2!!.setBackgroundColor(-0x1)
        footer2!!.visibility = View.VISIBLE
        footer!!.visibility = View.GONE
        tab2!!.alpha = 1.toFloat()
        tab1!!.alpha = 0.6.toFloat()
        imagecart!!.alpha = 0.6.toFloat()
        imagecheck!!.alpha = 1.toFloat()
        totalview!!.visibility = View.GONE
    }

    private fun checaListaVazia() {
        if (produtosArrayList.size == 0) {
            warn1!!.visibility = View.VISIBLE
            linear_list1!!.gravity = Gravity.CENTER
            totalview!!.visibility = View.GONE
        } else {
            warn1!!.visibility = View.GONE
            linear_list1!!.gravity = Gravity.CENTER_HORIZONTAL
            if (tabadd!!.visibility == View.VISIBLE) {
                totalview!!.visibility = View.VISIBLE
            }
        }
        if (listaArrayList.size == 0) {
            warn2!!.visibility = View.VISIBLE
            linear_list2!!.gravity = Gravity.CENTER
        } else {
            warn2!!.visibility = View.GONE
            linear_list2!!.gravity = Gravity.CENTER_HORIZONTAL
        }
    }
    /*
    inner class CarrinhoAdapter(private val produtosArrayList: ArrayList<Produto>) : RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder>() {

        inner class CarrinhoViewHolder(val carrinhoView: View) : RecyclerView.ViewHolder(carrinhoView) {

            init {
            }

            fun dialog(produtosArrayList: ArrayList<Produto>, position: Int, adapter: CarrinhoAdapter) {
                carrinhoView.setOnClickListener {
                    val dialog = AlertDialog.Builder(itemView.context)

                    dialog.setTitle("O que deseja fazer?")
                    dialog.setPositiveButton("Deletar Item") { _, _ ->
                        Log.d("Produtos: ", produtosArrayList.toString())
                        deletaProduto(position, adapter)
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
            val produtoModel = produtosArrayList[position]
            (holder.carrinhoView.findViewById(R.id.produto_nome) as TextView).text = produtoModel.nome
            (holder.carrinhoView.findViewById(R.id.produto_qtde) as TextView).text = "(%s)".format(produtoModel.qtde)
            (holder.carrinhoView.findViewById(R.id.produto_preco) as TextView).text = DecimalFormat("#,##0,00").format(produtoModel.preco * 100)
            (holder.carrinhoView.findViewById(R.id.produto_preco_total) as TextView).text = DecimalFormat("#,##0,00").format(produtoModel.getTotal() * 100)

            holder.dialog(produtosArrayList, position, this)
        }

        override fun getItemCount(): Int {
            return produtosArrayList.size
        }
    }
    */
    /*
    inner class CarrinhoAdapter(private var data: ArrayList<Produto>) : BaseAdapter() {

        override fun getCount(): Int {
            return data.size
        }

        override fun getItem(i: Int): Produto {
            return data[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
            val inflater = baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var v = view
            if (v == null) {
                v = inflater.inflate(R.layout.item_list, null)
            }
            if (data[position].noCarrinho) {
                val linear1 = v!!.findViewById(R.id.linear1) as LinearLayout
                val item = v.findViewById(R.id.produto_nome) as TextView
                val linear2 = v.findViewById(R.id.linear2) as LinearLayout
                val linear3 = v.findViewById(R.id.linear3) as LinearLayout
                val preco_t = v.findViewById(R.id.produto_preco_total) as TextView
                val textview2 = v.findViewById(R.id.textview2) as TextView
                val preco = v.findViewById(R.id.produto_preco) as TextView

                item.text = produtosArrayList[position].nome + " (" + data[position].qtde + ")"
                preco.text = DecimalFormat("#,##0,00").format(data[position].preco * 100)
                preco_t.text = DecimalFormat("#,##0,00").format(data[position].getTotal() * 100)

                return v
            } else {
                v!!.visibility = View.GONE
                return v
            }
        }
    }

    */

    /* adapter da lista de compras
    inner class ListaAdapter(private var data: ArrayList<Produto>) : BaseAdapter() {

        override fun getCount(): Int {
            return data.size
        }

        override fun getItem(i: Int): Produto {
            return data[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
            val inflater = baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var v: View? = view
            if (v == null) {
                v = inflater.inflate(R.layout.shoplist, null)
            }
            if (!data[position].noCarrinho) {
                val linear1 = v!!.findViewById(R.id.linear1) as LinearLayout
                val ord2 = v.findViewById(R.id.ord2) as TextView
                val name2 = v.findViewById(R.id.name2) as TextView
                val linear2 = v.findViewById(R.id.linear2) as LinearLayout
                val checkbox2 = v.findViewById(R.id.checkbox2) as CheckBox

                name2.text = data[position].nome

                checkbox2.isChecked = data[position].isSelected

                checkbox2.setOnClickListener {
                    if (checkbox2.isChecked) {
                        val dialog_add = AlertDialog.Builder(this@MainActivity).create()
                        val inflate = layoutInflater.inflate(R.layout.check_dialog, null)
                        val editpreco = inflate.findViewById(R.id.editpreco) as EditText
                        val editqtde = inflate.findViewById(R.id.editqtde) as EditText
                        val butcancel = inflate.findViewById(R.id.button1) as Button
                        val butadd = inflate.findViewById(R.id.button2) as Button

                        dialog_add.setView(inflate)
                        editpreco.isFocusableInTouchMode = true
                        editpreco.isFocusable = true
                        editpreco.requestFocus()
                        editpreco.setSelection(0)
                        dialog_add.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

                        butadd.setOnClickListener {
                           showMessage("ADD!")
                        }

                        butcancel.setOnClickListener { dialog_add.dismiss() }

                        dialog_add.show()

                        data[position].isSelected = true
                    } else {
                        data[position].isSelected = false
                    }
                }
                return v
            } else {
                v!!.visibility = View.GONE
                return v
            }
        }
    }
*/
    // Método que tranforma o texto em tempo real para monetário
    inner class NumberTextWatcher(private val campo: EditText) : TextWatcher {

        private var isUpdating = false
        // Pega a formatacao do sistema, se for brasil R$ se EUA US$
        private val nf = NumberFormat.getCurrencyInstance()

        override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                   after: Int) {
            var s = s
            // Evita que o método seja executado varias vezes.
            // Se tirar ele entre em loop
            if (isUpdating) {
                isUpdating = false
                return
            }

            isUpdating = true
            var str = s.toString()
            // Verifica se já existe a máscara no texto.
            val hasMask = (str.contains("R$") || str.contains("$")) && (str.contains(".") || str.contains(","))
            // Verificamos se existe máscara
            if (hasMask) {
                // Retiramos a máscara.
                str = str.replace("[R$]".toRegex(), "").replace("[,]".toRegex(), "")
                        .replace("[.]".toRegex(), "")
            }

            try {
                // Transformamos o número que está escrito no EditText em monetário
                str = nf.format(java.lang.Double.parseDouble(str) / 100)
                campo.setText(str)
                campo.setSelection(campo.text.length)
            } catch (e: NumberFormatException) {
                s = ""
            }

        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            campo.setText("R$0,00")
        }

        override fun afterTextChanged(s: Editable) {
            // Não utilizado
        }
    }

    // created automatically
    private fun showMessage(_s: String) {
        Toast.makeText(applicationContext, _s, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun create(): MainActivity = MainActivity()
    }
}

