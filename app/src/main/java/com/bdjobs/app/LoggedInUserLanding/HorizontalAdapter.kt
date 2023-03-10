package com.bdjobs.app.LoggedInUserLanding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.MoreHorizontalData
import com.bdjobs.app.InviteCode.InviteCodeBaseActivity
import com.bdjobs.app.R
import org.jetbrains.anko.startActivity


class HorizontalAdapter(val context: Context) : RecyclerView.Adapter<HorizontalViewHolder>() {

    private var moreItems: ArrayList<MoreHorizontalData>? = ArrayList()
    private val homeCommunicator = context as HomeCommunicator

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        return HorizontalViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_data_horizontal_view, parent, false))

    }

    override fun getItemCount(): Int {
        return if (moreItems == null) 0 else moreItems!!.size
      }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        setFadeAnimation(holder.itemView)
       // holder.resourceID_Value.background = context.getDrawable(moreItems!![position].resourceID)
        try {
            holder.resourceID_Value.background = AppCompatResources.getDrawable(context, moreItems?.get(position)?.resourceID!!)

        } catch (e:Exception){
            e.printStackTrace()
        }
        holder.resourceName_Value.text = moreItems!![position].resourceName
        holder.itemView.setOnClickListener {
            when(moreItems!![position].resourceName){

                "Favourite\nSearch"->{
                    homeCommunicator.goToFavSearchFilters()
                }
                "Applied\nJobs"->{
                    homeCommunicator.setTime("0")
                    homeCommunicator.goToAppliedJobs()
                }
                "Followed\nEmployers"->{
                   // homeCommunicator.goToFollowedEmployerList("follow")
                    homeCommunicator.setTime("0")
                    homeCommunicator.goToFollowedEmployerList("follow")
                }
                "Employer\nList"->{
                    homeCommunicator.goToFollowedEmployerList("employer")
                }

                "Manage\nResume"->{
                    //context.startActivity<EditResLandingActivity>()
                    homeCommunicator.goToResumeManager()
                }

                "?????????????????? &\n????????????"->{
                    context.startActivity<InviteCodeBaseActivity>(
                            "userType" to homeCommunicator.getInviteCodeUserType(),
                            "pcOwnerID" to homeCommunicator.getInviteCodepcOwnerID(),
                            "inviteCodeStatus" to homeCommunicator.getInviteCodeStatus())
                }

                "??????????????????\n?????????"->{
                    context.startActivity<InviteCodeBaseActivity>(
                            "userType" to homeCommunicator.getInviteCodeUserType(),
                            "pcOwnerID" to homeCommunicator.getInviteCodepcOwnerID(),
                            "inviteCodeStatus" to homeCommunicator.getInviteCodeStatus())
                }
            }
        }

       }

    fun add(r: MoreHorizontalData) {
        moreItems?.add(r)
        notifyItemInserted(moreItems!!.size - 1)
    }

    fun addAll(moveResults: List<MoreHorizontalData>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun removeAll() {
        moreItems?.clear()
        notifyDataSetChanged()
    }

    fun setFadeAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 500
        view.startAnimation(anim)
    }
}
class HorizontalViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    var resourceID_Value: ImageView = itemView.findViewById(R.id.resourceID)
    var resourceName_Value: TextView = itemView.findViewById(R.id.resourceName)


}