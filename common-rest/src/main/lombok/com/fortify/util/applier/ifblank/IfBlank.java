/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.util.applier.ifblank;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class IfBlank {
	public static final IfBlankAction APPLY() { return IfBlank::applyIfBlank; }
	public static final IfBlankAction SKIP() { return IfBlank::skipIfBlank; }
	public static final IfBlankAction ERROR() { return IfBlank::errorIfBlank; }
	
	public static final <V> IfBlankAction DEFAULT(V defaultValue) {
		return DEFAULT(()->defaultValue);
	}
	public static final <V> IfBlankAction DEFAULT(Supplier<V> defaultValue) {
		return new DefaultIfBlankAction<>(defaultValue);
	}
	
	private static final class DefaultIfBlankAction<D> implements IfBlankAction {
		private final Supplier<D> defaultValueSupplier;
		private DefaultIfBlankAction(Supplier<D> defaultValueSupplier) {
			this.defaultValueSupplier = defaultValueSupplier;
		}
		@SuppressWarnings("unchecked")
		@Override
		public <V> void apply(String name, V value, Function<V, Boolean> isBlankFunction, Consumer<V> consumer) {
			if ( isBlankFunction.apply(value) ) {
				consumer.accept((V)defaultValueSupplier.get());
			} else {
				consumer.accept(value);
			}
		}
	}
	
	private static final <V> void applyIfBlank(String name, V value, Function<V, Boolean> isBlankFunction, Consumer<V> consumer ) {
		consumer.accept(value);
	}

	private static final <V> void skipIfBlank(String name, V value, Function<V, Boolean> isBlankFunction, Consumer<V> consumer) {
		if ( !isBlankFunction.apply(value) ) { consumer.accept(value); } 
	}
	
	private static final <V> void errorIfBlank(String name, V value, Function<V, Boolean> isBlankFunction, Consumer<V> consumer) {
		if ( isBlankFunction.apply(value) ) { 
			throw new IllegalArgumentException(String.format("%s must have a value", name)); 
		} else {
			consumer.accept(value);
		}
	}
}
