package bbox.com.todoapp_kotlin_mvp.mvp.view

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import bbox.com.todoapp_kotlin_mvp.R
import bbox.com.todoapp_kotlin_mvp.models.Todo
import bbox.com.todoapp_kotlin_mvp.mvp.DetailMvp
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import java.util.*
import javax.inject.Inject

class DetailActivity : DaggerAppCompatActivity(), DetailMvp.View, View.OnClickListener {

    @set:Inject
    var presenter: DetailMvp.Presenter? = null

    //region.. declare variables
    lateinit var rootView: ViewGroup
    lateinit var edTxtName: EditText
    lateinit var edTxtDesc:EditText
    lateinit var txtExpireDate: TextView
    lateinit var txtVwId:TextView

    lateinit var rdBtnTodo: RadioButton
    lateinit var rdBtnCompleted:RadioButton
    lateinit var btnSave: Button

    lateinit var rdGroupStatus: RadioGroup


     var isEditModeOn :Boolean = false
     var isMenuEnabledOn :Boolean = false
     var strIntentMode:String ?= null

     private var mYear: Int = 0
    private var mMonth:Int = 0
    private var mDay:Int = 0


    enum class IntentMode
    {
        TOADD, TOVIEW
    }

//endregion



    override fun updateData(todo: Todo) {
        if (todo != null) {
            edTxtName.setText(todo.name)
            txtExpireDate.setText(todo.expiry_date)

            if (todo.status.toLowerCase().contains("completed"))
                rdBtnCompleted.isChecked = true
            else
                rdBtnTodo.isChecked = true


            edTxtDesc.setText("" + todo.description)
            txtVwId.text = "" + todo.id

            disbleEditing()
        }
    }

    override fun showSnackbar(msg: String) {
        Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun setViewMode(str: String) {
        strIntentMode = str
    }

    override fun setSaveButtonVisibility(viewMode: Int) {
        btnSave.visibility = viewMode
    }

    override fun setMenuEnabled(boln: Boolean) {
        isMenuEnabledOn = boln
        invalidateOptionsMenu()
    }

    override fun navigateToMain() {
        NavUtils.navigateUpFromSameTask(this@DetailActivity)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initialize()

        getIntentFromMain()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        presenter?.setView(this)
    }

    private fun getIntentFromMain() {
        if (intent.hasExtra("intent_Main_mode")) {
            strIntentMode = intent.getStringExtra("intent_Main_mode")

            isEditModeOn = intent.getBooleanExtra("intent_Main_setEditModeEnabled", false)
            isMenuEnabledOn = intent.getBooleanExtra("intent_Main_setMenuEnabled", false)

            if (strIntentMode == IntentMode.TOVIEW.name) {
                val intentTodo = intent.getParcelableExtra<Parcelable>("intent_Main_obj") as Todo

                edTxtName.setText(intentTodo.name)
                txtExpireDate.setText(intentTodo.expiry_date)
                edTxtDesc.setText(intentTodo.description)
                txtVwId.setText(intentTodo.id.toString())

                if (intentTodo.status.toLowerCase().contains("completed"))
                    rdBtnCompleted.isChecked = true
                else
                    rdBtnTodo.isChecked = true
            }

            if (isEditModeOn) {
                enableEditing()
                btnSave.visibility = View.VISIBLE
            } else
                btnSave.visibility = View.GONE


        }
    }


    fun clkSave(view: View) {

        val radioButtonId_date = rdGroupStatus.checkedRadioButtonId
        val mRdBtn_Sts = findViewById<View>(radioButtonId_date) as RadioButton

        val objToDo = Todo(
            0,
            edTxtName.text.toString(),
            mRdBtn_Sts.text.toString(),
            edTxtDesc.text.toString(),
            txtExpireDate.text.toString()
        )

        presenter?.saveTodo(Integer.valueOf(txtVwId.text.toString()), objToDo, strIntentMode)
    }


    //------ for calender
    override  fun onClick(v: View) {
        if (v.id == R.id.txtVw_Detail_ExpDate) {
            val c = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

            var datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    txtExpireDate.text =
                        year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.detail_menu, menu)

        if (isMenuEnabledOn == false) {
            for (i in 0 until menu.size()) {
                menu.getItem(i).isVisible = false
            }
        } else {
            for (i in 0 until menu.size()) {
                menu.getItem(i).isVisible = true
            }
        }
        return true
    }

     override fun onSaveInstanceState(savedInstanceState: Bundle) {

        savedInstanceState.putBoolean("blnIsEditModeOn", isEditModeOn)
        savedInstanceState.putBoolean("blnIsMenuEnabled", isMenuEnabledOn)

        super.onSaveInstanceState(savedInstanceState)
    }

     override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        super.onRestoreInstanceState(savedInstanceState)

        isEditModeOn = savedInstanceState.getBoolean("blnIsEditModeOn")
         isMenuEnabledOn = savedInstanceState.getBoolean("blnIsMenuEnabled")

    }

    override fun onStop() {
        super.onStop()
        presenter?.rxUnsubscribe()
    }

    //========================================================

    private fun initialize() {

        rootView = findViewById(R.id.detailRootView) as ViewGroup
        edTxtName = findViewById(R.id.edTxtVw_Detail_Name) as EditText
        txtExpireDate = findViewById(R.id.txtVw_Detail_ExpDate) as TextView

        rdGroupStatus = findViewById(R.id.radioGpStatus) as RadioGroup
        rdBtnTodo = findViewById(R.id.radBtnTodo) as RadioButton
        rdBtnCompleted = findViewById(R.id.radBtnCompleted) as RadioButton

        edTxtDesc = findViewById(R.id.edTxtVw_Detail_Desc) as EditText

        txtVwId = findViewById(R.id.txtVw_Desc_Id) as TextView

        btnSave = findViewById(R.id.btn_Detail_Save) as Button

        txtExpireDate.setOnClickListener(this)

        disbleEditing()


    }


    private fun disbleEditing() {
        disableEditText(edTxtName)
        disableEditText(edTxtDesc)
        rdBtnCompleted.setEnabled(false)
        rdBtnTodo.setEnabled(false)
        txtExpireDate.setEnabled(false)
        txtExpireDate.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun enableEditing() {
        isEditModeOn = true
        enableEditText(edTxtName)
        enableEditText(edTxtDesc)
        rdBtnCompleted.setEnabled(true)
        rdBtnTodo.setEnabled(true)

        edTxtName.requestFocus()
        txtExpireDate.setEnabled(true)
        txtExpireDate.setBackgroundColor(ContextCompat.getColor(this, R.color.colorListItem))
    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun enableEditText(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.isEnabled = true
        editText.isCursorVisible = true
        editText.setBackgroundColor(ContextCompat.getColor(this, R.color.colorListItem))
    }


}
