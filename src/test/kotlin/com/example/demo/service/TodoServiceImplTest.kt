package com.example.demo.service

import com.example.demo.common.TodoNotFoundException
import com.example.demo.persistence.TodoEntity
import com.example.demo.persistence.TodoRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class TodoServiceImplTest(
    @Autowired private val todoService: TodoService
) {
    @MockkBean
    private lateinit var todoRepository: TodoRepository

    private fun aTodo(
        task: String = "a sample task",
        completed: Boolean = false
    ) = TodoEntity(
        task = task,
        completed = completed
    )

    @Test
    fun `will create or update`() {
        val aTestTodo = aTodo()
        every { todoRepository.save(aTestTodo) } returns aTestTodo

        todoService.createOrUpdate(aTestTodo)
        verify { todoRepository.save(aTestTodo) }
    }

    @Test
    fun `will get a todo by id`() {
        val aTestTodo = aTodo()
        every { todoRepository.findByIdOrNull(71) } returns aTestTodo

        val result = todoService.getById(71)
        assertThat(result).isEqualTo(aTestTodo)
    }

    @Test
    fun `will throw an error on id not exists`() {
        aTodo()
        every { todoRepository.findByIdOrNull(71) } returns null

        Assertions.assertThrows(TodoNotFoundException::class.java) {
            todoService.getById(71)
        }
    }

    @Test
    fun `can find all completed`() {
        val completedTodo = aTodo(completed = true)
        every { todoRepository.findAllByCompleted(true) } returns listOf(completedTodo)

        val result = todoService.getByCompleted(true)
        assertThat(result).containsExactly(completedTodo)
    }

    @Test
    fun `can remove all completed`() {
        justRun { todoRepository.deleteAllByCompleted(any()) }

        todoService.removeAllCompleted()
        verify { todoRepository.deleteAllByCompleted(true) }
    }

}