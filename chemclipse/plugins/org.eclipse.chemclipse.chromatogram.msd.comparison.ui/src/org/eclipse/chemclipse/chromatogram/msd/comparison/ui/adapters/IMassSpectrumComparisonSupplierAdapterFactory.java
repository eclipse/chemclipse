/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.ui.adapters;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.IMassSpectrumComparisonSupplier;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.LabelProvider;

public class IMassSpectrumComparisonSupplierAdapterFactory extends LabelProvider implements IAdapterFactory, IToolTipProvider {

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {

		if(adaptableObject instanceof IMassSpectrumComparisonSupplier) {
			if(adapterType.isInstance(this)) {
				return adapterType.cast(this);
			}
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {

		return new Class<?>[]{ILabelProvider.class};
	}

	@Override
	public String getText(Object element) {

		if(element instanceof IMassSpectrumComparisonSupplier massSpectrumComparisonSupplier) {
			return massSpectrumComparisonSupplier.getComparatorName();
		}
		return super.getText(element);
	}

	@Override
	public String getToolTipText(Object element) {

		if(element instanceof IMassSpectrumComparisonSupplier massSpectrumComparisonSupplier) {
			return massSpectrumComparisonSupplier.getDescription();
		}
		return null;
	}
}
