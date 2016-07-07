/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
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
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.ux.extension.ui.explorer.ISelectionView;

public interface IQuantitationCompoundSelectionView extends ISelectionView, IQuantitationCompoundUpdater {

	IQuantitationCompoundMSD getQuantitationCompoundDocument();

	void setQuantitationCompoundDocument(IQuantitationCompoundMSD quantitationCompound);

	IQuantDatabase getDatabase();

	void setDatabase(IQuantDatabase database);

	boolean doUpdate();
}
