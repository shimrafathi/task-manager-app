package com.example.tasknoteapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasknoteapp.data.Task
import com.example.tasknoteapp.databinding.ItemTaskBinding

class TaskAdapter(
    private val tasks: List<Task>,
    private val onTaskClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit,
    private val onCheckboxClick: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.binding.tvTaskTitle.text = task.title
        holder.binding.tvTaskDesc.text = task.description
        holder.binding.cbTaskCompleted.isChecked = task.isCompleted

        // Task click for editing
        holder.binding.root.setOnClickListener {
            onTaskClick(task)
        }

        // Delete button click
        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(task)
        }

        // Checkbox status change
        holder.binding.cbTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
            onCheckboxClick(task, isChecked)
        }
    }

    override fun getItemCount() = tasks.size
}