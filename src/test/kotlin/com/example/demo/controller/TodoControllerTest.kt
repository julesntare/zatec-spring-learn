package com.example.demo.controller

import com.example.demo.persistence.TodoEntity
import com.example.demo.persistence.TodoRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*

@AutoConfigureMockMvc
@SpringBootTest
class TodoControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val todoRepository: TodoRepository
) {

    private fun aTodo(
        task: String = "a sample task",
        completed: Boolean = false
    ) = TodoEntity(
        task = task,
        completed = completed
    )

    private val testData = listOf(
        aTodo(task = "cleaning up", completed = true),
        aTodo(task = "cooking a meal"),
        aTodo(task = "coding spring", completed = false),
    )

    @BeforeEach
    fun clearDb() {
        todoRepository.deleteAll()
    }

    @Test
    fun `can get a todo by id`() {
        val persistedTestData = todoRepository.saveAll(testData)

        mockMvc.get("/todo/${persistedTestData[1].id}")
            .andExpect {
                status { isOk() }

                content {
                    jsonPath("task", Matchers.containsString("cooking a meal"))
                }

            }
    }

    @Test
    fun `can get all todo`() {
        todoRepository.saveAll(testData)

        val responseBody: List<TodoResponse> = mockMvc.get("/todo/all")
            .andExpect {
                status { isOk() }
            }.andExtractBody()

        Assertions.assertThat(responseBody.size).isGreaterThanOrEqualTo(3)
    }

    @Test
    fun `can get all completed todos`() {
        todoRepository.saveAll(testData)

        val responseBody: List<TodoResponse> = mockMvc.get("/todo/completed")
            .andExpect {
                status { isOk() }
            }.andExtractBody()

        Assertions.assertThat(responseBody.map { it.task }).containsExactly("cleaning up")
    }

    @Test
    fun `can get all incomplete todos`() {
        todoRepository.saveAll(testData)

        val responseBody: List<TodoResponse> = mockMvc.get("/todo/incomplete")
            .andExpect {
                status { isOk() }
            }.andExtractBody()

        Assertions.assertThat(responseBody.map { it.task }).containsExactly("cooking a meal", "coding spring")
    }

    @Test
    fun `can remove todo`() {
        val persistedTestData = todoRepository.saveAll(testData)

        mockMvc.delete("/todo/${persistedTestData.last().id}")
            .andExpect { status { isOk() } }

        Assertions.assertThat(todoRepository.findAll().map { it.task })
            .containsExactly(
                persistedTestData[0].task,
                persistedTestData[1].task
            )
    }

    @Test
    fun `can remove all completed todos`() {
        todoRepository.saveAll(testData)

        mockMvc.delete("/todo/completed")
            .andExpect { status { isOk() } }

        Assertions.assertThat(todoRepository.findAllByCompleted(true).map { it.task })
            .isNullOrEmpty()
    }

    @Test
    fun `can put todo`() {
        mockMvc.put("/todo") {
            contentType = MediaType.APPLICATION_JSON
            content = TodoRequest(task = "test").asJsonString()
        }.andExpect {
            status { isOk() }
        }
    }

    private fun TodoRequest.asJsonString() =
        jacksonObjectMapper().writeValueAsString(this)

    private inline fun <reified T> ResultActionsDsl.andExtractBody() =
        andReturn().response.let {
            jacksonObjectMapper().readValue<T>(it.contentAsString)
        }
}