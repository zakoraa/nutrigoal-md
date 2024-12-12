package com.c242pS371.nutrigoal.ui.plan_diet

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.data.remote.entity.DietCategory
import com.c242pS371.nutrigoal.data.remote.entity.FoodRecommendationItem
import com.c242pS371.nutrigoal.data.remote.entity.MealScheduleItem
import com.c242pS371.nutrigoal.data.remote.entity.PerDayItem
import com.c242pS371.nutrigoal.data.remote.entity.SurveyRequest
import com.c242pS371.nutrigoal.data.remote.entity.UserEntity
import com.c242pS371.nutrigoal.data.remote.response.SurveyResponse
import com.c242pS371.nutrigoal.databinding.ActivityAddFoodRecommendationBinding
import com.c242pS371.nutrigoal.ui.MainActivity
import com.c242pS371.nutrigoal.ui.common.HistoryViewModel
import com.c242pS371.nutrigoal.ui.plan_diet.PlanDietFragment.Companion.EXTRA_PER_DAY
import com.c242pS371.nutrigoal.ui.plan_diet.PlanDietFragment.Companion.EXTRA_PLAN_DIET_USER
import com.c242pS371.nutrigoal.ui.plan_diet.PlanDietFragment.Companion.IS_NULL
import com.c242pS371.nutrigoal.ui.survey.FavoriteProcessedAdapter
import com.c242pS371.nutrigoal.ui.survey.SurveyViewModel
import com.c242pS371.nutrigoal.utils.InputValidator
import com.c242pS371.nutrigoal.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class AddFoodRecommendationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFoodRecommendationBinding
    private val fruitMap: MutableMap<String, String> = mutableMapOf()
    private val vegetableMap: MutableMap<String, String> = mutableMapOf()
    private val animalMap: MutableMap<String, String> = mutableMapOf()
    private lateinit var favoriteProcessedList: List<String>
    private lateinit var adapter: FavoriteProcessedAdapter
    private var userEntity: UserEntity? = null
    private var perDay: PerDayItem? = null
    private var isNull: Boolean? = null
    private val historyViewModel: HistoryViewModel by viewModels()
    private val surveyViewModel: SurveyViewModel by viewModels()
    private val inputValidator: InputValidator by lazy { InputValidator(this@AddFoodRecommendationActivity) }
    private lateinit var breakfastTimes: Array<String>
    private lateinit var launchTimes: Array<String>
    private lateinit var dinnerTimes: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodRecommendationBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpView()
        setUpAction()
    }

    private fun setUpView() {
        addMapItems()
        setUpAdapter()
        setUpMealScheduleDropdown()

        lifecycleScope.launch {
            historyViewModel.addPerDayItemState.collect {
                handleAddPerDayItem(it)
            }
        }

        lifecycleScope.launch {
            surveyViewModel.surveyResponseState.collect {
                handelGetSurveyResult(it)
            }
        }

        lifecycleScope.launch {
            historyViewModel.addFoodRecommendationState.collect {
                handleAddFoodRecommendation(it)
            }
        }
    }


    private fun handleAddFoodRecommendation(result: ResultState<Unit?>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                ToastUtil.showToast(
                    this@AddFoodRecommendationActivity,
                    getString(R.string.add_food_recommendation_success)
                )
                showLoading(false)
                if (isNull == true) {
                    startActivity(
                        Intent(
                            this@AddFoodRecommendationActivity,
                            MainActivity::class.java
                        )
                    )
                    finish()
                } else {
                    finish()
                }
            }

            is ResultState.Error -> {
                ToastUtil.showToast(
                    this@AddFoodRecommendationActivity,
                    getString(R.string.error_add_per_day_item)
                )
                showLoading(false)
            }

            is ResultState.Initial -> {}
        }
    }

    private fun handelGetSurveyResult(result: ResultState<SurveyResponse?>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                val data = result.data
                val foodRecommendationItem = data?.recommendedFoodPreference?.map {
                    FoodRecommendationItem(
                        id = it?.rfpId,
                        name = it?.name,
                        calories = it?.calories,
                        protein = it?.proteinG,
                        carbohydrate = it?.carbohydrateG,
                        fat = it?.fatG
                    )
                }

                historyViewModel.addFoodRecommendation(
                    userId = userEntity?.id ?: "",
                    calorieNeeds = data?.recommendedFoodBasedOnCalories?.rfbocDailyCalorieNeeds?.toFloatOrNull()
                        ?: 0f,
                    foodRecommendation = foodRecommendationItem?.toSet()?.toList() ?: emptyList()
                )
                showLoading(false)
            }

            is ResultState.Error -> {
                ToastUtil.showToast(
                    this@AddFoodRecommendationActivity,
                    getString(R.string.error_add_per_day_item)
                )
                showLoading(false)
            }

            is ResultState.Initial -> {}
        }
    }

    private fun handleAddPerDayItem(result: ResultState<Unit?>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                val surveyRequest = SurveyRequest(
                    age = userEntity?.age ?: 0,
                    height = userEntity?.height ?: 0f,
                    weight = userEntity?.bodyWeight ?: 0f,
                    gender = userEntity?.gender ?: false,
                    activity_level = userEntity?.activityLevel ?: 1,
                    diet_category = userEntity?.dietCategory ?: DietCategory.KETO.toString(),
                    has_gastric_issue = userEntity?.hasGastricIssue ?: false,
                    food_preference = userEntity?.foodPreference ?: emptyList()
                )
                surveyViewModel.getSurveyResult(surveyRequest)
            }

            is ResultState.Error -> {
                ToastUtil.showToast(
                    this@AddFoodRecommendationActivity,
                    getString(R.string.error_add_per_day_item)
                )
                showLoading(false)
            }

            is ResultState.Initial -> {}
        }
    }

    private fun setUpMealScheduleDropdown() {
        binding.apply {
            breakfastTimes = resources.getStringArray(R.array.breakfast_times)
            launchTimes = resources.getStringArray(R.array.launch_times)
            dinnerTimes = resources.getStringArray(R.array.dinner_times)

            val breakfastAdapter =
                ArrayAdapter(
                    this@AddFoodRecommendationActivity,
                    R.layout.dropdown_item,
                    breakfastTimes
                )
            val launchAdapter = ArrayAdapter(
                this@AddFoodRecommendationActivity,
                R.layout.dropdown_item,
                launchTimes
            )
            val dinnerAdapter = ArrayAdapter(
                this@AddFoodRecommendationActivity,
                R.layout.dropdown_item,
                dinnerTimes
            )

            dropdownBreakfastTime.setText(breakfastTimes[0])
            dropdownLunchTime.setText(launchTimes[0])
            dropdownDinnerTime.setText(dinnerTimes[0])

            dropdownBreakfastTime.setAdapter(breakfastAdapter)
            dropdownLunchTime.setAdapter(launchAdapter)
            dropdownDinnerTime.setAdapter(dinnerAdapter)
        }
    }

    private fun setUpAction() {
        with(binding) {

            ivBack.setOnClickListener {
                finish()
            }
            handleForm()

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter(newText.orEmpty())
                    return true
                }
            })
        }
    }

    private fun handleForm() {
        userEntity = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(
                EXTRA_PLAN_DIET_USER,
                UserEntity::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_PLAN_DIET_USER)
        }

        perDay = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(
                EXTRA_PER_DAY,
                PerDayItem::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_PER_DAY)
        }

        isNull = intent.getBooleanExtra(IS_NULL, false)

        with(binding) {
            if (isNull == true) {
                ivBack.visibility = View.GONE
                line.visibility = View.GONE
            } else {
                ivBack.visibility = View.VISIBLE
                line.visibility = View.VISIBLE
            }
            userEntity?.age?.let { edAge.setText(String.format(it.toString())) }

            val selectedDietCategoryId = rgDietCategory.checkedRadioButtonId

            when (userEntity?.dietCategory) {
                DietCategory.VEGAN.toString() -> rgDietCategory.check(R.id.rb_vegan)
                DietCategory.KETO.toString() -> rgDietCategory.check(R.id.rb_keto)
                else -> rgDietCategory.check(R.id.rb_vegan)
            }

            val initialCheckedId = rgDietCategory.checkedRadioButtonId
            val initialItems = when (initialCheckedId) {
                R.id.rb_vegan -> {
                    favoriteProcessedList.filterNot { animalMap.containsKey(it) }
                }

                R.id.rb_keto -> {
                    fruitMap.keys + vegetableMap.keys + animalMap.keys
                }

                else -> emptyList()
            }

            adapter.clearCheckedItems()
            adapter.updateItems(initialItems)

            rgDietCategory.setOnCheckedChangeListener { _, checkedId ->
                val updatedItems = when (checkedId) {
                    R.id.rb_vegan -> {
                        favoriteProcessedList.filterNot { animalMap.containsKey(it) }
                    }

                    R.id.rb_keto -> {
                        fruitMap.keys + vegetableMap.keys + animalMap.keys
                    }

                    else -> emptyList()
                }
                adapter.clearCheckedItems()
                adapter.updateItems(updatedItems)
            }

            userEntity?.dietCategory = when (selectedDietCategoryId) {
                R.id.rb_vegan -> DietCategory.VEGAN.toString()
                R.id.rb_keto -> DietCategory.KETO.toString()
                else -> null
            }

            val activityLevels = resources.getStringArray(R.array.activity_levels)

            val selectedActivityLevel = autoCompleteTextView.text.toString()
            userEntity?.activityLevel = when (selectedActivityLevel) {
                activityLevels[0] -> 1
                activityLevels[1] -> 2
                activityLevels[2] -> 3
                activityLevels[3] -> 4
                activityLevels[4] -> 5
                else -> 1
            }

            var breakfastTime: String? = null
            var lunchTime = dropdownLunchTime.text.toString()
            var dinnerTime = dropdownDinnerTime.text.toString()

            when (userEntity?.activityLevel) {
                1, 2 -> {
                    inputLayoutBreakfast.visibility = View.GONE
                    tvBreakfast.visibility = View.GONE
                    breakfastTime = null
                }

                3, 4, 5 -> {
                    inputLayoutBreakfast.visibility = View.VISIBLE
                    tvBreakfast.visibility = View.VISIBLE
                    breakfastTime = breakfastTimes[0]
                }
            }

            autoCompleteTextView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val selectedActivity = autoCompleteTextView.text.toString()
                    userEntity?.activityLevel = when (selectedActivity) {
                        activityLevels[0] -> 1
                        activityLevels[1] -> 2
                        activityLevels[2] -> 3
                        activityLevels[3] -> 4
                        activityLevels[4] -> 5
                        else -> 1
                    }

                    when (userEntity?.activityLevel) {
                        1, 2 -> {
                            inputLayoutBreakfast.visibility = View.GONE
                            tvBreakfast.visibility = View.GONE
                            breakfastTime = null
                        }

                        3, 4, 5 -> {
                            inputLayoutBreakfast.visibility = View.VISIBLE
                            tvBreakfast.visibility = View.VISIBLE
                            breakfastTime = breakfastTimes[0]
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            dropdownBreakfastTime.setOnItemClickListener { _, _, position, _ ->
                breakfastTime = breakfastTimes[position]
            }
            dropdownLunchTime.setOnItemClickListener { _, _, position, _ ->
                lunchTime = launchTimes[position]
            }
            dropdownDinnerTime.setOnItemClickListener { _, _, position, _ ->
                dinnerTime = dinnerTimes[position]
            }

            userEntity?.hasGastricIssue?.let { hasIssue ->
                val selectedRadioButtonId = if (hasIssue) R.id.rb_yes else R.id.rb_no
                rgHistoryMag.check(selectedRadioButtonId)
            }

            btnSave.setOnClickListener {
                val mealScheduleItem = MealScheduleItem(
                    breakfastTime = breakfastTime,
                    launchTime = lunchTime,
                    dinnerTime = dinnerTime
                )

                val age = edAge.text?.trim().toString()
                userEntity?.age = age.toIntOrNull()
                val ageError = inputValidator.validateInput(edAge, getString(R.string.age))
                inputValidator.checkValidation(tvErrorAge, ageError)

                val selectedHistoryMagId = rgHistoryMag.checkedRadioButtonId

                userEntity?.hasGastricIssue = when (selectedHistoryMagId) {
                    R.id.rb_yes -> true
                    R.id.rb_no -> false
                    else -> null
                }

                val selectedFoodPreferences = adapter.getCheckedItems()

                userEntity?.foodPreference = selectedFoodPreferences

                if (ageError == null && selectedFoodPreferences.isNotEmpty()) {
                    userEntity?.mealSchedule = mealScheduleItem
                    val calendar = Calendar.getInstance()
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)

                    val createdAt: String
                    val dietTime: String

                    if (perDay != null) {
                        val previousCreatedAt = dateFormat.parse(perDay?.createdAt ?: "")
                        val previousDietTime = dateFormat.parse(perDay?.dietTime ?: "")
                        if (previousCreatedAt != null) {
                            calendar.time = previousCreatedAt
                        }
                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                        createdAt = dateFormat.format(calendar.time)

                        if (previousDietTime != null) {
                            calendar.time = previousDietTime
                        }
                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                        dietTime = dateFormat.format(calendar.time)
                    } else {
                        createdAt = dateFormat.format(calendar.time)
                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                        dietTime = dateFormat.format(calendar.time)
                    }

                    val perDayItem = PerDayItem(
                        id = UUID.randomUUID().toString(),
                        bodyWeight = null,
                        age = userEntity?.age,
                        height = userEntity?.height,
                        activityLevel = userEntity?.activityLevel,
                        dietCategory = userEntity?.dietCategory,
                        hasGastricIssue = userEntity?.hasGastricIssue,
                        foodPreference = userEntity?.foodPreference,
                        mealSchedule = userEntity?.mealSchedule,
                        createdAt = createdAt,
                        dietTime = dietTime,
                    )
                    historyViewModel.addPerDayItem(userEntity?.id ?: "", perDayItem)
                } else {
                    ToastUtil.showToast(
                        this@AddFoodRecommendationActivity,
                        getString(R.string.error_complete_form)
                    )
                }

            }
        }
    }

    private fun setUpAdapter() {
        val instantFoods = resources.getStringArray(R.array.activity_levels)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, instantFoods)

        with(binding) {
            autoCompleteTextView.setText(instantFoods[0])
            autoCompleteTextView.setAdapter(arrayAdapter)
            adapter = FavoriteProcessedAdapter(
                items = favoriteProcessedList,
                context = this@AddFoodRecommendationActivity
            )

            searchView.onActionViewExpanded()
            recyclerView.adapter = adapter
            recyclerView.layoutManager =
                GridLayoutManager(
                    this@AddFoodRecommendationActivity,
                    3,
                    GridLayoutManager.VERTICAL,
                    false
                )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                btnSave.visibility = View.GONE
                shimmerBtnSave.visibility = View.VISIBLE
                ivBack.isClickable = false
            } else {
                shimmerBtnSave.visibility = View.GONE
                btnSave.visibility = View.VISIBLE
                ivBack.isClickable = true
            }
        }
    }

    private fun addMapItems() {
        // fruits
        fruitMap["Apple"] = getString(R.string.Apple)
        fruitMap["Banana"] = getString(R.string.Banana)
        fruitMap["Orange"] = getString(R.string.Orange)
        fruitMap["Mango"] = getString(R.string.Mango)
        fruitMap["Strawberry"] = getString(R.string.Strawberry)
        fruitMap["Pineapple"] = getString(R.string.Pineapple)
        fruitMap["Watermelon"] = getString(R.string.Watermelon)
        fruitMap["Papaya"] = getString(R.string.Papaya)
        fruitMap["Grape"] = getString(R.string.Grape)
        fruitMap["Blueberry"] = getString(R.string.Blueberry)
        fruitMap["Blackberry"] = getString(R.string.Blackberry)
        fruitMap["Raspberry"] = getString(R.string.Raspberry)
        fruitMap["Cherry"] = getString(R.string.Cherry)
        fruitMap["Kiwi"] = getString(R.string.Kiwi)
        fruitMap["Lemon"] = getString(R.string.Lemon)
        fruitMap["Lime"] = getString(R.string.Lime)
        fruitMap["Peach"] = getString(R.string.Peach)
        fruitMap["Pear"] = getString(R.string.Pear)
        fruitMap["Plum"] = getString(R.string.Plum)
        fruitMap["Pomegranate"] = getString(R.string.Pomegranate)
        fruitMap["Avocado"] = getString(R.string.Avocado)
        fruitMap["Coconut"] = getString(R.string.Coconut)
        fruitMap["Guava"] = getString(R.string.Guava)
        fruitMap["Fig"] = getString(R.string.Fig)
        fruitMap["Dates"] = getString(R.string.Dates)
        fruitMap["Lychee"] = getString(R.string.Lychee)
        fruitMap["Dragonfruit"] = getString(R.string.Dragonfruit)
        fruitMap["Persimmon"] = getString(R.string.Persimmon)
        fruitMap["Mulberry"] = getString(R.string.Mulberry)
        fruitMap["Cranberry"] = getString(R.string.Cranberry)
        fruitMap["Cantaloupe"] = getString(R.string.Cantaloupe)
        fruitMap["Honeydew"] = getString(R.string.Honeydew)
        fruitMap["Starfruit"] = getString(R.string.Starfruit)
        fruitMap["Durian"] = getString(R.string.Durian)
        fruitMap["Jackfruit"] = getString(R.string.Jackfruit)
        fruitMap["Passionfruit"] = getString(R.string.Passionfruit)
        fruitMap["Rambutan"] = getString(R.string.Rambutan)
        fruitMap["Longan"] = getString(R.string.Longan)
        fruitMap["Soursop"] = getString(R.string.Soursop)
        fruitMap["Gooseberry"] = getString(R.string.Gooseberry)
        fruitMap["Mangosteen"] = getString(R.string.Mangosteen)
        fruitMap["SugarApple"] = getString(R.string.SugarApple)
        fruitMap["Salak"] = getString(R.string.Salak)
        fruitMap["Elderberry"] = getString(R.string.Elderberry)
        fruitMap["Quince"] = getString(R.string.Quince)
        fruitMap["Tamarind"] = getString(R.string.Tamarind)
        fruitMap["Sapodilla"] = getString(R.string.Sapodilla)
        fruitMap["Bael"] = getString(R.string.Bael)
        fruitMap["Breadfruit"] = getString(R.string.Breadfruit)
        fruitMap["Ackee"] = getString(R.string.Ackee)
        fruitMap["Jujube"] = getString(R.string.Jujube)
        fruitMap["Medlar"] = getString(R.string.Medlar)
        fruitMap["Currant"] = getString(R.string.Currant)
        fruitMap["Acerola"] = getString(R.string.Acerola)
        fruitMap["Loquat"] = getString(R.string.Loquat)
        fruitMap["Boysenberry"] = getString(R.string.Boysenberry)
        fruitMap["UgliFruit"] = getString(R.string.UgliFruit)

        // Vegetables
        vegetableMap["Carrot"] = getString(R.string.Carrot)
        vegetableMap["Potato"] = getString(R.string.Potato)
        vegetableMap["Tomato"] = getString(R.string.Tomato)
        vegetableMap["Onion"] = getString(R.string.Onion)
        vegetableMap["Garlic"] = getString(R.string.Garlic)
        vegetableMap["Spinach"] = getString(R.string.Spinach)
        vegetableMap["Lettuce"] = getString(R.string.Lettuce)
        vegetableMap["Kale"] = getString(R.string.Kale)
        vegetableMap["Broccoli"] = getString(R.string.Broccoli)
        vegetableMap["Cauliflower"] = getString(R.string.Cauliflower)
        vegetableMap["Cucumber"] = getString(R.string.Cucumber)
        vegetableMap["Zucchini"] = getString(R.string.Zucchini)
        vegetableMap["Eggplant"] = getString(R.string.Eggplant)
        vegetableMap["BellPepper"] = getString(R.string.BellPepper)
        vegetableMap["ChiliPepper"] = getString(R.string.ChiliPepper)
        vegetableMap["Beet"] = getString(R.string.Beet)
        vegetableMap["Radish"] = getString(R.string.Radish)
        vegetableMap["Turnip"] = getString(R.string.Turnip)
        vegetableMap["SweetPotato"] = getString(R.string.SweetPotato)
        vegetableMap["Pumpkin"] = getString(R.string.Pumpkin)
        vegetableMap["Squash"] = getString(R.string.Squash)
        vegetableMap["Celery"] = getString(R.string.Celery)
        vegetableMap["Asparagus"] = getString(R.string.Asparagus)
        vegetableMap["GreenBean"] = getString(R.string.GreenBean)
        vegetableMap["Pea"] = getString(R.string.Pea)
        vegetableMap["Okra"] = getString(R.string.Okra)
        vegetableMap["Cabbage"] = getString(R.string.Cabbage)
        vegetableMap["BokChoy"] = getString(R.string.BokChoy)
        vegetableMap["BrusselsSprouts"] = getString(R.string.BrusselsSprouts)
        vegetableMap["Artichoke"] = getString(R.string.Artichoke)
        vegetableMap["Leek"] = getString(R.string.Leek)
        vegetableMap["Fennel"] = getString(R.string.Fennel)
        vegetableMap["Shallot"] = getString(R.string.Shallot)
        vegetableMap["Ginger"] = getString(R.string.Ginger)
        vegetableMap["Turmeric"] = getString(R.string.Turmeric)
        vegetableMap["CollardGreens"] = getString(R.string.CollardGreens)
        vegetableMap["MustardGreens"] = getString(R.string.MustardGreens)
        vegetableMap["SwissChard"] = getString(R.string.SwissChard)
        vegetableMap["Watercress"] = getString(R.string.Watercress)
        vegetableMap["Arugula"] = getString(R.string.Arugula)
        vegetableMap["Taro"] = getString(R.string.Taro)
        vegetableMap["Yam"] = getString(R.string.Yam)
        vegetableMap["BambooShoot"] = getString(R.string.BambooShoot)
        vegetableMap["Corn"] = getString(R.string.Corn)
        vegetableMap["Parsley"] = getString(R.string.Parsley)
        vegetableMap["Cilantro"] = getString(R.string.Cilantro)
        vegetableMap["Basil"] = getString(R.string.Basil)
        vegetableMap["Thyme"] = getString(R.string.Thyme)
        vegetableMap["Rosemary"] = getString(R.string.Rosemary)
        vegetableMap["Oregano"] = getString(R.string.Oregano)
        vegetableMap["Mint"] = getString(R.string.Mint)
        vegetableMap["Chives"] = getString(R.string.Chives)
        vegetableMap["Dill"] = getString(R.string.Dill)
        vegetableMap["CurryLeaf"] = getString(R.string.CurryLeaf)
        vegetableMap["BayLeaf"] = getString(R.string.BayLeaf)

        // Animals
        animalMap["Chicken"] = getString(R.string.Chicken)
        animalMap["Duck"] = getString(R.string.Duck)
        animalMap["Turkey"] = getString(R.string.Turkey)
        animalMap["Goose"] = getString(R.string.Goose)
        animalMap["Quail"] = getString(R.string.Quail)
        animalMap["Cow"] = getString(R.string.Cow)
        animalMap["Pig"] = getString(R.string.Pig)
        animalMap["Sheep"] = getString(R.string.Sheep)
        animalMap["Goat"] = getString(R.string.Goat)
        animalMap["Buffalo"] = getString(R.string.Buffalo)
        animalMap["Camel"] = getString(R.string.Camel)
        animalMap["Yak"] = getString(R.string.Yak)
        animalMap["Horse"] = getString(R.string.Horse)
        animalMap["Deer"] = getString(R.string.Deer)
        animalMap["Kangaroo"] = getString(R.string.Kangaroo)
        animalMap["Ostrich"] = getString(R.string.Ostrich)
        animalMap["Emu"] = getString(R.string.Emu)
        animalMap["Pheasant"] = getString(R.string.Pheasant)
        animalMap["Pigeon"] = getString(R.string.Pigeon)
        animalMap["Crab"] = getString(R.string.Crab)
        animalMap["Lobster"] = getString(R.string.Lobster)
        animalMap["Shrimp"] = getString(R.string.Shrimp)
        animalMap["Fish"] = getString(R.string.Fish)
        animalMap["Tuna"] = getString(R.string.Tuna)
        animalMap["Salmon"] = getString(R.string.Salmon)
        animalMap["Cod"] = getString(R.string.Cod)
        animalMap["Tilapia"] = getString(R.string.Tilapia)
        animalMap["Catfish"] = getString(R.string.Catfish)
        animalMap["Sardine"] = getString(R.string.Sardine)
        animalMap["Anchovy"] = getString(R.string.Anchovy)
        animalMap["Mackerel"] = getString(R.string.Mackerel)
        animalMap["Trout"] = getString(R.string.Trout)
        animalMap["Bass"] = getString(R.string.Bass)
        animalMap["Eel"] = getString(R.string.Eel)
        animalMap["Squid"] = getString(R.string.Squid)
        animalMap["Octopus"] = getString(R.string.Octopus)
        animalMap["Clam"] = getString(R.string.Clam)
        animalMap["Oyster"] = getString(R.string.Oyster)
        animalMap["Mussel"] = getString(R.string.Mussel)
        animalMap["Scallop"] = getString(R.string.Scallop)
        animalMap["SeaUrchin"] = getString(R.string.SeaUrchin)
        animalMap["Cuttlefish"] = getString(R.string.Cuttlefish)
        animalMap["Hare"] = getString(R.string.Hare)
        animalMap["GuineaPig"] = getString(R.string.GuineaPig)
        animalMap["Llama"] = getString(R.string.Llama)
        animalMap["Bison"] = getString(R.string.Bison)
        animalMap["Crocodile"] = getString(R.string.Crocodile)
        animalMap["Alligator"] = getString(R.string.Alligator)
        animalMap["WildBoar"] = getString(R.string.WildBoar)
        animalMap["Grouse"] = getString(R.string.Grouse)
        animalMap["Peafowl"] = getString(R.string.Peafowl)
        animalMap["Capon"] = getString(R.string.Capon)
        animalMap["SilkieChicken"] = getString(R.string.SilkieChicken)
        animalMap["Snake"] = getString(R.string.Snake)
        animalMap["Turtle"] = getString(R.string.Turtle)
        animalMap["Pangolin"] = getString(R.string.Pangolin)
        animalMap["Armadillo"] = getString(R.string.Armadillo)

        favoriteProcessedList =
            fruitMap.values.toList() + vegetableMap.values.toList()
    }
}