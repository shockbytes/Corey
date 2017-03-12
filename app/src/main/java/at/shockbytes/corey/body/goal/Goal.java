package at.shockbytes.corey.body.goal;

/**
 * @author Martin Macheiner
 *         Date: 05.03.2017.
 */

public class Goal {

    private String id;
    private String message;
    private boolean isDone;

    public Goal() {
        this("");
    }

    public Goal(String message) {
        this(message, false, "");
    }

    public Goal(String message, boolean isDone, String id) {
        this.message = message;
        this.isDone = isDone;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Goal) {
            return id.equals(((Goal) obj).getId());
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Goal: " + message + "\nDone: " + isDone + "\nId: " + id;
    }
}
