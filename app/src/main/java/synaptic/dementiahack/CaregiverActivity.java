package synaptic.dementiahack;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class CaregiverActivity extends AppCompatActivity {

    private static ArrayList<Entry> entries = new ArrayList<>();
    private static ListView main_list;
    private static ArrayList<Entry> finished_entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        main_list = (ListView) findViewById(R.id.main_list);
        generateList();
        FileServerAsyncTask server_thread = new FileServerAsyncTask();
        server_thread.execute();
    }
    public void generateList(){
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                String title;
                LinearLayout layout = new LinearLayout(getApplicationContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText title_edittext = new EditText(this);
                layout.addView(title_edittext);
                TextView date_title = new TextView(getApplicationContext());
                date_title.setText("Set a time (optional)");
                final EditText date_edittext = new EditText(getApplicationContext());
                layout.addView(date_title);
                layout.addView(date_edittext);
                date_edittext.setInputType(InputType.TYPE_CLASS_DATETIME);
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Add new task").setView(layout).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int w) {
                        String task = String.valueOf(title_edittext.getText());
                        String current_date_time_string = DateFormat.getDateTimeInstance().format(new Date());
                        Entry current_entry = new Entry(task, current_date_time_string);
                        String str = date_edittext.getText().toString();
                        DateFormat formatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                        try {
                            Date date = formatter.parse(str);
                            if(date.compareTo(new Date()) < 0) current_entry.setNextDay(true);
                            Log.d(date.toString(), date.toString());
                            Log.d(date.toString(), String.valueOf(current_entry.isNextDay()));
                            current_entry.setTimeToRemind(date);
                        } catch (Exception e){
                            Log.d("Date", e.getMessage());
                        }
                        entries.add(current_entry);
                        main_list.setAdapter(new ListAdapter(getApplicationContext(), entries, finished_entries));
                    }
                }).setNegativeButton("Cancel", null).create();
                dialog.show();
                Collections.sort(entries);
                return true;
            case R.id.action_reset:
                entries.clear();
                finished_entries.clear();
                main_list.setAdapter(new ListAdapter(this, entries, finished_entries));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public static class FileServerAsyncTask extends AsyncTask <String, Integer, String>{
        @Override
        protected String doInBackground(String... params) {
           try{
                while(true) {
                    try {
                        ServerSocket serverSocket = new ServerSocket(8888);
                        Socket client = serverSocket.accept();
                        Scanner s = new Scanner(client.getInputStream());
                        String question = s.nextLine();
                        if(question.equals("t1me")){
                            question = s.nextLine();
                            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                            writer.write(timeQuery(question));
                            writer.flush();
                        } else {
                            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                            writer.write(query(question));
                            writer.flush();
                        }
                    } catch (IOException e) {
                        Log.d("Exception","e");
                    }
                }
           } catch (Exception e){
               return null;
           }
        }
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
            }
        }
    }
    public static String query(String question){
        for(Entry ety: finished_entries){
            if(question.toLowerCase().contains(ety.getNoun().toLowerCase())){
                if(ety.getNoun().toLowerCase().contains("my")) ety.setNoun("your "+ety.getNoun().substring(ety.getNoun().indexOf("my")+3));
                if(ety.getFinished()) return "Yes, you did " + ety.getVerb() + " " + ety.getNoun() + " today.";
                else return "No, you did not " + ety.getVerb() + " " + ety.getNoun() + " today.";
            }
        }
        for(Entry ety: entries){
            if(question.toLowerCase().contains(ety.getNoun().toLowerCase())){
                if(ety.getNoun().toLowerCase().contains("my")) ety.setNoun("your "+ety.getNoun().substring(ety.getNoun().indexOf("my")+3));
                if(ety.getFinished()) return "Yes, you did " + ety.getVerb() + " " + ety.getNoun() + " today.";
                else return "No, you did not " + ety.getVerb() + " " + ety.getNoun() + " today.";
            }
        }
        return "Sorry, I couldn't find any relevant planned event in the checklist.";
    }
    public static String timeQuery(String question){
        for(Entry ety: entries){
            if(question.toLowerCase().contains(ety.getNoun().toLowerCase())){
                if(ety.getTimeToRemind() != null) {
                    if(ety.getTimeToRemind().getMinutes() == 0) {
                        return "Scheduled for " + ety.getTimeToRemind().getHours() + " today.";
                    } else if (ety.getTimeToRemind().getMinutes() < 10){
                        return "Scheduled for " + ety.getTimeToRemind().getHours() + " O" + ety.getTimeToRemind().getMinutes() + " today.";
                    } else {
                        return "Scheduled for " + ety.getTimeToRemind().getHours() + " " + ety.getTimeToRemind().getMinutes() + " today.";
                    }
                }
            }
        }
        return "There is no scheduled time for this event";
    }
}
