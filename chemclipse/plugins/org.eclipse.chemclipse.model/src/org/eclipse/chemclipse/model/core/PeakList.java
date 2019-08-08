/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Interface that supplies a stream of {@link PeakPosition}s
 * 
 * @author Christoph Läubrich
 *
 */
public interface PeakList extends Iterable<PeakPosition> {

	/**
	 * 
	 * @param collection
	 * @return an unmodifiable {@link PeakList} <b>backed by</b> the given collection
	 */
	public static PeakList fromCollection(Collection<? extends PeakPosition> collection) {

		Collection<PeakPosition> unmodifiableCollection = Collections.unmodifiableCollection(collection);
		return unmodifiableCollection::iterator;
	}

	/**
	 * 
	 * @param peakPositions
	 * @return an unmodifiable {@link PeakList} <b>copied from</b> the given positions
	 */
	public static PeakList fromArray(PeakPosition... peakPositions) {

		Collection<PeakPosition> unmodifiableCollection = Collections.unmodifiableCollection(Arrays.asList(peakPositions));
		return unmodifiableCollection::iterator;
	}
}
