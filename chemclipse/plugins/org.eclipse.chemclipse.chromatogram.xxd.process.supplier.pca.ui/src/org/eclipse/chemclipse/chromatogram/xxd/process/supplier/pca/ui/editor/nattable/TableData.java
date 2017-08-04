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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.PcaEditor;

public class TableData {

	private List<Boolean> isSelectedRetentionTimes = new ArrayList<>();
	private PcaEditor pcaEditor;
	private List<String> peaksNames = new ArrayList<>();
	private List<Integer> retentionTimes = new ArrayList<>();
	private List<ISample> samples = new ArrayList<>();

	public TableData(PcaEditor pcaEditor) {
		this.pcaEditor = pcaEditor;
	}

	public List<String> getPeaksNames() {

		return peaksNames;
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

	public List<Boolean> isSelectedRetentionTimes() {

		return isSelectedRetentionTimes;
	}

	public void update() {

		Optional<IPcaResults> result = pcaEditor.getPcaResults();
		if(!result.isPresent()) {
			return;
		}
		IPcaResults pcaResults = result.get();
		/*
		 * remove old data
		 */
		this.samples.clear();
		isSelectedRetentionTimes = pcaResults.isSelectedRetentionTimes();
		/*
		 * copy data and insert object ISample and IGroup and sort this object by group name
		 */
		samples.addAll(pcaResults.getSampleList().stream().filter(s -> s.isSelected()).collect(Collectors.toList()));
		samples.addAll(pcaResults.getGroupList().stream().filter(s -> s.isSelected()).collect(Collectors.toList()));
		PcaUtils.sortSampleListByName(samples);
		PcaUtils.sortSampleListByGroup(samples);
		/*
		 * set retention time
		 */
		retentionTimes = pcaResults.getExtractedRetentionTimes();
		/*
		 * Set peaks names
		 */
		peaksNames.clear();
		List<TreeSet<String>> names = PcaUtils.getPeaksNames(pcaResults.getSampleList());
		for(int i = 0; i < names.size(); i++) {
			StringBuilder builder = new StringBuilder("");
			TreeSet<String> set = names.get(i);
			for(Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
				String name = iterator.next();
				builder.append(name);
				if(iterator.hasNext()) {
					builder.append("; ");
				}
			}
			peaksNames.add(builder.toString());
		}
	}
}
