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
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.core;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;

import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;

import junit.framework.TestCase;

public class FirstDerivativeSlopesTestCase extends TestCase {

	private IFirstDerivativeDetectorSlope slope;
	private IFirstDerivativeDetectorSlopes slopes;
	private IPoint p1, p2;
	private int retentionTime;
	private List<Float> abundances;
	private ITotalScanSignals signals;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		abundances = new ArrayList<Float>();
		abundances.add(21563.38028f);
		abundances.add(21718.30986f);
		abundances.add(21782.39437f);
		abundances.add(21623.94366f);
		abundances.add(21896.47887f);
		abundances.add(21348.59155f);
		abundances.add(22217.60563f);
		abundances.add(27317.60563f);
		abundances.add(45388.02817f);
		abundances.add(84380.98592f);
		abundances.add(127508.4507f);
		abundances.add(153907.7465f);
		abundances.add(153160.5634f);
		abundances.add(133292.9577f);
		abundances.add(109999.2958f);
		abundances.add(90078.16901f);
		abundances.add(75899.29577f);
		abundances.add(61307.04225f);
		abundances.add(50657.04225f);
		abundances.add(42513.38028f);
		abundances.add(37465.49296f);
		abundances.add(32107.74648f);
		abundances.add(29959.15493f);
		abundances.add(27964.78873f);
		abundances.add(26906.33803f);
		abundances.add(24441.5493f);
		abundances.add(23981.69014f);
		abundances.add(26587.3239436619f);
		abundances.add(36452.1126760563f);
		abundances.add(58026.0563380281f);
		abundances.add(81481.690140845f);
		abundances.add(97009.1549295774f);
		abundances.add(96869.7183098591f);
		abundances.add(88360.5633802816f);
		abundances.add(74409.1549295774f);
		abundances.add(62448.5915492956f);
		abundances.add(52422.5352112676f);
		abundances.add(44944.366197183f);
		abundances.add(38869.014084507f);
		abundances.add(34469.014084507f);
		signals = EasyMock.createMock(ITotalScanSignals.class);
		EasyMock.expect(signals.getStartScan()).andStubReturn(1);
		EasyMock.expect(signals.getStopScan()).andStubReturn(abundances.size());
		EasyMock.replay(signals);
		slopes = new FirstDerivativeDetectorSlopes(signals);
		for(int i = 1; i < abundances.size(); i++) {
			retentionTime = i * 1000;
			p1 = new Point(retentionTime, abundances.get(i - 1));
			p2 = new Point((i + 1) * 1000, abundances.get(i));
			slope = new FirstDerivativeDetectorSlope(p1, p2, retentionTime);
			slopes.add(slope);
		}
	}

	@Override
	protected void tearDown() throws Exception {

		abundances = null;
		p1 = null;
		p2 = null;
		slope = null;
		slopes = null;
		super.tearDown();
	}

	public IFirstDerivativeDetectorSlopes getFirstDerivativeSlopes() {

		return slopes;
	}
}
