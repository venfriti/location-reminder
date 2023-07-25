package com.udacity.project4.locationreminders.reminderslist

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentRemindersBinding
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.utils.SwipeToDeleteCallback
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.setTitle
import com.udacity.project4.utils.setup
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : BaseFragment() {

    // Use Koin to retrieve the ViewModel instance
    override val _viewModel: RemindersListViewModel by viewModel()
    private lateinit var binding: FragmentRemindersBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_reminders, container, false
        )
        binding.viewModel = _viewModel
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        binding.refreshLayout.setOnRefreshListener { _viewModel.loadReminders() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()

        binding.addReminderFAB.setOnClickListener {
            navigateToAddReminder()
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.logout -> {
                        signOutButtonClicked()
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    override fun onResume() {
        super.onResume()
        // Load the reminders list on the ui
        _viewModel.loadReminders()
    }

    private fun navigateToAddReminder() {
        // Use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(ReminderListFragmentDirections.toSaveReminder())
        )
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter(_viewModel) {}
        // Setup the recycler view using the extension function
        binding.reminderssRecyclerView.setup(adapter)

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.reminderssRecyclerView)
    }

    private fun signOutButtonClicked() {
        AuthUI.getInstance().signOut(requireContext())
        val intent = Intent(requireContext(), AuthenticationActivity::class.java)
        startActivity(intent)
    }
}