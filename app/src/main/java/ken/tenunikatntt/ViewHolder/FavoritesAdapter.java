package ken.tenunikatntt.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import ken.tenunikatntt.Common.Common;
import ken.tenunikatntt.Database.Database;
import ken.tenunikatntt.Interface.ItemClickListener;
import ken.tenunikatntt.KainDetail;
import ken.tenunikatntt.KainList;
import ken.tenunikatntt.Model.Favorites;
import ken.tenunikatntt.Model.Kain;
import ken.tenunikatntt.Model.Order;
import ken.tenunikatntt.R;

/**
 * Created by Emilken18 on 11/28/2018.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder>{

    private Context context;
    private List<Favorites> favoritesList;

    public FavoritesAdapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.favorites_item,parent,false);
        return new FavoritesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder viewHolder, final int position) {

        viewHolder.kain_name.setText(favoritesList.get(position).getKainName());
        viewHolder.kain_price.setText(String.format("Rp %s", favoritesList.get(position).getKainPrice().toString()));
        Picasso.with(context).load(favoritesList.get(position).getKainImage())
                .into(viewHolder.kain_image);

        //Quick Cart

        viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isExists = new Database(context).checkKainExists(favoritesList.get(position).getKainId(), Common.currentUser.getPhone());
                if (!isExists) {
                    new Database(context).addToCart(new Order(
                            Common.currentUser.getPhone(),
                            favoritesList.get(position).getKainId(),
                            favoritesList.get(position).getKainName(),
                            "1",
                            favoritesList.get(position).getKainPrice(),
                            favoritesList.get(position).getKainDiscount(),
                            favoritesList.get(position).getKainImage()
                    ));
                } else {
                    new Database(context).increaseCart(Common.currentUser.getPhone(),
                            favoritesList.get(position).getKainId());
                }

                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        final Favorites local = favoritesList.get(position);
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                // start aktivity baru
                Intent kainDetail = new Intent(context, KainDetail.class);
                kainDetail.putExtra("menuId", favoritesList.get(position).getKainId()); // Kirim Kain Id ke activity baru
                context.startActivity(kainDetail);
            }
        });

    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }


    public void removeItem (int position)
    {
        favoritesList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem (Favorites item, int position)
    {
        favoritesList.add(position,item);
        notifyItemInserted(position);
    }

    public Favorites getItem (int position)
    {
        return favoritesList.get(position);
    }
}
