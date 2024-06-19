/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.implementation.Peak;
import org.eclipse.chemclipse.model.implementation.PeakModel;
import org.eclipse.chemclipse.model.implementation.Scan;

import junit.framework.TestCase;

public class PeakMerger_1_Test extends TestCase {

	private IPeak peakMerged;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		List<IPeak> peaks = new ArrayList<>();
		peaks.add(createPeak1());
		peaks.add(createPeak2());
		peaks.add(createPeak3());
		boolean mergeIdentificationTargets = true;
		peakMerged = PeakMerger.mergePeaks(peaks, mergeIdentificationTargets);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertNotNull(peakMerged);
	}

	public void test2() {

		assertEquals(3, peakMerged.getTargets().size());
	}

	public void test3() {

		Set<IIdentificationTarget> targets = peakMerged.getTargets();
		Set<String> names = targets.stream().map(t -> t.getLibraryInformation().getName()).collect(Collectors.toSet());
		assertTrue(names.contains("Peak A"));
		assertTrue(names.contains("Peak B"));
		assertTrue(names.contains("Peak C"));
	}

	public void test4() {

		assertEquals(3032172, peakMerged.getPeakModel().getStartRetentionTime());
	}

	public void test5() {

		assertEquals(3041420, peakMerged.getPeakModel().getStopRetentionTime());
	}

	public void test6() {

		assertEquals(0.0f, peakMerged.getPeakModel().getBackgroundAbundance(3032172));
	}

	public void test7() {

		assertEquals(0.0f, peakMerged.getPeakModel().getBackgroundAbundance(3041420));
	}

	public void test8() {

		assertEquals(302275.1f, peakMerged.getPeakModel().getPeakMaximum().getTotalSignal());
	}

	public void test9() {

		assertEquals(0.0f, peakMerged.getPeakModel().getPeakAbundance(3032172));
	}

	public void test10() {

		assertEquals(22269.988f, peakMerged.getPeakModel().getPeakAbundance(3032883));
	}

	public void test11() {

		assertEquals(68642.74f, peakMerged.getPeakModel().getPeakAbundance(3033595));
	}

	public void test12() {

		assertEquals(156971.8f, peakMerged.getPeakModel().getPeakAbundance(3034306));
	}

	public void test13() {

		assertEquals(250912.84f, peakMerged.getPeakModel().getPeakAbundance(3035017));
	}

	public void test14() {

		assertEquals(302275.1f, peakMerged.getPeakModel().getPeakAbundance(3035729));
	}

	public void test15() {

		assertEquals(262477.62f, peakMerged.getPeakModel().getPeakAbundance(3036440));
	}

	public void test16() {

		assertEquals(186973.06f, peakMerged.getPeakModel().getPeakAbundance(3037151));
	}

	public void test17() {

		assertEquals(113701.43f, peakMerged.getPeakModel().getPeakAbundance(3037863));
	}

	public void test18() {

		assertEquals(62131.695f, peakMerged.getPeakModel().getPeakAbundance(3038574));
	}

	public void test19() {

		assertEquals(35237.76f, peakMerged.getPeakModel().getPeakAbundance(3039286));
	}

	public void test20() {

		assertEquals(17581.832f, peakMerged.getPeakModel().getPeakAbundance(3039997));
	}

	public void test21() {

		assertEquals(9934.551f, peakMerged.getPeakModel().getPeakAbundance(3040708));
	}

	public void test22() {

		assertEquals(4159.5664f, peakMerged.getPeakModel().getPeakAbundance(3041420));
	}

	public void test23() {

		assertEquals(0.0d, peakMerged.getIntegratedArea());
	}

	private IPeak createPeak1() {

		float totalSignal = 239;
		float startBackgroundAbundance = 0;
		float stopBackgroundAbundance = 0;
		//
		TreeMap<Integer, Float> retentionTimeIntensityMap = new TreeMap<>();
		retentionTimeIntensityMap.put(3032172, 0.0f);
		retentionTimeIntensityMap.put(3032883, 61.0f);
		retentionTimeIntensityMap.put(3033595, 118.0f);
		retentionTimeIntensityMap.put(3034306, 60.0f);
		retentionTimeIntensityMap.put(3035017, 0.0f);
		//
		IPeak peak = createPeak(totalSignal, retentionTimeIntensityMap, startBackgroundAbundance, stopBackgroundAbundance);
		peak.getTargets().add(IIdentificationTarget.createDefaultTarget("Peak A", "0-00-0", "Merge"));
		//
		return peak;
	}

	private IPeak createPeak2() {

		float totalSignal = 302275.089f;
		float startBackgroundAbundance = 0;
		float stopBackgroundAbundance = 0;
		//
		TreeMap<Integer, Float> retentionTimeIntensityMap = new TreeMap<>();
		retentionTimeIntensityMap.put(3032172, 0.0f);
		retentionTimeIntensityMap.put(3032883, 4508.001f);
		retentionTimeIntensityMap.put(3033595, 13895.004f);
		retentionTimeIntensityMap.put(3034306, 31775.01f);
		retentionTimeIntensityMap.put(3035017, 50791.016f);
		retentionTimeIntensityMap.put(3035729, 61188.016f);
		retentionTimeIntensityMap.put(3036440, 53132.016f);
		retentionTimeIntensityMap.put(3037151, 37848.012f);
		retentionTimeIntensityMap.put(3037863, 23016.006f);
		retentionTimeIntensityMap.put(3038574, 12577.004f);
		retentionTimeIntensityMap.put(3039286, 7133.002f);
		retentionTimeIntensityMap.put(3039997, 3559.001f);
		retentionTimeIntensityMap.put(3040708, 2011.001f);
		retentionTimeIntensityMap.put(3041420, 842.0f);
		//
		IPeak peak = createPeak(totalSignal, retentionTimeIntensityMap, startBackgroundAbundance, stopBackgroundAbundance);
		peak.getTargets().add(IIdentificationTarget.createDefaultTarget("Peak B", "0-00-0", "Merge"));
		//
		return peak;
	}

	private IPeak createPeak3() {

		float totalSignal = 160227.921f;
		float startBackgroundAbundance = 0;
		float stopBackgroundAbundance = 0;
		//
		TreeMap<Integer, Float> retentionTimeIntensityMap = new TreeMap<>();
		retentionTimeIntensityMap.put(3032883, 0.0f);
		retentionTimeIntensityMap.put(3033595, 1424.999f);
		retentionTimeIntensityMap.put(3034306, 6588.996f);
		retentionTimeIntensityMap.put(3035017, 15174.991f);
		retentionTimeIntensityMap.put(3035729, 27185.988f);
		retentionTimeIntensityMap.put(3036440, 34038.984f);
		retentionTimeIntensityMap.put(3037151, 33438.984f);
		retentionTimeIntensityMap.put(3037863, 23870.988f);
		retentionTimeIntensityMap.put(3038574, 11007.995f);
		retentionTimeIntensityMap.put(3039286, 4556.998f);
		retentionTimeIntensityMap.put(3039997, 2042.999f);
		retentionTimeIntensityMap.put(3040708, 895.999f);
		//
		IPeak peak = createPeak(totalSignal, retentionTimeIntensityMap, startBackgroundAbundance, stopBackgroundAbundance);
		peak.getTargets().add(IIdentificationTarget.createDefaultTarget("Peak C", "0-00-0", "Merge"));
		//
		return peak;
	}

	private IPeak createPeak(float totalSignal, TreeMap<Integer, Float> retentionTimeIntensityMap, float startBackgroundAbundance, float stopBackgroundAbundance) {

		IScan peakMaximum = new Scan(totalSignal);
		IPeakIntensityValues peakIntensityValues = PeakMerger.create(retentionTimeIntensityMap);
		IPeakModel peakModel = new PeakModel(peakMaximum, peakIntensityValues, startBackgroundAbundance, stopBackgroundAbundance);
		peakModel.setStrictModel(true);
		//
		return new Peak(peakModel);
	}
}
