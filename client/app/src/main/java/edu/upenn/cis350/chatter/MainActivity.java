package edu.upenn.cis350.chatter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    final String[] urls = {"https://files.freemusicarchive.org/storage-freemusicarchive-org/music/no_curator/J_Syreus_Bach/Ability_to_Break__Energetic_Tracks/J_Syreus_Bach_-_Lesser_Faith.mp3",
            "https://files.freemusicarchive.org/storage-freemusicarchive-org/music/ccCommunity/Kai_Engel/Satin/Kai_Engel_-_07_-_Interception.mp3",
            "https://files.freemusicarchive.org/storage-freemusicarchive-org/music/Ziklibrenbib/The_Ghost_in_Your_Piano/The_Ghost_in_your_piano/The_Ghost_in_Your_Piano_-_04_-_Lullaby.mp3",
            "https://files.freemusicarchive.org/storage-freemusicarchive-org/music/ccCommunity/Kai_Engel/Satin/Kai_Engel_-_10_-_Cabaret.mp3",
            "https://files.freemusicarchive.org/storage-freemusicarchive-org/music/no_curator/J_Syreus_Bach/It_Rains__Abstract_Jazz/J_Syreus_Bach_-_Goodbye_Grandmother.mp3",
            "https://files.freemusicarchive.org/storage-freemusicarchive-org/music/ccCommunity/Chad_Crouch/Field_Report_Vol_I_Oaks_Bottom_Instrumental/Chad_Crouch_-_The_Chorus_Ceases_Instrumental.mp3",
            "https://files.freemusicarchive.org/storage-freemusicarchive-org/music/ccCommunity/Dee_Yan-Key/Thalatta_Thalatta/Dee_Yan-Key_-_03_-_Triton.mp3",
            "https://files.freemusicarchive.org/storage-freemusicarchive-org/music/ccCommunity/Dee_Yan-Key/Classical_Lounge/Dee_Yan-Key_-_09_-_Wedding_Ballad.mp3",
            "https://files.freemusicarchive.org/storage-freemusicarchive-org/music/ccCommunity/Dee_Yan-Key/Classical_Lounge/Dee_Yan-Key_-_01_-_A_Little_Night_Romance.mp3"};

    private ArrayList<Snippet> snippets = new ArrayList<Snippet>();
    MediaPlayer mediaPlayer;
    Button currentPlaying;
    Snippet currentSnippet;
    Timer timer;
    RecyclerView rv;
    SeekBar seekBar;
    RequestQueue queue;
    String test;
    JsonArrayRequest jsonObjectRequest;
    final Context context = this;
    JSONArray originalRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        currentPlaying = null;
/*
        Snippet snip = new Snippet("Test Snippet", url1);
        snippets.add(snip);

        Snippet snip2 = new Snippet("Test Snippet2", url2);
        snippets.add(snip2);
*/

        queue = Volley.newRequestQueue(this);
        /*
        String reqUrl1 = "http://10.0.1.39:3000/snippetById/5e9b6dbe4d8950b2a42a81bd";
        String reqUrl2 = "http://10.0.1.39:3000/snippetById/5e9b6dbe4d8950b2a42a81bd";
         */
        String reqUrl3 = "http://10.0.2.2:3000/snippets";

        jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, reqUrl3, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            originalRequest = response;
                            for(int i = 0; i < Math.min(response.length(), 10); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                String authorEntry = obj.getString("author") + ", "
                                        + obj.getString("sourceId") + ", "
                                        + obj.getString("publishedAt");
                                String url = "http://10.0.2.2:3000/getAudioById/" + obj.getString("_id");
                                Snippet snippet = new Snippet(obj.getString("title"), authorEntry, url, obj.getString("_id"), obj.getString("description"));
                                snippets.add(snippet);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        instantiate(context);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snippet snip3 = new Snippet("failure", "failure", urls[0], "failure", "no snips");
                snippets.add(snip3);
            }
        });
        queue.add(jsonObjectRequest);

    }

    public void instantiate(Context context) {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.receycler);
        rv = recyclerView;
        SnippetDesign snippetDesign = new SnippetDesign(snippets, context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        DividerItemDecoration dividerItemDecoration = new
                DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(snippetDesign);

        snippetDesign.setOnitemClickListener(new SnippetDesign.OnitemClickListener() {

            @Override
            public void onItemClick(final Button b, TextView title, TextView author, View v, Snippet snip, int position) {
                try {
                    if(b.getText().toString().equals("Stop")) {
                        b.setText("Play");
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        currentPlaying = null;
                    } else {
                        if(currentPlaying != null) {
                            currentPlaying.setText("Play");
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                        String url = "http://10.0.2.2:3000/addListen/" + snip.snippetId;
                        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                                new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    Log.d("Response", "Successfully updated listens");
                                }
                                }, null);
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(snip.snippetUrl);
                        mediaPlayer.prepareAsync();
                        currentPlaying = b;
                        currentSnippet = snip;
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                                b.setText("Stop");
                            }
                        });
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                currentPlaying.setText("Play");
                                int currentIndex = snippets.indexOf(currentSnippet);
                                if (currentIndex + 1 < snippets.size()) {
                                    SnippetDesign.SnippetHolder holder =
                                            (SnippetDesign.SnippetHolder)
                                             recyclerView.findViewHolderForAdapterPosition(currentIndex + 1);
                                    currentPlaying = holder.button;
                                    currentSnippet = snippets.get(currentIndex + 1);
                                    instantiateNewMediaPlayer();
                                }

                            }
                        }, amountToUpdate);
                        queue.add(jsonObjectRequest);
                    }
                } catch (IOException e) {
                    //F
                }
            }
        });
    }

    private void instantiateNewMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(currentSnippet.snippetUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    currentPlaying.setText("Stop");
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    currentPlaying.setText("Play");
                    int currentIndex = snippets.indexOf(currentSnippet);
                    if (currentIndex + 1 < snippets.size()) {
                        SnippetDesign.SnippetHolder holder =
                                (SnippetDesign.SnippetHolder)
                                        rv.findViewHolderForAdapterPosition(currentIndex + 1);
                        currentPlaying = holder.button;
                        currentSnippet = snippets.get(currentIndex + 1);
                        instantiateNewMediaPlayer();
                    }

                }
            });

        } catch (IOException e) {
            //F
        }
    }

    public void searchSnippets(View v) {
        EditText searchEdit = findViewById(R.id.search_bar);
        final String searchCrit = searchEdit.getText().toString().toLowerCase();
        ArrayList<Snippet> copy = new ArrayList<Snippet>();

        try {
            for (Snippet a : snippets) {
                if (a.snippetName.toLowerCase().contains(searchCrit)
                    || a.description.toLowerCase().contains(searchCrit)) {
                    copy.add(a);
                }
            }
            snippets = copy;
        } catch (Exception e) {
            e.printStackTrace();
        }
        instantiate(context);
    }
}
