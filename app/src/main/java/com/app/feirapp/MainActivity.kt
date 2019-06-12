package com.app.feirapp

import android.app.*
import android.os.*
import android.view.*
import android.widget.*
import android.content.*
import android.graphics.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*
import java.text.*
import java.text.NumberFormat
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.R.id.edit
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.collections.ArrayList


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
    private var nome: EditText? = null
    private var preco: EditText? = null
    private var qtde: EditText? = null
    private var warn1: TextView? = null
    private var listview1: ListView? = null
    private var inputproduto: EditText? = null
    private var warn2: TextView? = null
    private var listview2: ListView? = null
    private var add2: Button? = null
    private var add: Button? = null
    private var total: TextView? = null

    // Declarando variáveis de dados
    private var ord = 0.0
    private var n_total = 0.0
    private var n2 = 0.0
    private var n3 = 0.0
    private var stringao = ""
    private var key = 0.0
    private var filename = ""
    private var ord2 = 0.0

    // Declarando hashmaps de produtos
    private val ord_list = ArrayList<HashMap<String, Any>>()
    private val item_list = ArrayList<HashMap<String, Any>>()
    private val qtde_list = ArrayList<HashMap<String, Any>>()
    private val price_list = ArrayList<HashMap<String, Any>>()
    private val price_t_list = ArrayList<HashMap<String, Any>>()
    private val ord_list2 = ArrayList<HashMap<String, Any>>()
    private val item_list2 = ArrayList<HashMap<String, Any>>()

    // Array de produto para ser trocado pelos array lists genéricos acima futuramente
    private var produtos = ArrayList<Produto>()

    // Declarando shared preferences
    private var feira_arq: SharedPreferences? = null
    private var item_dialog: AlertDialog.Builder? = null
    private var shop_arq: SharedPreferences? = null
    private var checks: SharedPreferences? = null

    private var produtosPref: SharedPreferences? = null

    // Declarando data atual
    private var date = Calendar.getInstance()

    private val displayWidthPixels: Int
        get() = resources.displayMetrics.widthPixels

    private val displayHeightPixels: Int
        get() = resources.displayMetrics.heightPixels

    // Instanciando view inicial
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        initialize()
        initializeLogic()
    }

    private fun initialize() {
        // Inicializando todas as views e construtores
        menu = findViewById(R.id.menu) as Button
        tab1adapter = findViewById(R.id.tab1adapter) as LinearLayout
        footab1 = findViewById(R.id.footab1) as LinearLayout
        imagecart = findViewById(R.id.imagecart) as ImageView
        tab1 = findViewById(R.id.tab1) as TextView
        tab2adapter = findViewById(R.id.tab2adapter) as LinearLayout
        footab2 = findViewById(R.id.footab2) as LinearLayout
        imagecheck = findViewById(R.id.imagecheck) as ImageView
        tab2 = findViewById(R.id.tab2) as TextView
        tabadd = findViewById(R.id.tabadd) as LinearLayout
        tabshoplist = findViewById(R.id.tabshoplist) as LinearLayout
        nome = findViewById(R.id.nome) as EditText
        preco = findViewById(R.id.preco) as EditText
        qtde = findViewById(R.id.qtde) as EditText
        warn1 = findViewById(R.id.warn1) as TextView
        listview1 = findViewById(R.id.listview1) as ListView
        inputproduto = findViewById(R.id.inputproduto) as EditText
        warn2 = findViewById(R.id.warn2) as TextView
        listview2 = findViewById(R.id.listview2) as ListView
        add2 = findViewById(R.id.add2) as Button
        add = findViewById(R.id.add) as Button
        total = findViewById(R.id.total) as TextView
        item_dialog = AlertDialog.Builder(this)

        // Esse método altera o preço na medida que editamos
        preco!!.addTextChangedListener(NumberTextWatcher(preco!!))

        // Carregando arquivos
        shop_arq = getSharedPreferences("shoplist.txt", MODE_PRIVATE)
        checks = getSharedPreferences("checks", MODE_PRIVATE)

        produtosPref = getSharedPreferences("feiraPrefs", MODE_PRIVATE)

        // BOTÃO ADD NO CARRINHO
        add!!.setOnClickListener {
            // VERIFICA SE OS CAMPOS ESTÃO VAZIOS
            if (nome!!.text.toString() == "" || preco!!.text.toString() == "" || qtde!!.text.toString() == "") {
                showMessage("Há campos vazios!")
            } else {

                // ADICIONA UM NOVO PRODUTO NO CARRINHO
                produtos.add(Produto(
                    nome!!.text.toString(),
                    _removeCurrency(preco!!.text.toString()).toDouble(),
                    qtde!!.text.toString().toInt(),
                    noCarrinho = true,
                    isSelected = false
                    )
                )

                // ATUALIZA PREÇO TOTAL
                atualizarTotal()


                nome!!.setText("")
                preco!!.setText("")
                qtde!!.setText("")

                // Salvando dados no arquivo
                salvarPreferencias()

                nome!!.isFocusableInTouchMode = true
                nome!!.isFocusable = true
                nome!!.requestFocus()
                nome!!.setSelection(0)
            }
        }

        // Criando um click listener para itens da listview1
        listview1!!.onItemClickListener = AdapterView.OnItemClickListener { _parent, _view, _position, _id ->
            item_dialog!!.setTitle("O que deseja fazer?")
            item_dialog!!.setPositiveButton("Deletar Item") { _dialog, _which -> _del_item(_position.toDouble()) }
            item_dialog!!.setNegativeButton("Cancelar") { _dialog, _which -> }
            item_dialog!!.create().show()
        }

        // Inicializando um popup menu e criando um click listener para seus itens
        menu!!.setOnClickListener {
            val popupMenu = PopupMenu(this@MainActivity, this@MainActivity.menu, 10)
            val menu = popupMenu.menu
            menu.add("Salvar Feira")
            menu.add("Limpar")
            menu.add("Sobre")
            popupMenu.setOnMenuItemClickListener { item ->
                val i = item.title.toString()

                when (i) {
                    "Salvar Feira" -> {
                        _save_feira()
                        true
                    }
                    "Limpar" -> {
                        _clear_feira()
                        true
                    }
                    "Sobre" -> true
                    else -> false
                }
            }
            // Mostrando o menu
            popupMenu.show()
        }

        // Setando click listener para o botão de adicionar na janela da lista de compras
        add2!!.setOnClickListener {
            if (inputproduto!!.text.toString() == "") {
                showMessage("Há campos vazios!")
            } else {
                ord2 = ord_list2.size.toDouble()
                ord2++

                // FUTURAMENTE MÉTODO PARA DATACLASS PRODUTO

                run {
                    val _item = HashMap<String, Any>()
                    _item["ord"] = ord2.toLong().toString() + "."
                    ord_list2.add(_item)
                }

                run {
                    val _item = HashMap<String, Any>()
                    _item["item"] = inputproduto!!.text.toString()
                    item_list2.add(_item)
                }

                (listview2!!.adapter as BaseAdapter).notifyDataSetChanged()
                inputproduto!!.setText("")
                _save_data2(ord2 - 1)
                checaListaVazia()

                inputproduto!!.isFocusableInTouchMode = true
                inputproduto!!.isFocusable = true
                inputproduto!!.requestFocus()
                inputproduto!!.setSelection(0)
            }
        }
    }

    // Método para remover a moeda e deixar como uma string para se tornar float
    private fun _removeCurrency(text: String): String {
        return text.replace("R", "").replace("$", "").replace(",", ".")
    }

    private fun initializeLogic() {
        checaListaVazia()

        // seta o que deve ou não está visivel inicialmente
        tabadd!!.visibility = View.VISIBLE
        tabshoplist!!.visibility = View.GONE
        add!!.visibility = View.VISIBLE
        add2!!.visibility = View.GONE
        tab1adapter!!.setOnClickListener { _tab1click() }
        tab2adapter!!.setOnClickListener { _tab2click() }
        listview1!!.isVerticalScrollBarEnabled = false
        listview2!!.isVerticalScrollBarEnabled = false

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

        listview1!!.adapter = CarrinhoAdapter(produtos)
        (listview1!!.adapter as BaseAdapter).notifyDataSetChanged()

        listview2!!.adapter = Listview2Adapter(ord_list2)
        (listview2!!.adapter as BaseAdapter).notifyDataSetChanged()

        atualizarTotal()

        _load2()

    }

    // SALVA PRODUTOS EM JSON
    private fun salvarPreferencias() {
        val prefsEditor = produtosPref?.edit()
        val gson = Gson()
        val json = gson.toJson(produtos) //tasks is an ArrayList instance variable
        Log.v("JSON Save", json)
        prefsEditor?.putString("carrinho", json)
        prefsEditor?.apply()
        Log.v("Array List", produtos.toString())
    }

    // RECUPERA PRODUTOS DO ARQUIVO
    private fun carregarPreferencias() {
        val gson = Gson()
        val json = produtosPref?.getString("carrinho", "")
        Log.v("JSON Load", json)
        val type = object : TypeToken<ArrayList<Produto>>() {}.type
        produtos = gson.fromJson(json, type) ?: produtos
        Log.v(type.toString(), produtos.toString())
    }

    // método pra inserir texto do arquivo nas hashmaps
    private fun _split(_string: String) {
        val arr = _string.split(";".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        run {
            val _item = HashMap<String, Any>()
            _item["ord"] = arr[0]
            ord_list.add(_item)
        }
        run {
            val _item = HashMap<String, Any>()
            _item["item"] = arr[1]
            item_list.add(_item)
        }
        run {
            val _item = HashMap<String, Any>()
            _item["qtde"] = arr[2]
            qtde_list.add(_item)
        }
        run {
            val _item = HashMap<String, Any>()
            _item["preco"] = arr[3]
            price_list.add(_item)
        }
        run {
            val _item = HashMap<String, Any>()
            _item["preco_t"] = arr[4]
            price_t_list.add(_item)
        }
    }

    // método pra carregar o total
    private fun _load_total() {
        n2 = 0.0
        n_total = 0.0
        for (_repeat11 in 0 until price_t_list.size) {
            n_total = n_total + java.lang.Double.parseDouble(price_t_list[n2.toInt()]["preco_t"].toString())
            n2++
        }
        total!!.text = DecimalFormat("#,##0,00").format(n_total * 100)
        checaListaVazia()
    }

    // NOVO METODO CARREGAR TOTAL
    private fun atualizarTotal() {
        val precoTotal: Double = produtos.sumByDouble{ it.getTotal() }
        total!!.text = DecimalFormat("#,##0,00").format(precoTotal * 100)
        checaListaVazia()
    }

    // método pra atualizar arquivo em caso de delete
    private fun _update(_index: Double) {
        if (ord_list.size.toLong().toString() == "1") {
            feira_arq!!.edit().remove(_index.toLong().toString()).commit()
        } else {
            n3 = 0.0
            for (_repeat51 in 0 until ord_list.size) {
                _save_data(n3)
                n3++
            }
        }
    }

    // método pra salvar dados no arquivo
    private fun _save_data(_i: Double) {
        feira_arq!!.edit().putString(_i.toLong().toString(),
                ord_list[_i.toInt()]["ord"].toString() + (";" + (item_list[_i.toInt()]["item"].toString() + (";" + (qtde_list[_i.toInt()]["qtde"].toString() + (";" + (price_list[_i.toInt()]["preco"].toString() + (";" + price_t_list[_i.toInt()]["preco_t"].toString())))))))).apply()
    }

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

    // método pra limpar a feira
    private fun _clear_feira() {
        n3 = 0.0
        for (_repeat16 in 0 until ord_list.size) {
            feira_arq!!.edit().remove(n3.toLong().toString()).apply()
            n3++
        }
        ord_list.clear()
        item_list.clear()
        qtde_list.clear()
        price_list.clear()
        price_t_list.clear()
        (listview1!!.adapter as BaseAdapter).notifyDataSetChanged()
        _load_total()
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
        _load_total()
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

            _load_total()
            (listview1!!.adapter as BaseAdapter).notifyDataSetChanged()
            _save_data(ord - 1)
            showMessage("Adicionado ao carrinho!")
        }
    }

    // clique na tab carrinho
    private fun _tab1click() {
        tabadd!!.visibility = View.VISIBLE
        tabshoplist!!.visibility = View.GONE
        footab2!!.setBackgroundColor(-0xa8400)
        footab1!!.setBackgroundColor(-0x1)
        add!!.visibility = View.VISIBLE
        add2!!.visibility = View.GONE
        tab1!!.alpha = 1.toFloat()
        tab2!!.alpha = 0.6.toFloat()
        imagecart!!.alpha = 1.toFloat()
        imagecheck!!.alpha = 0.6.toFloat()
    }

    // clique na tab lista de compras
    private fun _tab2click() {
        tabadd!!.visibility = View.GONE
        tabshoplist!!.visibility = View.VISIBLE
        footab1!!.setBackgroundColor(-0xa8400)
        footab2!!.setBackgroundColor(-0x1)
        add2!!.visibility = View.VISIBLE
        add!!.visibility = View.GONE
        tab2!!.alpha = 1.toFloat()
        tab1!!.alpha = 0.6.toFloat()
        imagecart!!.alpha = 0.6.toFloat()
        imagecheck!!.alpha = 1.toFloat()
    }

    private fun checaListaVazia() {
        if (produtos.size == 0) {
            warn1!!.visibility = View.VISIBLE
        } else {
            warn1!!.visibility = View.GONE
        }
        if (ord_list2.size == 0) {
            warn2!!.visibility = View.VISIBLE
        } else {
            warn2!!.visibility = View.GONE
        }
    }

    inner class CarrinhoAdapter(var data: ArrayList<Produto>) : BaseAdapter() {

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
                v = inflater.inflate(R.layout.item_list, null)
            }
            val linear1 = v!!.findViewById(R.id.linear1) as LinearLayout
            val item = v.findViewById(R.id.item) as TextView
            val linear2 = v.findViewById(R.id.linear2) as LinearLayout
            val linear3 = v.findViewById(R.id.linear3) as LinearLayout
            val preco_t = v.findViewById(R.id.preco_t) as TextView
            val textview2 = v.findViewById(R.id.textview2) as TextView
            val preco = v.findViewById(R.id.preco) as TextView

            item.text = produtos[position].nome + " (" + produtos[position].qtde + ")"
            Log.v("Item inserido", produtos[position].nome + " (" + produtos[position].qtde + ")")
            preco.text = DecimalFormat("#,##0,00").format(produtos[position].preco * 100)
            Log.v("Preco inserido", produtos[position].preco.toString())
            preco_t.text = DecimalFormat("#,##0,00").format(produtos[position].getTotal() * 100)
            Log.v("Total inserido", produtos[position].getTotal().toString())

            return v
        }
    }

    // adapter da lista de compras
    inner class Listview2Adapter(internal var _data: ArrayList<HashMap<String, Any>>) : BaseAdapter() {

        override fun getCount(): Int {
            return _data.size
        }

        override fun getItem(_i: Int): HashMap<String, Any> {
            return _data[_i]
        }

        override fun getItemId(_i: Int): Long {
            return _i.toLong()
        }

        override fun getView(_position: Int, _view: View?, _viewGroup: ViewGroup): View {
            val _inflater = baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var _v: View? = _view
            if (_v == null) {
                _v = _inflater.inflate(R.layout.shoplist, null)
            }
            val linear1 = _v!!.findViewById(R.id.linear1) as LinearLayout
            val ord2 = _v.findViewById(R.id.ord2) as TextView
            val name2 = _v.findViewById(R.id.name2) as TextView
            val linear2 = _v.findViewById(R.id.linear2) as LinearLayout
            val checkbox2 = _v.findViewById(R.id.checkbox2) as CheckBox

            ord2.text = ord_list2[_position]["ord"].toString()
            name2.text = item_list2[_position]["item"].toString()
            if (checks!!.getString(_position.toLong().toString(), "") == "c") {
                checkbox2.isChecked = true
            } else {
                checkbox2.isChecked = false
            }
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
                        _addtocart(_position.toDouble(), editpreco.text.toString(), editqtde.text.toString())
                        dialog_add.dismiss()
                    }

                    butcancel.setOnClickListener { dialog_add.dismiss() }

                    dialog_add.show()

                    checks!!.edit().putString(_position.toLong().toString(), "c").apply()
                } else {
                    checks!!.edit().putString(_position.toLong().toString(), "u").apply()
                }
            }
            return _v
        }
    }

    // Método que tranforma o texto em tempo real para monetário
    inner class NumberTextWatcher(internal val campo: EditText) : TextWatcher {

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

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                       after: Int) {
            // Não utilizado
        }

        override fun afterTextChanged(s: Editable) {
            // Não utilizado
        }
    }

    // created automatically
    private fun showMessage(_s: String) {
        Toast.makeText(applicationContext, _s, Toast.LENGTH_SHORT).show()
    }

}
