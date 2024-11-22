
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.apitest.R
import com.example.apitest.activities.DailyQuizNotificationActivity
import com.example.apitest.activities.SelectAvatarActivity
import com.example.apitest.data_classes.UserModel
import com.example.apitest.databinding.FragmentPreferencesBinding
import com.example.apitest.firebase.FirebaseSetup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PreferencesBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentPreferencesBinding
    private val firebaseSetup = FirebaseSetup()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreferencesBinding.inflate(inflater, container, false)

        loadUserPreferences()
        setupPreferenceListeners()





        return binding.root
    }


    private fun loadUserPreferences() {
        firebaseSetup.getUserInfo(object : FirebaseSetup.UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                userInfo?.let { user ->
                    // Set difficulty radio button
                    val difficultyRadioButton = when (user.preferredDifficulty) {
                        "easy" -> binding.radioEasy
                        "medium" -> binding.radioMedium
                        "hard" -> binding.radioHard
                        else -> binding.radioAny
                    }
                    difficultyRadioButton.isChecked = true

                    // Set question count radio button
                    val questionRadioButton = when (user.preferredQuestionCount) {
                        5 -> binding.radio5
                        15 -> binding.radio15
                        else -> binding.radio10
                    }
                    questionRadioButton.isChecked = true

                    val resourceId = resources.getIdentifier(
                        user.image.lowercase(), "drawable", requireContext().packageName
                    )
                    binding.userProfilePicture.setImageResource(resourceId)


                }
            }
        })
    }



    private fun setupPreferenceListeners() {
        // Difficulty radio group listener
        binding.difficultyRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val difficulty = when (checkedId) {
                R.id.radio_easy -> "easy"
                R.id.radio_medium -> "medium"
                R.id.radio_hard -> "hard"
                else -> "any"
            }
            updatePreferences(difficulty = difficulty)
        }

        // Question count radio group listener
        binding.questionsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val count = when (checkedId) {
                R.id.radio_5 -> 5
                R.id.radio_15 -> 15
                else -> 10
            }
            updatePreferences(questionCount = count)
        }

        binding.changePictureButton.setOnClickListener {
            startActivity(Intent(requireContext(), SelectAvatarActivity::class.java))
        }

        binding.dailyQuizButton.setOnClickListener {
            startActivity(Intent(requireContext(), DailyQuizNotificationActivity::class.java))
        }
    }

    private fun updatePreferences(difficulty: String? = null, questionCount: Int? = null) {
        firebaseSetup.getUserInfo(object : FirebaseSetup.UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                userInfo?.let { currentUser ->
                    val updatedUser = currentUser.copy(
                        preferredDifficulty = difficulty ?: currentUser.preferredDifficulty,
                        preferredQuestionCount = questionCount ?: currentUser.preferredQuestionCount
                    )
                    firebaseSetup.updateUserPreferences(updatedUser)
                }
            }
        })
    }








    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) as FrameLayout?

            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                // Set the height to 90% of the screen height
                setupFullHeight(sheet)
                // Start expanded
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                // Optional: prevent collapsing
                behavior.skipCollapsed = true
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = getWindowHeight() * 90 / 100 // 90% of screen height
        bottomSheet.layoutParams = layoutParams
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = requireActivity().resources.displayMetrics
        return displayMetrics.heightPixels
    }

    companion object {
        const val TAG = "PreferencesBottomSheet"
    }

    override fun onResume() {
        super.onResume()
        loadUserPreferences()
    }
}
