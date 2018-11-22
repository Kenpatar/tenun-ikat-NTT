package ken.tenunikatntt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;
import ken.tenunikatntt.Common.Common;
import ken.tenunikatntt.Model.User;

public class SignIn extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button btnSignIn;
    com.rey.material.widget.CheckBox ckbRemember;
    TextView txtForgotPwd;
    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        ckbRemember = (com.rey.material.widget.CheckBox) findViewById(R.id.ckbRemember);
        txtForgotPwd = findViewById(R.id.txtForgotPwd);

        //Init Paper
        Paper.init(this);

        // insialisasi Database firebase
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");


        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPwdDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtPhone.getText().toString().length() == 0) {
                    edtPhone.setError("Masukan Nomor HP");
                } else if (edtPassword.getText().toString().length() == 0) {
                    edtPassword.setError("Masukan password anda");
                } else {

                    if (Common.isConnectedToInternet(getBaseContext())) {
                        //Simpan user & password
                        if (ckbRemember.isChecked()) {
                            Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                            Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                        }

                        final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                        mDialog.setMessage("Please Waiting...");
                        mDialog.show();

                        table_user.addListenerForSingleValueEvent(new ValueEventListener() {


                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                //cek Database jika belum ada
                                if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                    // Get informasi user
                                    mDialog.dismiss();

                                    //Panggil data No HP dan Password dari User di firebase
                                    User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                    user.setPhone(edtPhone.getText().toString()); //set Phone
                                    if (user.getPassword().equals(edtPassword.getText().toString())) {
                                        {
                                            Intent homeIntent = new Intent(SignIn.this, Home.class);
                                            Common.currentUser = user;
                                            startActivity(homeIntent);
                                            finish();

                                            table_user.removeEventListener(this);

                                        }
                                    } else {
                                        Toast.makeText(SignIn.this, "Password salah !!!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    mDialog.dismiss();
                                    Toast.makeText(SignIn.this, "User tidak ada di Database ", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } else {
                        Toast.makeText(SignIn.this, "Mohon cek koneksi internet anda", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

    }

    private void showForgotPwdDialog() {

        final AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setTitle("Forgot Password");
        builer.setMessage("Masukan Secure Code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_password_layout, null);

        builer.setView(forgot_view);
        builer.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhone = forgot_view.findViewById(R.id.edtPhone);
        final MaterialEditText edtSecureCode = forgot_view.findViewById(R.id.edtSecureCode);

        builer.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                //Check if user available
                table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(edtPhone.getText().toString())
                                .getValue(User.class);
                        if (user.getSecureCode().equals(edtSecureCode.getText().toString()))
                            Toast.makeText(SignIn.this, "Password Anda : " + user.getPassword(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(SignIn.this, "Secure Code Salah! : " + user.getPassword(), Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        builer.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        builer.show();
    }
}
