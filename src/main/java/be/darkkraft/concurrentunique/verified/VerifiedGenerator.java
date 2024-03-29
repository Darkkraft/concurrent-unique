/*
 * MIT License
 *
 * Copyright (c) 2024 Darkkraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.darkkraft.concurrentunique.verified;

import be.darkkraft.concurrentunique.UniqueGenerator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

public interface VerifiedGenerator<T> extends UniqueGenerator<T> {

    static <T> @NotNull VerifiedGenerator<T> wrap(final @NotNull UniqueGenerator<T> generator,
                                                  final int maxRetry,
                                                  final @NotNull Predicate<T> existPredicate) {
        return new WrappedVerifiedGenerator<>(generator, maxRetry, existPredicate);
    }

    @Override
    default @Nullable T generate() {
        return this.generate(this.getMaxRetry());
    }

    default @Nullable T generate(final int maxRetry) {
        int remaining = Math.max(maxRetry, 1);
        T generated;
        do {
            if (--remaining < 0) {
                return null;
            }
            generated = this.regenerate();
        } while (generated == null || this.isAlreadyExists(generated));
        return generated;
    }

    @Nullable T regenerate();

    boolean isAlreadyExists(final @NotNull T generated);

    int getMaxRetry();

    @Contract("_, _ -> new")
    default <R> @NotNull VerifiedGenerator<R> map(final @NotNull Function<T, R> function, final @NotNull Predicate<R> existPredicate) {
        return this.map(function, this.getMaxRetry(), existPredicate);
    }

    @Contract("_, _, _ -> new")
    default <R> @NotNull VerifiedGenerator<R> map(final @NotNull Function<T, R> function,
                                                  final int maxRetry,
                                                  final @NotNull Predicate<R> existPredicate) {
        return wrap(() -> function.apply(this.regenerate()), maxRetry, existPredicate);
    }

    @Override
    @NotNull
    default VerifiedGenerator<T> synchronize() {
        return new ChainedSynchronizedVerifiedGenerator<>(this);
    }

}