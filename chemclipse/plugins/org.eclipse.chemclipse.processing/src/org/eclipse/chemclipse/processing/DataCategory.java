/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Matthias Mailänder - add support for MALDI
 * Philip Wenig - add support for TSD
 *******************************************************************************/
package org.eclipse.chemclipse.processing;

import org.eclipse.chemclipse.support.text.ILabel;

/**
 * Describes very generic and broaden data categories a filter, processor or converter might handle
 * 
 * @author Christoph Läubrich
 *
 */
public enum DataCategory implements ILabel {

	MSD(Messages.getString("DataCategory.MSD")), //
	CSD(Messages.getString("DataCategory.CSD")), //
	WSD(Messages.getString("DataCategory.WSD")), //
	TSD(Messages.getString("DataCategory.TSD")), //
	FID(Messages.getString("DataCategory.FID")), //
	NMR(Messages.getString("DataCategory.NMR")), //
	XIR(Messages.getString("DataCategory.XIR")), //
	PCR(Messages.getString("DataCategory.PCR")), //
	MALDI(Messages.getString("DataCategory.MALDI")), //
	MSD_DATABASE(Messages.getString("DataCategory.MSD_DATABASE")), //
	/**
	 * Suggests that this Filter can support a wide range of datatypes and content-sensing is the only option to check if the filter, processor or converter can really handle the data or not
	 */
	AUTO_DETECT(Messages.getString("DataCategory.AUTO_DETECT")); //$NON-NLS-1$

	private String label;

	private DataCategory(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}
}