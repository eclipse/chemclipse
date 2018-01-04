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
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results;

public interface IHit {

	/**
	 * Get the name.
	 * 
	 * @return String
	 */
	String getName();

	/**
	 * Set the name.
	 * 
	 * @param formula
	 */
	void setName(String name);

	/**
	 * Get the formula.
	 * 
	 * @return String
	 */
	String getFormula();

	/**
	 * Set the formula.
	 * 
	 * @param formula
	 */
	void setFormula(String formula);

	/**
	 * Get the match factor.
	 * 
	 * @return float
	 */
	float getMF();

	/**
	 * Set the match factor.
	 * 
	 * @param mf
	 */
	void setMF(float mf);

	/**
	 * Get the reverse match factor.
	 * 
	 * @return float
	 */
	float getRMF();

	/**
	 * Set the reverse match factor.
	 * 
	 * @param rmf
	 */
	void setRMF(float rmf);

	/**
	 * Get the probability.
	 * 
	 * @return float
	 */
	float getProb();

	/**
	 * Set the probability.
	 * 
	 * @param prob
	 */
	void setProb(float prob);

	/**
	 * Get the cas name.
	 * 
	 * @return String
	 */
	String getCAS();

	/**
	 * Set the cas name.
	 * 
	 * @param cas
	 */
	void setCAS(String cas);

	/**
	 * Get the mw value.
	 * 
	 * @return float
	 */
	int getMw();

	/**
	 * Set the mw value.
	 * 
	 * @param mw
	 */
	void setMw(int mw);

	/**
	 * Get the library.
	 * 
	 * @return String
	 */
	String getLib();

	/**
	 * Set the library.
	 * 
	 * @param lib
	 */
	void setLib(String lib);

	/**
	 * Get the id.
	 * 
	 * @return float
	 */
	int getId();

	/**
	 * Set the id.
	 * 
	 * @param id
	 */
	void setId(int id);
}
