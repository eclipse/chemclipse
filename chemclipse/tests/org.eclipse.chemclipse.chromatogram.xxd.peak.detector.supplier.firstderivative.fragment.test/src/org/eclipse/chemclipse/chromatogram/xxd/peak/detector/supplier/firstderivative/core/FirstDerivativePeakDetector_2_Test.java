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
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

/**
 * peakDetectorSettings.getThreshold() is MEDIUM > threshold = 0.05d;
 * WindowSize.SCANS_5
 * 
 * @author eselmeister
 */
public class FirstDerivativePeakDetector_2_Test extends FirstDerivativeSlopesTestCase {

	private IFirstDerivativeDetectorSlopes slopes;
	private PeakDetectorMSD firstDerivativePeakDetector;
	private Class<?> firstDerivativePeakDetectorClass;
	private Method method;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		firstDerivativePeakDetector = new PeakDetectorMSD();
		firstDerivativePeakDetectorClass = BasePeakDetector.class;
		slopes = getFirstDerivativeSlopes();
		slopes.calculateMovingAverage(WindowSize.WIDTH_5);
	}

	@Override
	protected void tearDown() throws Exception {

		firstDerivativePeakDetector = null;
		firstDerivativePeakDetectorClass = null;
		slopes = null;
		super.tearDown();
	}

	public void testSize_1() {

		assertEquals("Size", 39, slopes.size());
	}

	public void testDetectPeakStart_1() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		Integer result;
		method = firstDerivativePeakDetectorClass.getDeclaredMethod("detectPeakStart", new Class[]{IFirstDerivativeDetectorSlopes.class, Integer.TYPE, Integer.TYPE, Double.TYPE});
		method.setAccessible(true);
		result = (Integer)method.invoke(firstDerivativePeakDetector, new Object[]{slopes, 1, 0, 0.05d});
		/*
		 * The peak starts at scan 4 and has a scan offset of 0 (means starts at
		 * scan 1).
		 */
		assertEquals("detectPeakStart", Integer.valueOf(4), result);
	}

	public void testDetectPeakMaximum_1() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		Integer result;
		method = firstDerivativePeakDetectorClass.getDeclaredMethod("detectPeakMaximum", new Class[]{IFirstDerivativeDetectorSlopes.class, Integer.TYPE, Integer.TYPE});
		method.setAccessible(true);
		/*
		 * Start the search at the peak beginning at scan 4 and has a scan
		 * offset of 0 (means starts at scan 1).
		 */
		result = (Integer)method.invoke(firstDerivativePeakDetector, new Object[]{slopes, 4, 0});
		/*
		 * The peak maximum is at scan 12.
		 */
		assertEquals("detectPeakMaximum", Integer.valueOf(12), result);
	}

	public void testDetectPeakStop_1() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		Integer result;
		method = firstDerivativePeakDetectorClass.getDeclaredMethod("detectPeakStop", new Class[]{IFirstDerivativeDetectorSlopes.class, Integer.TYPE, Integer.TYPE});
		method.setAccessible(true);
		/*
		 * Start the search at the peak maximum at scan 12 and has a scan offset
		 * of 0 (means starts at scan 1).
		 */
		result = (Integer)method.invoke(firstDerivativePeakDetector, new Object[]{slopes, 12, 0});
		/*
		 * The peak stop is at scan 26.
		 */
		assertEquals("detectPeakStop", Integer.valueOf(26), result);
	}
}
