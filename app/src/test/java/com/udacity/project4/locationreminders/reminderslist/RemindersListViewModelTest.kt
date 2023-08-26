package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.nullValue
import com.udacity.project4.locationreminders.getOrAwaitValue
import org.hamcrest.core.IsNot.not
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

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
        stopKoin()

        reminderRepository = FakeDataSource()

        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            reminderRepository
        )

    }

    @Test
    fun loadRemindersWhenUnavailable_causesError(){
        //Given a fresh ViewModel

        reminderRepository.setReturnError(true)

        // WHEN - we want to load rhe reminders
        remindersListViewModel.loadReminders()

        // THEN - It's an error, there's a snackbar
        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue(), not(nullValue()))


    }


}