package com.aquamorph.frcmanager.models.tba

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MatchBreakdown(
    // Generic
    @Expose
    @SerializedName("adjustPoints")
    val adjustPoints: Int,
    @Expose
    @SerializedName("autoPoints")
    val autoPoints: Int,
    @Expose
    @SerializedName("foulCount")
    val foulCount: Int,
    @Expose
    @SerializedName("foulPoints")
    val foulPoints: Int,
    @Expose
    @SerializedName("techFoulCount")
    val techFoulCount: Int,
    @Expose
    @SerializedName("teleopPoints")
    val teleopPoints: Int,
    @Expose
    @SerializedName("rp")
    var rp: Int,
    @Expose
    @SerializedName("totalPoints")
    val totalPoints: Int,
    // 2023
    @Expose
    @SerializedName("activationBonusAchieved")
    val activationBonusAchieved: Boolean,
    @Expose
    @SerializedName("autoBridgeState")
    val autoBridgeState: String,
    @Expose
    @SerializedName("autoChargeStationPoints")
    val autoChargeStationPoints: Int,
    @Expose
    @SerializedName("autoChargeStationRobot1")
    val autoChargeStationRobot1: String,
    @Expose
    @SerializedName("autoChargeStationRobot2")
    val autoChargeStationRobot2: String,
    @Expose
    @SerializedName("autoChargeStationRobot3")
    val autoChargeStationRobot3: String,
    @Expose
    @SerializedName("autoDocked")
    val autoDocked: Boolean,
    // @Expose
    // @SerializedName("autoCommunity")
    // TODO
    // val autoCommunity: String,
    @Expose
    @SerializedName("autoGamePieceCount")
    val autoGamePieceCount: Int,
    @Expose
    @SerializedName("autoGamePiecePoints")
    val autoGamePiecePoints: Int,
    @Expose
    @SerializedName("autoMobilityPoints")
    val autoMobilityPoints: Int,
    @Expose
    @SerializedName("mobilityRobot1")
    val mobilityRobot1: String,
    @Expose
    @SerializedName("mobilityRobot2")
    val mobilityRobot2: String,
    @Expose
    @SerializedName("mobilityRobot3")
    val mobilityRobot3: String,
    @Expose
    @SerializedName("coopGamePieceCount")
    val coopGamePieceCount: Int,
    @Expose
    @SerializedName("coopertitionCriteriaMet")
    val coopertitionCriteriaMet: Boolean,
    @Expose
    @SerializedName("endGameBridgeState")
    val endGameBridgeState: String,
    @Expose
    @SerializedName("endGameChargeStationPoints")
    val endGameChargeStationPoints: Int,
    @Expose
    @SerializedName("endGameChargeStationRobot1")
    val endGameChargeStationRobot1: String,
    @Expose
    @SerializedName("endGameChargeStationRobot2")
    val endGameChargeStationRobot2: String,
    @Expose
    @SerializedName("endGameChargeStationRobot3")
    val endGameChargeStationRobot3: String,
    @Expose
    @SerializedName("endGameParkPoints")
    val endGameParkPoints: Int,
    @Expose
    @SerializedName("linkPoints")
    val linkPoints: Int,
    // @Expose
    // @SerializedName("links")
    // TODO
    // val links: String,
    @Expose
    @SerializedName("sustainabilityBonusAchieved")
    val sustainabilityBonusAchieved: Boolean,
    // @Expose
    // @SerializedName("teleopCommunity")
    // TODO
    // val teleopCommunity: String,
    @Expose
    @SerializedName("teleopGamePieceCount")
    val teleopGamePieceCount: Int,
    @Expose
    @SerializedName("teleopGamePiecePoints")
    val teleopGamePiecePoints: Int,
    @Expose
    @SerializedName("totalChargeStationPoints")
    val totalChargeStationPoints: Int
)
