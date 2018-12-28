/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.controller;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.QuantitationCompoundAlreadyExistsException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;

public class DatabaseController implements IDatabaseController {

	private static final Logger logger = Logger.getLogger(DatabaseController.class);
	private IQuantDatabase database;

	public DatabaseController(IQuantDatabase database) {
		this.database = database;
	}

	@Override
	public void addQuantitationCompound(IQuantitationCompound quantitationCompound) {

		try {
			database.addQuantitationCompound(quantitationCompound);
		} catch(QuantitationCompoundAlreadyExistsException e) {
			logger.warn(e);
		}
	}
}
