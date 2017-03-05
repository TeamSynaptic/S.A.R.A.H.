package synaptic.dementiahack;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

import static android.util.Log.d;

public class IndividualActivity extends AppCompatActivity{
    Button not_what_you_asked;
    TextView i_heard;
    TextView i_think;
    TextView display_answer;
    static Socket socket;
    static String question = "";
    static String result = "";
    static Context mc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button mic = (Button) findViewById(R.id.mic_button);
        mic.setOnClickListener(individual_button_listener);
        not_what_you_asked = (Button) findViewById(R.id.not_what_you_asked);
        not_what_you_asked.setOnClickListener(manual_input);
        i_heard = (TextView) findViewById(R.id.i_heard);
        i_think = (TextView) findViewById(R.id.i_think);
        display_answer = (TextView) findViewById(R.id.display_answer);
        mc = getApplicationContext();
    }
    /*
      * MANUAL INPUT LISTENER
      */
    public View.OnClickListener manual_input = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final EditText manual_edittext = new EditText(IndividualActivity.this);
            AlertDialog dialog = new AlertDialog.Builder(IndividualActivity.this).setTitle("Enter your question manually").setView(manual_edittext).setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int w) {
                    question = String.valueOf(manual_edittext.getText());
                    TextView display_result = (TextView) findViewById(R.id.display_text);
                    display_result.setText("\"" + question + "?\"");
                    not_what_you_asked.setVisibility(View.INVISIBLE);
                    i_heard.setVisibility(View.INVISIBLE);
                    i_think.setVisibility(View.VISIBLE);
                    getMessage();
                }
            }).setNegativeButton("Cancel", null).create();
            dialog.show();
        }
    };
    public View.OnClickListener individual_button_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startSpeechToText();
        }
    };
    private void startSpeechToText() {
        Intent start_mic = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        start_mic.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        start_mic.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        start_mic.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");
        try {
            startActivityForResult(start_mic, 1);
        } catch (ActivityNotFoundException a) {
        }
    }
    /**
     * Callback for speech recognition activity
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    question = result.get(0);
                    TextView display_result = (TextView) findViewById(R.id.display_text);
                    display_result.setText("\"" + question + "?\"");
                    not_what_you_asked.setVisibility(View.VISIBLE);
                    i_heard.setVisibility(View.VISIBLE);
                    i_think.setVisibility(View.VISIBLE);
                    getMessage();
                }
                break;
            default:
                not_what_you_asked.setVisibility(View.INVISIBLE);
                i_heard.setVisibility(View.INVISIBLE);
                i_think.setVisibility(View.INVISIBLE);
                display_answer.setVisibility(View.INVISIBLE);
        }
    }
    public void getMessage(){
        if (question.toLowerCase().equals("hey sarah how are you doing today")) {
            display_answer.setVisibility(View.VISIBLE);
            display_answer.setText("I'm great. I feel like I was born just yesterday.");
        } else if (question.toLowerCase().equals("hey sarah who's your daddy")) {
            display_answer.setVisibility(View.VISIBLE);
            display_answer.setText("I have four dads: Emon, Rui, Alex and Mahir. I was created by Team Synaptic at Dementia Hack 2017.");
        } else if (question.toLowerCase().equals("hey sarah what can i do about my dementia")){
            display_answer.setVisibility(View.VISIBLE);
            display_answer.setText("I'm not a doctor, but I'll always be by your side. How can I help?");
        } else getResponse();
    }
    public void getResponse() {
        new Thread(new Runnable() {
            public void run() {
                // a potentially  time consuming task
                try {
                    d("check", "here");
                    socket = new Socket("192.168.137.26", 8888);
                    d("check", "connected");
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    writer.write(question+"\n");
                    writer.flush();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    d("dd","reached1");
                    result = reader.readLine();
                    Log.d("result", result);
                } catch (NullPointerException e) {
                    d("Connecting", e.getMessage());
                } catch (IOException ei){
                    d("Connecting", ei.getMessage());
                } catch (Exception e){
                    d("WHY", e.getMessage());
                }
                display_answer.setVisibility(View.VISIBLE);
                display_answer.post(new Runnable() {
                    public void run() {
                        display_answer.setText(result);
                    }
                });
            }
        }).start();
    }
    /*static TextToSpeech t1 = new TextToSpeech(mc.getApplicationContext(), new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if(status != TextToSpeech.ERROR) {
                t1.setLanguage(Locale.US);
            }
        }
    });							//add this snippet to main activity
    public void onWhateverAction(){	        //answer to query
        t1.speak(result, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }*/
}
