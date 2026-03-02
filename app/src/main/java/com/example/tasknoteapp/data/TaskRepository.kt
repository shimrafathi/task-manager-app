package com.example.tasknoteapp.data

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {
    fun getAllTasks(): LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun getTaskById(id: Int): Task? = taskDao.getTaskById(id)
}