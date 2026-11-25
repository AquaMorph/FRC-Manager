package com.aquamorph.frcmanager.network

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aquamorph.frcmanager.utils.Logging
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Refreshes and state updates are serialized on the UI thread. */
internal class EventViewModel(
    private val load: (EventSelection) -> Observable<EventUpdate>,
    private val ui: Scheduler
) : ViewModel() {
    private val activeRefresh = SerialDisposable()
    private val mutableState = MutableStateFlow(EventState())
    val state = mutableState.asStateFlow()

    fun configure(selection: EventSelection, display: EventDisplay) {
        if (selection != state.value.selection) {
            startRefresh(selection, display)
        } else {
            mutableState.update { it.copy(display = display) }
        }
    }

    fun refresh(selection: EventSelection = state.value.selection) {
        startRefresh(selection, state.value.display)
    }

    private fun startRefresh(selection: EventSelection, display: EventDisplay) {
        val request = SerialDisposable()
        activeRefresh.set(request)
        mutableState.update { it.copy(display = display).beginRefresh(selection) }
        if (selection.eventKey.isEmpty()) return

        // Install the disposable first so even a synchronous replacement can cancel this refresh.
        request.set(Observable.defer { load(selection) }.observeOn(ui).subscribe(
            { update -> if (!request.isDisposed) mutableState.update(update) },
            { error ->
                Logging.error(this, error.toString(), 0)
                if (!request.isDisposed) mutableState.update(EventState::endRefresh)
            },
            { if (!request.isDisposed) mutableState.update(EventState::endRefresh) }
        ))
    }

    override fun onCleared() {
        activeRefresh.dispose()
    }

    companion object {
        fun factory(context: Context) = viewModelFactory {
            val appContext = context.applicationContext
            initializer { EventViewModel(EventRepository(appContext)::load, AndroidSchedulers.mainThread()) }
        }
    }
}
