/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createContainer;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.gridData;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.support.ui.wizards.SinglePageWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ProcessMethodTypeSelectionWizard {

	public static DataCategory[] open(Shell shell) {

		SelectionPage page = new SelectionPage();
		page.setDescription("Please choose the desired categories to create a new method for");
		WizardDialog wizardDialog = new WizardDialog(shell, new SinglePageWizard("Select Data Categories", page));
		wizardDialog.setPageSize(200, 150);
		wizardDialog.create();
		if(wizardDialog.open() == WizardDialog.OK) {
			return page.categories.toArray(new DataCategory[0]);
		}
		return null;
	}

	static final class SelectionPage extends WizardPage {

		private final Set<DataCategory> categories = EnumSet.noneOf(DataCategory.class);

		protected SelectionPage() {
			super(SelectionPage.class.getName());
		}

		@Override
		public void createControl(Composite parent) {

			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout());
			Button chromatographyButton = new Button(composite, SWT.RADIO);
			chromatographyButton.setSelection(true);
			chromatographyButton.setText("Chromatography");
			Button[] chromatographyCheckboxes = createButtons(composite, DataCategory.CSD, DataCategory.MSD, DataCategory.WSD);
			Button spectroscopyButton = new Button(composite, SWT.RADIO);
			spectroscopyButton.setText("Spectroscopy");
			Button[] spectroscopyCheckBoxes = createButtons(composite, DataCategory.FID, DataCategory.NMR);
			setupListener(chromatographyButton, chromatographyCheckboxes);
			setupListener(spectroscopyButton, spectroscopyCheckBoxes);
			setControl(composite);
		}

		private void setupListener(Button button, Button[] checkBoxes) {

			SelectionListener listener = new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					if(button.getSelection()) {
						for(Button checkBox : checkBoxes) {
							DataCategory data = (DataCategory)checkBox.getData();
							if(checkBox.getSelection()) {
								categories.add(data);
							} else {
								categories.remove(data);
							}
							checkBox.setEnabled(true);
						}
					} else {
						for(Button checkBox : checkBoxes) {
							categories.remove(checkBox.getData());
							checkBox.setEnabled(false);
						}
					}
					setPageComplete(!categories.isEmpty());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			};
			button.addSelectionListener(listener);
			for(Button checkBox : checkBoxes) {
				checkBox.addSelectionListener(listener);
			}
			listener.widgetSelected(null);
		}

		private Button[] createButtons(Composite parent, DataCategory... categories) {

			Composite container = createContainer(parent);
			gridData(container).horizontalIndent = 20;
			Button[] buttons = new Button[categories.length];
			for(int i = 0; i < buttons.length; i++) {
				Button button = new Button(container, SWT.CHECK);
				button.setText(categories[i].getLabel());
				button.setData(categories[i]);
				button.setSelection(true);
				buttons[i] = button;
			}
			return buttons;
		}
	}
}
