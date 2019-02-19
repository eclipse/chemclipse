/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.easymock.EasyMock;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.implementation.Peaks;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

public class PeakExtractionSupportTest4 {

	@Test
	public void testExtractPeakData() {

		PeakExtractionSupport peakExtractionSupport = new PeakExtractionSupport(1);
		Map<IDataInputEntry, IPeaks> dataInput = new HashMap<>();
		putDataToMap("Sample1", "Group1", new int[]{1, 2}, new double[]{3.2, 3.4}, dataInput);
		putDataToMap("Sample2", "Group2", new int[]{1, 2}, new double[]{7, 10}, dataInput);
		Samples samples = peakExtractionSupport.extractPeakData(dataInput, new NullProgressMonitor());
	}

	protected void putDataToMap(String name, String groupName, int[] retentionTimes, double[] integrationArea, Map<IDataInputEntry, IPeaks> map) {

		map.put(createDataInputEntry(name, groupName), cretePeaks(retentionTimes, integrationArea));
	}

	private IDataInputEntry createDataInputEntry(String name, String groupName) {

		IDataInputEntry entry = EasyMock.createMock(IDataInputEntry.class);
		EasyMock.expect(entry.getName()).andStubReturn(name);
		EasyMock.expect(entry.getGroupName()).andStubReturn(groupName);
		EasyMock.replay(entry);
		return entry;
	}

	private IPeaks cretePeaks(int[] retentionTimes, double[] integrationArea) {

		IPeaks peaks = new Peaks();
		for(int i = 0; i < integrationArea.length; i++) {
			IPeakModelMSD peakModel = EasyMock.createMock(IPeakModelMSD.class);
			EasyMock.expect(peakModel.getRetentionTimeAtPeakMaximum()).andStubReturn(retentionTimes[i]);
			EasyMock.replay(peakModel);
			IChromatogramPeakMSD peak = EasyMock.createMock(IChromatogramPeakMSD.class);
			EasyMock.expect(peak.getPeakModel()).andStubReturn(peakModel);
			EasyMock.expect(peak.getIntegratedArea()).andStubReturn(integrationArea[i]);
			EasyMock.expect(peak.getTargets()).andStubReturn(new HashSet<>());
			EasyMock.replay(peak);
			peaks.addPeak(peak);
		}
		return peaks;
	}
}
