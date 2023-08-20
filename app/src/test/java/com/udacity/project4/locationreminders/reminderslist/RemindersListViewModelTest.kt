package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    //TODO: provide testing to the RemindersListViewModel and its live data objects

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersListViewModel: RemindersListViewModel

    private lateinit var reminderRepository : FakeDataSource

    @Before
    fun setUpViewModel() {

        reminderRepository = FakeDataSource()

        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            reminderRepository
        )

    }

    @Test
    fun loadRemindersWhenUnavailable_causesError(){
        //Given a fresh ViewModel


    }


}