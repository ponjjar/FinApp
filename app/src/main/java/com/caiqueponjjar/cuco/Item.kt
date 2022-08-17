package com.caiqueponjjar.cuco;

import android.graphics.Color

public class Item {
    var itemType: String = " "
    var itemTitle : String = " "
    var itemSubtitle : String = " "
    var itemCategory : Int = 0

    var itemColor : Int = Color.parseColor("#2291e0");
    var itemKey : String = " "
    constructor(itemTitle: String, itemSubtitle : String, itemColor : Int, itemKey : String, itemCategory : Int, itemType: String) {
        this.itemTitle = itemTitle
        this.itemSubtitle = itemSubtitle
        this.itemKey = itemKey
        this.itemColor = itemColor

        this.itemCategory = itemCategory
        this.itemType = itemType
    }
}