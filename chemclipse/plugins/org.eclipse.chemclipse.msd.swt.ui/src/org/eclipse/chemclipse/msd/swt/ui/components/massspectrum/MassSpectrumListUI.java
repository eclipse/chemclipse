/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.notifier.MassSpectrumSelectionUpdateNotifier;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListFilter;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListTableComparator;
import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.swt.ui.viewers.ExtendedTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MassSpectrumListUI extends Composite {

	private static final int MAX_SPECTRA_LOAD_COMPLETE = 5000;
	private Text text;
	private ExtendedTableViewer tableViewer;
	private Label label;
	private MassSpectrumListTableComparator massSpectrumTableComparator;
	private MassSpectrumListFilter massSpectrumListFilter;
	private String[] titles = {"Retention Time", "Retention Index", "Base Peak", "Base Peak Abundance", "Number of Ions", "Name", "CAS", "MW", "Formula"};
	private int bounds[] = {100, 100, 100, 100, 100, 100, 100, 100, 100};
	private int massSpectraSize = 0;

	public MassSpectrumListUI(Composite parent, int style) {

		super(parent, style);
		this.setLayout(new GridLayout(2, false));
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		text = new Text(this, SWT.BORDER);
		text.setText("");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Button button = new Button(this, SWT.PUSH);
		button.setText("Search");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String searchText = text.getText().trim();
				massSpectrumListFilter.setSearchText(searchText);
				tableViewer.refresh();
				updateLabel();
			}
		});
		/*
		 * Table
		 */
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		//
		tableViewer = new ExtendedTableViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.getTable().setLayoutData(gridData);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new MassSpectrumListContentProvider());
		tableViewer.setLabelProvider(new MassSpectrumListLabelProvider());
		massSpectrumListFilter = new MassSpectrumListFilter();
		tableViewer.setFilters(new ViewerFilter[]{massSpectrumListFilter});
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object firstElement = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if(firstElement != null && firstElement instanceof IScanMSD) {
					/*
					 * Fire an update if a mass spectrum has been selected.
					 */
					List<String> viewIds = new ArrayList<String>();
					viewIds.add(IPerspectiveAndViewIds.VIEW_OPTIMIZED_MASS_SPECTRUM);
					viewIds.add(IPerspectiveAndViewIds.VIEW_MASS_SPECTRUM_TARGETS);
					PerspectiveSwitchHandler.focusPerspectiveAndView(IPerspectiveAndViewIds.PERSPECTIVE_MSD, viewIds);
					IScanMSD massSpectrum = (IScanMSD)firstElement;
					MassSpectrumSelectionUpdateNotifier.fireUpdateChange(massSpectrum, true);
				}
			}
		});
		/*
		 * Copy and Paste of the table content.
		 */
		tableViewer.getTable().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * The selected content will be placed to the clipboard if the
				 * user is using "Function + c". "Function-Key" 262144
				 * (stateMask) + "c" 99 (keyCode)
				 */
				if(e.keyCode == 99 && e.stateMask == 262144) {
					tableViewer.copyToClipboard(titles);
				}
			}
		});
		/*
		 * Sorting the table.
		 */
		massSpectrumTableComparator = new MassSpectrumListTableComparator();
		tableViewer.setComparator(massSpectrumTableComparator);
		/*
		 * Table
		 */
		label = new Label(this, SWT.NONE);
		label.setText("");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void update(IMassSpectra massSpectra, boolean forceReload) {

		if(massSpectra != null) {
			//
			if(massSpectra.size() > MAX_SPECTRA_LOAD_COMPLETE) {
				String searchString = "Please use this filter";
				massSpectrumListFilter.setSearchText(searchString);
				text.setText(searchString);
			}
			tableViewer.setInput(massSpectra);
			massSpectraSize = massSpectra.size();
			updateLabel();
		}
	}

	public void clear() {

		tableViewer.setInput(null);
	}

	public TableViewer getTableViewer() {

		return tableViewer;
	}

	// -----------------------------------------private methods
	private void updateLabel() {

		label.setText("Mass Spectra: " + massSpectraSize + " (Filtered Mass Spectra: " + tableViewer.getTable().getItemCount() + " [" + text.getText().trim() + "])");
	}
}