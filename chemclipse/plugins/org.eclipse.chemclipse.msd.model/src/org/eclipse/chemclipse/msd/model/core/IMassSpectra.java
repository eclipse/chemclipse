/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 * Matthias Mailänder - add dirty handling
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.model.updates.IUpdateListener;

/**
 * This class stores a list of mass spectra.
 * 
 * @author eselmeister
 * @author <a href="mailto:alexanderkerner24@gmail.com">Alexander Kerner</a>
 */
public interface IMassSpectra extends IUpdateListener {

	/**
	 * Adds the mass spectrum to the end of the list.
	 * 
	 * @param massSpectrum
	 */
	void addMassSpectrum(IScanMSD massSpectrum);

	void addMassSpectra(Collection<? extends IScanMSD> massSpectra);

	/**
	 * Removes the mass spectrum from the list.
	 * 
	 * @param massSpectrum
	 */
	void removeMassSpectrum(IScanMSD massSpectrum);

	/**
	 * Returns the mass spectrum with the given number.<br/>
	 * Be aware, the index is 1 based and not 0 based like in a normal list.<br/>
	 * If no mass spectrum is available, null will be returned.
	 * 
	 * @param i
	 * @return IMassSpectrum
	 */
	IScanMSD getMassSpectrum(int i);

	/**
	 * Returns a list of stored mass spectra.
	 * 
	 * @return List<IMassSpectrum>
	 */
	List<IScanMSD> getList();

	/**
	 * Returns true if the list is empty.
	 * 
	 * @return boolean
	 */
	boolean isEmpty();

	/**
	 * Returns the number of stored mass spectra.
	 * 
	 * @return int
	 */
	int size();

	/**
	 * Returns the converter id.
	 * 
	 * @return String
	 */
	String getConverterId();

	/**
	 * Sets the converter id.
	 * 
	 * @param converterId
	 */
	void setConverterId(String converterId);

	/**
	 * Returns the name.
	 * 
	 * @return String
	 */
	String getName();

	/**
	 * Sets the name.
	 * 
	 * @param name
	 */
	void setName(String name);

	/**
	 * Use the update listener to react to updates.
	 * 
	 * @param updateListener
	 */
	void addUpdateListener(IUpdateListener updateListener);

	/**
	 * Remove the specified update listener.
	 * 
	 * @param updateListener
	 */
	void removeUpdateListener(IUpdateListener updateListener);

	/**
	 * This flag marks if a this list of mass spectra has been edited.
	 */
	boolean isDirty();

	/**
	 * This flag marks if this list of mass spectra has been edited.<br/>
	 * It will only be saved if it is dirty. It should save a little bit of
	 * process time.
	 */
	void setDirty(boolean isDirty);
}
