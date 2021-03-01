package com.escodro.task.presentation.detail

import com.escodro.task.mapper.AlarmIntervalMapper
import com.escodro.task.mapper.TaskMapper
import com.escodro.task.presentation.detail.main.TaskDetailState
import com.escodro.task.presentation.detail.main.TaskDetailViewModel
import com.escodro.task.presentation.fake.FAKE_DOMAIN_TASK
import com.escodro.task.presentation.fake.LoadTaskFake
import com.escodro.task.presentation.fake.UpdateTaskDescriptionFake
import com.escodro.task.presentation.fake.UpdateTaskTitleFake
import com.escodro.test.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class TaskDetailViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val loadTask = LoadTaskFake()

    private val updateTaskTitle = UpdateTaskTitleFake()

    private val updateDescription = UpdateTaskDescriptionFake()

    private val taskMapper = TaskMapper(AlarmIntervalMapper())

    private val viewModel = TaskDetailViewModel(
        loadTaskUseCase = loadTask,
        updateTaskTitle = updateTaskTitle,
        updateTaskDescription = updateDescription,
        taskMapper = taskMapper
    )

    @Test
    fun `test if when a task exist it returns the success state`() {
        // Given the viewModel is called to load the task info
        loadTask.taskToBeReturned = FAKE_DOMAIN_TASK
        viewModel.setTaskInfo(FAKE_DOMAIN_TASK.id)

        // When the latest event is collected
        val state = viewModel.state.value

        // Then the state contain the loaded task
        require(state is TaskDetailState.Loaded)
        val assertViewTask = taskMapper.toView(FAKE_DOMAIN_TASK)
        Assert.assertEquals(assertViewTask, state.task)
    }

    @Test
    fun `test if when a task does not exist it returns the error state`() {
        // Given the viewModel is called to load the task info
        loadTask.taskToBeReturned = null
        viewModel.setTaskInfo(FAKE_DOMAIN_TASK.id)

        // When the latest event is collected
        val state = viewModel.state.value

        // Then the state contain the loaded task
        Assert.assertTrue(state is TaskDetailState.Error)
    }

    @Test
    fun `test if when the update title is called, the field is updated`() {

        // Given the viewModel is called to load the task info
        loadTask.taskToBeReturned = FAKE_DOMAIN_TASK
        viewModel.setTaskInfo(FAKE_DOMAIN_TASK.id)

        // When the title is updated
        val newTitle = "title"
        viewModel.updateTitle(title = newTitle)
        coroutineTestRule.testDispatcher.advanceUntilIdle() /* Advance typing debounce */

        // Then the task will be updated with given title
        Assert.assertTrue(updateTaskTitle.isTitleUpdated(FAKE_DOMAIN_TASK.id))
        val updatedTitle = updateTaskTitle.getUpdatedTitle(FAKE_DOMAIN_TASK.id)
        Assert.assertEquals(newTitle, updatedTitle)
    }

    @Test
    fun `test if when the update description is called, the field is updated`() {

        // Given the viewModel is called to load the task info
        loadTask.taskToBeReturned = FAKE_DOMAIN_TASK
        viewModel.setTaskInfo(FAKE_DOMAIN_TASK.id)

        // When the description is updated
        val newDescription = "description"
        viewModel.updateDescription(description = newDescription)
        coroutineTestRule.testDispatcher.advanceUntilIdle() /* Advance typing debounce */

        // Then the task will be updated with given description
        Assert.assertTrue(updateDescription.isDescriptionUpdated(FAKE_DOMAIN_TASK.id))
        val updatedDesc = updateDescription.getUpdatedDescription(FAKE_DOMAIN_TASK.id)
        Assert.assertEquals(newDescription, updatedDesc)
    }
}
