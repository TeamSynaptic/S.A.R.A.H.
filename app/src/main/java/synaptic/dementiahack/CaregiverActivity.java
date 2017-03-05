package synaptic.dementiahack;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        FileServerAsyncTask server_thread = new FileServerAsyncTask(this);
        server_thread.execute();
    }
    public void generateList(){

    }
    public void saveEntries() {
        try {
            File saving_loc = new File(this.getFilesDir(), "objects.txt");
            FileOutputStream fos = this.openFileOutput(saving_loc.getAbsolutePath(), Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
        } catch (Exception e){
            Log.d("Test", e.getMessage());
        }
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
                final EditText title_edittext = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Add new task").setView(title_edittext).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int w) {
                        String task = String.valueOf(title_edittext.getText());
                        String current_date_time_string = DateFormat.getDateTimeInstance().format(new Date());
                        Entry current_entry = new Entry(task, current_date_time_string);
                        entries.add(current_entry);
                        main_list.setAdapter(new ListAdapter(getApplicationContext(), entries, finished_entries));
                    }
                }).setNegativeButton("Cancel", null).create();
                dialog.show();
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

        private Context context;
        private TextView statusText;

        public FileServerAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
           try{
                while(true) {
                    try {
                        ServerSocket serverSocket = new ServerSocket(8888);
                        /**
                         * Create a server socket and wait for client connections. This
                         * call blocks until a connection is accepted from a client
                         */
                        Socket client = serverSocket.accept();
                        Scanner s = new Scanner(client.getInputStream());
                        String question = s.nextLine();
                        Log.d(question, question);
                        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                        writer.write(query(question));
                        Log.d(question, query(question));
                        writer.flush();
                    } catch (IOException e) {
                        Log.d("Exception","e");
                    }
                }
           } catch (Exception e){
               return null;
           }
        }

        /**
         * Start activity that can handle the JPEG image
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
            }
        }
    }
    public static String query(String question){											   //returns answer for query
        for(Entry ety: finished_entries){
            if(question.contains(ety.getNoun())){
                if(ety.getNoun().toLowerCase().contains("my")) ety.setNoun("your "+ety.getNoun().substring(ety.getNoun().indexOf("my")+3));
                if(ety.getFinished()) return "Yes, you did " + ety.getVerb() + " " + ety.getNoun() + " today.";
                else return "No, you did not " + ety.getVerb() + " " + ety.getNoun() + " today.";
            }
        }
        for(Entry ety: entries){
            if(question.contains(ety.getNoun())){
                if(ety.getNoun().toLowerCase().contains("my")) ety.setNoun("your "+ety.getNoun().substring(ety.getNoun().indexOf("my")+3));
                if(ety.getFinished()) return "Yes, you did " + ety.getVerb() + " " + ety.getNoun() + " today.";
                else return "No, you did not " + ety.getVerb() + " " + ety.getNoun() + " today.";
            }
        }
        return "Sorry, I couldn't find any relevant planned event in the checklist.";
    }
}
