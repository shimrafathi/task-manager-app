package com.example.tasknoteapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasknoteapp.data.TaskDatabase
import com.example.tasknoteapp.data.TaskRepository
import com.example.tasknoteapp.databinding.ActivityMainBinding
import com.example.tasknoteapp.ui.adapter.TaskAdapter
import com.example.tasknoteapp.viewmodel.TaskViewModel
import com.example.tasknoteapp.viewmodel.TaskViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val taskDao = TaskDatabase.getDatabase(this).taskDao()
        val repository = TaskRepository(taskDao)
        taskViewModel = ViewModelProvider(this, TaskViewModelFactory(repository))
            .get(TaskViewModel::class.java)

        setupRecyclerView()
        setupClickListeners()
        observeTasks()
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            emptyList(),
            onTaskClick = { task ->
                // Open Edit Activity
                val intent = Intent(this, AddEditTaskActivity::class.java)
                intent.putExtra("TASK_ID", task.id)
                startActivity(intent)
            },
            onDeleteClick = { task ->
                taskViewModel.deleteTask(task)
            },
            onCheckboxClick = { task, isChecked ->
                val updatedTask = task.copy(isCompleted = isChecked, updatedAt = System.currentTimeMillis())
                taskViewModel.updateTask(updatedTask)
            }
        )

        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeTasks() {
        taskViewModel.allTasks.observe(this) { tasks ->
            adapter = TaskAdapter(
                tasks,
                onTaskClick = { task ->
                    val intent = Intent(this, AddEditTaskActivity::class.java)
                    intent.putExtra("TASK_ID", task.id)
                    startActivity(intent)
                },
                onDeleteClick = { task ->
                    taskViewModel.deleteTask(task)
                },
                onCheckboxClick = { task, isChecked ->
                    val updatedTask = task.copy(isCompleted = isChecked, updatedAt = System.currentTimeMillis())
                    taskViewModel.updateTask(updatedTask)
                }
            )
            binding.rvTasks.adapter = adapter
        }
    }
}