package com.udacity.project4.locationreminders.data

import androidx.lifecycle.MutableLiveData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source

    private var remindersServiceData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()

    private var shouldReturnError: Boolean = false

    fun setReturnError(value: Boolean){
        shouldReturnError = value
    }
    override suspend fun getAllReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Test Exception")
        }
        return Result.Success(remindersServiceData.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersServiceData.values.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
       if (shouldReturnError) {
           return Result.Error("Test Exception")
       }
        remindersServiceData[id]?.let {
            return Result.Success(it)
        }
        return Result.Error("Could not find Reminder")
    }

    override suspend fun deleteReminder(id: String) {
        remindersServiceData.values.remove(remindersServiceData[id])
    }

    override suspend fun deleteAllReminders() {
        remindersServiceData.clear()
    }


}