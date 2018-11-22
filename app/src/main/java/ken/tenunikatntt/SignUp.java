package ken.tenunikatntt;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.concurrent.TimeUnit;

import ken.tenunikatntt.Common.Common;
import ken.tenunikatntt.Model.User;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtPhone, edtName, edtPassword, edtSecureCode, edtVerif;
    Button btnSignUp, btnVerif;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

//        mAuth = FirebaseAuth.getInstance();
//
//
//        edtVerif = (MaterialEditText) findViewById(R.id.edtVerif);
        edtName = (MaterialEditText) findViewById(R.id.edtName);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtSecureCode = (MaterialEditText) findViewById(R.id.edtSecureCode);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);
//        btnVerif = (Button) findViewById(R.id.btnVerif);
        

        // insialisasi Database firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtPhone !=null) {

                    if (edtPhone.length() > 0 && edtPhone.length() <= 10) {
                        edtPhone.setError("Masukan nomor HP yang benar");
                        } else if (edtPhone.getText().toString().length() == 0) {
                            edtPhone.setError("Nomor HP diperlukan!");
                        } else if (edtName.getText().toString().length() == 0) {
                            edtName.setError("Masukan nama anda");
                        } else if (edtPassword.getText().toString().length() == 0) {
                            edtPassword.setError("Masukan password anda");
                        } else if (edtSecureCode.getText().toString().length() == 0) {
                            edtSecureCode.setError("Masukan kode keamanan anda");
                        }
                    else if (Common.isConnectedToInternet(getBaseContext())) {
                        final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                        mDialog.setMessage("Please Waiting...");
                        mDialog.show();

                        table_user.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // cek apakah user phone sudah ada atau belum
                                if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                    mDialog.dismiss();
                                    Toast.makeText(SignUp.this, "Nomor HP sudah terdaftar", Toast.LENGTH_SHORT).show();
                                } else {
                                    mDialog.dismiss();
                                    User user = new User(edtName.getText().toString(), edtPassword.getText().toString(), edtSecureCode.getText().toString());
                                    table_user.child(edtPhone.getText().toString()).setValue(user);
                                    Toast.makeText(SignUp.this, "Sign Up berhasil !", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        Toast.makeText(SignUp.this, "Mohon cek koneksi internet anda", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
    }
}

