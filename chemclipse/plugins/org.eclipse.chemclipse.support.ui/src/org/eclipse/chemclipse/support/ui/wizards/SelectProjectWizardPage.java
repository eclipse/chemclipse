/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.l10n.Messages;
import org.eclipse.chemclipse.support.messages.ISupportMessages;
import org.eclipse.chemclipse.support.messages.SupportMessages;
import org.eclipse.chemclipse.support.ui.internal.provider.ProjectContentProvider;
import org.eclipse.chemclipse.support.ui.internal.provider.ProjectLabelProvider;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SelectProjectWizardPage extends AbstractExtendedWizardPage {

	private ISelection selection;
	private IWizardElements wizardElements;
	private Text projectNameText;
	private Button buttonSelectExistingProject;
	private Button buttonCreateNewProject;
	private Combo comboExistingProjects;
	private TreeViewer treeViewerProject;
	//
	private IWorkspaceRoot workspaceRoot;

	public SelectProjectWizardPage(ISelection selection, IWizardElements wizardElements) {
		super("SelectProjectWizardPage"); // $NON-NLS-1$
		setTitle(SupportMessages.INSTANCE().getMessage(ISupportMessages.LABEL_SELECT_CREATE_PROJECT));
		setDescription(SupportMessages.INSTANCE().getMessage(ISupportMessages.LABEL_SELECT_CREATE_PROJECT_INFO));
		this.selection = selection;
		this.wizardElements = wizardElements;
		workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
	}

	@Override
	public boolean canFinish() {

		/*
		 * Project name.
		 */
		IContainer container = wizardElements.getContainer();
		if(container == null) {
			return false;
		}
		return true;
	}

	@Override
	public void setDefaultValues() {

	}

	@Override
	public void createControl(Composite parent) {

		Messages messages = SupportMessages.INSTANCE();
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.verticalIndent = 5;
		GridData gridDataTreeViewer = new GridData(GridData.FILL_HORIZONTAL);
		gridDataTreeViewer.verticalIndent = 5;
		gridDataTreeViewer.heightHint = 200;
		/*
		 * Radio: Select project -------------------------------------------------------------------------
		 */
		buttonSelectExistingProject = new Button(composite, SWT.RADIO);
		buttonSelectExistingProject.setText(messages.getMessage(ISupportMessages.LABEL_SELECT_EXISTING_PROJECT));
		buttonSelectExistingProject.setLayoutData(gridData);
		buttonSelectExistingProject.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableWidgets(true);
				validateComboBoxSelection();
			}
		});
		/*
		 * Existing projects
		 */
		String[] items = getProjectItems();
		comboExistingProjects = new Combo(composite, SWT.READ_ONLY);
		comboExistingProjects.setItems(items);
		comboExistingProjects.setLayoutData(gridData);
		comboExistingProjects.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateExistingProjectSelection();
			}
		});
		/*
		 * TreeViewer of selected project.
		 */
		treeViewerProject = new TreeViewer(composite, SWT.NONE);
		treeViewerProject.setContentProvider(new ProjectContentProvider());
		treeViewerProject.setLabelProvider(new ProjectLabelProvider());
		treeViewerProject.getControl().setLayoutData(gridDataTreeViewer);
		treeViewerProject.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object object = ((IStructuredSelection)event.getSelection()).getFirstElement();
				validateTreeViewSelection(object);
			}
		});
		treeViewerProject.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				Object object = ((IStructuredSelection)event.getSelection()).getFirstElement();
				validateTreeViewSelection(object);
			}
		});
		/*
		 * Radio: New project -------------------------------------------------------------------------
		 */
		buttonCreateNewProject = new Button(composite, SWT.RADIO);
		buttonCreateNewProject.setText(messages.getMessage(ISupportMessages.LABEL_CREATE_NEW_PROJECT));
		buttonCreateNewProject.setLayoutData(gridData);
		buttonCreateNewProject.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableWidgets(false);
				validateTextSelection();
			}
		});
		/*
		 * Create a new project
		 */
		projectNameText = new Text(composite, SWT.BORDER);
		projectNameText.setText("");
		projectNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectNameText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				validateTextSelection();
			}
		});
		/*
		 * Set default choice
		 */
		if(selection != null && !selection.isEmpty()) {
			/*
			 * Try to set the selected project name.
			 */
			IContainer container = null;
			if(selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection)selection;
				Object object = structuredSelection.getFirstElement();
				if(object instanceof IResource) {
					if(object instanceof IContainer) {
						container = (IContainer)object;
					} else {
						IResource resource = (IResource)object;
						container = resource.getParent();
					}
					/*
					 * Select the project where the folder resides in.
					 */
					if(container != null) {
						IContainer rootContainer = container.getProject();
						if(rootContainer != null) {
							comboExistingProjects.setText(rootContainer.getName());
						}
						treeViewerProject.setInput(container);
						wizardElements.setContainer(container);
					} else {
						validateComboBoxSelection();
					}
				}
			}
			/*
			 * Set: Select project
			 */
			buttonSelectExistingProject.setSelection(true);
			enableWidgets(true);
		} else {
			/*
			 * Set: Create project
			 */
			buttonCreateNewProject.setSelection(true);
			enableWidgets(false);
			validateTextSelection();
		}
		//
		setControl(composite);
	}

	/**
	 * Returns the open and accessible project names.
	 * 
	 * @return String[]
	 */
	private String[] getProjectItems() {

		IProject[] projects = workspaceRoot.getProjects();
		List<IProject> projectsAccessible = new ArrayList<IProject>();
		for(IProject project : projects) {
			/*
			 * Add the project only if it is open and accessible.
			 */
			if(project.isAccessible()) {
				projectsAccessible.add(project);
			}
		}
		//
		String[] items = new String[projectsAccessible.size()];
		int counter = 0;
		for(IProject project : projectsAccessible) {
			items[counter++] = project.getName();
		}
		return items;
	}

	/**
	 * Enables/Disables selected elements.
	 * 
	 * choice = true (select project)
	 * choice = false (create project)
	 * 
	 * @param choice
	 */
	private void enableWidgets(boolean choice) {

		comboExistingProjects.setEnabled(choice);
		treeViewerProject.getControl().setEnabled(choice);
		projectNameText.setEnabled(!choice);
	}

	/**
	 * Updates the combo box and tree viewer if the existing project selection.
	 */
	private void updateExistingProjectSelection() {

		Messages messages = SupportMessages.INSTANCE();
		String message = null;
		//
		String projectName = comboExistingProjects.getText().trim();
		if(projectName == null || projectName.equals("")) {
			message = messages.getMessage(ISupportMessages.PROCESSING_SELECT_PROJECT);
		} else {
			IProject project = workspaceRoot.getProject(projectName);
			if(!project.exists()) {
				/*
				 * The user has typed in a wrong project name.
				 * It's a combo box, he shouldn't edit the field.
				 * But we know, he will.
				 */
				message = messages.getMessage(ISupportMessages.PROCESSING_PROJECT_NOT_EXISTS);
			} else {
				/*
				 * O.K. - all checks are passed.
				 * Expand the tree viewer.
				 */
				treeViewerProject.setInput(project);
				wizardElements.setContainer(project);
			}
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}

	/**
	 * Validates the tree view selection.
	 */
	private void validateTreeViewSelection(Object object) {

		Messages messages = SupportMessages.INSTANCE();
		String message = null;
		//
		if(object instanceof IFolder) {
			IFolder folder = (IFolder)object;
			if(!folder.exists()) {
				message = messages.getMessage(ISupportMessages.PROCESSING_SELECT_VALID_FOLDER);
			} else {
				/*
				 * O.K. - all checks are passed.
				 */
				wizardElements.setContainer(folder);
			}
		} else {
			message = messages.getMessage(ISupportMessages.PROCESSING_SELECT_VALID_PROJECT);
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}

	/**
	 * Validates combo box selection.
	 */
	private void validateComboBoxSelection() {

		Messages messages = SupportMessages.INSTANCE();
		String message = null;
		//
		String containerName = comboExistingProjects.getText().trim();
		if(containerName == null || containerName.equals("")) {
			message = messages.getMessage(ISupportMessages.PROCESSING_SELECT_PROJECT);
		} else {
			/*
			 * Existing project
			 */
			IProject project = workspaceRoot.getProject(containerName);
			if(!project.exists()) {
				message = messages.getMessage(ISupportMessages.PROCESSING_PROJECT_NOT_EXISTS);
			} else {
				/*
				 * O.K. - all checks are passed.
				 */
				wizardElements.setContainer(project);
			}
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}

	/**
	 * Validates the text field selection.
	 */
	private void validateTextSelection() {

		Messages messages = SupportMessages.INSTANCE();
		String message = null;
		//
		String containerName = projectNameText.getText().trim();
		if(containerName == null || containerName.equals("")) {
			message = messages.getMessage(ISupportMessages.PROCESSING_TYPE_PROJECT_NAME);
		} else {
			/*
			 * Check that the new project doesn't exists.
			 */
			IProject project = workspaceRoot.getProject(containerName);
			if(project.exists()) {
				message = messages.getMessage(ISupportMessages.PROCESSING_PROJECT_ALREADY_EXISTS);
			} else {
				/*
				 * O.K. - all checks are passed.
				 */
				wizardElements.setContainer(project);
			}
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}