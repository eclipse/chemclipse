/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.easymock.EasyMock;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.DataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PeakSampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.implementation.Peaks;
import org.eclipse.chemclipse.model.implementation.Scan;
import org.eclipse.chemclipse.model.statistics.RetentionTime;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class TestSupport {

	public TestSupport() {

	}

	public static void putScanDataToMap(String name, String groupName, int[] retentionTimes, float[] totalSignal, Map<IDataInputEntry, Collection<IScan>> map) {

		map.put(createDataInputEntry(name, groupName), createScans(retentionTimes, totalSignal));
	}

	public static void putPeakDataToMap(String name, String groupName, int[] retentionTimes, double[] integrationArea, Map<IDataInputEntry, IPeaks<?>> map) {

		map.put(createDataInputEntry(name, groupName), createPeaks(retentionTimes, integrationArea));
	}

	public static Samples createSamples2() {

		String[][] samples = {{"Sample1", "Group1"}, //
				{"Sample2", "Group1"}, //
				{"Sample3", "Group2"}, //
				{"Sample4", "Group2"}};
		int[] variables = {10, 20, 30, 40, 50};
		double[][] data = {{1.2, 3.5, 4.6, 0.9, 9.8}, //
				{1.3, 2.5, 5.6, 0.5, 10.8}, //
				{0.5, 3.2, 9.0, 1.9, 7.8}, //
				{1.4, 3.9, 4.8, 0.8, 9.7}};
		return createSamples(samples, variables, data);
	}

	public static Samples createSamples3() {

		String[][] samples = {{"Sample1", "Group1"}, //
				{"Sample2", "Group1"}, //
				{"Sample3", "Group2"}, //
				{"Sample4", "Group2"}};
		int[] variables = {10, 20, 30, 40, 50};
		double[][] data = {{1.2, 3.5, 4.6, 0.9, Double.NaN}, //
				{1.3, 2.5, 5.6, 0.5, Double.NaN}, //
				{0.5, 3.2, 9.0, 1.9, Double.NaN}, //
				{1.4, 3.9, Double.NaN, 0.8, 9.7}};
		return createSamples(samples, variables, data);
	}

	public static Samples createSamples(String[][] samples, int[] variables, double[][] data) {

		List<IDataInputEntry> dataInputEntries = new ArrayList<>();
		for(String[] sample : samples) {
			dataInputEntries.add(createDataInputEntry(sample[0], sample[1]));
		}
		//
		List<Sample> samplesList = new ArrayList<>();
		dataInputEntries.forEach(d -> samplesList.add(new Sample(d.getName(), d.getGroupName())));
		Samples samplesOutput = new Samples(samplesList);
		//
		Arrays.stream(variables).forEach(v -> samplesOutput.getVariables().add(new RetentionTime(v)));
		int i = 0;
		for(double[] sampleData : data) {
			final int iFinal = i;
			IntStream.range(0, variables.length).forEach(variable -> //
			samplesOutput.getSampleList().get(iFinal).getSampleData().add(new PeakSampleData(sampleData[variable], null)));
			i++;
		}
		return samplesOutput;
	}

	public static Samples createSamples(String[][] samples, boolean[] selected, int[] variables, double[][] data) {

		Samples samplesOutput = createSamples(samples, variables, data);
		for(int i = 0; i < selected.length; i++) {
			samplesOutput.getSampleList().get(i).setSelected(selected[i]);
		}
		return samplesOutput;
	}

	private static IDataInputEntry createDataInputEntry(String name, String groupName) {

		IDataInputEntry d = new DataInputEntry("/" + name);
		d.setGroupName(groupName);
		return d;
	}

	private static IPeaks<?> createPeaks(int[] retentionTimes, double[] integrationArea) {

		IPeaks<?> peaks = new Peaks();
		for(int i = 0; i < integrationArea.length; i++) {
			IPeakModelMSD peakModel = EasyMock.createMock(IPeakModelMSD.class);
			EasyMock.expect(peakModel.getRetentionTimeAtPeakMaximum()).andStubReturn(retentionTimes[i]);
			IScanMSD scan = EasyMock.createMock(IScanMSD.class);
			EasyMock.expect(peakModel.getPeakMaximum()).andStubReturn(scan);
			EasyMock.replay(peakModel);
			IChromatogramPeakMSD peak = EasyMock.createMock(IChromatogramPeakMSD.class);
			EasyMock.expect(peak.getPeakModel()).andStubReturn(peakModel);
			EasyMock.expect(peak.getIntegratedArea()).andStubReturn(integrationArea[i]);
			EasyMock.expect(peak.getTargets()).andStubReturn(new HashSet<>());
			EasyMock.expect(peak.getClassifier()).andStubReturn(new HashSet<>());
			EasyMock.replay(peak);
			peaks.addPeak(peak);
		}
		return peaks;
	}

	private static List<IScan> createScans(int[] retentionTimes, float[] totalSignal) {

		List<IScan> scans = new ArrayList<>();
		for(int i = 0; i < totalSignal.length; i++) {
			IScan scan = new Scan(totalSignal[i]);
			scan.setRetentionTime(retentionTimes[i]);
			scans.add(scan);
		}
		return scans;
	}
}
