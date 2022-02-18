/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.msd.swt.ui.internal.editingsupport.LibraryTextEditingSupport;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListContentProviderLazy;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListFilter;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListTableComparator;
import org.eclipse.chemclipse.msd.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MassSpectrumListUI extends ExtendedTableViewer {

	private static final Logger logger = Logger.getLogger(MassSpectrumListUI.class);
	//
	public static final String NAME = "Name";
	public static final String RETENTION_TIME = "Retention Time";
	public static final String RELATIVE_RETENTION_TIME = "Relative Retention Time";
	public static final String RETENTION_INDEX = "Retention Index";
	public static final String BASE_PEAK = "Base Peak";
	public static final String BASE_PEAK_ABUNDANCE = "Base Peak Abundance";
	public static final String NUMBER_OF_IONS = "Number of Ions";
	public static final String CAS = "CAS";
	public static final String MW = "MW";
	public static final String FORMULA = "Formula";
	public static final String SMILES = "SMILES";
	public static final String INCHI = "InChI";
	public static final String REFERENCE_IDENTIFIER = "Reference Identifier";
	public static final String COMMENTS = "Comments";
	//
	private String[] titles = {//
			NAME, //
			RETENTION_TIME, //
			RELATIVE_RETENTION_TIME, //
			RETENTION_INDEX, //
			BASE_PEAK, //
			BASE_PEAK_ABUNDANCE, //
			NUMBER_OF_IONS, //
			CAS, //
			MW, //
			FORMULA, //
			SMILES, //
			INCHI, //
			REFERENCE_IDENTIFIER, //
			COMMENTS//
	};
	private int[] bounds = {//
			300, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100 //
	};
	//
	private ITableLabelProvider labelProvider;
	private IStructuredContentProvider normalContentProvider;
	private ILazyContentProvider lazyContentProvider;
	private MassSpectrumListFilter massSpectrumListFilter;
	private ViewerComparator tableComparator;
	/*
	 * Store an instance of the mass spectra.
	 * SWT.VIRTUAL supports no filtering by default.
	 */
	private IMassSpectra massSpectra;

	private class DatabaseSearchRunnable implements IRunnableWithProgress {

		private IMassSpectra filteredMassSpectra = new MassSpectra();

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

			try {
				monitor.beginTask("Search DB", IProgressMonitor.UNKNOWN);
				filteredMassSpectra = new MassSpectra();
				exitloop:
				for(IScanMSD scanMSD : massSpectra.getList()) {
					/*
					 * Search
					 */
					if(massSpectrumListFilter.matchElement(scanMSD)) {
						filteredMassSpectra.addMassSpectrum(scanMSD);
					}
					//
					if(monitor.isCanceled()) {
						break exitloop;
					}
				}
			} finally {
				monitor.done();
			}
		}

		public IMassSpectra getFilteredMassSpectra() {

			return filteredMassSpectra;
		}
	}

	public MassSpectrumListUI(Composite parent) {

		this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
	}

	public MassSpectrumListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		massSpectrumListFilter.setSearchText(searchText, caseSensitive);
		if(isVirtualTable() && isMassiveData(massSpectra)) {
			/*
			 * Virtual
			 */
			if(massSpectra != null) {
				if("".equals(searchText)) {
					/*
					 * Reset
					 */
					setInput(massSpectra);
				} else {
					/*
					 * Filter DB
					 */
					try {
						Shell shell = Display.getDefault().getActiveShell();
						ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
						DatabaseSearchRunnable databaseSearchRunnable = new DatabaseSearchRunnable();
						dialog.run(true, false, databaseSearchRunnable);
						updateInput(databaseSearchRunnable.getFilteredMassSpectra());
					} catch(InvocationTargetException e) {
						logger.warn(e);
						setInput(massSpectra);
					} catch(InterruptedException e) {
						logger.warn(e);
						setInput(massSpectra);
						Thread.currentThread().interrupt();
					}
				}
			}
		}
		/*
		 * Refresh the table.
		 */
		refresh();
	}

	public void setInput(IMassSpectra massSpectra) {

		this.massSpectra = massSpectra;
		updateInput(massSpectra);
	}

	private void updateInput(IMassSpectra massSpectra) {

		if(massSpectra != null) {
			boolean massiveData = isMassiveData(massSpectra);
			super.setInput(null); // Can only enable the hash look up before input has been set
			setLabelAndContentProviders(massiveData);
			super.setInput(massSpectra);
			setItemCount(massSpectra.size());
		}
	}

	private void createColumns() {

		createColumns(titles, bounds);
		//
		labelProvider = new MassSpectrumListLabelProvider();
		normalContentProvider = new MassSpectrumListContentProvider();
		lazyContentProvider = new MassSpectrumListContentProviderLazy(this);
		tableComparator = new MassSpectrumListTableComparator();
		//
		setLabelAndContentProviders(isVirtualTable());
		massSpectrumListFilter = new MassSpectrumListFilter();
		setFilters(new ViewerFilter[]{massSpectrumListFilter});
		setEditingSupport();
	}

	private void setLabelAndContentProviders(boolean massiveData) {

		setLabelProvider(labelProvider);
		if(massiveData && isVirtualTable()) {
			/*
			 * Lazy (Virtual)
			 */
			logger.info("Lazy (Virtual) Modus");
			setContentProvider(lazyContentProvider);
			setUseHashlookup(true);
			setComparator(null);
		} else {
			/*
			 * Normal
			 */
			logger.info("Normal Modus");
			setContentProvider(normalContentProvider);
			setUseHashlookup(false);
			setComparator(tableComparator);
		}
	}

	private void setEditingSupport() {

		Set<String> excludeFromEditing = new HashSet<>();
		excludeFromEditing.add(BASE_PEAK);
		excludeFromEditing.add(BASE_PEAK_ABUNDANCE);
		excludeFromEditing.add(NUMBER_OF_IONS);
		//
		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(TableViewerColumn tableViewerColumn : tableViewerColumns) {
			String columnLabel = tableViewerColumn.getColumn().getText();
			if(!excludeFromEditing.contains(columnLabel)) {
				tableViewerColumn.setEditingSupport(new LibraryTextEditingSupport(this, columnLabel));
			}
		}
	}

	private boolean isMassiveData(IMassSpectra massSpectra) {

		if(massSpectra != null) {
			int limitMassiveData = PreferenceSupplier.getLibraryMSDLimitSorting();
			return (massSpectra.size() > limitMassiveData);
		}
		return false;
	}

	private boolean isVirtualTable() {

		return ((getTable().getStyle() & SWT.VIRTUAL) == SWT.VIRTUAL);
	}
}
