package de.kontranik.freebudget.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.CategoryViewModel
import de.kontranik.freebudget.databinding.ActivityCategoryListBinding
import de.kontranik.freebudget.model.Category

class CategoryListActivity : AppCompatActivity() {
    private var category: Category? = null
    private lateinit var binding: ActivityCategoryListBinding

    private var categoryList: MutableList<Category> = mutableListOf()
    private lateinit var categoryArrayAdapter: ArrayAdapter<Category>

    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle(R.string.activity_category)

        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        binding.btnCategorySave.setOnClickListener { onSave() }
        binding.btnCategoryClose.setOnClickListener { v -> onClose() }
        binding.btnCategoryDelete.setOnClickListener { v -> onDelete() }

        categoryArrayAdapter = ArrayAdapter<Category>(
            this,
            android.R.layout.simple_list_item_1,
            categoryList
        )
        // устанавливаем для списка адаптер
        binding.listViewCategoryList.adapter = categoryArrayAdapter
        binding.listViewCategoryList.onItemClickListener =
            OnItemClickListener { parent, v, position, id -> // по позиции получаем выбранный элемент
                val selectedItem = categoryList[position].name
                // установка текста элемента TextView
                binding.editTextCategoryName.setText(selectedItem)
                categoryViewModel.loadCategoryByName(selectedItem)
            }
        categoryViewModel.categoryByName.observe(this, androidx.lifecycle.Observer {
            category = it
        })
        categoryViewModel.mAllCategorys.observe(this) {
            categoryList.clear()
            categoryList.addAll(it)
            categoryArrayAdapter.notifyDataSetChanged()
        }
    }


    private fun onSave() {
        if (category == null) {
            category = Category(0, binding.editTextCategoryName.text.toString())
        } else {
            category!!.name = binding.editTextCategoryName.text.toString()
        }
        if (category!!.id != null)
            categoryViewModel.update(category!!)
        else
            categoryViewModel.insert(category!!)
        category = null
        binding.editTextCategoryName.setText("")
    }

    private fun onDelete() {
        if (category?.id != null) {
            category?.id?.let { categoryViewModel.delete(it) }
            category = null
        }
        binding.editTextCategoryName.setText("")
    }

    private fun onClose() {
        val returnIntent = Intent()
        if (category != null) {
            returnIntent.putExtra(RESULT_CATEGORY, category!!.name)
        }
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    companion object {
        const val RESULT_CATEGORY = "RESULT_MONTH"
    }
}