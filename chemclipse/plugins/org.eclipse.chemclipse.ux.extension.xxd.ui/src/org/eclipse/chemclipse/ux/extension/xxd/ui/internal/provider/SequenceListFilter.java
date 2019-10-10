/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.converter.model.reports.ISequenceRecord;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class SequenceListFilter extends ViewerFilter {

	private String searchText;
	private boolean caseSensitive;

	public void setSearchText(String searchText, boolean caseSensitive) {

		this.searchText = ".*" + searchText + ".*";
		this.caseSensitive = caseSensitive;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		/*
		 * Pre-Condition
		 */
		if(searchText == null || searchText.equals("")) {
			return true;
		}
		//
		//
		if(element instanceof ISequenceRecord) {
			ISequenceRecord sequenceRecord = (ISequenceRecord)element;
			//
			String dataFile = sequenceRecord.getDataFile();
			String processMethod = sequenceRecord.getProcessMethod();
			String reportMethod = sequenceRecord.getReportMethod();
			String sampleName = sequenceRecord.getSampleName();
			String substance = sequenceRecord.getSubstance();
			String description = sequenceRecord.getDescription();
			//
			if(!caseSensitive) {
				searchText = searchText.toLowerCase();
				dataFile = dataFile.toLowerCase();
				processMethod = processMethod.toLowerCase();
				reportMethod = reportMethod.toLowerCase();
				sampleName = sampleName.toLowerCase();
				substance = substance.toLowerCase();
				description = description.toLowerCase();
			}
			//
			if(dataFile.matches(searchText)) {
				return true;
			}
			//
			if(sequenceRecord.getDescription().matches(searchText)) {
				return true;
			}
			//
			if(processMethod.matches(searchText)) {
				return true;
			}
			//
			if(reportMethod.matches(searchText)) {
				return true;
			}
			//
			if(sampleName.matches(searchText)) {
				return true;
			}
			//
			if(substance.matches(searchText)) {
				return true;
			}
			//
			if(description.matches(searchText)) {
				return true;
			}
		}
		//
		return false;
	}
}
