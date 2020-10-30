/*******************************************************************************
 * Copyright (c) 2011, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 * Christoph Läubrich - support new lazy table model, support double-click
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.ui.swt.DataExplorerTreeRoot;
import org.eclipse.chemclipse.ux.extension.ui.swt.DataExplorerTreeUI;
import org.eclipse.chemclipse.ux.extension.ui.swt.MultiDataExplorerTreeUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public class InputEntriesWizardPage extends WizardPage {

	private final class WizardMultiDataExplorerTreeUI extends MultiDataExplorerTreeUI {

		private InputEntriesWizardPage page;

		private WizardMultiDataExplorerTreeUI(Composite parent, IPreferenceStore preferenceStore, InputEntriesWizardPage page) {

			super(parent, preferenceStore);
			this.page = page;
		}

		@Override
		protected void handleSelection(File[] files, DataExplorerTreeUI treeUI) {

			treeSelection = treeUI.getRoot();
			selectedItems.clear();
			for(File file : files) {
				Map<ISupplierFileIdentifier, Collection<ISupplier>> identifier = getIdentifierSupplier().apply(file);
				if(!identifier.isEmpty()) {
					selectedItems.put(file, identifier);
				}
			}
			validate();
		}

		@Override
		protected String getUserLocationPreferenceKey() {

			String key = inputWizardSettings.getUserLocationPreferenceKey();
			if(key != null) {
				return key;
			}
			return super.getUserLocationPreferenceKey();
		}

		@Override
		protected void handleDoubleClick(File file, DataExplorerTreeUI treeUI) {

			IWizardContainer container = page.getContainer();
			try {
				Method method = container.getClass().getDeclaredMethod("buttonPressed", int.class);
				boolean accessible = method.isAccessible();
				try {
					method.setAccessible(true);
					method.invoke(container, IDialogConstants.FINISH_ID);
				} finally {
					method.setAccessible(accessible);
				}
			} catch(Exception e) {
				// trigger not possible then
				e.printStackTrace();
			}
		}
	}

	private final InputWizardSettings inputWizardSettings;
	private DataExplorerTreeRoot treeSelection = DataExplorerTreeRoot.NONE;
	private final Map<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> selectedItems = new HashMap<>();
	private MultiDataExplorerTreeUI explorerTreeUI;

	public InputEntriesWizardPage(InputWizardSettings inputWizardSettings) {

		//
		super(InputEntriesWizardPage.class.getName());
		this.inputWizardSettings = inputWizardSettings;
		//
		setTitle(inputWizardSettings.getTitle());
		setDescription(inputWizardSettings.getDescription());
		validate();
	}

	private void validate() {

		if(selectedItems.isEmpty()) {
			setPageComplete(false);
			setErrorMessage("Please select at least one valid data item");
		} else {
			setPageComplete(true);
			setErrorMessage(null);
		}
	}

	public Map<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> getSelectedItems() {

		return selectedItems;
	}

	@Override
	public void createControl(Composite parent) {

		explorerTreeUI = new WizardMultiDataExplorerTreeUI(parent, inputWizardSettings.getPreferenceStore(), this);
		explorerTreeUI.setSupplierFileIdentifier(inputWizardSettings.getSupplierFileEditorSupportList());
		explorerTreeUI.expandLastDirectoryPath();
		setControl(explorerTreeUI.getControl());
	}

	public DataExplorerTreeRoot getTreeSelection() {

		return treeSelection;
	}

	public void savePath() {

		explorerTreeUI.saveLastDirectoryPath();
	}
}
