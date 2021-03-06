package app.petkoul.help_us;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PatientRegister extends AppCompatActivity implements View.OnClickListener {

    EditText email_text;
    EditText pass_text;
    EditText name_text;
    EditText surname_text;
    EditText age_text;
    Button register_btn;
    Spinner drop_list;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDatabaseRefence;
    final String TAG = "PatientRegister";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_patient_register);

        email_text = findViewById(R.id.email_text);

        pass_text = findViewById(R.id.pass_text);

        name_text = findViewById(R.id.name_text);

        surname_text = findViewById(R.id.surname_text);

        age_text = findViewById(R.id.age_text);

        register_btn = findViewById(R.id.register_btn);

        register_btn.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();

        drop_list = (Spinner)findViewById(R.id.drop_list);

        progressDialog = new ProgressDialog(this);

    }

    @Override
    public void onClick(View view) {
        if(view == register_btn){
            createAccount(email_text.getText().toString(), pass_text.getText().toString());
        }

    }






    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        // showProgressDialog();

        // [START create_user_with_email]
        progressDialog.setMessage("Registering ...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in User's information
                            Log.d(TAG, "createUserWithEmail:success");

                            fire_dummy_console();

                            //updateUI(User);
                        } else {
                            // If sign in fails, display a message to the User.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(PatientRegister.this, "Authentication failed. "+task.getException(),
                                    Toast.LENGTH_SHORT).show();

                            //updateUI(null);
                        }
                        progressDialog.dismiss();
                        // [START_EXCLUDE]
                        // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }


    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verify_email_button).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(PatientRegister.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            fire_dummy_console();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(PatientRegister.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }


    private boolean validateForm() {
        boolean valid = true;
        double valid_age = 0 ;
        String email = email_text.getText().toString();
        if (TextUtils.isEmpty(email)) {
            email_text.setError("Required.");
            valid = false;
        } else {
            email_text.setError(null);
        }

        String password = pass_text.getText().toString();
        if (TextUtils.isEmpty(password)) {
            pass_text.setError("Required.");
            valid = false;
        } else {
            pass_text.setError(null);
        }

        String name = name_text.getText().toString();
        if (TextUtils.isEmpty(name)) {
            name_text.setError("Required.");
            valid = false;
        } else {
            name_text.setError(null);
        }
        String surname = surname_text.getText().toString();
        if (TextUtils.isEmpty(surname)) {
            surname_text.setError("Required.");
            valid = false;
        } else {
            surname_text.setError(null);

        }
        String age = age_text.getText().toString();
        try{

                valid_age = Double.valueOf(age);

        }catch (NumberFormatException ex){

            age_text.setError("please enter a valid number");
        }


        if (TextUtils.isEmpty(age)) {
            age_text.setError("Required.");
            valid = false;
        } else if(valid_age<0 || valid_age>150) {
            age_text.setError("please enter a valid number");
            valid = false;

        }else {
            age_text.setError(null);
        }

        return valid;
    }

    public  void fire_dummy_console(){
        update_db_with_user_properties();
        Intent in = new Intent(this, Dummy_Console.class);
        startActivity(in);
    }

    private void update_db_with_user_properties(){

        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        String email = email_text.getText().toString();
        String password = pass_text.getText().toString();
        String name = name_text.getText().toString();
        String surname = surname_text.getText().toString();
        String age = age_text.getText().toString();
        String category = drop_list.getSelectedItem().toString();
        usersDatabaseRefence = firebaseDatabase.getReference().child("users");
        User new_user = new User(uid , email , name ,surname, age, category);
        //usersDatabaseRefence.push().setValue(new_user);
        usersDatabaseRefence.child(uid).setValue(new_user);
        Log.d(TAG, "update database with user properties");

    }

}
