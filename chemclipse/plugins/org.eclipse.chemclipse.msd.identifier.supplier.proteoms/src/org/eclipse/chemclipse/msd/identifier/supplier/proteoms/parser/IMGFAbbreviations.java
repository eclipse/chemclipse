/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.parser;

/**
 * See:
 * http://www.matrixscience.com/help/data_file_help.html
 */
public interface IMGFAbbreviations {

	/**
	 * Database entries to be searched
	 */
	public final String ACCESSION = "ACCESSION";
	/**
	 * Peptide charge
	 */
	public final String CHARGE = "CHARGE";
	/**
	 * Enzyme
	 */
	public final String CLE = "CLE";
	/**
	 * Search title
	 */
	public final String COM = "COM";
	/**
	 * Peptide mass
	 */
	public final String PEPMASS = "PEPMASS";
	/**
	 * Start ions in lines.
	 */
	public final String BEGIN_IONS = "BEGIN IONS";
	/**
	 * End ions in lines.
	 */
	public final String END_IONS = "END IONS";
	/**
	 * Title
	 */
	public final String TITLE = "TITLE";
}
