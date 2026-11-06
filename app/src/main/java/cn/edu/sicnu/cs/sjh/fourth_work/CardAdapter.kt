package cn.edu.sicnu.cs.sjh.fourth_work

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * 翻牌游戏的 RecyclerView 适配器。
 *
 * @param model 游戏模型，适配器只负责把模型状态渲染到界面上。
 * @param onCardClick 卡牌点击回调，交给控制器 Fragment/Activity 处理。
 */
class CardAdapter(
    private val model: CardMatchingGame,
    private val onCardClick: (Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        val holder = CardViewHolder(view)
        holder.tvCard.setOnClickListener {
            val position = holder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onCardClick(position)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val context = holder.itemView.context
        val card = model.getCard(position)

        when {
            card.isMatched -> {
                holder.tvCard.text = card.value.toString()
                holder.tvCard.setTextColor(
                    ContextCompat.getColor(context, R.color.card_text_matched)
                )
                holder.tvCard.setBackgroundResource(R.drawable.bg_card_matched)
            }

            card.isChosen -> {
                holder.tvCard.text = card.value.toString()
                holder.tvCard.setTextColor(
                    ContextCompat.getColor(context, R.color.card_text_face)
                )
                holder.tvCard.setBackgroundResource(R.drawable.bg_card_front)
            }

            else -> {
                holder.tvCard.text = context.getString(R.string.card_back)
                holder.tvCard.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.tvCard.setBackgroundResource(R.drawable.bg_card_back)
            }
        }

        holder.tvCard.isEnabled = !card.isMatched
    }

    override fun getItemCount(): Int = model.cardCount

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCard: TextView = itemView.findViewById(R.id.tv_card)
    }
}
