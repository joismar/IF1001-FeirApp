package com.app.feirapp

import android.os.Parcel
import android.os.Parcelable

// O PRODUTO TA COMO PARCELABLE
class Produto(
    var nome: String = "",
    var preco: Double = 0.0,
    var qtde: Int = 0,
    var isSelected: Boolean = false) : Parcelable{

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readDouble(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte())

    fun getTotal(): Double{
        return this.preco * this.qtde
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nome)
        parcel.writeDouble(preco)
        parcel.writeInt(qtde)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Produto> {
        override fun createFromParcel(parcel: Parcel): Produto {
            return Produto(parcel)
        }

        override fun newArray(size: Int): Array<Produto?> {
            return arrayOfNulls(size)
        }
    }
}
