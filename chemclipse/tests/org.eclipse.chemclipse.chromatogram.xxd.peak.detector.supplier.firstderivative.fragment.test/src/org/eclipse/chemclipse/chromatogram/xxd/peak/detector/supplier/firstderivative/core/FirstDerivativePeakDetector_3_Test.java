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

import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.RawPeak;

import junit.framework.TestCase;

/**
 * peakDetectorSettings.getThreshold() is MEDIUM > threshold = 0.05d;
 * WindowSize.SCANS_5
 * 
 * @author eselmeister
 */
public class FirstDerivativePeakDetector_3_Test extends TestCase {

	private PeakDetectorMSD firstDerivativePeakDetector;
	private Class<?> firstDerivativePeakDetectorClass;
	private Method method;
	private IRawPeak rawPeak;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		firstDerivativePeakDetector = new PeakDetectorMSD();
		firstDerivativePeakDetectorClass = BasePeakDetector.class;
	}

	@Override
	protected void tearDown() throws Exception {

		firstDerivativePeakDetector = null;
		firstDerivativePeakDetectorClass = null;
		super.tearDown();
	}

	public void testIsValidRawPeak_1() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		rawPeak = new RawPeak(25, 26, 27);
		Boolean result;
		method = firstDerivativePeakDetectorClass.getDeclaredMethod("isValidRawPeak", new Class[]{IRawPeak.class});
		method.setAccessible(true);
		result = (Boolean)method.invoke(firstDerivativePeakDetector, new Object[]{rawPeak});
		assertEquals("detectPeakStart", Boolean.valueOf(true), result);
	}

	public void testIsValidRawPeak_2() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		rawPeak = new RawPeak(25, 25, 26);
		Boolean result;
		method = firstDerivativePeakDetectorClass.getDeclaredMethod("isValidRawPeak", new Class[]{IRawPeak.class});
		method.setAccessible(true);
		result = (Boolean)method.invoke(firstDerivativePeakDetector, new Object[]{rawPeak});
		assertEquals("detectPeakStart", Boolean.valueOf(false), result);
	}
}
