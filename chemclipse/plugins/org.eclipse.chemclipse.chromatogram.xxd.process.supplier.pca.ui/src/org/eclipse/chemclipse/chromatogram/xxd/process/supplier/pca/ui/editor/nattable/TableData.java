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
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IRetentionTime;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.PcaEditor;

public class TableData {

	private PcaEditor pcaEditor;
	private List<IRetentionTime> retentionTimes = new ArrayList<>();
	private List<ISample> samples = new ArrayList<>();

	public TableData(PcaEditor pcaEditor) {
		this.pcaEditor = pcaEditor;
	}

	/**
	 *
	 * @return retention times
	 */
	public List<IRetentionTime> getRetentionTimes() {

		return retentionTimes;
	}

	/**
	 * @return sorted samples by groups, this List contains instances of class Group
	 */
	public List<ISample> getSamples() {

		return samples;
	}

	public void update() {

		Optional<ISamples> result = pcaEditor.getSamples();
		if(!result.isPresent()) {
			return;
		}
		ISamples resultSamples = result.get();
		/*
		 * remove old data
		 */
		samples.clear();
		/*
		 * copy data and insert object ISample and IGroup and sort this object by group name
		 */
		samples.addAll(resultSamples.getSampleList().stream().filter(s -> s.isSelected()).collect(Collectors.toList()));
		samples.addAll(resultSamples.getGroupList().stream().filter(s -> s.isSelected()).collect(Collectors.toList()));
		PcaUtils.sortSampleListByName(samples);
		PcaUtils.sortSampleListByGroup(samples);
		/*
		 * set retention time
		 */
		retentionTimes = resultSamples.getExtractedRetentionTimes();
		/*
		 * Set peaks names
		 */
	}
}
