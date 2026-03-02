package com.example.tasknoteapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tasknoteapp.data.Task
import com.example.tasknoteapp.data.TaskDatabase
import com.example.tasknoteapp.data.TaskRepository
import com.example.tasknoteapp.databinding.ActivityAddEditTaskBinding
import kotlinx.coroutines.launch

class AddEditTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditTaskBinding
    private lateinit var repository: TaskRepository
    private var taskId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize repository
        val taskDao = TaskDatabase.getDatabase(this).taskDao()
        repository = TaskRepository(taskDao)

        // Get task ID if editing
        taskId = intent.getIntExtra("TASK_ID", -1).takeIf { it != -1 }

        setupUI()
        setupClickListeners()
        loadTaskIfEditing()
    }

    private fun setupUI() {
        if (taskId != null) {
            binding.tvScreenTitle.text = "Edit Task"
        } else {
            binding.tvScreenTitle.text = "Add New Task"
        }
    }

    private fun loadTaskIfEditing() {
        taskId?.let { id ->
            lifecycleScope.launch {
                val task = repository.getTaskById(id)
                task?.let {
                    binding.etTitle.setText(it.title)
                    binding.etDescription.setText(it.description)
                    binding.cbCompleted.isChecked = it.isCompleted
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveTask()
        }
    }

    private fun saveTask() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val isCompleted = binding.cbCompleted.isChecked

        // Input validation (Secure Coding Practice 1)
        if (title.isEmpty()) {
            binding.etTitle.error = "Title is required"
            return
        }

        lifecycleScope.launch {
            if (taskId != null) {
                // Update existing task
                val existingTask = repository.getTaskById(taskId!!)
                existingTask?.let {
                    val updatedTask = it.copy(
                        title = title,
                        description = description,
                        isCompleted = isCompleted,
                        updatedAt = System.currentTimeMillis()
                    )
                    repository.updateTask(updatedTask)
                }
            } else {
                // Create new task
                val newTask = Task(
                    title = title,
                    description = description,
                    isCompleted = isCompleted
                )
                repository.insertTask(newTask)
            }
            finish()
        }
    }

    // Handle screen rotation - ViewModel would be better but for simplicity
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("TITLE", binding.etTitle.text.toString())
        outState.putString("DESCRIPTION", binding.etDescription.text.toString())
        outState.putBoolean("COMPLETED", binding.cbCompleted.isChecked)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.etTitle.setText(savedInstanceState.getString("TITLE", ""))
        binding.etDescription.setText(savedInstanceState.getString("DESCRIPTION", ""))
        binding.cbCompleted.isChecked = savedInstanceState.getBoolean("COMPLETED", false)
    }
}