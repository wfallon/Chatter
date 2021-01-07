package edu.upenn.cis350.chatter;

public class Snippet {
    public String snippetName, snippetAuthor, snippetUrl, description, snippetId;
  
    public Snippet(String sn, String sa, String su, String id, String d){
        this.snippetName = sn;
        this.snippetUrl = su;
        this.snippetAuthor = sa;
        this.snippetId = id;
        this.description = d;
    }
}
