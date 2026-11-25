package com.aquamorph.frcmanager.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class ScheduleAdapter(private val context: Context) :
    ListAdapter<ScheduleRow, ScheduleAdapter.MatchHolder>(ScheduleRowDiff) {

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private val timeFormat = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchHolder =
        MatchHolder(LayoutInflater.from(parent.context).inflate(R.layout.match, parent, false))

    override fun onBindViewHolder(holder: MatchHolder, position: Int) = holder.bind(getItem(position))

    /**
     * getPredictionColor() gets the color the prediction text should be based on predicted winner
     * and their probability of winning.
     * @param probability win probability
     * @param winningAlliance the predicted winning alliance
     * @return text color
     */
    private fun getPredictionColor(probability: Double, winningAlliance: String): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        when {
            probability <= 0.55 -> {
                theme.resolveAttribute(R.attr.textOnBackground, typedValue, true)
            }
            winningAlliance == "red" -> {
                when {
                    probability <= 0.6 ->
                        theme.resolveAttribute(R.attr.redLeaning, typedValue, true)
                    probability <= 0.65 ->
                        theme.resolveAttribute(R.attr.redLikely, typedValue, true)
                    else ->
                        theme.resolveAttribute(R.attr.redHeavily, typedValue, true)
                }
            }
            else -> {
                when {
                    probability <= 0.6 ->
                        theme.resolveAttribute(R.attr.blueLeaning, typedValue, true)
                    probability <= 0.65 ->
                        theme.resolveAttribute(R.attr.blueLikely, typedValue, true)
                    else ->
                        theme.resolveAttribute(R.attr.blueHeavily, typedValue, true)
                }
            }
        }
        return typedValue.data
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
                probability <= 0.6 -> "Leaning %s".format(winner)
                probability <= 0.65 -> "Likely %s".format(winner)
                else -> "Heavily %s".format(winner)
            }
        }
    }

    /**
     * rpToString() converts additional rank points to indicator string.
     *
     * @param rp additional rank points
     * @return rank point unicode
     */
    private fun rpToString(rp: Int): String {
        return when (rp) {
            1 -> "⬤"
            2 -> "⬤⬤"
            else -> ""
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
        private val redScore = itemView.findViewById<TextView>(R.id.red_score)
        private val blueScore = itemView.findViewById<TextView>(R.id.blue_score)
        private val scores = itemView.findViewById<View>(R.id.score_table)
        private val prediction = itemView.findViewById<TextView>(R.id.prediction)

        fun bind(row: ScheduleRow) {
            matchNumber.text = Constants.matchString(row.compLevel, row.setNumber, row.matchNumber)
            matchNumber.setOnClickListener {
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
            bindAlliance(row.red, redTeams, redScore)
            bindAlliance(row.blue, blueTeams, blueScore)
            scores.visibility = if (row.time == null) View.VISIBLE else View.GONE
            matchStatus.visibility = if (row.time == null) View.GONE else View.VISIBLE
            matchTime.text = row.time?.let { timeFormat.format(Date(it * 1000)) }.orEmpty()
            prediction.visibility = if (row.prediction == null) View.GONE else View.VISIBLE
            row.prediction?.let {
                prediction.text = predictionToString(it.probability, it.winner, it.percentage)
                prediction.setTextColor(getPredictionColor(it.probability, it.winner))
            }
        }

        private fun bindAlliance(alliance: ScheduleAlliance, slots: List<TextView>, score: TextView) {
            slots.forEachIndexed { index, slot ->
                val number = alliance.teams.getOrNull(index).orEmpty()
                slot.text = if (number.isNotEmpty() && number == alliance.highlightedTeam) {
                    SpannableString(number).apply {
                        setSpan(UnderlineSpan(), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                } else number
                slot.setTypeface(Typeface.MONOSPACE, if (alliance.emphasized) Typeface.BOLD else Typeface.NORMAL)
                slot.setOnClickListener(if (number.isEmpty()) null else View.OnClickListener {
                    context.startActivity(Intent(context, TeamSummary::class.java).putExtra("teamNumber", number))
                })
                slot.isClickable = number.isNotEmpty()
            }
            score.setTypeface(Typeface.MONOSPACE, if (alliance.emphasized) Typeface.BOLD else Typeface.NORMAL)
            score.text = alliance.score?.let {
                context.getString(R.string.rankPointFormat, rpToString(alliance.bonusRp), it.toString())
            }.orEmpty()
        }
    }
}

internal object ScheduleRowDiff : DiffUtil.ItemCallback<ScheduleRow>() {
    override fun areItemsTheSame(oldItem: ScheduleRow, newItem: ScheduleRow) = oldItem.key == newItem.key

    override fun areContentsTheSame(oldItem: ScheduleRow, newItem: ScheduleRow) = oldItem == newItem
}
