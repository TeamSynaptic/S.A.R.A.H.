package synaptic.dementiahack;

/**
 * Created by Team Synaptic on 2017-03-04.
 */

public class Entry {
    private String title;
    private String timeEnded;
    private String timeCreated;
    private String noun;
    private String verb;
    private boolean finished = false;
    public Entry(){}
    public Entry(String title, String timeCreated){
        this.title = title;
        this.timeCreated = timeCreated;
        verb = title.substring(0,title.indexOf(" ")).toLowerCase();
        noun = title.substring(title.indexOf(" ") + 1).toLowerCase();
    }
    public Entry(String title){
        this.title = title;
        verb = title.substring(0,title.indexOf(" ")).toLowerCase();
        noun = title.substring(title.indexOf(" ") + 1).toLowerCase();
    }
    public String getTitle(){
        return title;
    }
    public String getTimeCreated(){
        return timeCreated;
    }
    public String getTimeEnded(){
        return timeEnded;
    }
    public boolean getFinished(){
        return finished;
    }
    public void setTimeCreated(String timeCreated){
        this.timeCreated = timeCreated;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setTimeEnded(String timeEnded){
        this.timeEnded = timeEnded;
    }
    public void setFinished(boolean finished){
        this.finished = finished;
    }

    public String getNoun() {
        return noun;
    }

    public void setNoun(String noun) {
        this.noun = noun;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }
}
