package com.aquamorph.frcmanager.network

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aquamorph.frcmanager.models.tba.Match
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EventViewModelTest {
    private val ui = TestScheduler()
    private val first = EventSelection(eventKey = "first", districtKey = "district", predictionMode = "tba")
    private val second = EventSelection(eventKey = "second")

    @Test
    fun `switching events cancels the previous request and drops its queued update`() {
        val oldRequest = PublishSubject.create<EventUpdate>()
        val newRequest = PublishSubject.create<EventUpdate>()
        val model = EventViewModel({ if (it == first) oldRequest else newRequest }, ui)

        model.refresh(first)
        oldRequest.onNext { it.copy(feedNotice = FeedNotice.EVENT_DOWN) }
        assertTrue(oldRequest.hasObservers())

        model.refresh(second)
        newRequest.onNext { it.copy(matches = it.matches.finish(emptyList())) }
        ui.triggerActions()

        assertFalse(oldRequest.hasObservers())
        assertEquals(second, model.state.value.selection)
        assertEquals(null, model.state.value.feedNotice)
        assertFalse(model.state.value.matches.loading)
        assertFalse(model.state.value.districtRankings.loading)
    }

    @Test
    fun `refreshing the same event also cancels the previous request`() {
        val oldRequest = PublishSubject.create<EventUpdate>()
        val newRequest = PublishSubject.create<EventUpdate>()
        val requests = listOf(oldRequest, newRequest).iterator()
        val model = EventViewModel({ requests.next() }, ui)

        model.refresh(first)
        oldRequest.onNext { it.copy(feedNotice = FeedNotice.EVENT_DOWN) }
        model.refresh()
        newRequest.onNext { it.copy(feedNotice = FeedNotice.FIRST_DOWN) }
        ui.triggerActions()

        assertFalse(oldRequest.hasObservers())
        assertEquals(FeedNotice.FIRST_DOWN, model.state.value.feedNotice)
    }

    @Test
    fun `matches finish loading while predictions are still pending`() {
        val predictions = PublishSubject.create<EventUpdate>()
        val alliance = Match.MatchAlliance(-1, arrayListOf(), arrayListOf(), arrayListOf())
        val match = Match(
            "first_qm1", "qm", 1, 1, Match.Alliances(alliance, alliance), "",
            first.eventKey, 0, 0, 0, 0, null
        )
        val matches = Observable.just<EventUpdate> { it.copy(matches = it.matches.finish(listOf(match))) }
        val model = EventViewModel({ Observable.merge(matches, predictions) }, ui)

        model.refresh(first)
        assertTrue(model.state.value.matches.loading)
        ui.triggerActions()

        assertTrue(predictions.hasObservers())
        assertFalse(model.state.value.matches.loading)
        assertEquals(listOf(match), model.state.value.matches.items)
    }

    @Test
    fun `unchanged selection updates display without starting another request`() {
        var loads = 0
        val model = EventViewModel({
            loads++
            Observable.never()
        }, ui)
        model.configure(first, EventDisplay(teamNumber = "123"))
        model.configure(first, EventDisplay(teamNumber = "456"))

        assertEquals(1, loads)
        assertEquals("456", model.state.value.display.teamNumber)
    }

    @Test
    fun `clearing the view model store cancels ongoing requests`() {
        val request = PublishSubject.create<EventUpdate>()
        val store = ViewModelStore()
        val factory = viewModelFactory { initializer { EventViewModel({ request }, ui) } }
        val model = ViewModelProvider.create(store, factory)[EventViewModel::class]
        model.refresh(first)
        assertTrue(request.hasObservers())

        store.clear()

        assertFalse(request.hasObservers())
    }
}
