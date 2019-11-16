/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for child segments
 * 
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.Collection;
import java.util.Collections;

public interface IAnalysisSegment extends IScanRange {

	/**
	 * Returns the width of the analysis segment in scan units.
	 * 
	 * @deprecated use {@link #getWidth()} instead
	 * @return int
	 */
	@Deprecated
	default int getSegmentWidth() {

		return getWidth();
	}

	/**
	 * An {@link IAnalysisSegment} might be further be separated, this method allows access to its child segments
	 * 
	 * @return the child segments for this segment
	 */
	default Collection<? extends IAnalysisSegment> getChildSegments() {

		return Collections.emptyList();
	}
}
