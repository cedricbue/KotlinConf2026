package org.course.challenge03

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface StocksRepository : CoroutineCrudRepository<Stock, Long> {

    /**
     * Challenge 3 - Part 1 - Exercise A
     * Take a look at the reactive @see [JStockRepository.findBySymbol] Java implementation,
     * which makes use of [reactor.core.publisher.Mono]<T> as reactive abstraction for a single result fetched from a database.
     * In this exercise you have to implement the findBySymbol method in a reactive way
     * using Coroutines.
     * Tip: if you use or return a Mono<T> you are not on the right track: with Coroutines
     * no reactive abstraction is required. You have to stick to normal method signatures only
     * adding the suspend keyword to the method.
     * Tip2: you even can make the method signature more expressive than in Java by using
     * a nullable type as return value.
     * Make the corresponding test in @see [org.course.challenge03.Coroutines01ServiceRepositoryTest] pass.
     */
    //TODO: implement the findBySymbol method
    suspend fun findBySymbol(symbol: String): Stock?

}

@Repository
interface StocksEventRepository : CoroutineCrudRepository<StockEvent, Long> {

    fun findById_GreaterThan(id: Long): Flow<StockEvent>

    suspend fun findLastBySymbol(symbol: String): StockEvent?
}
