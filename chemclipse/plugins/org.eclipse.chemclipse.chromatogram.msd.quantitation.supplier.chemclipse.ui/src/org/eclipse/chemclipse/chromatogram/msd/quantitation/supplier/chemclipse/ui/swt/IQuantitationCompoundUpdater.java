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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.swt;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;

public interface IQuantitationCompoundUpdater {

	/**
	 * The quantitation compound document could be null to show that the element is not available anymore.
	 * 
	 * 
	 * @param quantitationCompoundDocument
	 * @param database
	 */
	void update(IQuantitationCompound quantitationCompound, IQuantDatabase database);
}
