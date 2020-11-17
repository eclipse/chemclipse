/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuCategories;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakDataSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class ExtendedQuantitationListUI extends Composite implements IExtendedPartUI {

	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private QuantitationListUI quantitationListUI;
	//
	private IPeak peak;
	//
	private PeakDataSupport peakDataSupport = new PeakDataSupport();

	public ExtendedQuantitationListUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public boolean setFocus() {

		updateObject();
		return true;
	}

	public void update(IPeak peak) {

		this.peak = peak;
		toolbarInfo.get().setText(peakDataSupport.getPeakLabel(peak));
		updateObject();
	}

	private void updateObject() {

		if(peak != null) {
			quantitationListUI.setInput(peak);
		} else {
			quantitationListUI.clear();
		}
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createQuantitationTable(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private QuantitationListUI createQuantitationTable(Composite parent) {

		quantitationListUI = new QuantitationListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		quantitationListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Add the delete targets support.
		 */
		Shell shell = quantitationListUI.getTable().getShell();
		ITableSettings tableSettings = quantitationListUI.getTableSettings();
		addDeleteMenuEntry(shell, tableSettings);
		addKeyEventProcessors(shell, tableSettings);
		quantitationListUI.applySettings(tableSettings);
		//
		return quantitationListUI;
	}

	private void addDeleteMenuEntry(Shell shell, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Quantitation Entrie(s)";
			}

			@Override
			public String getCategory() {

				return ITableMenuCategories.STANDARD_OPERATION;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteQuantitationEntries(shell);
			}
		});
	}

	private void addKeyEventProcessors(Shell shell, ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					/*
					 * DEL
					 */
					deleteQuantitationEntries(shell);
				}
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private void deleteQuantitationEntries(Shell shell) {

		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Delete Quantitation Entrie(s)");
		messageBox.setMessage("Would you like to delete the selected quantitation entrie(s)?");
		if(messageBox.open() == SWT.YES) {
			/*
			 * Delete Quantitation Entry
			 */
			Iterator iterator = quantitationListUI.getStructuredSelection().iterator();
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof IQuantitationEntry) {
					deleteQuantitationEntry((IQuantitationEntry)object);
				}
			}
			//
			updateObject();
		}
	}

	private void deleteQuantitationEntry(IQuantitationEntry quantitationEntry) {

		if(peak != null) {
			peak.removeQuantitationEntry(quantitationEntry);
		}
	}
}
