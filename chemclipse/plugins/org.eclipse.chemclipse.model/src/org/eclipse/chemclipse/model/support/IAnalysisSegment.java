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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface IAnalysisSegment extends IScanRange, IRetentionTimeRange {

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

	/**
	 * the list of segments from this result in the given range
	 * 
	 * @param range
	 *            the range for which segments should be fetched
	 * @param includeBorders
	 *            if <code>true</code> segments that only partially match are included in the result (e.g. a segment starts outside the range but ends inside it)
	 * @return a possible empty list of segments that are available for the given range ordered by the start scan
	 */
	static <T extends IAnalysisSegment> List<T> getSegments(IScanRange range, boolean includeBorders, Collection<T> segments) {

		List<T> list = new ArrayList<>();
		for(T segment : segments) {
			if(range.containsScan(segment.getStartScan())) {
				if(includeBorders || segment.getStopScan() <= range.getStopScan()) {
					list.add(segment);
				}
			} else if(includeBorders && range.containsScan(segment.getStopScan())) {
				list.add(segment);
			}
		}
		Collections.sort(list, (o1, o2) -> o1.getStartScan() - o2.getStartScan());
		return list;
	}
}
