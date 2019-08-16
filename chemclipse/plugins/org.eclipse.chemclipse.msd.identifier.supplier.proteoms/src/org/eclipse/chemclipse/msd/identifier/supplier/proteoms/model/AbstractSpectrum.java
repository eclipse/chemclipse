/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model;

import java.util.ArrayList;
import java.util.List;

public class AbstractSpectrum {

	protected List<Peak> peaks = null;
	protected String name;
	private long id;

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + (int)(id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		AbstractSpectrum other = (AbstractSpectrum)obj;
		if(id != other.id)
			return false;
		return true;
	}

	public AbstractSpectrum() {
	}

	public AbstractSpectrum(long id, String name) {
		this.setId(id);
		this.name = name;
	}

	public void addPeak(Peak p) {

		if(this.peaks == null) {
			this.peaks = new ArrayList<>();
		}
		getPeaks().add(p);
	}

	public int getNumberOfPeak() {

		if(peaks == null) {
			return 0;
		}
		return peaks.size();
	}

	public String getName() {

		return name;
	}

	/**
	 * For MS may be a Spot name and for MSMS may be a mass of peptide.
	 *
	 * @param name
	 *            Spectrum name.
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * @return List<Peak> or empty list.
	 */
	public List<Peak> getPeaks() {

		if(peaks == null) {
			peaks = new ArrayList<>();
		}
		return peaks;
	}

	public void setPeaks(List<Peak> peaks) {

		this.peaks = peaks;
	}

	public long getId() {

		return id;
	}

	public void setId(long id) {

		this.id = id;
	}
}
