package com.example.doanckapi.data.model

import com.google.gson.annotations.SerializedName

data class PredictionRequest(
    @SerializedName("HighBP") val HighBP: Float,
    @SerializedName("HighChol") val HighChol: Float,
    @SerializedName("HeartDiseaseorAttack") val HeartDiseaseorAttack: Float,
    @SerializedName("Stroke") val Stroke: Float,
    @SerializedName("GenHlth") val GenHlth: Float,
    @SerializedName("MentHlth") val MentHlth: Float,
    @SerializedName("PhysHlth") val PhysHlth: Float,
    @SerializedName("BMI") val BMI: Float,
    @SerializedName("Age") val Age: Float,
    @SerializedName("Income") val Income: Float,
    @SerializedName("PhysActivity") val PhysActivity: Float,
    @SerializedName("DiffWalk") val DiffWalk: Float,
    @SerializedName("Sex") val Sex: Int? = -1,
    @SerializedName("Pregnancies") val Pregnancies: Int? = 0,
    @SerializedName("Glucose") val Glucose: Float? = -1.0f,
    @SerializedName("Insulin") val Insulin: Float? = -1.0f
)

data class PredictionResponse(
    @SerializedName("prediction") val predictionLabel: Int,
    @SerializedName("prediction_label") val predictionLabelString: String? = null,
    @SerializedName("probability_diabetes") val probabilityDiabetes: Float? = null,
    @SerializedName("diabetes_type") val diabetesType: String? = null,
    @SerializedName("insulin_recommendation") val insulinRecommendation: String? = null,
    @SerializedName("longevity_impact") val longevityImpact: String? = null,
    @SerializedName("suggestions") val suggestions: List<String>? = emptyList()
)
