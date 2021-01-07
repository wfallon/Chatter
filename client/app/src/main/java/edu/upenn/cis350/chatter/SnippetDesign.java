package edu.upenn.cis350.chatter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class SnippetDesign extends RecyclerView.Adapter<SnippetDesign.SnippetHolder> {

    OnitemClickListener listener;
    Context context;
    ArrayList<Snippet> snippets;

    public SnippetDesign(ArrayList<Snippet> arr, Context c){
        this.context = c;
        this.snippets = arr;
    }

    public void setOnitemClickListener(OnitemClickListener l) {
        this.listener = l;
    }

    @Override
    public SnippetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.snippet, parent, false);
        return new SnippetHolder(view);
    }

    @Override
    public void onBindViewHolder(final SnippetHolder holder, final int position) {
        final Snippet snip = snippets.get(position);
        holder.title.setText(snip.snippetName);
        holder.author.setText(snip.snippetAuthor);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onItemClick(holder.button, holder.title, holder.author, v, snip, position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return snippets.size();
    }

    public interface OnitemClickListener {
        void onItemClick(Button b, TextView title, TextView author, View v, Snippet snip, int position);
    }

    public class SnippetHolder extends RecyclerView.ViewHolder{
        Button button;
        TextView title;
        TextView author;
        public SnippetHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            author = (TextView) itemView.findViewById(R.id.author);
            button = (Button) itemView.findViewById(R.id.button);
        }
    }
}
