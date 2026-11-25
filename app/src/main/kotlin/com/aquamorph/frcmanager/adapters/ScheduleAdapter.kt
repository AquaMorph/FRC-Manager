package com.aquamorph.frcmanager.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.format.DateFormat
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MatchBreakdownActivity
import com.aquamorph.frcmanager.activities.TeamSummary
import com.aquamorph.frcmanager.utils.Constants
import java.util.Date

internal class ScheduleAdapter(private val context: Context) :
    ListAdapter<ScheduleRow, ScheduleAdapter.MatchHolder>(ScheduleRowDiff) {

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private val timeFormat = DateFormat.getTimeFormat(context)
    private val timeTbd = context.getString(R.string.scheduleTimeTbd)
    private val redWinBackground = themeColor(R.attr.scheduleRedWinBackground)
    private val blueWinBackground = themeColor(R.attr.scheduleBlueWinBackground)
    private val neutralPrediction = themeColor(R.attr.textOnBackground)
    private val redPredictionColors = intArrayOf(
        themeColor(R.attr.redLeaning), themeColor(R.attr.redLikely), themeColor(R.attr.redHeavily)
    )
    private val bluePredictionColors = intArrayOf(
        themeColor(R.attr.blueLeaning), themeColor(R.attr.blueLikely), themeColor(R.attr.blueHeavily)
    )
    private val boldNumbers = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)

    private fun themeColor(attr: Int): Int = TypedValue().let {
        if (context.theme.resolveAttribute(attr, it, true)) it.data else Color.TRANSPARENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchHolder =
        MatchHolder(LayoutInflater.from(parent.context).inflate(R.layout.match, parent, false))

    override fun onBindViewHolder(holder: MatchHolder, position: Int) = holder.bind(getItem(position))

    fun fastScrollLabel(position: Int): FastScrollLabel? = currentList.getOrNull(position)?.let { row ->
        val match = Constants.matchString(row.compLevel, row.setNumber, row.matchNumber)
        val round = when (row.compLevel) {
            "qm" -> R.string.scheduleRoundQualification
            "ef" -> R.string.scheduleRoundOctofinal
            "qf" -> R.string.scheduleRoundQuarterfinal
            "sf" -> R.string.scheduleRoundSemifinal
            "f" -> R.string.scheduleRoundFinal
            else -> return@let FastScrollLabel(null, match)
        }
        FastScrollLabel(context.getString(round), match)
    }

    data class FastScrollLabel(val round: String?, val match: String)

    /**
     * getPredictionColor() gets the color the prediction text should be based on predicted winner
     * and their probability of winning.
     * @param probability win probability
     * @param winningAlliance the predicted winning alliance
     * @return text color
     */
    private fun getPredictionColor(probability: Double, winningAlliance: String): Int {
        if (probability <= 0.55) return neutralPrediction
        val colors = if (winningAlliance == "red") redPredictionColors else bluePredictionColors
        return when {
            probability <= 0.6 -> colors[0]
            probability <= 0.65 -> colors[1]
            else -> colors[2]
        }
    }

    /**
     * predictionToString() converts a prediction to string with either percentage or text
     * description.
     *
     * @param probability match win probability
     * @param winner predicted winning alliance name
     * @param percentageEnabled raw percentage enabled
     * @return prediction text
     */
    private fun predictionToString(probability: Double, winner: String, percentageEnabled: Boolean):
            String {
        return when {
            percentageEnabled -> {
                "%2.0f%% %s".format(probability * 100, winner)
            }
            else -> when {
                probability <= 0.55 -> "Tossup"
                probability <= 0.6 -> "Leaning $winner"
                probability <= 0.65 -> "Likely $winner"
                else -> "Heavily $winner"
            }
        }
    }

    inner class MatchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val matchNumber = itemView.findViewById<TextView>(R.id.match_number)
        private val redTeams = listOf(R.id.red_team_1, R.id.red_team_2, R.id.red_team_3)
            .map { itemView.findViewById<TextView>(it) }
        private val blueTeams = listOf(R.id.blue_team_1, R.id.blue_team_2, R.id.blue_team_3)
            .map { itemView.findViewById<TextView>(it) }
        private val matchTime = itemView.findViewById<TextView>(R.id.match_time)
        private val matchStatus = itemView.findViewById<View>(R.id.match_status)
        private val scores = itemView.findViewById<View>(R.id.score_table)
        private val redScore = itemView.findViewById<TextView>(R.id.red_score)
        private val blueScore = itemView.findViewById<TextView>(R.id.blue_score)
        private val prediction = itemView.findViewById<TextView>(R.id.prediction)
        private var boundRow: ScheduleRow? = null

        init {
            matchNumber.setOnClickListener {
                val row = boundRow ?: return@setOnClickListener
                context.startActivity(Intent(context, MatchBreakdownActivity::class.java).apply {
                    putExtra("matchKey", row.key)
                    putExtra("compLevel", row.compLevel)
                    putExtra("setNumber", row.setNumber)
                    putExtra("matchNumber", row.matchNumber)
                    for (i in 0..2) {
                        putExtra("redRobot${i + 1}", row.red.teams.getOrNull(i).orEmpty())
                        putExtra("blueRobot${i + 1}", row.blue.teams.getOrNull(i).orEmpty())
                    }
                })
            }
            redTeams.forEachIndexed { index, slot -> setTeamClick(slot, index, red = true) }
            blueTeams.forEachIndexed { index, slot -> setTeamClick(slot, index, red = false) }
        }

        private fun setTeamClick(slot: TextView, index: Int, red: Boolean) {
            slot.setOnClickListener {
                val row = boundRow ?: return@setOnClickListener
                val number = (if (red) row.red else row.blue).teams.getOrNull(index).orEmpty()
                if (number.isNotEmpty()) {
                    context.startActivity(Intent(context, TeamSummary::class.java).putExtra("teamNumber", number))
                }
            }
        }

        fun bind(row: ScheduleRow) {
            boundRow = row
            matchNumber.text = Constants.matchString(row.compLevel, row.setNumber, row.matchNumber)
            matchNumber.setBackgroundColor(when (row.winningAlliance) {
                "red" -> redWinBackground
                "blue" -> blueWinBackground
                else -> Color.TRANSPARENT
            })
            bindAlliance(row.red, redTeams, redScore)
            bindAlliance(row.blue, blueTeams, blueScore)
            val hasResult = row.red.score != null || row.blue.score != null
            scores.visibility = if (hasResult) View.VISIBLE else View.GONE
            matchStatus.visibility = if (hasResult) View.GONE else View.VISIBLE
            if (!hasResult) matchTime.text = row.time?.let { timeFormat.format(Date(it * 1000)) } ?: timeTbd
            prediction.visibility = if (row.prediction == null) View.INVISIBLE else View.VISIBLE
            prediction.text = row.prediction?.let {
                prediction.setTextColor(getPredictionColor(it.probability, it.winner))
                predictionToString(it.probability, it.winner, it.percentage)
            }.orEmpty()
        }

        private fun bindAlliance(alliance: ScheduleAlliance, slots: List<TextView>, score: TextView) {
            val typeface = if (alliance.emphasized) boldNumbers else Typeface.MONOSPACE
            slots.forEachIndexed { index, slot ->
                val number = alliance.teams.getOrNull(index).orEmpty()
                slot.text = if (number.isNotEmpty() && number == alliance.highlightedTeam) {
                    SpannableString(number).apply {
                        setSpan(UnderlineSpan(), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                } else number
                slot.typeface = typeface
                slot.isClickable = number.isNotEmpty()
            }
            score.typeface = typeface
            score.text = when {
                alliance.score == null -> ""
                alliance.bonusRp == 0 -> alliance.score.toString()
                else -> context.getString(R.string.scheduleScoreWithBonus, bonusRpDots(alliance.bonusRp), alliance.score)
            }
        }
    }
}

internal fun bonusRpDots(bonusRp: Int): String = "\u2022".repeat(bonusRp)

internal object ScheduleRowDiff : DiffUtil.ItemCallback<ScheduleRow>() {
    override fun areItemsTheSame(oldItem: ScheduleRow, newItem: ScheduleRow) = oldItem.key == newItem.key

    override fun areContentsTheSame(oldItem: ScheduleRow, newItem: ScheduleRow) = oldItem == newItem
}
