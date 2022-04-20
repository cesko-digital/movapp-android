package cz.movapp.android

import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock

fun <T> mockCheck(classToMock: Class<T>?): T {
    return printNotStubbedMethods(classToMock)
}

/**
 * Create helper mock which prints all not mocked methods
 * @param classToMock
 * @param <T>
 * @return
</T> */
fun <T> printNotStubbedMethods(classToMock: Class<T>?): T {
    return Mockito.mock(
        classToMock
    ) { invocation: InvocationOnMock ->
        val message = invocation.method.toString() + " Not Stubbed!"
        println(message)
        null
    }
}