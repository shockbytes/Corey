package at.shockbytes.corey.common.core.util.view.model;

/**
 * @author Martin Macheiner
 *         Date: 24.02.2017.
 */

public class SpinnerData {

    private String text;
    private int iconId;

    public SpinnerData(String text, int iconId) {
        this.text = text;
        this.iconId = iconId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}
