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
package org.eclipse.chemclipse.model.results;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.model.support.IScanRange;

public abstract class AnalysisSegmentMeasurementResult<T extends IAnalysisSegment> implements IMeasurementResult<List<T>> {

	@Override
	public String getIdentifier() {

		return getClass().getName();
	}

	@Override
	public String getDescription() {

		return "";
	}

	/**
	 * the list of segments from this result in the given range
	 * 
	 * @param range
	 * @param includeBorders
	 * @return
	 */
	public List<T> getSegments(IScanRange range, boolean includeBorders) {

		List<T> list = new ArrayList<>();
		for(T item : getResult()) {
			if(item.containsScan(range.getStartScan())) {
				if(item.containsScan(range.getStopScan()) || includeBorders) {
					list.add(item);
				}
			} else if(item.containsScan(range.getStopScan()) && includeBorders) {
				list.add(item);
			}
		}
		return list;
	}
}
