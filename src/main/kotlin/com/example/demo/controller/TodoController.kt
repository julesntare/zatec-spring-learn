package com.example.demo.controller

import com.example.demo.persistence.TodoEntity
import com.example.demo.service.TodoService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/todo")
class TodoController(
    private val todoService: TodoService
) {
    @GetMapping("/all")
    fun getAll() =
        todoService.getAll().toResponse()

    @GetMapping("/incomplete")
    fun getIncompleteTasks() =
        todoService.getByCompleted(false).toResponse()

    @GetMapping("/completed")
    fun getCompletedTasks() =
        todoService.getByCompleted(true).toResponse()

    @DeleteMapping("/{id}")
    fun deleteTodo(
        @PathVariable id: Int
    ) {
        todoService.removeById(id)
    }

    @DeleteMapping("/completed")
    fun deleteAllCompleted(
    ) {
        todoService.removeAllCompleted()
    }

    @PutMapping
    fun putTodo(
        @RequestBody todoRequest: TodoRequest
    ) {
        todoService.createOrUpdate(todoRequest.toEntity())
    }
}


data class TodoRequest(
    val task: String,
    val completed: Boolean = false
) {
    fun toEntity() = TodoEntity(
        task = task,
        completed = completed
    )
}

data class TodoResponse(
    val id: Int,
    val task: String,
    val completed: Boolean = false
)

fun TodoEntity.toResponse() = TodoResponse(
    id, task, completed
)

fun List<TodoEntity>.toResponse() = map { it.toResponse() }