package com.aquamorph.frcmanager.network

import com.aquamorph.frcmanager.models.tba.TBAPrediction
import io.reactivex.Observable
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class SchedulePredictionsTest {
    @Test
    fun `network failure emits an empty prediction state`() {
        val failure = IOException("Prediction service unavailable")
        val errors = mutableListOf<Throwable>()

        Observable.error<Response<String>>(failure)
            .optionalPredictions({ error("No body expected") }, { errors.add(it) })
            .test()
            .assertResult(SchedulePredictions())

        assertSame(failure, errors.single())
    }

    @Test
    fun `unsuccessful HTTP response emits an empty prediction state`() {
        val errors = mutableListOf<Throwable>()

        Observable.just(Response.error<String>(503, "".toResponseBody()))
            .optionalPredictions({ error("No body expected") }, { errors.add(it) })
            .test()
            .assertResult(SchedulePredictions())

        assertTrue(errors.single() is HttpException)
    }

    @Test
    fun `successful response without a body emits an empty prediction state`() {
        Observable.just(Response.success<String>(204, null))
            .optionalPredictions({ error("No body expected") }, { throw AssertionError(it) })
            .test()
            .assertResult(SchedulePredictions())
    }

    @Test
    fun `successful response emits predictions`() {
        val expected = SchedulePredictions(tba = listOf(TBAPrediction.PredMatch("event_qm1", 0.7, "red")))

        Observable.just(Response.success(expected))
            .optionalPredictions({ it }, { throw AssertionError(it) })
            .test()
            .assertResult(expected)
    }
}
