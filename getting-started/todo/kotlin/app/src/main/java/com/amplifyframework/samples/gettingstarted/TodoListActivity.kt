package com.amplifyframework.samples.gettingstarted

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.amplifyframework.ResetPasswordActivity
import com.amplifyframework.SignInActivity
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Priority
import com.amplifyframework.datastore.generated.model.Todo
import com.amplifyframework.samples.core.ActivityNavigationUtil
import com.amplifyframework.samples.core.Constants
import com.amplifyframework.samples.core.ListActivity

class TodoListActivity : ListActivity(), TodoItemAdapter.OnItemClickListener {
    private val itemAdapter = TodoItemAdapter(this, this)
    private var hideStatus: Boolean = true // Whether we want to show or hide completed tasks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // testAuth()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        recyclerView.adapter = itemAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Swipe to delete feature
        val swipeHandler = object : SwipeToDelete(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as TodoItemAdapter
                adapter.deleteModel(viewHolder.adapterPosition)
            }
        }

        // Pull to refresh feature
        swipeRefresh.setOnRefreshListener {
            itemAdapter.query(hideStatus)
            swipeRefresh.isRefreshing = false
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // Observe changes bi-directional
        itemAdapter.observe()
    }

    // Call query on start to load from backend
    override fun onStart() {
        super.onStart()
        itemAdapter.query(hideStatus)
    }

    // Inflates the options menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.todo_menu, menu)
        return true
    }

    // Click listener for menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_hide -> {
                if (item.title == getString(R.string.show_tasks)) {
                    hideStatus = false
                    itemAdapter.showCompletedTasks()
                    item.title = getString(R.string.hide_tasks)
                } else {
                    hideStatus = true
                    itemAdapter.hideCompletedTasks()
                    item.title = getString(R.string.show_tasks)
                }
                true
            }
            R.id.created -> {
                itemAdapter.sortDateCreated(hideStatus)
                true
            }
            R.id.priority_asc -> {
                itemAdapter.sortPriority(hideStatus, TodoItemAdapter.SortOrder.ASCENDING)
                true
            }
            R.id.priority_des -> {
                itemAdapter.sortPriority(hideStatus, TodoItemAdapter.SortOrder.DESCENDING)
                true
            }
            R.id.name_asc -> {
                itemAdapter.sortName(hideStatus, TodoItemAdapter.SortOrder.ASCENDING)
                true
            }
            R.id.name_des -> {
                itemAdapter.sortName(hideStatus, TodoItemAdapter.SortOrder.DESCENDING)
                true
            }
            R.id.change_password -> {
                launchChangePassword()
                true
            }
            R.id.signout -> {
                Amplify.Auth.signOut(
                    {
                        showToastMessage("Signed out")
                        ActivityNavigationUtil.navigateToActivity(
                            this,
                            SignInActivity::class.java,
                            ActivityNavigationUtil.ActivityFinishMode.FINISH_ALL
                        )
                    },
                    { e ->
                        showToastMessage(e.message ?: e.toString())
                        Log.w(TAG, "Sign out unsuccessful", e)
                    }
                )
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun launchChangePassword() {
        Log.d(TAG, "launchChangePassword::")
        ActivityNavigationUtil.navigateToActivity(
            this,
            ResetPasswordActivity::class.java,
            Bundle().apply {
                putBoolean(Constants.EXTRA_IS_NEED_CODE, false)
            },
            ActivityNavigationUtil.ActivityFinishMode.KEEP_ACTIVITY
        )
    }

    override fun fabAction() {
        val optionsInstance =
            OptionsBarFragment.newInstance(itemAdapter, true, -1, "", Priority.LOW)
        optionsInstance.show(supportFragmentManager, "TAG")
    }

    // If hideStatus is true, when checkBox is clicked, remove item from list and mark it complete/incomplete
    // Else, simply get the item and mark it complete/incomplete
    override fun onCheckClick(position: Int, isChecked: Boolean) {
        val todo: Todo = if (hideStatus) {
            itemAdapter.removeItemFromList(position)
        } else {
            itemAdapter.getItem(position)
        }
        if (isChecked) {
            itemAdapter.markComplete(todo)
        } else {
            itemAdapter.markIncomplete(position, todo)
        }
    }

    override fun onTextClick(position: Int, data: Todo?) {
        data?.let {
            val optionsInstance =
                OptionsBarFragment.newInstance(itemAdapter, false, position, it.name, it.priority)
            optionsInstance.show(supportFragmentManager, "TAG")
        } ?: Log.w(TAG, "todo item unexpectedly null!")
    }

    companion object {
        private const val TAG = "TodoListActivity"
    }
}
