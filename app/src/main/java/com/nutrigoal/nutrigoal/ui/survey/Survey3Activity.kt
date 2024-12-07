package com.nutrigoal.nutrigoal.ui.survey

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.data.remote.entity.DietCategory
import com.nutrigoal.nutrigoal.data.remote.entity.UserEntity
import com.nutrigoal.nutrigoal.databinding.ActivitySurvey3Binding
import com.nutrigoal.nutrigoal.ui.MainActivity
import com.nutrigoal.nutrigoal.ui.auth.LoginActivity.Companion.EXTRA_SURVEY
import com.nutrigoal.nutrigoal.utils.AnimationUtil
import com.nutrigoal.nutrigoal.utils.ToastUtil


class Survey3Activity : AppCompatActivity() {
    private lateinit var binding: ActivitySurvey3Binding
    private val fruitMap: MutableMap<String, String> = mutableMapOf()
    private val vegetableMap: MutableMap<String, String> = mutableMapOf()
    private val animalMap: MutableMap<String, String> = mutableMapOf()
    private lateinit var favoriteProcessedList: List<String>
    private lateinit var adapter: FavoriteProcessedAdapter
    private var userEntity: UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurvey3Binding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playAnimation()
        setUpView()
        setUpAction()
    }

    private fun setUpView() {
        addMapItems()
        setUpAdapter()
    }

    private fun setUpAction() {
        with(binding) {
            btnBack.setOnClickListener {
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
                EXTRA_SURVEY,
                UserEntity::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_SURVEY)
        }

        with(binding) {
            btnNext.setOnClickListener {
                val selectedActivityLevel = autoCompleteTextView.text.toString()

                val instantFoods = resources.getStringArray(R.array.activity_levels)

                userEntity?.activityLevel = when (selectedActivityLevel) {
                    instantFoods[0] -> 1
                    instantFoods[1] -> 2
                    instantFoods[2] -> 3
                    instantFoods[3] -> 4
                    instantFoods[4] -> 5
                    else -> null
                }

                val selectedDietCategoryId = rgDietCategory.checkedRadioButtonId

                userEntity?.dietCategory = when (selectedDietCategoryId) {
                    R.id.rb_vegan -> {
                        adapter.updateItems(
                            favoriteProcessedList.filterNot { animalMap.containsKey(it) }
                        )
                        DietCategory.VEGAN.toString()
                    }

                    R.id.rb_keto -> {
                        val allItems = mutableListOf<String>().apply {
                            addAll(fruitMap.keys)
                            addAll(vegetableMap.keys)
                            addAll(animalMap.keys)
                        }
                        adapter.updateItems(allItems)
                        DietCategory.KETO.toString()
                    }

                    else -> null
                }

                val selectedHistoryMagId = rgHistoryMag.checkedRadioButtonId

                userEntity?.hasGastricIssue = when (selectedHistoryMagId) {
                    R.id.rb_yes -> true
                    R.id.rb_no -> false
                    else -> null
                }

                val selectedFoodPreferences = adapter.getCheckedItems()

                userEntity?.foodPreference = selectedFoodPreferences

                Log.d("FLORAAAAAA", "handleGetSurveyResult: ${userEntity}")
                if (selectedFoodPreferences.isNotEmpty()) {
                    val intent = Intent(this@Survey3Activity, MainActivity::class.java)
                    intent.putExtra(EXTRA_SURVEY, userEntity)
                    startActivity(intent)
                } else {
                    ToastUtil.showToast(
                        this@Survey3Activity,
                        getString(R.string.error_favorite_food)
                    )
                }

            }
        }
    }

    private fun playAnimation() {
        with(binding) {
            val durationDefault = 1000L

            val animators = listOf(
                AnimationUtil.createTranslationAnimator(tvStep3),
                AnimationUtil.createTranslationAnimator(tvTitle, durationDefault + 100),
                AnimationUtil.createTranslationAnimator(tvDesc, durationDefault + 150),
                AnimationUtil.createTranslationAnimator(tvHistoryMag, durationDefault + 200),
                AnimationUtil.createTranslationAnimator(rgHistoryMag, durationDefault + 250),
                AnimationUtil.createTranslationAnimator(tvDietCategory, durationDefault + 300),
                AnimationUtil.createTranslationAnimator(rgDietCategory, durationDefault + 350),
                AnimationUtil.createTranslationAnimator(
                    tvFavoriteProcessedFood,
                    durationDefault + 400
                ),
            )

            val together = AnimatorSet().apply {
                playTogether(animators)
            }

            AnimatorSet().apply {
                playSequentially(together)
                start()
            }
        }
    }

    private fun setUpAdapter() {
        val instantFoods = resources.getStringArray(R.array.activity_levels)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, instantFoods)

        with(binding) {
            autoCompleteTextView.setText(instantFoods[0])
            autoCompleteTextView.setAdapter(arrayAdapter)
            adapter = FavoriteProcessedAdapter(items = favoriteProcessedList)

            searchView.onActionViewExpanded()
            recyclerView.adapter = adapter
            recyclerView.layoutManager =
                GridLayoutManager(this@Survey3Activity, 3, GridLayoutManager.VERTICAL, false)
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
            fruitMap.values.toList() + vegetableMap.values.toList() + animalMap.values.toList()
    }

}