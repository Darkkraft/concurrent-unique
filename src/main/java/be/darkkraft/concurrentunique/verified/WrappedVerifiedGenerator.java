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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

final class WrappedVerifiedGenerator<T> extends AbstractVerifiedGenerator<T> {

    private final Predicate<T> existPredicate;

    WrappedVerifiedGenerator(final @NotNull UniqueGenerator<T> generator, final int maxRetry, final @NotNull Predicate<T> existPredicate) {
        super(generator, maxRetry);
        this.existPredicate = Objects.requireNonNull(existPredicate, "Predicate cannot be null");
    }

    @Override
    public boolean isAlreadyExists(@NotNull final T generated) {
        return this.existPredicate.test(generated);
    }

}