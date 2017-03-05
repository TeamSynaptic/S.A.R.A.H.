package synaptic.dementiahack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    static File individual_or_caregiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        individual_or_caregiver = new File(this.getFilesDir(), "setting.txt");
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
        Button individual = (Button) findViewById(R.id.individual_button);
        Button caregiver = (Button) findViewById(R.id.caregiver_button);
        individual.setOnClickListener(individual_button_listener);
        caregiver.setOnClickListener(caregiver_button_listener);
    }
    public View.OnClickListener caregiver_button_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                FileOutputStream writer = new FileOutputStream(individual_or_caregiver);
                writer.write("Caregiver".getBytes());
                startActivity(new Intent(getApplicationContext(), CaregiverActivity.class));
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
                startActivity(new Intent(getApplicationContext(), IndividualActivity.class));
            } catch (Exception e) {
                Log.d("Individual", e.getMessage());
            }
        }
    };
}
