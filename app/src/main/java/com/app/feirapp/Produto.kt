package com.app.feirapp

class Produto(
    var nome: String,
    var preco: Double,
    var qtde: Int,
    var noCarrinho: Boolean,
    var isSelected: Boolean
){
    fun getTotal(): Double{
        return this.preco * this.qtde
    }
}
