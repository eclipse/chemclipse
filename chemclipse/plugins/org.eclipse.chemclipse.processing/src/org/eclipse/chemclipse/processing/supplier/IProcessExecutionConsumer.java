/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.supplier;

/**
 * A {@link IProcessExecutionConsumer} consumes execution items
 * 
 *
 */
public interface IProcessExecutionConsumer<T> extends IProcessExecutor {

	/**
	 * a consumer might be used to execute several times and thus the result might vary over time until a final result is produced
	 * 
	 * @return the current result, or <code>null</code> if no result is produced yet
	 */
	default T getResult() {

		return null;
	}

	/**
	 * test if this Consumer can possibly execute the given {@link IProcessorPreferences}, this method works on a best guess basis,
	 * the general contract is that if this method return <code>false</code> it is guaranteed that execute will be a noop or throw an exception,
	 * but if the method return <code>true</code> it is not guaranteed that execute will succeed
	 * 
	 * @param preferences
	 * @return <code>false</code> if this consumer can't consume this, or <code>true</code> if it might be possible
	 */
	default <X> boolean canExecute(IProcessorPreferences<X> preferences) {

		return true;
	}

	/**
	 * creates a a new instance of this consumer with the given initial result
	 * 
	 * @param initialResult
	 * @return a new instance or <code>null</code> if it can't be created from the given result object
	 */
	default IProcessExecutionConsumer<T> withResult(Object initialResult) {

		return null;
	}
}
