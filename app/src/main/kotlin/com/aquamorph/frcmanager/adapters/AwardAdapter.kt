package com.aquamorph.frcmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.models.tba.Award
import com.aquamorph.frcmanager.utils.Constants

class AwardAdapter(
    private val awards: List<Award>,
    private val teamNames: Map<String, String>
) : RecyclerView.Adapter<AwardAdapter.AwardHolder>() {
    override fun getItemCount(): Int = awards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AwardHolder {
        return AwardHolder(LayoutInflater.from(parent.context).inflate(R.layout.award, parent, false))
    }

    override fun onBindViewHolder(holder: AwardHolder, position: Int) {
        holder.bind(awards[position], teamNames)
    }

    class AwardHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.award_name)
        private val art: ImageView = view.findViewById(R.id.award_art)
        private val recipients: LinearLayout = view.findViewById(R.id.award_recipients)
        private val inflater = LayoutInflater.from(view.context)

        init {
            ViewCompat.setAccessibilityHeading(title, true)
        }

        fun bind(award: Award, teamNames: Map<String, String>) {
            val banner = award.awardType in BLUE_BANNER_TYPES
            itemView.setBackgroundResource(if (banner) R.drawable.bg_award_banner_card else R.drawable.bg_award_card)
            art.contentDescription = if (banner) itemView.resources.getString(R.string.award_blue_banner) else null
            art.setImageResource(when {
                banner -> R.drawable.ic_award_blue_banner
                award.awardType == FINALIST -> R.drawable.ic_award_medal
                else -> R.drawable.ic_award_plaque
            })
            title.text = award.name
            recipients.removeAllViews()

            for (recipient in award.recipientList) {
                val teamNumber = recipient.teamKey?.let(Constants::formatTeamNumber).orEmpty()
                val name = recipient.awardee.orEmpty()
                if (teamNumber.isEmpty() && name.isBlank()) continue

                val row = inflater.inflate(R.layout.item_award_recipient, recipients, false)
                val teamLabel = row.findViewById<TextView>(R.id.award_recipient_team)
                teamLabel.isVisible = teamNumber.isNotEmpty()
                teamLabel.text = row.context.getString(R.string.award_team_number, teamNumber)

                val teamName = recipient.teamKey?.let(teamNames::get).orEmpty()
                val teamNameLabel = row.findViewById<TextView>(R.id.award_recipient_team_name)
                teamNameLabel.isVisible = teamName.isNotBlank()
                teamNameLabel.text = teamName

                val nameLabel = row.findViewById<TextView>(R.id.award_recipient_name)
                nameLabel.isVisible = name.isNotBlank()
                nameLabel.text = name
                recipients.addView(row)
            }
        }
    }

    private companion object {
        private const val FINALIST = 2

        // The Blue Alliance's BLUE_BANNER_AWARDS, keyed by stable API award type (not display name).
        // Impact/Chairman's, Winner, Woodie Flowers, Impact/Chairman's Finalist, and the 2021 challenge winners.
        private val BLUE_BANNER_TYPES = setOf(0, 1, 3, 69, 74, 80)
    }
}
