package de.kontranik.freebudget.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.DatabaseAdapter
import de.kontranik.freebudget.databinding.ActivityCategoryListBinding
import de.kontranik.freebudget.model.Category

class CategoryListActivity : AppCompatActivity() {
    private var category: Category? = null
    private lateinit var binding: ActivityCategoryListBinding

    private var dbAdapter: DatabaseAdapter? = null
    private var categoryList: MutableList<Category> = mutableListOf()
    private lateinit var categoryArrayAdapter: ArrayAdapter<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle(R.string.activity_category)

        binding.btnCategorySave.setOnClickListener { v -> onSave(v) }
        binding.btnCategoryClose.setOnClickListener { v -> onClose(v) }
        binding.btnCategoryDelete.setOnClickListener { v -> onDelete(v) }
        dbAdapter = DatabaseAdapter(this)
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
                dbAdapter!!.open()
                category = dbAdapter!!.getCategory(selectedItem)
                dbAdapter!!.close()
            }
    }

    override fun onResume() {
        super.onResume()
        getCategory()
    }

    private fun getCategory() {
        dbAdapter!!.open()
        categoryList.clear()
        categoryList.addAll(dbAdapter!!.allCategory)
        dbAdapter!!.close()
        categoryArrayAdapter.notifyDataSetChanged()
    }

    private fun onSave(view: View?) {
        dbAdapter!!.open()
        if (category == null) {
            category = Category(0, binding.editTextCategoryName.text.toString())
        } else {
            category!!.name = binding.editTextCategoryName.text.toString()
        }
        if (category!!.id > 0) dbAdapter!!.update(category!!) else dbAdapter!!.insert(category!!)
        dbAdapter!!.close()
        category = null
        binding.editTextCategoryName.setText("")
        getCategory()
    }

    private fun onDelete(view: View?) {
        if (category != null) {
            dbAdapter!!.open()
            if (dbAdapter!!.deleteCategory(category!!.id) > 0) {
                binding.editTextCategoryName.setText("")
                category = null
            }
            dbAdapter!!.close()
            getCategory()
        } else {
            binding.editTextCategoryName.setText("")
        }
    }

    private fun onClose(view: View?) {
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