/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class SampleQuantReport implements ISampleQuantReport {

	private String name;
	private String dataName;
	private String date;
	private String operator;
	private String miscInfo;
	//
	private String pathChromatogramEdited = "";
	private String pathChromatogramOriginal = "";
	private List<ISampleQuantSubstance> sampleQuantSubstances;

	public SampleQuantReport() {
		sampleQuantSubstances = new ArrayList<ISampleQuantSubstance>();
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String getDataName() {

		return dataName;
	}

	@Override
	public void setDataName(String dataName) {

		this.dataName = dataName;
	}

	@Override
	public String getDate() {

		return date;
	}

	@Override
	public void setDate(String date) {

		this.date = date;
	}

	@Override
	public String getOperator() {

		return operator;
	}

	@Override
	public void setOperator(String operator) {

		this.operator = operator;
	}

	@Override
	public String getMiscInfo() {

		return miscInfo;
	}

	@Override
	public void setMiscInfo(String miscInfo) {

		this.miscInfo = miscInfo;
	}

	@Override
	public String getPathChromatogramOriginal() {

		return pathChromatogramOriginal;
	}

	@Override
	public void setPathChromatogramOriginal(String pathChromatogramOriginal) {

		this.pathChromatogramOriginal = pathChromatogramOriginal;
	}

	@Override
	public String getPathChromatogramEdited() {

		return pathChromatogramEdited;
	}

	@Override
	public void setPathChromatogramEdited(String pathChromatogramEdited) {

		this.pathChromatogramEdited = pathChromatogramEdited;
	}

	@Override
	public void setMinMatchQuality(double minMatchQuality) {

		for(ISampleQuantSubstance sampleQuantSubstance : sampleQuantSubstances) {
			sampleQuantSubstance.setMinMatchQuality(minMatchQuality);
		}
	}

	@Override
	public List<ISampleQuantSubstance> getSampleQuantSubstances() {

		return sampleQuantSubstances;
	}

	@Override
	public List<ISampleQuantSubstance> getSampleQuantSubstances(String searchTerms) {

		if(searchTerms == null || searchTerms == "") {
			return sampleQuantSubstances;
		}
		//
		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		boolean isCaseSensitive = preferences.getBoolean(PreferenceSupplier.P_SAMPLEQUANT_SEARCH_CASE_SENSITIVE, PreferenceSupplier.DEF_SAMPLEQUANT_SEARCH_CASE_SENSITIVE);
		if(!isCaseSensitive) {
			searchTerms = searchTerms.toLowerCase();
		}
		//
		List<ISampleQuantSubstance> sampleQuantSubstancesSearch = new ArrayList<ISampleQuantSubstance>();
		String[] searchItems = searchTerms.split(" ");
		//
		for(ISampleQuantSubstance sampleQuantSubstance : sampleQuantSubstances) {
			/*
			 * Check whether it is case sensitive.
			 */
			String name;
			if(isCaseSensitive) {
				name = sampleQuantSubstance.getName();
			} else {
				name = sampleQuantSubstance.getName().toLowerCase();
			}
			String casNumber = sampleQuantSubstance.getCasNumber();
			//
			for(String searchItem : searchItems) {
				if(name.contains(searchItem) || casNumber.contains(searchItem)) {
					sampleQuantSubstancesSearch.add(sampleQuantSubstance);
					continue;
				}
			}
		}
		//
		return sampleQuantSubstancesSearch;
	}
}
