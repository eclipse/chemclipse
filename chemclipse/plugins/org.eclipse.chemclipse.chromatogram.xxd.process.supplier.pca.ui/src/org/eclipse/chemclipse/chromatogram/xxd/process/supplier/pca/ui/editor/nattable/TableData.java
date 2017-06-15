/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.PcaEditor;

public class TableData {

	private PcaEditor pcaEditor;
	private List<Integer> retentionTimes;
	private List<ISample> samples;

	public TableData(PcaEditor pcaEditor) {
		this.pcaEditor = pcaEditor;
		samples = new ArrayList<>();
		retentionTimes = new ArrayList<>();
	}

	/**
	 *
	 * @return retention times
	 */
	public List<Integer> getRetentionTimes() {

		return retentionTimes;
	}

	/**
	 * @return sorted samples by groups, this List contains instances of class Group
	 */
	public List<ISample> getSamples() {

		return samples;
	}

	public void update() {

		IPcaResults pcaResults = pcaEditor.getPcaResults();
		if(pcaResults == null) {
			return;
		}
		/*
		 * remove old data
		 */
		this.samples.clear();
		retentionTimes.clear();
		/*
		 * copy data and insert object SampleGroupMean and sort this object by group
		 */
		List<ISample> newSamples = PcaUtils.insertGroup(pcaResults.getSampleList());
		PcaUtils.sortSampleListByGroup(newSamples);
		this.samples.addAll(newSamples);
		retentionTimes.addAll(pcaEditor.getPcaResults().getExtractedRetentionTimes());
	}
}
