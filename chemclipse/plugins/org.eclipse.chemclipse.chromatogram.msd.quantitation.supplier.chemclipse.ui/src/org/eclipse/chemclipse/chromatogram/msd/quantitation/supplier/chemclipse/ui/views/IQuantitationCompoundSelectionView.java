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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.views;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.swt.IQuantitationCompoundUpdater;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.ux.extension.ui.explorer.ISelectionView;

public interface IQuantitationCompoundSelectionView extends ISelectionView, IQuantitationCompoundUpdater {

	IQuantitationCompound getQuantitationCompoundDocument();

	void setQuantitationCompoundDocument(IQuantitationCompound quantitationCompound);

	IQuantDatabase getDatabase();

	void setDatabase(IQuantDatabase database);

	boolean doUpdate();
}
