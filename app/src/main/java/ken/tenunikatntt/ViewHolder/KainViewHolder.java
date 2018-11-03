package ken.tenunikatntt.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ken.tenunikatntt.Interface.ItemClickListener;
import ken.tenunikatntt.R;

/**
 * Created by Emilken18 on 6/12/2018.
 */

public class KainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView kain_name, kain_price;
    public ImageView kain_image, fav_image, share_image, quick_cart;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public KainViewHolder(View itemView) {
        super(itemView);

        kain_name = (TextView) itemView.findViewById(R.id.kain_name);
        kain_image = (ImageView) itemView.findViewById(R.id.kain_image);
        fav_image = (ImageView) itemView.findViewById(R.id.fav);
        kain_price = (TextView) itemView.findViewById(R.id.kain_price);
//        share_image = (ImageView)itemView.findViewById(R.id.btnShare);
        quick_cart = (ImageView) itemView.findViewById(R.id.btn_quick_cart);


        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
