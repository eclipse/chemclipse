/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;

public abstract class AbstractRegularLibraryMassSpectrum extends AbstractRegularMassSpectrum implements IRegularLibraryMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -3521383640386911035L;
	//
	private ILibraryInformation libraryInformation;
	private String precursorType;
	private double neutralMass = 0.0d;
	private Map<String, String> properties = null; // Initialization on demand

	/**
	 * Creates a new instance of {@code AbstractRegularLibraryMassSpectrum} by creating a
	 * shallow copy of provided {@code templateScan}.
	 * 
	 * @param templateScan
	 *            {@link IScan scan} that is used as a template
	 */
	protected AbstractRegularLibraryMassSpectrum(IScanMSD templateScan) {

		super(templateScan);
	}

	protected AbstractRegularLibraryMassSpectrum(short massSpectrometer, short massSpectrumType) {

		super(massSpectrometer, massSpectrumType);
	}

	protected AbstractRegularLibraryMassSpectrum() {

	}

	protected AbstractRegularLibraryMassSpectrum(Collection<? extends IIon> ions) {

		super(ions);
	}

	@Override
	public String getPrecursorType() {

		return precursorType;
	}

	@Override
	public void setPrecursorType(String precursorType) {

		this.precursorType = precursorType;
	}

	@Override
	public double getNeutralMass() {

		return neutralMass;
	}

	@Override
	public void setNeutralMass(double neutralMass) {

		this.neutralMass = neutralMass;
	}

	@Override
	public String getPolarity() {

		if(properties != null) {
			String precursorType = getProperty(PROPERTY_PRECURSOR_TYPE);
			if(precursorType.contains("]+")) {
				return "+";
			} else if(precursorType.contains("]-")) {
				return "-";
			}
		}
		//
		return "";
	}

	@Override
	public Set<String> getPropertyKeySet() {

		if(properties == null) {
			return Collections.emptySet();
		} else {
			return properties.keySet();
		}
	}

	@Override
	public String getProperty(String property) {

		if(properties == null) {
			return "";
		} else {
			return properties.getOrDefault(property, "");
		}
	}

	@Override
	public void putProperty(String key, String value) {

		if(properties == null) {
			properties = new HashMap<>();
		}
		//
		properties.put(key, value);
	}

	/*
	 * Why is get/set LibraryInformation implemented twice? Here and in {@link
	 * AbstractCombinedLibraryMassSpectrum}?<br/> Java does not support multiple
	 * inheritance like in c++.
	 */
	@Override
	public ILibraryInformation getLibraryInformation() {

		if(libraryInformation == null) {
			libraryInformation = new LibraryInformation();
		}
		return libraryInformation;
	}

	@Override
	public void setLibraryInformation(ILibraryInformation libraryInformation) {

		this.libraryInformation = libraryInformation;
	}
}