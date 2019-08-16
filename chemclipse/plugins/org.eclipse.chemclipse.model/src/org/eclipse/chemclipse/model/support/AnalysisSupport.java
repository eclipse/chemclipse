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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.exceptions.AnalysisSupportException;

/**
 * The analysis support helps you to divide a chromatogram into different
 * analysis segments of a given segment width.
 * 
 * @author eselmeister
 */
public class AnalysisSupport implements IAnalysisSupport {

	private List<IAnalysisSegment> analysisSegments;

	/**
	 * Creates a new {@link AnalysisSupport} instance.<br/>
	 * The scans number starts at position 1 to numberOfScans.<br/>
	 * The class calculates the number of segments to cut the numberOfScan into
	 * parts of segmentWidth.<br/>
	 * The last segment could have less scans than the given segment width to
	 * fit to number of scans.
	 * 
	 * @param numberOfScans
	 * @param segmentWidth
	 * @throws AnalysisSupportException
	 */
	public AnalysisSupport(int numberOfScans, int segmentWidth) throws AnalysisSupportException {
		/*
		 * Starts at scan position 1.
		 */
		if(segmentWidth >= 3 && numberOfScans >= segmentWidth) {
			initializeAnalysisSegments(numberOfScans, 1, segmentWidth);
		} else {
			throw new AnalysisSupportException("The segmentWidth must be >= 3 and the number of scans must be >= segmentWidth.");
		}
	}

	/**
	 * Creates a new {@link AnalysisSupport} instance.<br/>
	 * The scans number starts at position scanRange.getStartScan() to
	 * scanRange.getStopScan().<br/>
	 * The class calculates the number of segments to cut the numberOfScan into
	 * parts of segmentWidth.<br/>
	 * The last segment could have less scans than the given segment width to
	 * fit to number of scans.
	 * 
	 * @param scanRange
	 * @param segmentWidth
	 * @throws AnalysisSupportException
	 */
	public AnalysisSupport(IScanRange scanRange, int segmentWidth) throws AnalysisSupportException {
		if(scanRange == null) {
			throw new AnalysisSupportException("The scan range must not be null.");
		}
		/*
		 * Starts at scan position scanRange.getStartScan().
		 */
		if(segmentWidth >= 3 && scanRange.getWidth() >= segmentWidth) {
			initializeAnalysisSegments(scanRange.getWidth(), scanRange.getStartScan(), segmentWidth);
		} else {
			throw new AnalysisSupportException("The segmentWidth must be >= 3 and the number of scans must be >= segmentWidth.");
		}
	}

	// ----------------------------------------IAnalysisSupport
	@Override
	public int getNumberOfAnalysisSegments() {

		return analysisSegments.size();
	}

	@Override
	public List<IAnalysisSegment> getAnalysisSegments() {

		return analysisSegments;
	}

	// ----------------------------------------IAnalysisSupport
	// ----------------------------------------private methods
	private void initializeAnalysisSegments(int numberOfScans, int startScan, int segmentWidth) {

		assert numberOfScans > 0 : "The number of scans must be > 0";
		assert segmentWidth > 0 : "The segment width must be > 0";
		AnalysisSegment segment;
		boolean addLastSegment = false;
		/*
		 * Calculate the number of scans in the last segment and afterwards the
		 * number of segments with a scan width of the given segment width. Do
		 * scans exists in the last segment?
		 */
		int lastSegmentWidth = numberOfScans % segmentWidth;
		if(lastSegmentWidth > 0) {
			addLastSegment = true;
		}
		/*
		 * Retrieve the number of segment parts.
		 */
		numberOfScans -= lastSegmentWidth;
		int segmentParts = numberOfScans / segmentWidth;
		analysisSegments = new ArrayList<IAnalysisSegment>();
		for(int i = 1; i <= segmentParts; i++) {
			segment = new AnalysisSegment(startScan, segmentWidth);
			analysisSegments.add(segment);
			startScan += segmentWidth;
		}
		/*
		 * Add the last segment to the segment list if there exists one.
		 */
		if(addLastSegment) {
			segment = new AnalysisSegment(startScan, lastSegmentWidth);
			analysisSegments.add(segment);
		}
	}
	// ----------------------------------------private methods
}
