/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 * Philip Wenig - preference initialization
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.identifier.scan.IScanIdentifierSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.scan.IScanIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.scan.ScanIdentifier;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class ScanWebIdentifierUI extends Composite {

	private IScan scan;
	//
	private Button button;
	private Menu menu;
	//
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	private List<IScanIdentifierSupplier> identifierSuppliers = getScanIdentifierSuppliers();
	private IScanIdentifierSupplier scanIdentifierSupplier;

	public ScanWebIdentifierUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);
		button.setEnabled(enabled);
	}

	public void setInput(IScan scan) {

		this.scan = scan;
		setEnabled(scan != null && scanIdentifierSupplier != null);
	}

	public static String[][] getScanIdentifier() {

		List<IScanIdentifierSupplier> identifierSuppliers = getScanIdentifierSuppliers();
		String[][] scanIdentifier = new String[identifierSuppliers.size()][2];
		for(int i = 0; i < identifierSuppliers.size(); i++) {
			IScanIdentifierSupplier identifierSupplier = identifierSuppliers.get(i);
			scanIdentifier[i] = new String[]{identifierSupplier.getIdentifierName(), identifierSupplier.getId()};
		}
		return scanIdentifier;
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		//
		button = createButton(composite);
		menu = createMenuIdentifier(button, identifierSuppliers);
		button.setMenu(menu);
		//
		initialize();
	}

	private void initialize() {

		setEnabled(false);
		activateDefaultIdentifier(identifierSuppliers);
	}

	private Button createButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXTERNAL_BROWSER, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				launchBrowser();
			}
		});
		//
		return button;
	}

	private Menu createMenuIdentifier(Button button, List<IScanIdentifierSupplier> identifierSuppliers) {

		Menu menu = new Menu(button);
		//
		for(IScanIdentifierSupplier identifierSupplier : identifierSuppliers) {
			/*
			 * Identifier Handler
			 */
			MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText(identifierSupplier.getIdentifierName());
			menuItem.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					button.setToolTipText(identifierSupplier.getIdentifierName());
					preferenceStore.setValue(PreferenceSupplier.P_SCAN_IDENTIFER, identifierSupplier.getId());
					scanIdentifierSupplier = identifierSupplier;
					launchBrowser();
				}
			});
		}
		//
		return menu;
	}

	private void activateDefaultIdentifier(List<IScanIdentifierSupplier> identifierSuppliers) {

		String id = preferenceStore.getString(PreferenceSupplier.P_SCAN_IDENTIFER);
		if(!id.isEmpty()) {
			for(IScanIdentifierSupplier identifierSupplier : identifierSuppliers) {
				if(id.equals(identifierSupplier.getId())) {
					scanIdentifierSupplier = identifierSupplier;
				}
			}
		}
		if(scanIdentifierSupplier == null && !identifierSuppliers.isEmpty()) {
			scanIdentifierSupplier = identifierSuppliers.get(0);
		}
		if(scanIdentifierSupplier != null) {
			button.setToolTipText(scanIdentifierSupplier.getIdentifierName());
		}
	}

	private static List<IScanIdentifierSupplier> getScanIdentifierSuppliers() {

		IScanIdentifierSupport scanIdentifierSupport = ScanIdentifier.getScanIdentifierSupport();
		List<IScanIdentifierSupplier> identifierSuppliers = new ArrayList<>(scanIdentifierSupport.getSuppliers());
		Collections.sort(identifierSuppliers, (s1, s2) -> s1.getIdentifierName().compareTo(s2.getIdentifierName()));
		//
		return identifierSuppliers;
	}

	private void launchBrowser() {

		URL url = scanIdentifierSupplier.getURL(scan);
		if(url != null) {
			Program.launch(url.toString());
		}
	}
}
