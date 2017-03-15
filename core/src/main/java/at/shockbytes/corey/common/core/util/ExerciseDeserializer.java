package at.shockbytes.corey.common.core.util;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import at.shockbytes.corey.common.core.workout.model.Exercise;
import at.shockbytes.corey.common.core.workout.model.TimeExercise;

/**
 * @author Martin Macheiner
 *         Date: 28.02.2017.
 */

public class ExerciseDeserializer implements JsonDeserializer<Exercise> {

    private Gson gson;

    public ExerciseDeserializer() {
        gson = new Gson();
    }

    @Override
    public Exercise deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.has("workDuration")) {
            return gson.fromJson(jsonObject, TimeExercise.class);
        } else {
            return gson.fromJson(json, Exercise.class);
        }

    }
}