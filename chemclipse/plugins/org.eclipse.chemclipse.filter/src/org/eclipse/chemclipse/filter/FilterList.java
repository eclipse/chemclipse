/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.filter;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * A filterlist extends the {@link Iterable} interface in a way that allows optional adding/removal of elements
 * 
 * @author Christoph Läubrich
 *
 * @param <T>
 */
public interface FilterList<T> extends Iterable<T> {

	default <X extends T> void remove(X item) {

		throw new UnsupportedOperationException();
	}

	int size();

	default void add(T item) {

		throw new UnsupportedOperationException();
	}

	default void addBefore(T item, T beforeItem) {

		throw new UnsupportedOperationException();
	}

	default void addAfter(T item, T afterItem) {

		throw new UnsupportedOperationException();
	}

	static <Singleton> FilterList<Singleton> singelton(Singleton item) {

		return new FilterList<Singleton>() {

			@Override
			public Iterator<Singleton> iterator() {

				return Collections.singleton(item).iterator();
			}

			@Override
			public int size() {

				return 1;
			}
		};
	}

	static <X> Iterator<X> convert(Iterator<? extends X> other) {

		return new Iterator<X>() {

			@Override
			public boolean hasNext() {

				return other.hasNext();
			}

			@Override
			public X next() {

				return other.next();
			}

			@Override
			public void remove() {

				other.remove();
			}

			@Override
			public void forEachRemaining(Consumer<? super X> action) {

				other.forEachRemaining(action);
			}
		};
	}
}
