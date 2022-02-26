/*******************************************************************************
 * Copyright (c) 2015, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - increase performance for large spectra
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import java.util.List;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.swt.ui.converter.ConverterMSD;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.IonListLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.IonListTableComparator;
import org.eclipse.chemclipse.msd.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumIonsListUI extends ExtendedTableViewer {

	private String[] titles = {"m/z", "abundance", "parent m/z", "parent resolution", "daughter m/z", "daughter resolution", "collision energy"};
	private int[] bounds = {120, 120, 120, 120, 120, 120, 120};

	public MassSpectrumIonsListUI(Composite parent) {

		super(parent);
		createColumns();
	}

	public MassSpectrumIonsListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void update(IScanMSD massSpectrum) {

		if(massSpectrum != null) {
			boolean massiveData = isMassiveData(massSpectrum);
			super.setInput(null); // Can only enable the hash look up before input has been set
			setContentProviders(massiveData);
			if(!massSpectrum.isTandemMS()) {
				List<TableViewerColumn> columns = getTableViewerColumns().stream().skip(2).collect(Collectors.toList());
				for(TableViewerColumn column : columns) {
					column.getColumn().setWidth(0);
				}
			} else {
				List<TableViewerColumn> columns = getTableViewerColumns();
				int i = 0;
				for(TableViewerColumn column : columns) {
					column.getColumn().setWidth(bounds[i]);
				}
			}
			List<IIon> ions = ConverterMSD.getFilteredIons(massSpectrum);
			super.setInput(ions);
			setItemCount(ions.size());
		} else {
			super.setInput(null);
		}
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(titles, bounds);
		setLabelProvider();
		setContentProviders(isVirtualTable());
	}

	private void setLabelProvider() {

		setLabelProvider(new IonListLabelProvider());
		// TODO add a filter to display a subset of m/z values only
		setEditingSupport();
	}

	private void setEditingSupport() {

		// TODO implement
	}

	private void setContentProviders(boolean isMassiveData) {

		setContentProvider(new ListContentProvider());
		if(isMassiveData && isVirtualTable()) {
			setUseHashlookup(true);
			setComparator(null);
		} else {
			setUseHashlookup(false);
			setComparator(new IonListTableComparator());
		}
	}

	private boolean isMassiveData(IScanMSD massSpectrum) {

		if(massSpectrum != null) {
			int limitMassiveData = PreferenceSupplier.getLibraryMSDLimitSorting();
			return (massSpectrum.getNumberOfIons() > limitMassiveData);
		}
		return false;
	}

	private boolean isVirtualTable() {

		return ((getTable().getStyle() & SWT.VIRTUAL) == SWT.VIRTUAL);
	}
}
