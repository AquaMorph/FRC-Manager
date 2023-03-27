package com.aquamorph.frcmanager.models.statbotics

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Match(
    @Expose
    @SerializedName("key")
    var key: String,
    @Expose
    @SerializedName("red_epa_sum")
    var redEPASum: Double,
    @Expose
    @SerializedName("red_auto_epa_sum")
    var redAutoEPASum: Double,
    @Expose
    @SerializedName("red_teleop_epa_sum")
    var redTeleopEPASum: Double,
    @Expose
    @SerializedName("red_endgame_epa_sum")
    var redEndgameEPASum: Double,
    @Expose
    @SerializedName("red_rp_1_epa_sum")
    var redRP1EPASum: Double,
    @Expose
    @SerializedName("red_rp_2_epa_sum")
    var redRP2EPASum: Double,
    @Expose
    @SerializedName("blue_epa_sum")
    var blueEPASum: Double,
    @Expose
    @SerializedName("blue_auto_epa_sum")
    var blueAutoEPASum: Double,
    @Expose
    @SerializedName("blue_teleop_epa_sum")
    var blueTeleopEPASum: Double,
    @Expose
    @SerializedName("blue_endgame_epa_sum")
    var blueEndgameEPASum: Double,
    @Expose
    @SerializedName("blue_rp_1_epa_sum")
    var blueRP1EPASum: Double,
    @Expose
    @SerializedName("blue_rp_2_epa_sum")
    var blueRP2EPASum: Double,
    @Expose
    @SerializedName("epa_winner")
    var epaWinner: String,
    @Expose
    @SerializedName("epa_win_prob")
    var epaWinProb: Double
)
