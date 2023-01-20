package com.seirios.taskbooking

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.seirios.taskbooking.data.model.BookingItem
import com.seirios.taskbooking.databinding.ActivityMainBinding
import com.seirios.taskbooking.di.BookingApplication
import com.seirios.taskbooking.viewmodel.BookingViewModel
import com.seirios.taskbooking.viewmodel.BookingViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.xml.datatype.DatatypeConstants.MONTHS
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var flag: String = "Clubhouse"
    lateinit var lineralayout_slots: LinearLayout
    val arrDropDown = arrayOf("Clubhouse","Tennis Court");
    private lateinit var tvDropdown: AutoCompleteTextView
    private lateinit var btnSelectdate: MaterialButton
    private lateinit var tvDate: TextView
    private lateinit var radioGroup: RadioGroup
    var clubhouseSlotSelected = ""
    private lateinit var btnStartTime: MaterialButton
    private lateinit var btnEndTime: MaterialButton
    var tennisStartTime = ""
    var tennisEndTime = ""
    private lateinit var tvStartTime: TextView
    private lateinit var tvEndTime: TextView
    private lateinit var btn_bookslot : MaterialButton
    lateinit var bookingViewModel: BookingViewModel
    @Inject
    lateinit var bookingViewModelFactory: BookingViewModelFactory
    private var list = ArrayList<BookingItem>()
    private var listId = ArrayList<Int>()
    private lateinit var ll_bookedItems : LinearLayout

    override fun onResume() {
        super.onResume()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrDropDown)
        binding.tvDropdown.setAdapter(adapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        (application as BookingApplication).applicationComponent.inject(this)
        bookingViewModel = ViewModelProvider(this,bookingViewModelFactory).get(BookingViewModel::class.java)

        //select facility drop down
        tvDropdown = binding.tvDropdown
        ll_bookedItems = binding.llBookedItems

        tvDropdown.setOnItemClickListener { adapterView, view, i, l ->
            val itemFacility = adapterView.getItemAtPosition(i).toString()
            flag = itemFacility
            changeSlotView(itemFacility)
         }

        //calendar date picker
        btnSelectdate = binding.btnSelectdate
        tvDate = binding.tvDate
        btnSelectdate.setOnClickListener {
            val cal = Calendar.getInstance()
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH)
            val d = cal.get(Calendar.DAY_OF_MONTH)

            val datepickerdialog: DatePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                tvDate.text = "" + dayOfMonth + "-" + (monthOfYear+1) + "-" + year
            }, y, m, d)
            datepickerdialog.show()
        }

        //change on drop down select
        lineralayout_slots = binding.lineralayoutSlots
        changeSlotView("Clubhouse")

        //book slot button
        btn_bookslot = binding.btnBookslot
        btn_bookslot.setOnClickListener {
            addBookedSlot()
        }
        getList()
    }

    private fun addBookedSlot() {
        if(flag.equals("Clubhouse")){
            val facilityName = flag
            if(facilityName.length < 1){
                Toast.makeText(this,"Please select Facility",Toast.LENGTH_SHORT).show()
                return
            }
            val date = tvDate.text
            if(date.length < 1 || date.equals("dd-mm-yyyy")){
                Toast.makeText(this,"Please select Date",Toast.LENGTH_SHORT).show()
                return
            }
            val slot = clubhouseSlotSelected
            if(slot.length < 1){
                Toast.makeText(this,"Please select Slot",Toast.LENGTH_SHORT).show()
                return
            }
            val fee = slot.split("/").get(2).toInt()
            val str: String = "FACILITY:"+facilityName+" DATE:"+date+" SLOT:"+slot+" FEE:"+fee
            Log.i("MYTAG", "FACILITY:"+facilityName+" DATE:"+date+" SLOT:"+slot+" FEE:"+fee)
            insertItem(facilityName,date.toString(),slot.split("/").get(0), slot.split("/").get(1), fee)

        }else if(flag.equals("Tennis Court")){

            val facilityName = flag
            if(facilityName.length < 1){
                Toast.makeText(this,"Please select Facility",Toast.LENGTH_SHORT).show()
                return
            }
            val date = tvDate.text
            if(date.length < 1 || date.equals("dd-mm-yyyy")){
                Toast.makeText(this,"Please select Date",Toast.LENGTH_SHORT).show()
                return
            }
            val startT = tennisStartTime
            if(startT.length < 1){
                Toast.makeText(this,"Please select Start Time",Toast.LENGTH_SHORT).show()
                return
            }
            val endT = tennisEndTime
            if(endT.length < 1){
                Toast.makeText(this,"Please select End Time",Toast.LENGTH_SHORT).show()
                return
            }
            var num2 = (endT.substring(0,endT.length-2)).toInt()
            val num1 = (startT.substring(0,startT.length-2)).toInt()
            if(num2 <= num1){
                Toast.makeText(this,"Start Time should less than End Time", Toast.LENGTH_SHORT).show()
                return
            }
            val timeDiff = num2-num1
            val fee = timeDiff*50
            val str: String = "FACILITY:"+facilityName+" DATE:"+date+" StartTime:"+startT+" EndTime:"+endT+
                    " Num2:"+num2+
                    " Num1:"+num1+
                    " TimeDiff:"+timeDiff+
                    " FEE:"+fee
            println("1212 "+str)
            insertItem(facilityName,date.toString(),startT, endT, fee)
        }
    }

    private fun showCustomDialog(msg: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_booking_output)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMsg : TextView = dialog.findViewById(R.id.tv_op_msg)
        val btnOk : Button = dialog.findViewById(R.id.btn_op_ok)
        tvMsg.text = msg
        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun changeSlotView(itemFacility: String) {
        if(itemFacility.equals("Clubhouse")){
            //add Clubhouse slot in linear layout
            lineralayout_slots.removeAllViews()
            val view = layoutInflater.inflate(R.layout.clubhouse_slots, null)
            lineralayout_slots.addView(view)
            radioGroup = view.findViewById(R.id.radioGroup)
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radiobtn_morningslot -> {
                        clubhouseSlotSelected = "10am/4pm/100"
                    }
                    R.id.radiobtn_eveningslot -> {
                        clubhouseSlotSelected = "4pm/10pm/500"
                    }
                }
            }
        }else{
            //add tennis slot in linear layout
            lineralayout_slots.removeAllViews()
            val view = layoutInflater.inflate(R.layout.tennis_slot, null)
            lineralayout_slots.addView(view)
            tvStartTime = view.findViewById(R.id.tv_startTime)
            tvEndTime = view.findViewById(R.id.tv_endTime)
            btnStartTime = view.findViewById(R.id.btn_start_time)
            btnEndTime = view.findViewById(R.id.btn_end_time)
            btnStartTime.setOnClickListener {
//                Toast.makeText(this,"Start Time", Toast.LENGTH_SHORT).show()
                val currentTime = Calendar.getInstance()
                val startHour = currentTime.get(Calendar.HOUR_OF_DAY)
                val startMinute = currentTime.get(Calendar.MINUTE)

                TimePickerDialog(this, TimePickerDialog.OnTimeSetListener{view, hourOfDay, minute ->
                    var slot = "am"
                    if(hourOfDay <= 12){ slot = "am" }
                    else{ slot = "pm"}
                    tennisStartTime = ""+hourOfDay+""+slot
                    tvStartTime.text ="$hourOfDay $slot"
                }, startHour, startMinute, false).show()
            }
            btnEndTime.setOnClickListener {
//                Toast.makeText(this,"End Time", Toast.LENGTH_SHORT).show()
                val currentTime = Calendar.getInstance()
                val startHour = currentTime.get(Calendar.HOUR_OF_DAY)
                val startMinute = currentTime.get(Calendar.MINUTE)

                TimePickerDialog(this, TimePickerDialog.OnTimeSetListener{view, hourOfDay, minute ->
                    var slot = "am"
                    if(hourOfDay <= 12){ slot = "am" }
                    else{ slot = "pm"}
                    tennisEndTime = ""+hourOfDay+""+slot
                    tvEndTime.text ="$hourOfDay $slot"
                }, startHour, startMinute, false).show()
            }
        }
    }

    //CREATE (INSERT)
    private fun insertItem(facilityName: String, date:String, startTime: String, endTime: String, fee:Int) {
        Log.i("MYTAG", "CHECK LIST DATE:"+date+" "+startTime+" "+endTime+" "+fee)
        Log.i("MYTAG", "CHECK LIST:"+list)
        for(item in list){
            if(facilityName.equals("Clubhouse")){
                if(date.equals(item.date)){
                    if(startTime.equals(item.startTime)){
                        showCustomDialog("Booking Failed, Already Booked")
                        return
                    }
                }
            }else if(facilityName.equals(item.facility)){
                if(date.equals(item.date)){
                    if((startTime.substring(0,startTime.length-2)).toInt() >= (item.startTime.substring(0,item.startTime.length-2)).toInt()
                        &&
                        (startTime.substring(0,startTime.length-2)).toInt() <= (item.endTime.substring(0,item.endTime.length-2)).toInt()    ){
                        showCustomDialog("Booking Failed, Already Booked")
                        return
                    }
                }
            }
        }

        val bookingItem = BookingItem(id = null, facility = facilityName, date = date, startTime = startTime, endTime= endTime,fee=fee ) //why id null? because id is auto generate
        CoroutineScope(Dispatchers.Main).launch {
            bookingViewModel.insertBookedItem(bookingItem).also {
                //do action here
                showCustomDialog("Booked, Rs. ${bookingItem.fee}")
                getList()
            }
        }
    }

    //READ
    private fun getList() {
        CoroutineScope(Dispatchers.Main).launch {
            bookingViewModel.getBookingItems().observe(this@MainActivity, { bookedItem ->

                for(item in bookedItem){
                    if (!listId.contains(item.id)){
                        item.id?.let { listId.add(it) }
                        list.add(item)

                        val view = layoutInflater.inflate(R.layout.booked_card, null)
                        view.findViewById<TextView>(R.id.tv_fn).text = item.facility
                        view.findViewById<TextView>(R.id.tv_dt).text = item.date
                        view.findViewById<TextView>(R.id.tv_st).text = item.startTime
                        view.findViewById<TextView>(R.id.tv_et).text = item.endTime
                        view.findViewById<TextView>(R.id.tv_fee).text = "Rs. "+item.fee.toString()

                        binding.llBookedItems.addView(view, binding.llBookedItems.childCount)
                    }
                }
            })
        }
    }
}













