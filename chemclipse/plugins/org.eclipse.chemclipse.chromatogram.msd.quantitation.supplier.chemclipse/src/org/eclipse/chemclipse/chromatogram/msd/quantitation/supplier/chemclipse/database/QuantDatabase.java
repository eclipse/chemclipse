/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.QuantitationCompoundAlreadyExistsException;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;

public class QuantDatabase implements IQuantDatabase {

	/**
	 * Renew this UUID on change.
	 */
	private static final long serialVersionUID = 6356089588484376410L;
	//
	private List<IQuantitationCompound> quantitationCompounds;
	private Map<IQuantitationCompound, List<IQuantitationPeak>> quantitationCompoundPeaks;

	public QuantDatabase() {
		quantitationCompounds = new ArrayList<IQuantitationCompound>();
		quantitationCompoundPeaks = new HashMap<IQuantitationCompound, List<IQuantitationPeak>>();
	}

	@Override
	public IQuantitationCompound getQuantitationCompound(String name) {

		for(IQuantitationCompound quantitationCompound : quantitationCompounds) {
			if(quantitationCompound.getName().equals(name)) {
				return quantitationCompound;
			}
		}
		return null;
	}

	@Override
	public void addQuantitationCompound(IQuantitationCompound quantitationCompound) throws QuantitationCompoundAlreadyExistsException {

		String name = quantitationCompound.getName();
		if(getQuantitationCompound(name) == null) {
			quantitationCompounds.add(quantitationCompound);
		} else {
			throw new QuantitationCompoundAlreadyExistsException();
		}
	}

	@Override
	public List<IQuantitationCompound> getQuantitationCompounds() {

		return quantitationCompounds;
	}

	@Override
	public List<String> getQuantitationCompoundNames() {

		List<String> names = new ArrayList<String>();
		for(IQuantitationCompound quantitationCompound : quantitationCompounds) {
			names.add(quantitationCompound.getName());
		}
		return names;
	}

	@Override
	public String getQuantitationCompoundConcentrationUnit(String name) {

		IQuantitationCompound quantitationCompoundMSD = getQuantitationCompound(name);
		if(quantitationCompoundMSD != null) {
			return quantitationCompoundMSD.getConcentrationUnit();
		} else {
			return "";
		}
	}

	@Override
	public long countQuantitationCompounds() {

		return quantitationCompounds.size();
	}

	@Override
	public boolean isQuantitationCompoundAlreadyAvailable(String name) {

		IQuantitationCompound quantitationCompoundMSD = getQuantitationCompound(name);
		if(quantitationCompoundMSD != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void deleteQuantitationCompound(IQuantitationCompound quantitationCompound) {

		quantitationCompounds.remove(quantitationCompound);
	}

	@Override
	public void deleteQuantitationCompound(List<IQuantitationCompound> quantitationCompounds) {

		this.quantitationCompounds.removeAll(quantitationCompounds);
	}

	@Override
	public void deleteAllQuantitationCompounds() {

		quantitationCompounds.clear();
	}

	@Override
	public List<IQuantitationPeak> getQuantitationPeaks(IQuantitationCompound quantitationCompound) {

		List<IQuantitationPeak> quantitationPeaks = quantitationCompoundPeaks.get(quantitationCompound);
		if(quantitationPeaks == null) {
			quantitationPeaks = new ArrayList<IQuantitationPeak>();
			quantitationCompoundPeaks.put(quantitationCompound, quantitationPeaks);
		}
		return quantitationPeaks;
	}

	@Override
	public void deleteQuantitationPeakDocument(IQuantitationCompound quantitationCompound, IQuantitationPeak quantitationPeak) {

		List<IQuantitationPeak> quantitationPeakList = quantitationCompoundPeaks.get(quantitationCompound);
		if(quantitationPeakList != null) {
			quantitationPeakList.remove(quantitationPeak);
		}
	}

	@Override
	public void deleteQuantitationPeakDocuments(IQuantitationCompound quantitationCompound, Set<IQuantitationPeak> quantitationPeaks) {

		List<IQuantitationPeak> quantitationPeakList = quantitationCompoundPeaks.get(quantitationCompound);
		if(quantitationPeakList != null) {
			quantitationPeakList.removeAll(quantitationPeaks);
		}
	}
}