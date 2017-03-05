package synaptic.dementiahack;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import static android.util.Log.d;

public class IndividualActivity extends AppCompatActivity{
    Button not_what_you_asked;
    TextToSpeech tts;
    TextView i_heard;
    TextView i_think;
    TextView display_answer;
    static Socket socket;
    static String question = "";
    static String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int s) {
                if(s != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }
            }
        });
        Button mic = (Button) findViewById(R.id.mic_button);
        mic.setOnClickListener(individual_button_listener);
        not_what_you_asked = (Button) findViewById(R.id.not_what_you_asked);
        not_what_you_asked.setOnClickListener(manual_input);
        i_heard = (TextView) findViewById(R.id.i_heard);
        i_think = (TextView) findViewById(R.id.i_think);
        display_answer = (TextView) findViewById(R.id.display_answer);
        Toast toast = Toast.makeText(this, "Please tap the microphone icon to ask a question", Toast.LENGTH_LONG);
        toast.show();
    }
    public void onAction(){
        tts.speak(result, TextToSpeech.QUEUE_FLUSH, null);
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
        start_mic.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask away!");
        try {
            startActivityForResult(start_mic, 1);
        } catch (ActivityNotFoundException a) {
        }
    }
    /**
     * Callback for speech recognition activity
     * */
    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data) {
        super.onActivityResult(request_code, result_code, data);
        switch (request_code) {
            case 1:
                if (result_code == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    question = result.get(0);
                    TextView display_result = (TextView) findViewById(R.id.display_text);
                    display_result.setText("\"" + question + "?\"");
                    not_what_you_asked.setVisibility(View.VISIBLE);
                    i_heard.setVisibility(View.VISIBLE);
                    i_think.setVisibility(View.VISIBLE);
                    i_think.setText("Thinking...");
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
    public void getMessage() {
        if (question.toLowerCase().equals("hey sarah how are you doing today")) {
            display_answer.setVisibility(View.VISIBLE);
            display_answer.setText("I'm great. I feel like I was born just yesterday.");
            i_think.setText("I think...");
            result = "I'm great. I feel like I was born just yesterday.";
            onAction();
        } else if (question.toLowerCase().equals("hey sarah who's your daddy")) {
            display_answer.setVisibility(View.VISIBLE);
            display_answer.setText("I have four dads: Emon, Rui, Alex and Mahir. I was created by Team Synaptic at Dementia Hack 2017.");
            i_think.setText("I think...");
            result = "I have four dads: Emon, Rui, Alex and Mahir. I was created by Team Synaptic at Dementia Hack 2017.";
            onAction();
        } else if (question.toLowerCase().equals("hey sarah what can i do about my dementia")) {
            display_answer.setVisibility(View.VISIBLE);
            display_answer.setText("I'm not a doctor, but I'll always be by your side. How can I help?");
            i_think.setText("I think...");
            result = "I'm not a doctor, but I'll always be by your side. How can I help?";
            onAction();
        } else if(question.toLowerCase().equals("hey sarah") || question.toLowerCase().equals("hello sarah") || question.toLowerCase().equals("hi sarah")){
            try {
                display_answer.setVisibility(View.VISIBLE);
                Scanner s = new Scanner(MainActivity.user_file);
                String name = s.nextLine();
                display_answer.setText("Hi " + name + "!");
                result = "Hi " + name + "!";
                onAction();
            } catch (Exception e){}
        } else if(question.toLowerCase().startsWith("when")){
            getTime();
        } else getResponse();
    }
    public void getTime() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    d("check", "here");
                    socket = new Socket("192.168.137.68", 8888);
                    d("check", "connected");
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    writer.write("t1me\n");
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
                        i_think.setText("I think...");
                        onAction();
                    }
                });
            }
        }).start();
    }
    public void getResponse() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    d("check", "here");
                    socket = new Socket("192.168.137.68", 8888);
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
                        i_think.setText("I think...");
                        onAction();
                    }
                });
            }
        }).start();
    }
}
