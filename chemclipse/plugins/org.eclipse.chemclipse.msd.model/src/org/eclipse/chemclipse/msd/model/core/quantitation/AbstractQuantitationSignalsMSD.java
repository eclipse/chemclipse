/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractQuantitationSignalsMSD implements IQuantitationSignalsMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 2941156198167427720L;
	//
	private List<IQuantitationSignalMSD> quantitationSignals;

	public AbstractQuantitationSignalsMSD() {
		quantitationSignals = new ArrayList<IQuantitationSignalMSD>();
	}

	@Override
	public void add(IQuantitationSignalMSD quantitationSignalMSD) {

		quantitationSignals.add(quantitationSignalMSD);
	}

	@Override
	public void addAll(List<IQuantitationSignalMSD> quantitationSignals) {

		this.quantitationSignals.addAll(quantitationSignals);
	}

	@Override
	public void remove(IQuantitationSignalMSD quantitationSignalMSD) {

		quantitationSignals.remove(quantitationSignalMSD);
	}

	@Override
	public void removeAll(List<IQuantitationSignalMSD> quantitationSignalsMSD) {

		quantitationSignals.removeAll(quantitationSignalsMSD);
	}

	@Override
	public List<Double> getSelectedIons() {

		List<Double> ions = new ArrayList<Double>();
		for(IQuantitationSignalMSD quantitationSignal : quantitationSignals) {
			/*
			 * Only selected ions shall be returned.
			 */
			if(quantitationSignal.isUse()) {
				ions.add(quantitationSignal.getIon());
			}
		}
		return ions;
	}

	@Override
	public void clear() {

		quantitationSignals.clear();
	}

	@Override
	public int size() {

		return quantitationSignals.size();
	}

	@Override
	public IQuantitationSignalMSD get(int index) {

		return quantitationSignals.get(index);
	}

	@Override
	public List<IQuantitationSignalMSD> getList() {

		return quantitationSignals;
	}

	@Override
	public void deselectAllSignals() {

		setSignalsUse(false);
	}

	@Override
	public void selectAllSignals() {

		setSignalsUse(true);
	}

	private void setSignalsUse(boolean use) {

		for(IQuantitationSignalMSD quantitationSignal : quantitationSignals) {
			quantitationSignal.setUse(use);
		}
	}

	@Override
	public void selectSignal(double ion) {

		/**
		 * The list isn't that big. But a hash map would be better.
		 * The action will be performed not very often, hence it's ok.
		 */
		for(IQuantitationSignalMSD quantitationSignal : quantitationSignals) {
			if(quantitationSignal.getIon() == ion) {
				quantitationSignal.setUse(true);
			}
		}
	}
}
