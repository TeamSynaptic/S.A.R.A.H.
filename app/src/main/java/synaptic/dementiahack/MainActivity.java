package synaptic.dementiahack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    static File individual_or_caregiver;
    static File user_file;
    Button individual;
    Button caregiver;
    Button user_button;
    TextView title;
    EditText user_name;
    boolean name_entered = false;
    int value = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        individual_or_caregiver = new File(this.getFilesDir(), "setting.txt");
        user_file = new File(this.getFilesDir(), "name.txt");
        if(individual_or_caregiver.exists()){
            try {
                Scanner s = new Scanner(individual_or_caregiver);
                String z = s.nextLine();
                if(z.equals("Caregiver")) {
                    startActivity(new Intent(this, CaregiverActivity.class));
                } else if(z.equals("Individual")) {
                    startActivity(new Intent(this, IndividualActivity.class));
                }
            }catch (Exception e){
                System.out.print(e.getMessage());
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        individual = (Button) findViewById(R.id.individual_button);
        caregiver = (Button) findViewById(R.id.caregiver_button);
        individual.setOnClickListener(individual_button_listener);
        caregiver.setOnClickListener(caregiver_button_listener);
        title = (TextView) findViewById(R.id.landing_title);
        user_button = (Button) findViewById(R.id.name_input_button);
    }
    public void initializeNameInput(){
        individual.setVisibility(View.INVISIBLE);
        caregiver.setVisibility(View.INVISIBLE);
        title.setVisibility(View.INVISIBLE);
        user_name = (EditText) findViewById(R.id.name_input);
        user_name.setVisibility(View.VISIBLE);
        user_button.setVisibility(View.VISIBLE);
        user_button.setOnClickListener(name_button_listener);
    }
    public View.OnClickListener caregiver_button_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                FileOutputStream writer = new FileOutputStream(individual_or_caregiver);
                writer.write("Caregiver".getBytes());
                initializeNameInput();
                value = 1;

            } catch (Exception e){
                Log.d("Caregiver", e.getMessage());
            }
        }
    };
    public View.OnClickListener individual_button_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                FileOutputStream writer = new FileOutputStream(individual_or_caregiver);
                writer.write("Individual".getBytes());
                initializeNameInput();
            } catch (Exception e) {
                Log.d("Individual", e.getMessage());
            }
        }
    };
    public View.OnClickListener name_button_listener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            try {
                FileOutputStream writer = new FileOutputStream(user_file);
                writer.write(String.valueOf(user_name.getText()).getBytes());
                name_entered = true;
                if(value == 1){
                    startActivity(new Intent(getApplicationContext(), CaregiverActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), IndividualActivity.class));
                }
            } catch (Exception e){
            }
        }
    };
}
