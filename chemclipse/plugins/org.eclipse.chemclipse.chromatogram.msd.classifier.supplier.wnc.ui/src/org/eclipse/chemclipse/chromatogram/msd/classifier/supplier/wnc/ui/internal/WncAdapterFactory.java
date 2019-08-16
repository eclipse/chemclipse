/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.internal;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.internal.provider.MeasurementResultTitles;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.internal.provider.WncResultsContentProvider;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.internal.provider.WncResultsLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinitionProvider;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;

public class WncAdapterFactory implements IAdapterFactory {

	private static final MeasurementResultTitles TITLES = new MeasurementResultTitles();
	private static final WncResultsLabelProvider LABEL_PROVIDER = new WncResultsLabelProvider();
	private static final WncResultsContentProvider CONTENT_PROVIDER = new WncResultsContentProvider();

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {

		if(adapterType == IStructuredContentProvider.class) {
			return adapterType.cast(CONTENT_PROVIDER);
		}
		if(adapterType == ITableLabelProvider.class) {
			return adapterType.cast(LABEL_PROVIDER);
		}
		if(adapterType == ColumnDefinitionProvider.class) {
			return adapterType.cast(TITLES);
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {

		return new Class<?>[]{IStructuredContentProvider.class, ColumnDefinitionProvider.class, ITableLabelProvider.class};
	}
}
