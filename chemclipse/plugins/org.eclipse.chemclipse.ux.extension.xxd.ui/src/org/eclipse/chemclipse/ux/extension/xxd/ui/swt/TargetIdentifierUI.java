/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.identifier.targets.ITargetIdentifierSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.targets.ITargetIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.targets.TargetIdentifier;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
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

public class TargetIdentifierUI extends Composite {

	private ILibraryInformation libraryInformation;
	//
	private Button button;
	private Menu menu;
	//
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	private List<ITargetIdentifierSupplier> identifierSuppliers = getTargetIdentifierSuppliers();
	private ITargetIdentifierSupplier targetIdentifierSupplier;

	public TargetIdentifierUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);
		button.setEnabled(enabled);
	}

	public void setInput(ILibraryInformation libraryInformation) {

		this.libraryInformation = libraryInformation;
		setEnabled(libraryInformation != null);
	}

	public static String[][] getTargetIdentifier() {

		List<ITargetIdentifierSupplier> identifierSuppliers = getTargetIdentifierSuppliers();
		String[][] targetIdentifier = new String[identifierSuppliers.size()][2];
		for(int i = 0; i < identifierSuppliers.size(); i++) {
			ITargetIdentifierSupplier identifierSupplier = identifierSuppliers.get(i);
			targetIdentifier[i] = new String[]{identifierSupplier.getIdentifierName(), identifierSupplier.getId()};
		}
		return targetIdentifier;
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

	private Menu createMenuIdentifier(Button button, List<ITargetIdentifierSupplier> identifierSuppliers) {

		Menu menu = new Menu(button);
		//
		for(ITargetIdentifierSupplier identifierSupplier : identifierSuppliers) {
			/*
			 * Identifier Handler
			 */
			MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText(identifierSupplier.getIdentifierName());
			menuItem.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					button.setToolTipText(identifierSupplier.getIdentifierName());
					preferenceStore.setValue(PreferenceConstants.P_TARGET_IDENTIFER, identifierSupplier.getId());
					targetIdentifierSupplier = identifierSupplier;
					launchBrowser();
				}
			});
		}
		//
		return menu;
	}

	private void activateDefaultIdentifier(List<ITargetIdentifierSupplier> identifierSuppliers) {

		String id = preferenceStore.getString(PreferenceConstants.P_TARGET_IDENTIFER);
		if(!id.isEmpty()) {
			for(ITargetIdentifierSupplier identifierSupplier : identifierSuppliers) {
				if(id.equals(identifierSupplier.getId())) {
					targetIdentifierSupplier = identifierSupplier;
				}
			}
		}
		if(targetIdentifierSupplier == null && !identifierSuppliers.isEmpty()) {
			targetIdentifierSupplier = identifierSuppliers.get(0);
		}
		if(targetIdentifierSupplier != null) {
			button.setToolTipText(targetIdentifierSupplier.getIdentifierName());
		}
	}

	private static List<ITargetIdentifierSupplier> getTargetIdentifierSuppliers() {

		ITargetIdentifierSupport targetIdentifierSupport = TargetIdentifier.getTargetIdentifierSupport();
		List<ITargetIdentifierSupplier> identifierSuppliers = new ArrayList<>(targetIdentifierSupport.getSuppliers());
		Collections.sort(identifierSuppliers, (s1, s2) -> s1.getIdentifierName().compareTo(s2.getIdentifierName()));
		//
		return identifierSuppliers;
	}

	private void launchBrowser() {

		URL url = targetIdentifierSupplier.getURL(libraryInformation);
		if(url != null) {
			Program.launch(url.toString());
		}
	}
}
