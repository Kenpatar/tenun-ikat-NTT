 package ken.tenunikatntt;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ken.tenunikatntt.Common.Common;
import ken.tenunikatntt.Database.Database;
import ken.tenunikatntt.Interface.ItemClickListener;
import ken.tenunikatntt.Model.Kain;
import ken.tenunikatntt.Model.Order;
import ken.tenunikatntt.ViewHolder.KainViewHolder;

 public class SearchActivity extends AppCompatActivity {

     FirebaseRecyclerAdapter<Kain, KainViewHolder> adapter;
    //Pencarian Kain Tenun
    FirebaseRecyclerAdapter<Kain, KainViewHolder> searchAdapter;
     List<String> suggestList = new ArrayList<>();
     MaterialSearchBar materialSearchBar;

     RecyclerView recyclerView;
     RecyclerView.LayoutManager layoutManager;

     FirebaseDatabase database;
     DatabaseReference kainList;

     //Favorites
     Database localDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Firebase
        database = FirebaseDatabase.getInstance();
        kainList = database.getReference("Kains");

        //Local DB
        localDB = new Database(this);

        recyclerView = findViewById(R.id.recycler_search);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        //Search
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Masukan nama kain");
        //MaterialSearchBar.setSpeechMode(false); Tidak dibutuhkan lagi karena sudah di define pada file XML
        loadSuggest(); //Tulis fungsi untuk ambil data suggest dari firebase

        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //ketika user type their text, we will change suggest list

                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) //Loop in suggest list
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //Ketika SearchBar ditutup
                //kembali ke Adapter awal
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //Ketika SearchBar finish
                //Menampilkan hasil pencarian
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        //Load semua kain
        loadAllKains();
    }

     private void loadAllKains() {

         //Create by category id
         Query searchByName = kainList;
         //Query searchByName = kainList.orderByChild("KainId").equalTo(categoryId);
         //Create Option with query
         FirebaseRecyclerOptions<Kain> kainOptions = new FirebaseRecyclerOptions.Builder<Kain>()
                 .setQuery(searchByName, Kain.class)
                 .build();


         adapter = new FirebaseRecyclerAdapter<Kain, KainViewHolder>(kainOptions) {
             @Override
             protected void onBindViewHolder(@NonNull final KainViewHolder viewHolder, final int position, @NonNull final Kain model) {
                 viewHolder.kain_name.setText(model.getName());
                 viewHolder.kain_price.setText(String.format("Rp %s", model.getPrice().toString()));
                 Picasso.with(getBaseContext()).load(model.getImage())
                         .into(viewHolder.kain_image);

                 //Quick Cart

                 viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {

                         boolean isExists = new Database(getBaseContext()).checkKainExists(adapter.getRef(position).getKey(), Common.currentUser.getPhone());
                         if (!isExists) {
                             new Database(getBaseContext()).addToCart(new Order(
                                     Common.currentUser.getPhone(),
                                     adapter.getRef(position).getKey(),
                                     model.getName(),
                                     "1",
                                     model.getPrice(),
                                     model.getDiscount(),
                                     model.getImage()
                             ));
                         } else {
                             new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(), adapter.getRef(position).getKey());
                         }

                         Toast.makeText(SearchActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                     }
                 });

                 //Add Favorites
                 if (localDB.isFavorite(adapter.getRef(position).getKey(), Common.currentUser.getPhone()))
                     viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);

//                //Click to share
//                viewHolder.share_image.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Picasso.with(getApplicationContext())
//                                .load(model.getImage())
//                                .into(target);
//                    }
//                });

                 //Click to change state of favorites
                 viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         if (!localDB.isFavorite(adapter.getRef(position).getKey(), Common.currentUser.getPhone())) {
                             localDB.addToFavorites(adapter.getRef(position).getKey(), Common.currentUser.getPhone());
                             viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                             Toast.makeText(SearchActivity.this, "" + model.getName() + " Ditambahkan ke Favorit", Toast.LENGTH_SHORT).show();
                         } else {
                             localDB.removeFromFavorites(adapter.getRef(position).getKey(), Common.currentUser.getPhone());
                             viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                             Toast.makeText(SearchActivity.this, "" + model.getName() + " Dihapus dari Favorit", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });


                 final Kain local = model;
                 viewHolder.setItemClickListener(new ItemClickListener() {
                     @Override
                     public void onClick(View view, int position, boolean isLongClick) {
                         // start aktivity baru
                         Intent kainDetail = new Intent(SearchActivity.this, KainDetail.class);
                         kainDetail.putExtra("menuId", adapter.getRef(position).getKey()); // Kirim Kain Id ke activity baru
                         startActivity(kainDetail);
                     }
                 });
             }

             @Override
             public KainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                 View itemView = LayoutInflater.from(parent.getContext())
                         .inflate(R.layout.kain_item, parent, false);
                 return new KainViewHolder(itemView);
             }
         };
         adapter.startListening();
         //Atur adapter
         recyclerView.setAdapter(adapter);
     }

     private void startSearch(CharSequence text) {
         //Create by name
         Query searchByName = kainList.orderByChild("name").equalTo(text.toString());
         //Create Option with query
         FirebaseRecyclerOptions<Kain> kainOptions = new FirebaseRecyclerOptions.Builder<Kain>()
                 .setQuery(searchByName, Kain.class)
                 .build();

         searchAdapter = new FirebaseRecyclerAdapter<Kain, KainViewHolder>(kainOptions) {
             @Override
             protected void onBindViewHolder(@NonNull KainViewHolder viewHolder, int position, @NonNull Kain model) {
                 viewHolder.kain_name.setText(model.getName());
                 Picasso.with(getBaseContext()).load(model.getImage())
                         .into(viewHolder.kain_image);

                 final Kain local = model;
                 viewHolder.setItemClickListener(new ItemClickListener() {
                     @Override
                     public void onClick(View view, int position, boolean isLongClick) {
                         // start aktivity baru
                         Intent kainDetail = new Intent(SearchActivity.this, KainDetail.class);
                         kainDetail.putExtra("menuId", searchAdapter.getRef(position).getKey()); // Kirim Kain Id ke activity baru
                         startActivity(kainDetail);
                     }
                 });
             }

             @NonNull
             @Override
             public KainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                 View itemView = LayoutInflater.from(parent.getContext())
                         .inflate(R.layout.kain_item, parent, false);
                 return new KainViewHolder(itemView);
             }
         };
         searchAdapter.startListening();
         recyclerView.setAdapter(searchAdapter); //search adapter untuk recycler View is search result
     }

     private void loadSuggest() {
         kainList.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                             Kain item = postSnapshot.getValue(Kain.class);
                             suggestList.add(item.getName()); //add name of Kain to suggest List
                         }
                         materialSearchBar.setLastSuggestions(suggestList);
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });
     }

     @Override
     protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        if (searchAdapter != null)
            searchAdapter.stopListening();
         super.onStop();
     }
 }
