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

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.model.support.IScanRange;

public class ChromatogramAnalysisSegment implements IAnalysisSegment {

	private final IScanRange range;
	private final IChromatogram<?> chromatogram;
	private final Collection<? extends IAnalysisSegment> childs;

	public ChromatogramAnalysisSegment(IScanRange range, IChromatogram<?> chromatogram, Collection<? extends IAnalysisSegment> childs) {

		this.range = range;
		this.chromatogram = chromatogram;
		this.childs = childs;
	}

	@Override
	public Collection<? extends IAnalysisSegment> getChildSegments() {

		if(childs == null) {
			return Collections.emptyList();
		}
		return childs;
	}

	public IChromatogram<?> getChromatogram() {

		return chromatogram;
	}

	@Override
	public int getStartScan() {

		int startScan = range.getStartScan();
		if(startScan < 1) {
			return 1;
		}
		return startScan;
	}

	@Override
	public int getStopScan() {

		int stopScan = range.getStopScan();
		if(stopScan < getStartScan()) {
			return chromatogram.getNumberOfScans();
		}
		return stopScan;
	}

	@Override
	public int getStartRetentionTime() {

		return chromatogram.getScan(getStartScan()).getRetentionTime();
	}

	@Override
	public int getStopRetentionTime() {

		return chromatogram.getScan(getStopScan()).getRetentionTime();
	}
}
