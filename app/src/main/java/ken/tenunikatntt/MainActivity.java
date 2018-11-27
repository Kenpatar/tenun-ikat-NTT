package ken.tenunikatntt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import ken.tenunikatntt.Common.Common;
import ken.tenunikatntt.Model.User;

public class MainActivity extends AppCompatActivity {
    private static final int APP_REQUEST_CODE  = 99;
    Button btnContinue;
    TextView txtSlogan;

    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AccountKit.initialize(this);
        setContentView(R.layout.activity_main);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("User");

        btnContinue = (Button) findViewById(R.id.btn_continue);
        txtSlogan = (TextView) findViewById(R.id.txtSlogan);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoginSystem ();
            }
        });

        //Cek session Facebook Account Kit
        if (AccountKit.getCurrentAccessToken() !=null)
        {
            //Tampilkan Dialog
            final AlertDialog waitingDialog = new SpotsDialog(this);
            waitingDialog.show();
            waitingDialog.setMessage("Mohon Tunggu");
            waitingDialog.setCancelable(false);

            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    //Copy code dari exists user
                    users.child(account.getPhoneNumber().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    User localUser = dataSnapshot.getValue(User.class);

                                    Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                    Common.currentUser = localUser;
                                    startActivity(homeIntent);

                                    //Dismiss Dialog
                                    waitingDialog.dismiss();

                                    finish();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }

                @Override
                public void onError(AccountKitError accountKitError) {


                }
            });
        }
    }

    private void startLoginSystem() {
        Intent intent = new Intent(MainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
        startActivityForResult(intent,APP_REQUEST_CODE );

    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("ken.tenunikatntt",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE )
        {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (result.getError() !=null)
            {
                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            else if (result.wasCancelled())
            {
                Toast.makeText(this, "Batal", Toast.LENGTH_SHORT).show();
                return;
            }
            else

            {
                if (result.getAccessToken() !=null)
                {
                    //Tampilkan Dialog
                    final AlertDialog waitingDialog = new SpotsDialog(this);
                    waitingDialog.show();
                    waitingDialog.setMessage("Mohon Tunggu");
                    waitingDialog.setCancelable(false);

                    //Get current Phone

                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            final String userPhone = account.getPhoneNumber().toString();

                            //Cek apakah sudah ada user didalam firebase
                            users.orderByKey().equalTo(userPhone)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (!dataSnapshot.child(userPhone).exists()) // Jika belum ada user maka
                                            {
                                                //Kita akan membuat user baru
                                                User newUser = new User();
                                                newUser.setPhone(userPhone);
                                                newUser.setName("");

                                                //Tambah ke FIrebase
                                                users.child(userPhone)
                                                      .setValue(newUser)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                    Toast.makeText(MainActivity.this, "Registrasi akun berhasil !", Toast.LENGTH_SHORT).show();

                                                                //Login
                                                                users.child(userPhone)
                                                                     .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                         @Override
                                                                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                             User localUser = dataSnapshot.getValue(User.class);

                                                                             Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                                                             Common.currentUser = localUser;
                                                                             startActivity(homeIntent);

                                                                             //Dismiss Dialog
                                                                             waitingDialog.dismiss();

                                                                             finish();

                                                                         }

                                                                         @Override
                                                                         public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                         }
                                                                     });
                                                            }
                                                        });

                                            }
                                            else //Jika sudah ada user maka
                                            {
                                                //Tinggal masuk sa
                                                //Login
                                                users.child(userPhone)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                User localUser = dataSnapshot.getValue(User.class);

                                                                Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                                                Common.currentUser = localUser;
                                                                startActivity(homeIntent);

                                                                //Dismiss Dialog
                                                                waitingDialog.dismiss();

                                                                finish();

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Toast.makeText(MainActivity.this, ""+accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        }
    }
}
