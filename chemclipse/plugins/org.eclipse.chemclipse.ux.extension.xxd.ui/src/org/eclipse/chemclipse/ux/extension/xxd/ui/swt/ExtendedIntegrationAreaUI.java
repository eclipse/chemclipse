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

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakDataSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ExtendedIntegrationAreaUI extends Composite implements IExtendedPartUI {

	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private IntegrationAreaUI integrationAreaUI;
	//
	private Object object;
	//
	private PeakDataSupport peakDataSupport = new PeakDataSupport();

	public ExtendedIntegrationAreaUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public boolean setFocus() {

		updateObject();
		return true;
	}

	public void update(Object object) {

		this.object = object;
		if(object instanceof IPeak) {
			IPeak peak = (IPeak)object;
			String description = peak.getIntegratorDescription();
			toolbarInfo.get().setText(peakDataSupport.getPeakLabel(peak) + " | " + description);
		} else if(object instanceof IChromatogram<?>) {
			IChromatogram<?> chromatogram = (IChromatogram<?>)object;
			String description = chromatogram.getIntegratorDescription();
			toolbarInfo.get().setText(ChromatogramDataSupport.getChromatogramLabel(chromatogram) + " | " + description);
		} else {
			toolbarInfo.get().setText("No data has been selected.");
		}
		updateObject();
	}

	private void updateObject() {

		if(object != null) {
			integrationAreaUI.setInput(object);
		} else {
			integrationAreaUI.clear();
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

	private void createQuantitationTable(Composite parent) {

		integrationAreaUI = new IntegrationAreaUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		integrationAreaUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}
