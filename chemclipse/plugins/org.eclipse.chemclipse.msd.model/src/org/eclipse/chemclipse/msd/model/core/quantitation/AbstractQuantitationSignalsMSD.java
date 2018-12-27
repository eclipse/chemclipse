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
package org.eclipse.chemclipse.msd.model.core.quantitation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;

public abstract class AbstractQuantitationSignalsMSD implements IQuantitationSignalsMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 2941156198167427720L;
	//
	private List<IQuantitationSignal> quantitationSignals;

	public AbstractQuantitationSignalsMSD() {
		quantitationSignals = new ArrayList<IQuantitationSignal>();
	}

	@Override
	public void add(IQuantitationSignal quantitationSignalMSD) {

		quantitationSignals.add(quantitationSignalMSD);
	}

	@Override
	public void addAll(List<IQuantitationSignal> quantitationSignals) {

		this.quantitationSignals.addAll(quantitationSignals);
	}

	@Override
	public void remove(IQuantitationSignal quantitationSignalMSD) {

		quantitationSignals.remove(quantitationSignalMSD);
	}

	@Override
	public void removeAll(List<IQuantitationSignal> quantitationSignalsMSD) {

		quantitationSignals.removeAll(quantitationSignalsMSD);
	}

	@Override
	public List<Double> getSelectedIons() {

		List<Double> ions = new ArrayList<Double>();
		for(IQuantitationSignal quantitationSignal : quantitationSignals) {
			/*
			 * Only selected ions shall be returned.
			 */
			if(quantitationSignal.isUse()) {
				ions.add(quantitationSignal.getSignal());
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
	public IQuantitationSignal get(int index) {

		return quantitationSignals.get(index);
	}

	@Override
	public List<IQuantitationSignal> getList() {

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

		for(IQuantitationSignal quantitationSignal : quantitationSignals) {
			quantitationSignal.setUse(use);
		}
	}

	@Override
	public void selectSignal(double ion) {

		/**
		 * The list isn't that big. But a hash map would be better.
		 * The action will be performed not very often, hence it's ok.
		 */
		for(IQuantitationSignal quantitationSignal : quantitationSignals) {
			if(quantitationSignal.getSignal() == ion) {
				quantitationSignal.setUse(true);
			}
		}
	}
}
