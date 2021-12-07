package com.bdjobs.app.Settings

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.AutoSuggestionModel
import com.bdjobs.app.API.ModelClasses.DataAutoSuggestion
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.*
import com.bdjobs.app.databinding.FragmentResumePrivacyBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class ResumePrivacyFragment : Fragment() {

    private lateinit var binding: FragmentResumePrivacyBinding
    private lateinit var communicator: SettingsCommunicator
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private var selectedStatus = "0"

    private var employerList = ArrayList<DataAutoSuggestion>()
    private var employerIDList = ArrayList<String>()
    private var employerListMap: HashMap<String, String> = HashMap()

    private var employerID = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResumePrivacyBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        communicator = activity as SettingsCommunicator
        bdjobsUserSession = BdjobsUserSession(requireContext())
        binding.backIV.setOnClickListener {
            employerIDList.clear()
            communicator.backButtonPressed()
        }

        Timber.d("EmployerIDs: ${TextUtils.join(",", employerIDList)}")

        fetchPrivacyStatus()

        radioGroupListener()

        employerNameClickedListener()

        binding.fabSubmit.setOnClickListener { updatePrivacy() }
    }

    private fun updatePrivacy() {

        binding.linearProgress.show()

        lifecycleScope.launch {
            try {
                val employers = TextUtils.join(",", employerIDList)

                val empIDs = if (selectedStatus == "3") ",$employers," else ""

                Timber.d("Employers: $empIDs --> Status: $selectedStatus")
                val response = ApiServiceMyBdjobs.create().resumePrivacyUpdate(
                    bdjobsUserSession.userId,
                    bdjobsUserSession.decodId,
                    empIDs,
                    selectedStatus
                )

                binding.linearProgress.hide()

                if (response.statuscode == "1") {
                    Snackbar.make(binding.clRoot, response.message!!, Snackbar.LENGTH_LONG).show()
                    employerIDList.clear()
                } else {
                    toast("Update failed")
                }

            } catch (e: Exception) {
                toast("Update failed")
                e.printStackTrace()
                binding.linearProgress.hide()
                Toast.makeText(
                    requireContext(),
                    "Update failed: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
                Timber.e("Exception while updating resume privacy: ${e.localizedMessage}")
            }
        }
    }

    private fun employerNameClickedListener() {
        val empList: ArrayList<String> = ArrayList()
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, empList)

        binding.actvEmployerName.setAdapter(adapter)
        adapter.setNotifyOnChange(true)

        binding.actvEmployerName.easyOnTextChangedListener { e: CharSequence ->
            fetchEmployerSuggestion(e.toString(), adapter)
        }

        binding.actvEmployerName.setOnItemClickListener { _, _, position, _ ->
            employerID = employerList[position].subCatId!!

            if (employerIDList.contains(employerID)) {
                binding.actvEmployerName.closeKeyboard(requireActivity())
                toast("Employer already added")
                binding.actvEmployerName.setText("")
                binding.actvEmployerName.clearFocus()
            } else {
                addChip(employerList[position].subName!!, employerID)
                addEmployerID(employerID)


                binding.actvEmployerName.setText("")
                binding.actvEmployerName.clearFocus()
            }
        }
    }

    private fun fetchEmployerSuggestion(query: String, adapter: ArrayAdapter<String>) {
        Timber.d("Query: $query")
        ApiServiceMyBdjobs.create().fetchAutoSuggestion(query, "8", ver = "EN").enqueue(object :
            Callback<AutoSuggestionModel> {
            override fun onFailure(call: Call<AutoSuggestionModel>, t: Throwable) {
                Timber.e("Failed Fetching Auto Suggestion: ${t.localizedMessage}")
            }

            override fun onResponse(
                call: Call<AutoSuggestionModel>,
                response: Response<AutoSuggestionModel>
            ) {
                Timber.d("Response: ${Gson().toJson(response.body())}")
                try {
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                val body = response.body()

                                if (body?.statuscode == "0") {

                                    employerList = body.data as ArrayList<DataAutoSuggestion>

                                    Timber.d("List size: ${employerList.size}")
                                    adapter.clear()

                                    if (employerList.isNullOrEmpty()) Timber.d("List is empty / null")
                                    else {
                                        val suggestion = ArrayList<String>()
                                        for (i in employerList.indices) {
                                            suggestion.add(employerList[i].subName!!)
                                        }

                                        val a = ArrayAdapter(
                                            activity!!,
                                            android.R.layout.simple_dropdown_item_1line,
                                            suggestion
                                        )
                                        binding.actvEmployerName.setAdapter(a)
                                        a.notifyDataSetChanged()
                                    }
                                }


                            }
                            else -> Timber.e("Response not fetched: Error: ${response.code()}")
                        }
                    } else Timber.e("Unsuccessful response: ${response.code()}")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Timber.e("Exception: ${e.localizedMessage}")
                }
            }

        })
    }

    private fun radioGroupListener() {

        binding.rgPrivacyStatus.setOnCheckedChangeListener { group, _ ->
            val checkedId = group.checkedRadioButtonId

            Timber.d("checked change")

            when (checkedId) {
                R.id.rb_public -> {
                    selectedStatus = "1"
                    setViews(false)
                }
                R.id.rb_private -> {
                    selectedStatus = "2"
                    setViews(false)
                }
                R.id.rb_limited -> {
                    selectedStatus = "3"
                    setViews(true)
                }
            }
        }
    }

    private fun setViews(isLimited: Boolean) {
        if (isLimited) {
            binding.tilEmployerName.show()
            binding.tvLabelSelectedEmployers.show()
            binding.empNameChipGroup.show()
        } else {
            binding.tilEmployerName.invisible()
            binding.tvLabelSelectedEmployers.invisible()
            binding.empNameChipGroup.invisible()
        }
    }

    private fun fetchPrivacyStatus() {
        binding.clParentViews.hide()
        binding.clProgressView.show()

        lifecycleScope.launch {
            try {
                val response = ApiServiceMyBdjobs.create()
                    .resumePrivacyStatus(bdjobsUserSession.userId, bdjobsUserSession.decodId)

                binding.clParentViews.show()
                binding.clProgressView.hide()

                if (response.statuscode == "0" && response.message == "Success") {
                    val data = response.data!![0]

                    selectedStatus = data?.resumeVisibilityType!!

                    Timber.d("Visibility Type: ${data.resumeVisibilityType}")

                    when (data.resumeVisibilityType) {
                        "1" -> binding.rbPublic.isChecked = true
                        "2" -> binding.rbPrivate.isChecked = true
                        "3" -> {
                            binding.rbLimited.isChecked = true

                            val employers = data.data
                            if (!employers.isNullOrEmpty()) {
                                for (employer in employers) {
                                    addChip(employer.employerName!!, employer.id!!)
                                    addEmployerID(employer.id)
//                                    val suggestionData = DataAutoSuggestion(employer.id,employer.employerName,"8")
//                                    employerList.add(suggestionData)
                                    employerListMap[employer.employerName] = employer.id
                                }
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("Exception while fetching Privacy stat")
                binding.clParentViews.show()
                binding.clProgressView.hide()

//                Toast.makeText(
//                    requireContext(),
//                    "Resume Privacy Stat Fetching failed: ${e.localizedMessage}",
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        }
    }

    private fun addChip(employerName: String, employerID: String) {
        // add chip here

        val chip = Chip(requireContext())

        chip.apply {
            text = employerName
            setChipDrawable(ChipDrawable.createFromResource(requireContext(), R.xml.chip_entry))
        }

        binding.empNameChipGroup.addView(chip)


        chip.setOnCloseIconClickListener {
            binding.empNameChipGroup.removeView(chip)
            removeEmployerID(employerID)
        }
    }

    private fun addEmployerID(id: String) {
        if (!employerIDList.contains(id)) {
            employerIDList.add(id)
        }
    }

    private fun removeEmployerID(id: String) {
        if (employerIDList.contains(id)) {
            employerIDList.remove(id)
        }
    }
}