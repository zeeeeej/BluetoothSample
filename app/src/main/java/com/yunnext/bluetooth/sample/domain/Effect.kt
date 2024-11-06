package com.yunnext.bluetooth.sample.domain

sealed interface Effect<out I, out O> {
    data object Idle : Effect<Nothing, Nothing>
    data class Progress<I, Progress>(val input: I, val progress: Progress) : Effect<I, Nothing>
    data class Success<I, O>(val input: I, val output: O) : Effect<I, O>
    data class Fail<I>(val input: I, val output: kotlin.Throwable) : Effect<I, Nothing>
    data object Completed : Effect<Nothing, Nothing>
}

val Effect<*,*>.doing:Boolean
    get() = this is Effect.Progress<*,*>


fun <Progress> effectDoIng(progress: Progress) = Effect.Success<Unit, Progress>(Unit, progress)
fun <I, Progress> effectDoIng(input: I, progress: Progress) =
    Effect.Progress<I, Progress>(input = input, progress)

fun <I> effectDoIng(input: I, progress: Int) =
    effectDoIng<I, Int>(input = input, progress = progress)

fun <I> effectDoIngUnit(input: I) = effectDoIng<I, Unit>(input = input, progress = Unit)
fun effectDoIng(progress: Int) = Effect.Progress<Unit, Int>(Unit, progress)
fun effectDoIng() = Effect.Progress<Unit, Unit>(Unit, Unit)
fun <I, O> effectSuccess(input: I, output: O) = Effect.Success<I, O>(input = input, output)
fun <O> effectSuccess(output: O) = Effect.Success<Unit, O>(Unit, output)
fun <I> effectFail(input: I, output: Throwable) = Effect.Fail<I>(input = input, output)
fun effectFail(output: Throwable) = Effect.Fail<Unit>(Unit, output)
fun effectSuccess() = Effect.Success<Unit, Unit>(Unit, Unit)
fun effectIdle() = Effect.Idle
fun effectCompleted() = Effect.Completed
