/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing;

/**
 * Describes very generic and broaden data categories a filter, processor or converter might handle
 * 
 * @author Christoph Läubrich
 *
 */
public enum DataCategory {
	MSD(Messages.getString("DataCategory.MSD")), CSD(Messages.getString("DataCategory.CSD")), WSD(Messages.getString("DataCategory.WSD")), FID(Messages.getString("DataCategory.FID")), NMR(Messages.getString("DataCategory.NMR")), XIR(Messages.getString("DataCategory.XIR")), PCR(Messages.getString("DataCategory.PCR")),
	/**
	 * Suggests that this Filter can support a wide range of datatypes and content-sensing is the only option to check if the filter, processor or converter can really handle the data or not
	 */
	AUTO_DETECT(Messages.getString("DataCategory.AUTO_DETECT")); //$NON-NLS-1$

	private String label;

	private DataCategory(String label) {
		this.label = label;
	}

	public String getLabel() {

		return label;
	}
}
