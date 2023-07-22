package com.udacity.project4.locationreminders.reminderslist

import com.udacity.project4.R
import com.udacity.project4.base.BaseRecyclerViewAdapter


// Use data binding to show the reminder on the item
class RemindersListAdapter( private val viewModel: RemindersListViewModel,
                            initialDataList: List<ReminderDataItem>,
                            callBack: (selectedReminder: ReminderDataItem) -> Unit) :
    BaseRecyclerViewAdapter<ReminderDataItem>(callBack) {

//    private var _items: MutableList<ReminderDataItem> = mutableListOf()
    private val _items: MutableList<ReminderDataItem> = initialDataList.toMutableList()



    fun deleteItem(position: Int) {
        val item = _items[position]
        viewModel.deleteReminder(item.id)
        _items.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getLayoutRes(viewType: Int) = R.layout.it_reminder
}