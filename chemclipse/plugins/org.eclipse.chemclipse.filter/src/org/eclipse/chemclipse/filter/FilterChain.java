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

import org.eclipse.chemclipse.processing.core.MessageConsumer;

/**
 * A {@link FilterChain} allows to trigger the next step in the processing of {@link Filter}s. This allows the following use cases:
 * <ul>
 * <li>A Filter can <b>replace</b> the object to be filtered downstream
 * <li>A Filter can <b>break></b> further processing e.g. if an error occurs
 * <li>depending on the filtered object, a filter can <b>intercept</b> the objects/calls
 * <li>A Filter can process the rest of the chain and do something with the result after others filter has proceed
 * </ul>
 * 
 * @author Christoph Läubrich
 *
 * @param <T>
 */
public interface FilterChain<T> {

	T doFilter(T item, MessageConsumer messageConsumer);
}
