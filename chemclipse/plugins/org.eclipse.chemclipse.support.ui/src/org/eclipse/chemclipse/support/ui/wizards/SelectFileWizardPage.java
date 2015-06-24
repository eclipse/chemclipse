/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SelectFileWizardPage extends WizardPage {

	private IWizardElements wizardElements;
	private String defaultFileName;
	private String fileExtension;
	private Label labelProjectName;
	private Text fileNameText;

	public SelectFileWizardPage(IWizardElements wizardElements, String defaultFileName, String fileExtension) {

		super("SelectFileWizardPage");
		setTitle("File Name");
		setDescription("Select a name for the file.");
		this.wizardElements = wizardElements;
		this.defaultFileName = defaultFileName;
		this.fileExtension = fileExtension;
	}

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			setSelectedProjectName();
			validateFileName();
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.verticalIndent = 5;
		/*
		 * Project name
		 */
		Label labelProject = new Label(composite, SWT.NONE);
		labelProject.setText("The new file will be created under the selected project path:");
		labelProject.setLayoutData(gridData);
		//
		labelProjectName = new Label(composite, SWT.WRAP);
		setSelectedProjectName();
		labelProjectName.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
		labelProjectName.setLayoutData(gridData);
		/*
		 * File name
		 */
		Label labelFile = new Label(composite, SWT.NONE);
		labelFile.setText("Select a file name:");
		labelFile.setLayoutData(gridData);
		//
		fileNameText = new Text(composite, SWT.BORDER);
		fileNameText.setText(defaultFileName);
		fileNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fileNameText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				validateFileName();
			}
		});
		//
		validateFileName();
		setControl(composite);
	}

	/**
	 * Sets the selected project name.
	 */
	private void setSelectedProjectName() {

		if(wizardElements.getContainer() != null) {
			labelProjectName.setText(wizardElements.getContainer().getFullPath().toString());
		} else {
			labelProjectName.setText("Please select a project.");
		}
	}

	/**
	 * Validates the made changes.
	 */
	private void validateFileName() {

		String message = null;
		//
		String fileName = fileNameText.getText().trim();
		if(fileName == null || fileName.equals("")) {
			message = "Please select a file name.";
		} else {
			/*
			 * Add the extension
			 */
			if(!fileName.endsWith(fileExtension)) {
				fileName += fileExtension;
			}
			/*
			 * Check that the file doesn't exists.
			 */
			IContainer container = wizardElements.getContainer();
			if(container != null) {
				if(container.exists()) {
					/*
					 * Is there a file already?
					 */
					if(container instanceof IFolder) {
						IFolder folder = (IFolder)container;
						IFile file = folder.getFile(fileName);
						if(file.exists()) {
							message = "The file already exists.";
						} else {
							/*
							 * O.K. - all checks are passed.
							 */
							wizardElements.setFileName(fileName);
						}
					} else if(container instanceof IProject) {
						IProject project = (IProject)container;
						/*
						 * Check whether the project exists or not.
						 */
						if(project.exists()) {
							IFile file = project.getFile(fileName);
							if(file.exists()) {
								message = "The file already exists.";
							} else {
								/*
								 * O.K. - all checks are passed.
								 */
								wizardElements.setFileName(fileName);
							}
						} else {
							/*
							 * O.K. - all checks are passed.
							 */
							wizardElements.setFileName(fileName);
						}
					} else {
						message = "Please select a valid folder to store the file.";
					}
				} else {
					/*
					 * If it's a new project, the file doesn't exist.
					 * Don't create the project, cause we don't know if the user finishes the wizard.
					 * O.K. - all checks are passed.
					 */
					wizardElements.setFileName(fileName);
				}
			} else {
				message = "Please select a valid project to store the file.";
			}
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}

	/**
	 * Updates whether the next page can be selected or not.
	 * 
	 * @param message
	 */
	private void updateStatus(String message) {

		setErrorMessage(message);
		setPageComplete(message == null);
	}
}