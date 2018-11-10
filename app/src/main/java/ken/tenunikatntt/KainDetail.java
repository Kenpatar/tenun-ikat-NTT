package ken.tenunikatntt;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

import ken.tenunikatntt.Common.Common;
import ken.tenunikatntt.Database.Database;
import ken.tenunikatntt.Model.Kain;
import ken.tenunikatntt.Model.Order;
import ken.tenunikatntt.Model.Rating;

import static android.R.attr.value;

public class KainDetail extends AppCompatActivity implements RatingDialogListener {


    TextView kain_description, kain_name, kain_price;
    ImageView kain_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnRating;
    CounterFab btnCart;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;

    String kainId = "";


    FirebaseDatabase database;
    DatabaseReference Kains;
    DatabaseReference ratingTbl;

    Kain currentKain, ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kain_detail);

        // Firebase
        database = FirebaseDatabase.getInstance();
        Kains = database.getReference("Kains");
        ratingTbl = database.getReference("Rating");


        //Init View
        numberButton = findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btnCart);
        btnRating = findViewById(R.id.btn_rating);
        ratingBar = findViewById(R.id.ratingBar);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDilaog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        kainId,
                        currentKain.getName(),
                        numberButton.getNumber(),
                        currentKain.getPrice(),
                        currentKain.getDiscount()
                ));
                Toast.makeText(KainDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });
        btnCart.setCount(new Database(this).getCountCart());

        kain_description = findViewById(R.id.kain_description);
        kain_name = findViewById(R.id.kain_name);
        kain_price = findViewById(R.id.kain_price);
        kain_image = findViewById(R.id.img_kain);

        collapsingToolbarLayout = findViewById(R.id.colappsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        // get kain Id from Intent
        if (getIntent() != null) {
            kainId = getIntent().getStringExtra("menuId");
            if (kainId != null && !kainId.isEmpty()) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    getDetailKain(kainId);
                    getRatingKain(kainId);
                } else {
                    Toast.makeText(KainDetail.this, "Mohon Cek koneksi anda", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    private void getRatingKain(String kainId) {
        Query kainRating = ratingTbl.orderByChild("KainId").equalTo(kainId);

        kainRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count != 0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDilaog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Buruk", "Tidak Suka", "Lumayan", "Suka", "Suka Sekali"))
                .setDefaultRating(1)
                .setTitle("Beri Nilai Kualitas Kain")
                .setDescription("Jumlah Bintang & Ulasan Singkat")
                .setTitleTextColor(R.color.colorPrimary)
                .setHint("Sampaikan pendapat anda")
                .setTitleTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.black)
                .setCommentBackgroundColor(R.color.colorAccent)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(KainDetail.this)
                .show();
    }

    private void getDetailKain(String kainId) {
        Kains.child(kainId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentKain = dataSnapshot.getValue(Kain.class);

                //set Image
                Picasso.with(getBaseContext()).load(currentKain.getImage())
                        .into(kain_image);

                collapsingToolbarLayout.setTitle(currentKain.getName());

                kain_price.setText(currentKain.getPrice());

                kain_name.setText(currentKain.getName());

                kain_description.setText(currentKain.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int value, @NotNull String comments) {
        //Get rating and upload to firebase
        final Rating rating = new Rating(Common.currentUser.getPhone(),
                kainId,
                String.valueOf(value),
                comments);
        ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currentUser.getPhone()).exists()) {
                    //Remove old value
                    ratingTbl.child(Common.currentUser.getPhone()).removeValue();
                    //Update new value
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                } else {
                    //Reove old value
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(KainDetail.this, "Terima kasih sudah memberi masukan !!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
