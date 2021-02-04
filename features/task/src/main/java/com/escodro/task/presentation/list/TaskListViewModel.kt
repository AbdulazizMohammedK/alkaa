package com.escodro.task.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.escodro.domain.usecase.task.UpdateTaskStatus
import com.escodro.domain.usecase.taskwithcategory.LoadUncompletedTasks
import com.escodro.task.mapper.TaskWithCategoryMapper
import com.escodro.task.model.TaskWithCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * ViewModel responsible to handle the interaction between the presentation and business logic from
 * Task Section.
 */
internal class TaskListViewModel(
    private val loadAllTasksUseCase: LoadUncompletedTasks,
    private val updateTaskStatusUseCase: UpdateTaskStatus,
    private val taskWithCategoryMapper: TaskWithCategoryMapper
) : ViewModel() {

    private val _state = MutableStateFlow<TaskListViewState>(TaskListViewState.Empty)

    val state: StateFlow<TaskListViewState>
        get() = _state

    fun loadTasks() = viewModelScope.launch {
        loadAllTasksUseCase()
            .map { task -> taskWithCategoryMapper.toView(task) }
            .catch { error -> _state.value = TaskListViewState.Error(error) }
            .collect { tasks ->
                _state.value = if (tasks.isNotEmpty()) {
                    TaskListViewState.Loaded(tasks)
                } else {
                    TaskListViewState.Empty
                }
            }
    }

    fun updateTaskStatus(item: TaskWithCategory) = viewModelScope.launch {
        updateTaskStatusUseCase(item.task.id)
    }
}
