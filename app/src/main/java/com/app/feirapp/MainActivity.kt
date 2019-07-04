package com.app.feirapp

import android.app.*
import android.content.Intent
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
import android.widget.Toast
import android.view.View.OnFocusChangeListener
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

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

        carregarPreferencias()
        checaListaVazia()

        input_preco.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                input_preco.setText("R$0,00")
                input_preco.setSelection(input_preco.text.length)
            }
        }

        input_qtde.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                input_qtde.hint = "1"
                input_qtde.setSelection(input_qtde.text.length)
        }

        limiteView.setOnClickListener {
            // Abrir popup para definição de valor limite
            TODO("Popup para definir limite de valor")
        }

        share.setOnClickListener {
            // Chamar login para compartilhamento da feira
            // INICIANDO ATIVIDADE
            val intent = Intent(this, ShareActivity::class.java).apply {}
            // MANDANDO A LISTA
            //intent.putParcelableArrayListExtra("comprasLista", produtosArrayList)
            startActivity(intent)
        }

        input_qtde!!.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                if (input_nome!!.text.toString() == "" || input_preco!!.text.toString() == "") {
                    showMessage("Há campos vazios!")
                } else {
                    if (input_qtde!!.text.toString() == "") {
                        input_qtde!!.setText("1")
                    }

                    // ADICIONA UM NOVO PRODUTO NO CARRINHO
                    produtosArrayList.add(Produto(
                            input_nome!!.text.toString(),
                            removeCurrency(input_preco!!.text.toString()).toDouble(),
                            input_qtde!!.text.toString().toInt(),
                            isSelected = false
                    ))

                    // ATUALIZA PREÇO TOTAL
                    atualizarTotal()

                    input_nome!!.setText("")
                    input_preco!!.setText("")
                    input_qtde!!.setText("")
                    input_qtde.hint = "Qtde."

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

                when (item.title.toString()) {
                    "Salvar Feira" -> {
                        salvarFeira()
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
                isSelected = false
        ))

        atualizarTotal()
        salvarPreferencias()
    }

    // Método para remover a moeda e deixar como uma string para se tornar float
    private fun removeCurrency(text: String): String {
        return text.replace("R", "").replace("$", "").replace(",", ".")
    }

    private fun initializeLogic() {

        // seta o que deve ou não está visivel inicialmente
        _tab1click()
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

    fun editaProduto(position: Int, adapter: CarrinhoAdapter) {
        // Edita o produto
        TODO("Editar produto")
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
    private fun atualizarTotal() {
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

    // método pra salvar o carrinho
    private fun salvarFeira() {
        TODO("Salvar feira em arquivo")
    }

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
    }

    private fun checaListaVazia() {
        if (produtosArrayList.size == 0) {
            warn1!!.visibility = View.VISIBLE
            linear_list1!!.gravity = Gravity.CENTER
        } else {
            warn1!!.visibility = View.GONE
            linear_list1!!.gravity = Gravity.CENTER_HORIZONTAL
        }
        if (listaArrayList.size == 0) {
            warn2!!.visibility = View.VISIBLE
            linear_list2!!.gravity = Gravity.CENTER
        } else {
            warn2!!.visibility = View.GONE
            linear_list2!!.gravity = Gravity.CENTER_HORIZONTAL
        }
    }

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

    companion object {
        fun create(): MainActivity = MainActivity()
    }
}

