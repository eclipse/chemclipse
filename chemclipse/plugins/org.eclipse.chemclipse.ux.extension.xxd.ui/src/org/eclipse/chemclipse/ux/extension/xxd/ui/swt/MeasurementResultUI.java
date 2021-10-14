/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - improve data handling
 * Matthias Mailänder - refactored into extended table
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinitionProvider;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.widgets.Composite;

public class MeasurementResultUI extends ExtendedTableViewer {

	private ProxySelectionChangedListener selectionChangedListener;
	private ProxyTableLabelProvider labelProvider;
	private ProxyStructuredContentProvider contentProvider;
	private IMeasurementResult<?> lastResult;

	public MeasurementResultUI(Composite parent, int style) {

		super(parent, style);
		//
		contentProvider = new ProxyStructuredContentProvider();
		setContentProvider(contentProvider);
		labelProvider = new ProxyTableLabelProvider();
		setLabelProvider(labelProvider);
		selectionChangedListener = new ProxySelectionChangedListener();
		addSelectionChangedListener(selectionChangedListener);
	}

	public void update(IMeasurementResult<?> measurementResult) {

		if(lastResult != measurementResult) {
			contentProvider.setProxy(adaptTo(measurementResult, IStructuredContentProvider.class));
			selectionChangedListener.setProxy(adaptTo(measurementResult, ISelectionChangedListener.class));
			clearColumns();
			ColumnDefinitionProvider columnDefinitionProvider = adaptTo(measurementResult, ColumnDefinitionProvider.class);
			if(columnDefinitionProvider != null) {
				addColumns(columnDefinitionProvider);
			}
			ITableLabelProvider tableLabelProvider = adaptTo(measurementResult, ITableLabelProvider.class);
			labelProvider.setProxy(tableLabelProvider);
			if(tableLabelProvider != null) {
				setLabelProvider(tableLabelProvider);
			}
			setInput(measurementResult);
		}
		lastResult = measurementResult;
	}

	private static <T> T adaptTo(IMeasurementResult<?> measurementResult, Class<T> desiredType) {

		if(measurementResult == null) {
			return null;
		}
		//
		T resultAdapted = Adapters.adapt(measurementResult, desiredType);
		if(resultAdapted != null) {
			return resultAdapted;
		} else {
			return Adapters.adapt(measurementResult.getResult(), desiredType);
		}
	}
}
