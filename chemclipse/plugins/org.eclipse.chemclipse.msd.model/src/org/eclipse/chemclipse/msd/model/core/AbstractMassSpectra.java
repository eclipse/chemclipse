/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.model.updates.IUpdateListener;

/**
 * @author eselmeister
 * @author <a href="mailto:alexanderkerner24@gmail.com">Alexander Kerner</a>
 */
public abstract class AbstractMassSpectra implements IMassSpectra {

	private final List<IScanMSD> massSpectra;
	private String converterId = "";
	private String name = "";
	private final List<IUpdateListener> updateListeners;

	public AbstractMassSpectra(List<IScanMSD> massSpectra) {
		this.massSpectra = massSpectra;
		updateListeners = new ArrayList<IUpdateListener>();
	}

	/**
	 * Initialize mass spectra and create a new internal mass spectra list.
	 */
	public AbstractMassSpectra() {
		this(new ArrayList<>());
	}

	@Override
	public void addMassSpectrum(IScanMSD massSpectrum) {

		if(massSpectrum != null) {
			massSpectra.add(massSpectrum);
		}
	}

	@Override
	public void addMassSpectra(Collection<? extends IScanMSD> massSpectra) {

		for(IScanMSD massSpectrum : massSpectra) {
			addMassSpectrum(massSpectrum);
		}
	}

	@Override
	public void removeMassSpectrum(IScanMSD massSpectrum) {

		if(massSpectrum != null) {
			massSpectra.remove(massSpectrum);
		}
	}

	@Override
	public IScanMSD getMassSpectrum(int i) {

		IScanMSD massSpectrum = null;
		if(i > 0 && i <= massSpectra.size()) {
			massSpectrum = massSpectra.get(--i);
		}
		return massSpectrum;
	}

	@Override
	public int size() {

		return massSpectra.size();
	}

	@Override
	public List<IScanMSD> getList() {

		return massSpectra;
	}

	@Override
	public String getConverterId() {

		return converterId;
	}

	@Override
	public void setConverterId(String converterId) {

		this.converterId = converterId;
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
	public void update() {

		for(IUpdateListener updateListener : updateListeners) {
			updateListener.update();
		}
	}

	@Override
	public void addUpdateListener(IUpdateListener updateListener) {

		updateListeners.add(updateListener);
	}

	@Override
	public void removeUpdateListener(IUpdateListener updateListener) {

		updateListeners.remove(updateListener);
	}
}
