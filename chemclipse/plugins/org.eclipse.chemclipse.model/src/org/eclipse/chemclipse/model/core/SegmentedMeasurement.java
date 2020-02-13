/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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

import java.util.List;

import org.eclipse.chemclipse.model.support.IAnalysisSegment;

public interface SegmentedMeasurement extends IMeasurement {

	/**
	 * 
	 * @return a read only view of all current {@link IAnalysisSegment}s in the current measurement
	 */
	List<IAnalysisSegment> getAnalysisSegments();
}
