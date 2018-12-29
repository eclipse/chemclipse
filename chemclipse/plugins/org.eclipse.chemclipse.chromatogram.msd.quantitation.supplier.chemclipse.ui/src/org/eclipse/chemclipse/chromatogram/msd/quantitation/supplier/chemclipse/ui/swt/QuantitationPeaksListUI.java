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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.events.IChemClipseQuantitationEvents;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.QuantitationPeaksContentProvider;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.QuantitationPeaksLabelProvider;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.QuantitationPeaksTableComparator;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationPeakMSD;
import org.eclipse.chemclipse.msd.model.notifier.PeakSelectionUpdateNotifier;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class QuantitationPeaksListUI extends AbstractTableViewerUI implements IQuantitationCompoundUpdater {

	private static final String MESSAGE_BOX_TEXT = "Quantitation Peaks";
	private IQuantitationCompound quantitationCompoundDocument;
	private IEventBroker eventBroker;
	private IQuantDatabase database;
	private Map<String, Object> map;

	public QuantitationPeaksListUI(Composite parent, int style, IEventBroker eventBroker) {
		parent.setLayout(new FillLayout());
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		addList(composite);
		addButtons(composite);
		//
		this.eventBroker = eventBroker;
		map = new HashMap<String, Object>();
	}

	@Override
	public void update(IQuantitationCompound quantitationCompoundDocument, IQuantDatabase database) {

		this.database = database;
		this.quantitationCompoundDocument = quantitationCompoundDocument;
		//
		if(quantitationCompoundDocument == null || database == null) {
			getTableViewer().setInput(null);
		} else {
			setTableViewerInput();
		}
	}

	private void addList(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataTable = new GridData(GridData.FILL_BOTH);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(gridDataTable);
		//
		String[] titles = {"Concentration", "Concentration Unit"};
		int bounds[] = {100, 100};
		IStructuredContentProvider contentProvider = new QuantitationPeaksContentProvider();
		ILabelProvider labelProvider = new QuantitationPeaksLabelProvider();
		QuantitationPeaksTableComparator viewerTableComparator = new QuantitationPeaksTableComparator();
		//
		createTableViewer(composite, gridDataTable, contentProvider, labelProvider, viewerTableComparator, titles, bounds);
		getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				/*
				 * Show the peak and the mass spectrum.
				 */
				IQuantitationPeak quantitationPeakDocument = getSelectedQuantitationPeak();
				if(quantitationPeakDocument != null) {
					IPeak peak = quantitationPeakDocument.getReferencePeak();
					if(peak instanceof IPeakMSD) {
						PeakSelectionUpdateNotifier.fireUpdateChange((IPeakMSD)peak, true);
					}
				}
			}
		});
		setTableViewerInput();
	}

	private void setTableViewerInput() {

		if(database != null && quantitationCompoundDocument != null) {
			List<IQuantitationPeak> quantitationPeakDocuments = database.getQuantitationPeaks(quantitationCompoundDocument);
			getTableViewer().setInput(quantitationPeakDocuments);
		} else {
			getTableViewer().setInput(null);
		}
	}

	private void addButtons(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		GridData gridDataButtons = new GridData(GridData.FILL_VERTICAL);
		gridDataButtons.verticalAlignment = SWT.TOP;
		composite.setLayoutData(gridDataButtons);
		//
		addButtonRemove(composite);
		addButtonRemoveAll(composite);
		addButtonCreate(composite);
	}

	private void addButtonRemove(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.setText("Delete");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IQuantitationPeak quantitationPeakDocument = getSelectedQuantitationPeak();
				if(quantitationCompoundDocument != null && quantitationPeakDocument != null && database != null) {
					database.deleteQuantitationPeakDocument(quantitationCompoundDocument, quantitationPeakDocument);
					triggerCompoundDocumentUpdateEvent();
				}
			}
		});
	}

	private void addButtonRemoveAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.setText("Delete All");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Set<IQuantitationPeak> quantitationPeakDocuments = new HashSet<IQuantitationPeak>(getSelectedQuantitationPeaks());
				if(quantitationCompoundDocument != null && quantitationPeakDocuments != null && database != null) {
					database.deleteQuantitationPeakDocuments(quantitationCompoundDocument, quantitationPeakDocuments);
					triggerCompoundDocumentUpdateEvent();
				}
			}
		});
	}

	private void addButtonCreate(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.setText("Create Response Table");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(database != null) {
					//
					List<IQuantitationPeak> quantitationPeakDocuments = database.getQuantitationPeaks(quantitationCompoundDocument);
					if(quantitationPeakDocuments.size() > 0) {
						if(showQuestion(MESSAGE_BOX_TEXT, "Would you like to create new concentration response and signal tables?") == SWT.YES) {
							IQuantitationCompound quantitationCompoundMSD = quantitationCompoundDocument;
							List<IQuantitationPeak> quantitationPeaks = new ArrayList<IQuantitationPeak>();
							for(IQuantitationPeak quantitationPeakDocument : quantitationPeakDocuments) {
								IPeak referencePeak = quantitationPeakDocument.getReferencePeak();
								double concentration = quantitationPeakDocument.getConcentration();
								String concentrationUnit = quantitationPeakDocument.getConcentrationUnit();
								if(referencePeak instanceof IPeakMSD) {
									IQuantitationPeak quantitationPeakMSD = new QuantitationPeakMSD((IPeakMSD)referencePeak, concentration, concentrationUnit);
									quantitationPeaks.add(quantitationPeakMSD);
								}
							}
							quantitationCompoundMSD.calculateQuantitationSignalsAndConcentrationResponseEntries();
							triggerCompoundDocumentUpdateEvent();
						}
					} else {
						showMessage(MESSAGE_BOX_TEXT, "There are no quantitation peaks stored.");
					}
				}
			}
		});
	}

	private void triggerCompoundDocumentUpdateEvent() {

		if(eventBroker != null && quantitationCompoundDocument != null && database != null) {
			/*
			 * Inform all other views about the selected quantitation compound.
			 * The element could be null.
			 */
			map.clear();
			map.put(IChemClipseQuantitationEvents.PROPERTY_QUANTITATION_COMPOUND_DOCUMENT, quantitationCompoundDocument);
			map.put(IChemClipseQuantitationEvents.PROPERTY_DATABASE, database);
			eventBroker.send(IChemClipseQuantitationEvents.TOPIC_QUANTITATION_COMPOUND_DOCUMENT_UPDATE, map);
		}
	}

	private IQuantitationPeak getSelectedQuantitationPeak() {

		IQuantitationPeak quantitationPeakDocument = null;
		Object element = getSelectedTableItem();
		if(element instanceof IQuantitationPeak) {
			quantitationPeakDocument = (IQuantitationPeak)element;
		}
		return quantitationPeakDocument;
	}

	private List<IQuantitationPeak> getSelectedQuantitationPeaks() {

		List<IQuantitationPeak> quantitationPeakDocuments = new ArrayList<IQuantitationPeak>();
		List<Object> elements = getSelectedTableItems();
		for(Object element : elements) {
			if(element instanceof IQuantitationPeak) {
				IQuantitationPeak quantitationPeakDocument = (IQuantitationPeak)element;
				quantitationPeakDocuments.add(quantitationPeakDocument);
			}
		}
		return quantitationPeakDocuments;
	}
}
