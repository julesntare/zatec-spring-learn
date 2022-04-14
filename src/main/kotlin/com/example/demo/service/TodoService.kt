package com.example.demo.service

import com.example.demo.persistence.TodoEntity

interface TodoService {
    fun createOrUpdate(todo: TodoEntity)
    fun removeById(id: Int)
    fun removeAllCompleted()
    fun getAll(): List<TodoEntity>
    fun getById(id: Int): TodoEntity
    fun getByCompleted(completed: Boolean): List<TodoEntity>
}