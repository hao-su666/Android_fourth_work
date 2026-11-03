package cn.edu.sicnu.cs.sjh.fourth_work

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView

class CardAdapter(
    private val context: Context,
    private val model: CardGameModel,
    private val onCardClick: (Int)->Unit
):BaseAdapter() {

    override fun getCount(): Int = model.getCardCount()

    override fun getItem(position: Int): Any = position

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val btn: Button
        if(convertView == null){
            btn = View.inflate(context,R.layout.item_card,null) as Button
        }else{
            btn = convertView as Button
        }

        // 牌的高度取列宽，让每张牌成为正方形并铺满网格
        if (parent is GridView && parent.width > 0) {
            val columns = parent.numColumns
            val spacing = parent.horizontalSpacing * (columns - 1)
            val available = parent.width - parent.paddingLeft - parent.paddingRight - spacing
            val cellSize = available / columns
            if (cellSize > 0 && btn.layoutParams?.height != cellSize) {
                btn.layoutParams = AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    cellSize
                )
            }
        }

        if(model.isCardOpen(position)){
            btn.text = model.getCardValue(position).toString()
        }else{
            btn.text = "?"
        }

        btn.setOnClickListener {
            onCardClick.invoke(position)
        }
        return btn
    }
}
