/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.l10n.SupportMessages;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SelectFileWizardPage extends AbstractExtendedWizardPage {

	private IWizardElements wizardElements;
	private String defaultReportName;
	private String reportExtension;
	private Label labelProjectName;
	private Text reportNameText;

	public SelectFileWizardPage(IWizardElements wizardElements, String defaultReportName, String reportExtension) {

		super("SelectFileWizardPage"); // $NON-NLS-1$
		setTitle(SupportMessages.labelFileName);
		setDescription(SupportMessages.labelFileNameInfo);
		this.wizardElements = wizardElements;
		this.defaultReportName = defaultReportName;
		this.reportExtension = reportExtension;
	}

	@Override
	public boolean canFinish() {

		/*
		 * Report name.
		 */
		String fileName = wizardElements.getFileName();
		if(fileName == null || fileName.equals("")) {
			return false;
		}
		return true;
	}

	@Override
	public void setDefaultValues() {

	}

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			setSelectedProjectName();
			validateReportName();
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
		labelProject.setText(SupportMessages.labelFileCreationProjectPath);
		labelProject.setLayoutData(gridData);
		//
		labelProjectName = new Label(composite, SWT.WRAP);
		setSelectedProjectName();
		labelProjectName.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
		labelProjectName.setLayoutData(gridData);
		/*
		 * Report name
		 */
		Label labelReport = new Label(composite, SWT.NONE);
		labelReport.setText(SupportMessages.labelSelectFileName);
		labelReport.setLayoutData(gridData);
		//
		reportNameText = new Text(composite, SWT.BORDER);
		reportNameText.setText(defaultReportName);
		reportNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		reportNameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				validateReportName();
			}
		});
		//
		validateReportName();
		setControl(composite);
	}

	/**
	 * Sets the selected project name.
	 */
	private void setSelectedProjectName() {

		if(wizardElements.getContainer() != null) {
			labelProjectName.setText(wizardElements.getContainer().getFullPath().toString());
		} else {
			labelProjectName.setText(SupportMessages.labelSelectProject);
		}
	}

	/**
	 * Validates the made changes.
	 */
	private void validateReportName() {

		String message = null;
		//
		String fileName = reportNameText.getText().trim();
		if(fileName == null || fileName.equals("")) {
			message = SupportMessages.processingSelectFileName;
		} else {
			/*
			 * Add the extension
			 */
			if(!fileName.endsWith(reportExtension)) {
				fileName += reportExtension;
			}
			/*
			 * Check that the report doesn't exists.
			 */
			IContainer container = wizardElements.getContainer();
			if(container != null) {
				if(container.exists()) {
					/*
					 * Is there a report file already?
					 */
					if(container instanceof IFolder folder) {
						IFile file = folder.getFile(fileName);
						if(file.exists()) {
							message = SupportMessages.processingFileExists;
						} else {
							/*
							 * O.K. - all checks are passed.
							 */
							wizardElements.setFileName(fileName);
						}
					} else if(container instanceof IProject project) {
						/*
						 * Check whether the project exists or not.
						 */
						if(project.exists()) {
							IFile file = project.getFile(fileName);
							if(file.exists()) {
								message = SupportMessages.processingFileExists;
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
						message = SupportMessages.processingSelectValidFolderFile;
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
				message = SupportMessages.processingSelectValidFolderFile;
			}
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}