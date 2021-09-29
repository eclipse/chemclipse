/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add static method, generalize function calls
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
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
			analysisSegments = initializeAnalysisSegments(numberOfScans, 1, segmentWidth, SimpleSegment::new);
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
			analysisSegments = initializeAnalysisSegments(scanRange.getWidth(), scanRange.getStartScan(), segmentWidth, SimpleSegment::new);
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
	private static <X extends IAnalysisSegment> List<X> initializeAnalysisSegments(int numberOfScans, int startScan, int segmentWidth, BiFunction<Integer, Integer, X> constructor) {

		assert numberOfScans > 0 : "The number of scans must be > 0";
		assert segmentWidth > 0 : "The segment width must be > 0";
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
		List<X> analysisSegments = new ArrayList<X>();
		for(int i = 1; i <= segmentParts; i++) {
			analysisSegments.add(constructor.apply(startScan, segmentWidth));
			startScan += segmentWidth;
		}
		/*
		 * Add the last segment to the segment list if there exists one.
		 */
		if(addLastSegment) {
			analysisSegments.add(constructor.apply(startScan, lastSegmentWidth));
		}
		return analysisSegments;
	}

	// ----------------------------------------private methods
	public static List<ChromatogramSegment> getChromatogramSegments(IChromatogram<?> chromatogram, int segmentWidth) {

		return initializeAnalysisSegments(chromatogram.getNumberOfScans(), 1, segmentWidth, (startScan, segmentWidth1) -> new ChromatogramAnalysisSegment(chromatogram, startScan, segmentWidth1));
	}

	private static final class ChromatogramAnalysisSegment extends AnalysisSegment implements ChromatogramSegment {

		private final IChromatogram<?> chromatogram;

		public ChromatogramAnalysisSegment(IChromatogram<?> chromatogram, int startScan, int segmentWidth) {

			super(startScan, segmentWidth);
			this.chromatogram = chromatogram;
		}

		@Override
		public IChromatogram<?> getChromatogram() {

			return chromatogram;
		}

		@Override
		public int getStartRetentionTime() {

			IScan scan = chromatogram.getScan(getStartScan());
			if(scan == null) {
				return -1;
			}
			return scan.getRetentionTime();
		}

		@Override
		public int getStopRetentionTime() {

			IScan scan = chromatogram.getScan(getStopScan());
			if(scan == null) {
				return -1;
			}
			return scan.getRetentionTime();
		}
	}

	private static final class SimpleSegment extends AnalysisSegment {

		public SimpleSegment(int startScan, int segmentWidth) {

			super(startScan, segmentWidth);
		}

		@Override
		public int getStartRetentionTime() {

			return -1;
		}

		@Override
		public int getStopRetentionTime() {

			return -1;
		}
	}
}
