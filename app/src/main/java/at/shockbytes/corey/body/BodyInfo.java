package at.shockbytes.corey.body;

import java.text.DateFormat;
import java.util.Collections;
import java.util.List;

import at.shockbytes.corey.common.core.util.ResourceManager;
import at.shockbytes.corey.body.points.BmiPoint;
import at.shockbytes.corey.body.points.BodyFatPoint;
import at.shockbytes.corey.body.points.WeightPoint;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Martin Macheiner
 *         Date: 03.08.2016.
 */
public class BodyInfo extends RealmObject {

    // Little hack, we just need this as primary key which is always the same value
    @PrimaryKey
    private long id = 1;

    private RealmList<WeightPoint> weightPoints;
    private RealmList<BodyFatPoint> bodyFatPoints;
    private RealmList<BmiPoint> bmiPoints;

    private double height;
    private BmiPoint latestBmi;
    private int dreamWeight;

    public BodyInfo() {
        latestBmi = new BmiPoint(0, 0);
        bmiPoints = new RealmList<>();
        weightPoints = new RealmList<>();
        bodyFatPoints = new RealmList<>();
    }

    public int getDreamWeight() {
        return dreamWeight;
    }

    public void setDreamWeight(int dreamWeight) {
        this.dreamWeight = dreamWeight;
    }

    public double getStartWeight() {

        if (weightPoints.size() > 0) {
            return weightPoints.get(0).getWeight();
        }
        return 0;
    }

    public double getLowestWeight() {
        return (weightPoints != null) ? Collections.min(weightPoints).getWeight() : 0;
    }

    public double getLowestBMI() {
        return (bmiPoints != null) ? Collections.min(bmiPoints).getBmi() : 0;
    }

    public List<WeightPoint> getWeightPoints() {
        return weightPoints;
    }

    void setWeightPoints(RealmList<WeightPoint> weightPoints) {
        this.weightPoints = weightPoints;
    }

    public List<BodyFatPoint> getBodyFatPoints() {
        return bodyFatPoints;
    }

    void setBodyFatPoints(RealmList<BodyFatPoint> bodyFatPoints) {
        this.bodyFatPoints = bodyFatPoints;
    }

    private List<BmiPoint> getBmiPoints() {

        // Lazy initialization of BmiPoints
        if (bmiPoints == null || bmiPoints.size() == 0) {
            bmiPoints = new RealmList<>();
            for (WeightPoint wp : weightPoints) {
                double bmi = wp.getWeight() / (height * height);
                bmiPoints.add(new BmiPoint(wp.getTime(), ResourceManager.roundDoubleWithDigits(bmi, 1)));
            }
            // Store a the last bmi point
            if (bmiPoints.size() > 0) {
                latestBmi = bmiPoints.get(bmiPoints.size()-1);
            }
        }
        return bmiPoints;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public BmiPoint getLatestBmi() {

        if (latestBmi.getBmi() != 0) {
            return latestBmi;
        }
        // Create all BMI points
        getBmiPoints();

        return latestBmi;
    }

    public WeightPoint getLatestWeightPoint() {

        if (weightPoints != null && weightPoints.size() > 0) {
            return weightPoints.get(weightPoints.size()-1);
        }
        return new WeightPoint(0, 0);
    }

    public BodyFatPoint getLatestBodyFatPoint() {

        if (bodyFatPoints != null && bodyFatPoints.size() > 0) {
            return bodyFatPoints.get(bodyFatPoints.size()-1);
        }
        return new BodyFatPoint(0, 0);
    }

    public BodyInfo appendAndUpdate(BodyInfo other) {

        this.weightPoints.addAll(other.weightPoints);
        this.bodyFatPoints.addAll(other.bodyFatPoints);
        this.dreamWeight = other.dreamWeight;
        this.height = other.height;

        // Calculate bmi points once again
        getBmiPoints();

        return this;
    }

    @Override
    public String toString() {

        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        StringBuilder sb = new StringBuilder("Body information\n\n");

        sb.append("Height:\t");
        sb.append(height);
        sb.append("m\n");

        sb.append("\nDream weight:\t");
        sb.append(dreamWeight);
        sb.append("kg\n");

        sb.append("\nWeight: (");
        sb.append(weightPoints.size());
        sb.append(" points)\n");
        for (WeightPoint wp : weightPoints) {
            sb.append("Time: ");
            sb.append(format.format(wp.getTime()));
            sb.append(" / Weight: ");
            sb.append(wp.getWeight());
            sb.append("kg\n");
        }
        sb.append("\nBody fat: (");
        sb.append(bodyFatPoints.size());
        sb.append(" points)\n");
        for (BodyFatPoint bfp : bodyFatPoints) {
            sb.append("Time: ");
            sb.append(format.format(bfp.getTime()));
            sb.append(" / Bodyfat: ");
            sb.append(bfp.getBodyFat());
            sb.append("%\n");
        }
        if (bmiPoints == null) {
            getBmiPoints();
        }
        sb.append("\nBMI: (");
        sb.append(bmiPoints.size());
        sb.append(" points)\n");
        for (BmiPoint bp : bmiPoints) {
            sb.append("Time: ");
            sb.append(format.format(bp.getTime()));
            sb.append(" / BMI: ");
            sb.append(bp.getBmi());
            sb.append("\n");
        }
        return sb.toString();
    }
}
