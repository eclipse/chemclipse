/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

/**
 * A analysis segment represents a segment of a chromatogram which should be
 * analyzed.<br/>
 * The segment gives information about the start and stop scan number and the
 * segment area.
 * 
 * @author eselmeister
 */
public class AnalysisSegment implements IAnalysisSegment {

	private int startScan = 0;
	private int stopScan = 0;

	public AnalysisSegment(int startScan, int segmentWidth) {
		if(startScan > 0 && segmentWidth > 0) {
			this.startScan = startScan;
			this.stopScan = startScan + segmentWidth - 1;
		}
	}

	@Override
	public int getSegmentWidth() {

		int delta = stopScan - startScan;
		if(delta == 0) {
			return 0;
		} else {
			return ++delta;
		}
	}

	@Override
	public int getStartScan() {

		return startScan;
	}

	@Override
	public int getStopScan() {

		return stopScan;
	}

	public void setStartScan(int startScan) {

		if(startScan > 0) {
			this.startScan = startScan;
		}
	}

	public void setStopScan(int stopScan) {

		if(stopScan >= startScan) {
			this.stopScan = stopScan;
		}
	}
}
