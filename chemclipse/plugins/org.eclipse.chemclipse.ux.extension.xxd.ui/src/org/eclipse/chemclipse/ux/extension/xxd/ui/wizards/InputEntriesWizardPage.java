/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 * Christoph Läubrich - support new lazy table model
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.swt.DataExplorerTreeUI;
import org.eclipse.chemclipse.ux.extension.ui.swt.DataExplorerTreeUI.DataExplorerTreeRoot;
import org.eclipse.chemclipse.ux.extension.ui.swt.MultiDataExplorerTreeUI;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public class InputEntriesWizardPage extends WizardPage {

	private InputWizardSettings inputWizardSettings;
	private DataExplorerTreeRoot treeSelection = DataExplorerTreeRoot.NONE;

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

		List<String> chromatograms = inputWizardSettings.getChromatogramWizardElements().getSelectedChromatograms();
		if(chromatograms.isEmpty()) {
			setPageComplete(false);
			setErrorMessage("Please select at least one valid data item");
		} else {
			setPageComplete(true);
			setErrorMessage(null);
		}
	}

	public void saveSelectedPath() {

		// FIXME
		// inputWizardSettings.saveSelectedPath(getTreeSelection());
	}

	public IChromatogramWizardElements getChromatogramWizardElements() {

		return inputWizardSettings.getChromatogramWizardElements();
	}

	@Override
	public void createControl(Composite parent) {

		MultiDataExplorerTreeUI explorerTreeUI = new MultiDataExplorerTreeUI(parent, DataExplorerTreeRoot.DRIVES, DataExplorerTreeRoot.HOME, DataExplorerTreeRoot.USER_LOCATION) {

			@Override
			protected void handleSelection(File[] files, DataExplorerTreeUI treeUI) {

				treeSelection = treeUI.getRoot();
				inputWizardSettings.getChromatogramWizardElements().clearSelectedChromatograms();
				DataExplorerContentProvider contentProvider = treeUI.getContentProvider();
				for(File file : files) {
					if(!contentProvider.getSupplierFileIdentifier(file).isEmpty()) {
						inputWizardSettings.getChromatogramWizardElements().addSelectedChromatogram(file.getAbsolutePath());
					}
				}
				validate();
			}
		};
		explorerTreeUI.setSupplierFileIdentifier(inputWizardSettings.getSupplierFileIdentifierList());
		explorerTreeUI.expandLastDirectoryPath();
		setControl(explorerTreeUI.getControl());
	}

	public DataExplorerTreeRoot getTreeSelection() {

		return treeSelection;
	}
}
