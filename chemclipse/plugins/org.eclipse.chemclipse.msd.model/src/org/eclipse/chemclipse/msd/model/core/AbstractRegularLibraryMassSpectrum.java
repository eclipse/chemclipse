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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private static final Pattern PATTERN_ELEMENT = Pattern.compile("([+-]\\w*)");
	//
	private ILibraryInformation libraryInformation;
	private String precursorType;
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
	public double getNeutralMass(double precursorIon) {

		if(properties != null) {
			/*
			 * [M+H]+
			 * [M-H]-
			 * ...
			 * [M-H-CO2-C14H23O2]-
			 * [M+H-C36H64O10-C12H20O10]+
			 * [M+H-C8H10N4O3]+
			 * [M+H-C8H18O2]+
			 * [M+H-C12H9NS]+
			 * [M+H-C23H38O21-C15H24O2]+
			 * [M-H-C18H32O18-CO]-
			 * [M+H-CH3N-C2H2O]+
			 * [M+H-CH5NO2-CH2O2]+
			 * [M-H-C12H18]-
			 */
			String precursorType = getProperty(PROPERTY_PRECURSOR_TYPE);
			if(!precursorType.isEmpty()) {
				String[] parts = precursorType.split("]");
				if(parts.length == 2) {
					double adjustByMass = 0;
					String chemicalIonization = parts[0].trim().replace("[M", "");
					Matcher matcher = PATTERN_ELEMENT.matcher(chemicalIonization);
					while(matcher.find()) {
						/*
						 * H, ...
						 */
						String formula = matcher.group();
						boolean add;
						if(formula.startsWith("+")) {
							add = false; // subtract to get the neutral mass
						} else {
							add = true; // add to get the neutral mass
						}
						formula = formula.substring(1, formula.length());
						/*
						 * TODO - formula calculator
						 */
						double mass = 0;
						if(formula.equals("H")) {
							mass = 1.00794d;
						} else if(formula.equals("Na")) {
							mass = 22.98976928d;
						} else if(formula.equals("NH4")) {
							mass = 18.03846d;
						} else if(formula.equals("H2O")) {
							mass = 18.01528d;
						}
						adjustByMass += add ? mass : -mass;
					}
					//
					return precursorIon + adjustByMass;
				}
			}
		}
		//
		return precursorIon;
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