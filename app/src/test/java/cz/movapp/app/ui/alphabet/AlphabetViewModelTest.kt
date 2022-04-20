package cz.movapp.app.ui.alphabet

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import org.junit.Test


class AlphabetViewModelTest {

    @Test
    fun flow() = runBlocking {

        val testContext = this.coroutineContext
        val async1 = async(Dispatchers.IO) {
            flowOf(1, 2, 3, 4, 5)
                .combine(flowOf(10)) { a, b -> a + b }
                .onEach {
                    withContext(testContext) {
                        println(it)
                    }
                }
                .toList()
        }
        println("start")

        val async2 = async(Dispatchers.IO) {
            flowOf(1, 2, 3, 4, 5)
                .combine(flowOf(100)) { a, b -> a + b }
                .onEach {
                    withContext(testContext) {
                        println(it)
                    }
                }
                .toList()
        }
        async1.join()
        async2.join()
        val message = listOf(async1, async2).awaitAll()
        assertThat(message[0]).hasSize(5)
        assertThat(message[1]).hasSize(5)
        println(message)
        delay(1000)
        println("delayed")
    }
}