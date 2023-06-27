/*
 * This file is part of euler-kt
 * Copyright (C) 2023 Bill Kozak
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package euler_kt.main.util.coroutines

import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

public class RecursiveSequence<T, P, R>(
    private val p0: P,
    private val function: suspend RecursiveSequenceScope<T, P, R>.(P) -> R
) : Sequence<T> {

    override fun iterator(): Iterator<T> {
        return RecursiveSequenceScopeImpl(function, p0)
    }
}

sealed class RecursiveSequenceScope<in T, in P, out R> {
    /**
     * Emits a value to the [Iterator] being built and suspends
     * until the next value is requested.
     */
    abstract suspend fun emit(value: T)

    /**
     * Emits all values to the [Iterator] being built and suspends until the next value is requested
     */
    abstract suspend fun emitAll(values: Iterable<T>)

    /**
     * Makes recursive call to this [DeepRecursiveFunction] function putting the call activation frame on the heap,
     * as opposed to the actual call stack that is used by a regular recursive call.
     */
    abstract suspend fun callRecursive(value: P): R
}

/**
 * Define my own startCoroutineUninterceptedOrReturn
 *
 * As StartCoroutineUninterceptedOrReturn is internal, I can't use it directly
 * the function definition is copied from the kotlin source code.
 *
 * @see kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
 *
 * Note that the warning suppression for "NOTHING_TO_INLINE" was also added by me. I don't know if inlining is
 * necessary but that's what the original code did.
 */
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
private inline fun <R, P, T> (suspend R.(P) -> T).myStartCoroutineUninterceptedOrReturn(
    receiver: R,
    param: P,
    completion: Continuation<T>
): Any? = (this as Function3<R, P, Continuation<T>, Any?>).invoke(receiver, param, completion)

@Suppress("UNCHECKED_CAST")
private class RecursiveSequenceScopeImpl<T, P, R>(
    block: suspend RecursiveSequenceScope<T, P, R>.(P) -> R,
    param: P
) : RecursiveSequenceScope<T, P, R>(), Continuation<R>, Iterator<T> {

    private var param: Any? = param
    private var function = block as suspend RecursiveSequenceScope<*, *, *>.(Any?) -> Any?
    private var cont = this as Continuation<Any?>
    private var result: Result<Any?> = Result.success(COROUTINE_SUSPENDED)
    private var value: T? = null
    private var state = RecursiveSequenceState.RECURSE

    override val context: CoroutineContext get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<R>) {
        result.getOrThrow()
        this.state = RecursiveSequenceState.DONE
        this.result = result
    }

    override suspend fun emit(value: T) {
        this.state = RecursiveSequenceState.VALUE_READY
        this.value = value

        suspendCoroutineUninterceptedOrReturn {cont ->
            this.cont = cont
            COROUTINE_SUSPENDED
        }
    }

    override suspend fun emitAll(values: Iterable<T>) {
        for(value in values) {
            emit(value)
        }
    }

    override suspend fun callRecursive(value: P): R = suspendCoroutineUninterceptedOrReturn { cont ->

        this.cont = cont as Continuation<Any?>
        this.param = value
        this.state = RecursiveSequenceState.RECURSE
        COROUTINE_SUSPENDED
    }

    override fun hasNext(): Boolean {
        if (state == RecursiveSequenceState.VALUE_READY) {
            return true
        }
        runUntilDoneOrNextValue()

        return state == RecursiveSequenceState.VALUE_READY
    }

    override fun next(): T {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        state = RecursiveSequenceState.YIELDED
        return value as T
    }

    private fun runUntilDoneOrNextValue() {
        while (state != RecursiveSequenceState.DONE && state != RecursiveSequenceState.VALUE_READY) {

            if(state == RecursiveSequenceState.YIELDED) {
                cont.resume(value)
            } else if(state == RecursiveSequenceState.RECURSE) {
                val r = try {
                    function.myStartCoroutineUninterceptedOrReturn(this, param, cont)

                } catch (e: Throwable) {
                    cont.resumeWithException(e)
                    continue
                }
                // If the function returns without suspension -- calls its continuation immediately
                if (r !== COROUTINE_SUSPENDED) {
                    cont.resume(r as R)
                }
            }
        }
    }

    private enum class RecursiveSequenceState { DONE, YIELDED, VALUE_READY, RECURSE }
}