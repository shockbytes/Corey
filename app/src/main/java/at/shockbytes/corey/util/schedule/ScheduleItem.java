package at.shockbytes.corey.util.schedule;

import java.util.UUID;

/**
 * @author Martin Macheiner
 *         Date: 28.02.2017.
 */

public class ScheduleItem {

    private int day;    // 0 - 6
    //private int pos;    // 0 - 1

    private String id;
    private String name;

    public ScheduleItem() {
        this("");
    }

    public ScheduleItem(String name) {
        this(name, -1);
    }

    public ScheduleItem(String name, int day) {
        this.name = name;
        this.day = day;
        id = UUID.randomUUID().toString();
    }


    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEmpty() {
        return name != null && name.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof  ScheduleItem) {
            return ((ScheduleItem) obj).getId().equals(id);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Name: " + name + " / Id: " + id + " / Day: " + day;
    }


}
