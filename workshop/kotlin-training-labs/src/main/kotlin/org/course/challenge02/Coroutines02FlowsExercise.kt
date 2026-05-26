package org.course.challenge02

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

data class NewsItem(val id: Long, val headline: String) {
    companion object {
        operator fun invoke(id: Long) = NewsItem(id, "headline #$id")
    }
}

/**
 * Simple in memory dao, keeping all its items in a MutableList
 */
class NewsFeedDao(val news: MutableList<NewsItem>) : MutableList<NewsItem> by news {
    fun findByIdGreaterThan(id: Long): Flow<NewsItem> = news.filter { it.id > id }.asFlow()
}

/**
 * NewsFeedService that offers Flows of NewsItems needed for
 * - Exercise C
 * - Exercise D
 */
class NewsFeedService(val dao: NewsFeedDao) {

    /**
     * Exercise C:
     * Now we are ready to implement a flow that interacts with a resource, like a data access object.
     * Implement an infinite flow that pools for [NewsItem]s via [NewsFeedDao.findByIdGreaterThan] every 200ms.
     * To ensure we don't serve items twice, we need to keep track of the latest id that was fetched.
     *
     * Task 1: Implement the fetch() method that fetches all NewsItems via [NewsFeedDao.findByIdGreaterThan].
     * For every item fetched, update the latestId. Which flow operator do you need to use?
     *
     * Task 2: Now we need to poll for new items in intervals of 200ms and emit them to the flow.
     * Hint A: For a simple implementation use a flow builder with a while loop that continuously
     * calls fetch() with a delay of 200ms and emits the result.
     * Hint B: you can also go for a more advanced implementation using  [generateSequence] to:
     * - generate a dummy trigger (like [Unit])
     * - delay 'each' value with 200ms
     * - then, for every trigger ([Unit]), call fetch() and flatten the resulting Flow.
     */
    fun pollingNewsFeedFlow(): Flow<NewsItem> {
        var latestId = -1L
        fun fetch(): Flow<NewsItem> = dao.findByIdGreaterThan(latestId).onEach {
            latestId = it.id
        }
        return flow {
            while (true) {
                fetch().collect { emit(it) }
                delay(200)
            }
        }
    }


    /**
     * Exercise D:
     * Rather than polling the [NewsFeedDao.findByIdGreaterThan] periodically, let's improve our solution:
     * Implement an infinite flow that initially fetches all [NewsItem]s via [NewsFeedDao.findByIdGreaterThan]
     * and then waits for the [SharedFlow] to emit a notification before fetching new items.
     * As such, items will only be fetched when new ones are available.
     *
     * Task 1: Implement the fetch() method that fetches all NewsItems via [NewsFeedDao.findByIdGreaterThan].
     * For every item fetched, update the latestId - same like in Exercise C.
     *
     * Task 2: whenever the [SharedFlow] emits a notification, fetch new items.
     * Hint: consider using the [flatMapConcat] on [sharedFlow] for triggering the fetch.
     *
     * Task 3: combine the two flows: ensure the [initialFetch] flow is first consumed before
     * the [triggeredFetches] is triggered.
     * Hint: consider using the [flowOf] and [flattenConcat] operators.
     */
    fun sharedFlowTriggeredNewsFeedFlow(sharedFlow: Flow<String>): Flow<NewsItem> {
        var latestId = -1L
        fun fetch(): Flow<NewsItem> =  dao.findByIdGreaterThan(latestId).onEach {
            latestId = it.id
        }

        val initialFetch: Flow<NewsItem> = fetch()

        val triggeredFetches: Flow<NewsItem> = sharedFlow.flatMapConcat { fetch() }

        return flowOf(initialFetch, triggeredFetches).flattenConcat()
    }
}
