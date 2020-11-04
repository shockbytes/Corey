package at.shockbytes.corey.data.nutrition.lookup.edamam

import com.google.gson.annotations.SerializedName

/* Example Response
{
  "text": "gala apple",
  "parsed": [
    {
      "food": {
        "foodId": "food_bfh0qoxboaspbtbj3gqnkafdf2r9",
        "uri": "http://www.edamam.com/ontologies/edamam.owl#Food_gala_apple",
        "label": "gala apple",
        "nutrients": {
          "ENERC_KCAL": 57.0,
          "PROCNT": 0.25,
          "FAT": 0.12,
          "CHOCDF": 13.68,
          "FIBTG": 2.3
        },
        "category": "Generic foods",
        "categoryLabel": "food",
        "image": "https://www.edamam.com/food-img/256/2568844fd3c89a9fa6ef0a07757ed572.jpg"
      }
    }
  ],
  "hints": [
    {
      "food": {
        "foodId": "food_bfh0qoxboaspbtbj3gqnkafdf2r9",
        "uri": "http://www.edamam.com/ontologies/edamam.owl#Food_gala_apple",
        "label": "gala apple",
        "nutrients": {
          "ENERC_KCAL": 57.0,
          "PROCNT": 0.25,
          "FAT": 0.12,
          "CHOCDF": 13.68,
          "FIBTG": 2.3
        },
        "category": "Generic foods",
        "categoryLabel": "food",
        "image": "https://www.edamam.com/food-img/256/2568844fd3c89a9fa6ef0a07757ed572.jpg"
      },
      "measures": [
        {
          "uri": "http://www.edamam.com/ontologies/edamam.owl#Measure_unit",
          "label": "Whole",
          "weight": 180.44444444444446,
          "qualified": [
            {
              "qualifiers": [
                {
                  "uri": "http://www.edamam.com/ontologies/edamam.owl#Qualifier_medium",
                  "label": "medium"
                }
              ],
              "weight": 172.0
            },
            {
              "qualifiers": [
                {
                  "uri": "http://www.edamam.com/ontologies/edamam.owl#Qualifier_large",
                  "label": "large"
                }
              ],
              "weight": 200.0
            },
            {
              "qualifiers": [
                {
                  "uri": "http://www.edamam.com/ontologies/edamam.owl#Qualifier_small",
                  "label": "small"
                }
              ],
              "weight": 157.0
            }
          ]
        },
        {
          "uri": "http://www.edamam.com/ontologies/edamam.owl#Measure_serving",
          "label": "Serving",
          "weight": 55.0
        },
        {
          "uri": "http://www.edamam.com/ontologies/edamam.owl#Measure_gram",
          "label": "Gram",
          "weight": 1.0
        },
        {
          "uri": "http://www.edamam.com/ontologies/edamam.owl#Measure_ounce",
          "label": "Ounce",
          "weight": 28.349523125
        },
        {
          "uri": "http://www.edamam.com/ontologies/edamam.owl#Measure_pound",
          "label": "Pound",
          "weight": 453.59237
        },
        {
          "uri": "http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram",
          "label": "Kilogram",
          "weight": 1000.0
        },
        {
          "uri": "http://www.edamam.com/ontologies/edamam.owl#Measure_cup",
          "label": "Cup",
          "weight": 109.0,
          "qualified": [
            {
              "qualifiers": [
                {
                  "uri": "http://www.edamam.com/ontologies/edamam.owl#Qualifier_sliced",
                  "label": "sliced"
                }
              ],
              "weight": 109.0
            }
          ]
        }
      ]
    }
    ........................................
    ........................................
  "_links": {
    "next": {
      "title": "Next page",
      "href": "https://api.edamam.com/api/food-database/v2/parser?session=42&ingr=gala+apple&app_id=bed7de23&app_key=723503d473f80798e6f2bae12c12c4be"
    }
  }
}
 */
data class EdamamLookupResponse(
    @SerializedName("text") val searchedKeyword: String,
    @SerializedName("parsed") protected val result: List<EdamamResult>,
    @SerializedName("hints") protected val otherHits: List<EdamamResult>
) {

    val results: List<EdamamResult>
        get() = result + otherHits

    data class EdamamResult(
        val food: Food
    ) {

        val image: String?
            get() = food.image

        val kcal: Int?
            get() = food.nutrients.energy.toInt()
    }

    data class Food(
        val foodId: String,
        val label: String,
        val nutrients: Nutrients,
        val image: String?
    )

    data class Nutrients(
        @SerializedName("ENERC_KCAL") val energy: Double,
        @SerializedName("PROCNT") val protein: Double,
        @SerializedName("FAT") val fat: Double,
        @SerializedName("FIBTG") val fiber: Double
    )
}