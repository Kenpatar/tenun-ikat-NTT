package ken.tenunikatntt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
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
//
//import com.facebook.CallbackManager;
//import com.facebook.share.model.SharePhoto;
//import com.facebook.share.model.SharePhotoContent;
//import com.facebook.share.widget.ShareDialog;
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
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import ken.tenunikatntt.Common.Common;
import ken.tenunikatntt.Database.Database;
import ken.tenunikatntt.Interface.ItemClickListener;
import ken.tenunikatntt.Model.Kain;
import ken.tenunikatntt.Model.Order;
import ken.tenunikatntt.ViewHolder.KainViewHolder;

public class KainList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference kainList;

    String categoryId = "";
    FirebaseRecyclerAdapter<Kain, KainViewHolder> adapter;

    //Search Fuctionallity
    FirebaseRecyclerAdapter<Kain, KainViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //Favorites
    Database localDB;

    SwipeRefreshLayout swipeRefreshLayout;

//    //Facebook share
//    CallbackManager callbackManager;
//    ShareDialog shareDialog;
//
//    //Create target from picasso
//    Target target = new Target() {
//        @Override
//        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//            //Create Photo from Bitmap
//            SharePhoto photo = new SharePhoto.Builder()
//                    .setBitmap(bitmap)
//                    .build();
//            if (ShareDialog.canShow(SharePhotoContent.class))
//            {
//                SharePhotoContent content = new SharePhotoContent.Builder()
//                        .addPhoto(photo)
//                        .build();
//                shareDialog.show(content);
//            }
//        }
//
//        @Override
//        public void onBitmapFailed(Drawable errorDrawable) {
//
//        }
//
//        @Override
//        public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//        }
//    };

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kain_list);

//        //Init Facebook
//        callbackManager = CallbackManager.Factory.create();
//        shareDialog = new ShareDialog(this);

        //Firebase
        database = FirebaseDatabase.getInstance();
        kainList = database.getReference("Kains");

        //Local DB
        localDB = new Database(this);


        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Get Intens Disini
                if (getIntent() != null)
                    categoryId = getIntent().getStringExtra("CategoryId");
                if (!categoryId.isEmpty() && categoryId != null) {
                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadListKain(categoryId);
                    else {
                        Toast.makeText(KainList.this, "Mohon cek koneksi internet anda", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //Get Intens Disini
                if (getIntent() != null)
                    categoryId = getIntent().getStringExtra("CategoryId");
                if (!categoryId.isEmpty() && categoryId != null) {
                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadListKain(categoryId);
                    else {
                        Toast.makeText(KainList.this, "Mohon cek koneksi internet anda", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        recyclerView = findViewById(R.id.recycler_kain);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        //Search
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Masukan nama kain");
        //MaterialSearchBar.setSpeechMode(false); No need because we already define it at XML
        loadSuggest(); //Write fuction to load Suggest from Firebase
        materialSearchBar.setLastSuggestions(suggestList);
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


    }


    private void startSearch(CharSequence text) {
        //Create by name
        Query searchByName = kainList.orderByChild("Name").equalTo(text.toString());
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
                        Intent kainDetail = new Intent(KainList.this, KainDetail.class);
                        kainDetail.putExtra("menuId", adapter.getRef(position).getKey()); // Kirim Kain Id ke activity baru
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
        kainList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Kain item = postSnapshot.getValue(Kain.class);
                            suggestList.add(item.getName()); //add name of Kain to suggest List
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void loadListKain(String categoryId) {

        //Create by category id
        Query searchByName = kainList.orderByChild("menuId").equalTo(categoryId);
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
                        new Database(getBaseContext()).addToCart(new Order(
                                adapter.getRef(position).getKey(),
                                model.getName(),
                                "1",
                                model.getPrice(),
                                model.getDiscount(),
                                model.getImage()
                        ));


                        Toast.makeText(KainList.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    }
                });

                //Add Favorites
                if (localDB.isFavorite(adapter.getRef(position).getKey()))
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
                        if (!localDB.isFavorite(adapter.getRef(position).getKey())) {
                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(KainList.this, "" + model.getName() + " Ditambahkan ke Favorit", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(KainList.this, "" + model.getName() + " Dihapus dari Favorit", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                final Kain local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // start aktivity baru
                        Intent kainDetail = new Intent(KainList.this, KainDetail.class);
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
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        //searchAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
