package com.udacity.project4.locationreminders.reminderslist

import com.udacity.project4.R
import com.udacity.project4.base.BaseRecyclerViewAdapter
import com.udacity.project4.locationreminders.data.dto.ReminderDTO


// Use data binding to show the reminder on the item
class RemindersListAdapter(private val viewModel: RemindersListViewModel,
                            callBack: (selectedReminder: ReminderDataItem) -> Unit) :
    BaseRecyclerViewAdapter<ReminderDataItem>(callBack) {

    override fun getItem(position: Int): ReminderDataItem {
        val remindersList = viewModel.remindersList.value ?: emptyList()
        return remindersList[position]
    }
    fun deleteItem(position: Int) {
        val item = getItem(position)
        viewModel.deleteReminder(item.id)
        viewModel.deleteReminders(item)
        notifyItemRemoved(position)
    }

    override fun getLayoutRes(viewType: Int) = R.layout.it_reminder
}