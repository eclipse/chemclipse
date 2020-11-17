/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePeaksAxes;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ExtendedQuantPeaksListUI extends Composite implements IExtendedPartUI {

	private static final String DESCRIPTION = "Quantitation Peaks";
	//
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private IQuantitationCompound quantitationCompound;
	private AtomicReference<QuantPeakListUI> tableViewer = new AtomicReference<>();
	private Button buttonTableEdit;

	public ExtendedQuantPeaksListUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IQuantitationCompound quantitationCompound) {

		this.quantitationCompound = quantitationCompound;
		updateInput();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(composite);
		createToolbarInfo(composite);
		createTable(composite);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
		enableEdit(tableViewer, buttonTableEdit, IMAGE_EDIT_ENTRY, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(4, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		createResponseTableButton(composite);
		buttonTableEdit = createButtonToggleEditTable(composite, tableViewer, IMAGE_EDIT_ENTRY);
		createSettingsButton(composite);
	}

	private void createResponseTableButton(Composite parent) {

		//
		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Create Response Table");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CALCULATE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(quantitationCompound != null) {
					if(quantitationCompound.getQuantitationPeaks().size() > 0) {
						if(MessageDialog.openQuestion(e.display.getActiveShell(), DESCRIPTION, "Would you like to create new concentration response and signal tables?")) {
							quantitationCompound.calculateSignalTablesFromPeaks();
						}
					} else {
						MessageDialog.openError(e.display.getActiveShell(), DESCRIPTION, "There are no quantitation peaks stored.");
					}
				}
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePagePeaksAxes.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createTable(Composite parent) {

		QuantPeakListUI quantPeakListUI = new QuantPeakListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		quantPeakListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		tableViewer.set(quantPeakListUI);
	}

	private void applySettings() {

		updateInput();
	}

	private void updateInput() {

		if(quantitationCompound != null) {
			toolbarInfo.get().setText("Quantitation Compound: " + quantitationCompound.getName());
			tableViewer.get().setInput(quantitationCompound.getQuantitationPeaks());
		} else {
			tableViewer.get().clear();
		}
	}
}
