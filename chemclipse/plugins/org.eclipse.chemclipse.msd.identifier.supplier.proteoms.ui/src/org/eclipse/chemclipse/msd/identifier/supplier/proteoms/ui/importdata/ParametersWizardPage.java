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
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.importdata;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.Activator;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.RCPUtil;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.importdata.ImportParameters.DERIVATIZATION_REAGENTS;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.importdata.ImportParameters.ENZYME;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.importdata.ImportParameters.FILE_FORMAT;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.project.CreateProjectWizard;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;

public class ParametersWizardPage extends WizardPage {

	private static final Logger log = Logger.getLogger(ParametersWizardPage.class);
	private static final String PAGE_NAME = "selectParameters";
	private ImportParameters parameters;
	private ListViewer listViewer;

	public ParametersWizardPage(ImportParameters parameters, IStructuredSelection selection) {

		super(PAGE_NAME);
		setTitle("Import MS data"); // NON-NLS-1
		setDescription("Select parameters"); // NON-NLS-1
		this.parameters = parameters;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(1, false));
		Group grpSpectrumFormat = new Group(container, SWT.NONE);
		grpSpectrumFormat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpSpectrumFormat.setLayout(new GridLayout(1, false));
		grpSpectrumFormat.setText("Spectrum format");
		{// MGF
			Button btnRadioMGFappliedBiosystems = new Button(grpSpectrumFormat, SWT.RADIO);
			btnRadioMGFappliedBiosystems.setText(parameters.getFileFormat().getFormatName());
			btnRadioMGFappliedBiosystems.setToolTipText(parameters.getFileFormat().getFormatName());
			btnRadioMGFappliedBiosystems.setSelection(parameters.getFileFormat() == FILE_FORMAT.MFG_APPLIED_BIOSYSTEM);
		}
		Group grpEnzyme = new Group(container, SWT.NONE);
		grpEnzyme.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpEnzyme.setText("Enzyme");
		{ // Enzyme trypsin
			Button btnRadioEnzymeTrypsin = new Button(grpEnzyme, SWT.RADIO);
			btnRadioEnzymeTrypsin.setBounds(10, 31, 111, 20);
			btnRadioEnzymeTrypsin.setText(ENZYME.TRYPSIN.getNameEnzyme());
			btnRadioEnzymeTrypsin.setToolTipText(ENZYME.TRYPSIN.getDescription());
			btnRadioEnzymeTrypsin.setSelection(parameters.getEnzyme() == ENZYME.TRYPSIN);
		}
		Group grpDerivatizationReagents = new Group(container, SWT.NONE);
		grpDerivatizationReagents.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpDerivatizationReagents.setLayout(new GridLayout(1, false));
		grpDerivatizationReagents.setText("Derivatization reagents");
		{ // Der. reag. none
			Button btnRadioNonDetivatizationReagents = new Button(grpDerivatizationReagents, SWT.RADIO);
			btnRadioNonDetivatizationReagents.setText("None");
			btnRadioNonDetivatizationReagents.setSelection(parameters.getDerivatiozationReagents() == DERIVATIZATION_REAGENTS.NONE);
		}
		{ // Der. reag. CAF/CAF
			Button btnRadioCAFCAF = new Button(grpDerivatizationReagents, SWT.RADIO);
			btnRadioCAFCAF.setText("CAF/CAF");
		}
		Group grpProject = new Group(container, SWT.NONE);
		grpProject.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpProject.setText("Project");
		grpProject.setLayout(new GridLayout(1, false));
		listViewer = new ListViewer(grpProject, SWT.BORDER | SWT.V_SCROLL);
		List listProject = listViewer.getList();
		listProject.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		Button btnNewButton = new Button(grpProject, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					CreateProjectWizard wizard = new CreateProjectWizard();
					wizard.init(Activator.getDefault().getWorkbench(), null);
					WizardDialog dialog = new WizardDialog(e.display.getActiveShell(), wizard);
					dialog.setBlockOnOpen(true);
					int result = dialog.open();
					if(result == WizardDialog.OK) {
						IProject newProject = wizard.getCreatedProject();
						log.debug("New project " + newProject);
						// listViewer.sets
						getControl().getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {

								listViewer.setSelection(new StructuredSelection(newProject), true);
							}
						});
						// listViewer.getList().fo
					}
					updateProjectInList();
				} catch(Throwable e1) {
					log.error(e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnNewButton.setText("New project...");
		setUpProjectViewer();
	}

	@Override
	public boolean canFlipToNextPage() {

		return super.canFlipToNextPage();
	}

	@Override
	public IWizardPage getNextPage() {

		if(parameters.getFileFormat() == FILE_FORMAT.MFG_APPLIED_BIOSYSTEM) {
			ImportWizard im = (ImportWizard)getWizard();
			return im.getPage(MGFimportWizardPage.PAGE_NAME);
		}
		return super.getNextPage();
	}

	@Override
	public boolean isPageComplete() {

		if(listViewer.getSelection().isEmpty()) {
			return false;
		}
		return true;
	}

	private void setUpProjectViewer() {

		listViewer.setContentProvider(ArrayContentProvider.getInstance());
		listViewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IProject) {
					IProject p = (IProject)element;
					return p.getName();
				}
				if(element instanceof IResource) {
					IResource r = (IResource)element;
					return r.getName();
				}
				log.debug("What classs " + element.getClass());
				return "???";
			}
		});
		//
		// listViewer.setSorter(new ViewerSorter() {
		//
		// @Override
		// public int compare(Viewer viewer, Object e1, Object e2) {
		//
		// IProject p1 = (IProject)e1;
		// IProject p2 = (IProject)e2;
		// return Long.compare(p1.getLocalTimeStamp(), p2.getLocalTimeStamp());
		// // return super.compare(viewer, e1, e2);
		// }
		// });
		//
		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				getWizard().getContainer().updateButtons();
				IStructuredSelection sel = listViewer.getStructuredSelection();
				if(!sel.isEmpty()) {
					IProject project = (IProject)sel.getFirstElement();
					parameters.setProject(project);
				}
			}
		});
		// ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
		// IBaseLabelProvider
		updateProjectInList();
	}

	private void updateProjectInList() {

		java.util.List<IProject> projects = RCPUtil.findProteomsProjects();
		listViewer.setInput(projects);
		// Select project if is only element
		if(projects.size() == 1) {
			listViewer.getList().select(0);
		}
		// listViewer.u
	}

	@Override
	public void dispose() {

		// ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	// @Override
	// public void resourceChanged(IResourceChangeEvent event) {
	//
	// IResourceDelta d = event.getDelta();
	// if(d != null) {
	// log.debug("kind " + d.getKind());
	// if(d.getKind() == IResourceDelta.ADDED) {
	// log.debug("Project added " + d.getResource());
	// updateProjectData();
	// }
	// }
	// }
}
