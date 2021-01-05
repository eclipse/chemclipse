/*******************************************************************************
 * Copyright (c) 2016, 2021 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.project;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class CreateProjectPage extends WizardPage {

	private Text projectText;
	private Label lblprojectName;
	private Button btnTrypsin;
	private Label lblEnzimes;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public CreateProjectPage() {

		super("wizardPage");
		setTitle("New Peptide Tandem Mass Spectra project");
		setDescription("This wizard creates a new Peptide Tandem Mass Spectra project.");
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		lblprojectName = new Label(container, SWT.NULL);
		lblprojectName.setText("&ProjectName");
		projectText = new Text(container, SWT.BORDER | SWT.SINGLE);
		projectText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				textChanged();
			}
		});
		setControl(container);
		lblEnzimes = new Label(container, SWT.NONE);
		lblEnzimes.setText("Enzimes");
		btnTrypsin = new Button(container, SWT.RADIO);
		btnTrypsin.setSelection(true);
		btnTrypsin.setText("Trypsin");
		textChanged();
	}

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			projectText.setFocus();
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */
	private void textChanged() {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		String projectName = projectText.getText();
		if(projectName == null || projectName.isEmpty()) {
			setPageComplete(false);
			return;
		}
		IStatus validationResult = workspace.validateName(projectName, IResource.PROJECT);
		if(!validationResult.isOK()) {
			updateStatus("Wrong name '" + projectName + "'");
			return;
		}
		if(workspace.getRoot().exists(new Path(projectName))) {
			updateStatus("Project exist '" + projectName + "'");
			return;
		}
		updateStatus(null);
	}

	@SuppressWarnings("unused")
	private void dialogChanged2() {

		IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getProjectName()));
		String fileName = "";
		if(getProjectName().length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		if(container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}
		if(!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if(fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		if(fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("File name must be valid");
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if(dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if(ext.equalsIgnoreCase("mpe") == false) {
				updateStatus("File extension must be \"mpe\"");
				return;
			}
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {

		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getProjectName() {

		return projectText.getText();
	}
}