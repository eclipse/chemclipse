/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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

import java.util.List;
import java.util.Observable;

import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.model.support.IScanRange;

public abstract class AnalysisSegmentMeasurementResult<T extends IAnalysisSegment> extends Observable implements IMeasurementResult<List<T>> {

	private T selection;

	@Override
	public String getIdentifier() {

		return getClass().getName();
	}

	@Override
	public String getDescription() {

		return "";
	}

	public abstract Class<T> getType();

	/**
	 * the list of segments from this result in the given range
	 * 
	 * @param range
	 *            the range for which segments should be fetched
	 * @param includeBorders
	 *            if <code>true</code> segments that only partially match are included in the result (e.g. a segment starts outside the range but ends inside it)
	 * @return a possible empty list of segments that are available for the given range ordered by the start scan
	 */
	public List<T> getSegments(IScanRange range, boolean includeBorders) {

		return IAnalysisSegment.getSegments(range, includeBorders, getResult());
	}

	public T getSelection() {

		return selection;
	}

	public void setSelection(T selection) {

		if(this.selection != selection) {
			this.selection = selection;
			notifyListener();
		}
	}

	public boolean isSelected(T item) {

		return this.selection == item;
	}

	public void notifyListener() {

		setChanged();
		notifyObservers();
	}
}
