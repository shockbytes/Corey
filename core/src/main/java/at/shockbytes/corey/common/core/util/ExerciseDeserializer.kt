package at.shockbytes.corey.common.core.util

import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.common.core.workout.model.TimeExercise
import com.google.gson.*
import java.lang.reflect.Type

/**
 * @author Martin Macheiner
 * Date: 28.02.2017.
 */

class ExerciseDeserializer : JsonDeserializer<Exercise> {

    private val gson: Gson = Gson()

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type,
                             context: JsonDeserializationContext): Exercise {

        val jsonObject = json.asJsonObject
        return if (jsonObject.has("workDuration")) {
            gson.fromJson(jsonObject, TimeExercise::class.java)
        } else {
            gson.fromJson(json, Exercise::class.java)
        }

    }
}