/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.checkbox;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createContainer;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.gridData;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.radiobutton;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.DataCategoryGroup;
import org.eclipse.chemclipse.support.ui.wizards.SinglePageWizard;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class DataTypeTypeSelectionWizard {

	public static final int DEFAULT_WIDTH = 200;
	public static final int DEFAULT_HEIGHT = 300;
	//
	private static final String PREFIX = "DataTypeTypeSelectionWizard.";
	private static final DataCategoryGroup[] GROUPS = DataCategoryGroup.defaultGroups();

	public static DataCategoryGroup open(Shell shell, String description, IPreferenceStore preferenceStore) {

		preferenceStore.setDefault(groupPreferenceKey(GROUPS[0]), true);
		for(DataCategoryGroup group : GROUPS) {
			for(DataCategory category : group.getDataCategories()) {
				preferenceStore.setDefault(categoryPreferenceKey(group, category), true);
			}
		}
		//
		SelectionPage selectionPage = new SelectionPage(preferenceStore);
		selectionPage.setDescription(description);
		WizardDialog wizardDialog = new WizardDialog(shell, new SinglePageWizard("Select Data Categories", selectionPage));
		wizardDialog.setPageSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		wizardDialog.create();
		if(wizardDialog.open() == WizardDialog.OK) {
			return selectionPage.group;
		}
		return null;
	}

	private static String categoryPreferenceKey(DataCategoryGroup group, DataCategory category) {

		return PREFIX + group.getName() + "." + category.name();
	}

	private static String groupPreferenceKey(DataCategoryGroup group) {

		return PREFIX + group.getName();
	}

	private static final class SelectionPage extends WizardPage {

		private DataCategoryGroup group;
		private final IPreferenceStore preferenceStore;

		protected SelectionPage(IPreferenceStore preferenceStore) {

			super(SelectionPage.class.getName());
			this.preferenceStore = preferenceStore;
		}

		@Override
		public void createControl(Composite parent) {

			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout());
			for(DataCategoryGroup group : GROUPS) {
				String preferenceKey = groupPreferenceKey(group);
				Button radiobutton = radiobutton(composite, group.getName(), preferenceStore.getBoolean(preferenceKey));
				radiobutton.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {

						preferenceStore.setValue(preferenceKey, radiobutton.getSelection());
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {

					}
				});
				Button[] checkBoxes = createButtons(composite, group);
				setupListener(radiobutton, checkBoxes, group.getName());
			}
			setControl(composite);
		}

		private void setupListener(Button button, Button[] checkBoxes, String groupName) {

			SelectionListener listener = new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					boolean selection = button.getSelection();
					if(selection) {
						List<DataCategory> selectedCategories = new ArrayList<DataCategory>();
						for(Button checkBox : checkBoxes) {
							DataCategory data = (DataCategory)checkBox.getData();
							if(checkBox.getSelection()) {
								selectedCategories.add(data);
							}
							checkBox.setEnabled(true);
						}
						group = new DataCategoryGroup(groupName, selectedCategories);
					} else {
						for(Button checkBox : checkBoxes) {
							checkBox.setEnabled(false);
						}
					}
					setPageComplete(group != null && !group.getDataCategories().isEmpty());
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

		private Button[] createButtons(Composite parent, DataCategoryGroup group) {

			DataCategory[] dataCategories = group.getDataCategories().toArray(new DataCategory[0]);
			Composite container = createContainer(parent);
			gridData(container).horizontalIndent = 20;
			Button[] buttons = new Button[dataCategories.length];
			for(int i = 0; i < buttons.length; i++) {
				DataCategory category = dataCategories[i];
				String preferenceKey = categoryPreferenceKey(group, category);
				Button button = checkbox(container, dataCategories[i].getLabel(), preferenceStore.getBoolean(preferenceKey));
				button.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {

						preferenceStore.setValue(preferenceKey, button.getSelection());
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {

					}
				});
				button.setData(category);
				buttons[i] = button;
			}
			return buttons;
		}
	}
}
