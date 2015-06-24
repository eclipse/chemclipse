/*******************************************************************************
 * Copyright (c) 2010, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.util.Set;

public interface ILibraryInformation {

	/**
	 * Returns the name of the library mass spectrum.
	 * 
	 * @return String
	 */
	String getName();

	/**
	 * Sets the name of the library mass spectrum.
	 */
	void setName(String name);

	/**
	 * Returns the list of synonyms or an empty list.
	 * 
	 * @return {@link Set}
	 */
	Set<String> getSynonyms();

	/**
	 * Sets the synonyms.
	 * The set must be not null.
	 * 
	 * @param synonyms
	 */
	void setSynonyms(Set<String> synonyms);

	/**
	 * Returns CAS number of the library mass spectrum.
	 * 
	 * @return String
	 */
	String getCasNumber();

	/**
	 * Sets the CAS number of the library mass spectrum.
	 */
	void setCasNumber(String casNumber);

	/**
	 * Returns the formula of the library mass spectrum.
	 * 
	 * @return String
	 */
	String getFormula();

	/**
	 * Sets the formula of the library mass spectrum.
	 */
	void setFormula(String formula);

	/**
	 * Returns the mol weight of the library mass spectrum.
	 * 
	 * @return String
	 */
	double getMolWeight();

	/**
	 * Sets the mol weight of the library mass spectrum.
	 */
	void setMolWeight(double molWeight);

	/**
	 * Returns comments of the library mass spectrum.
	 * 
	 * @return String
	 */
	String getComments();

	/**
	 * Sets comments to the library mass spectrum.
	 */
	void setComments(String comments);

	/**
	 * Returns miscellaneous information, e.g. InLib factors ...
	 * 
	 * @return String
	 */
	String getMiscellaneous();

	/**
	 * Sets miscellaneous information, e.g. InLib factors ...
	 * 
	 * @return String
	 */
	void setMiscellaneous(String miscellaneous);
}