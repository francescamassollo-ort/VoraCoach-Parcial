package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import com.example.voracoach.data.repository.NotificationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationsRepository
) : ViewModel() {

    val allRead: StateFlow<Boolean> = repository.allRead

    fun markAllAsRead() = repository.markAllAsRead()
}
