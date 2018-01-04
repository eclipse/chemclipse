/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.parts;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMS;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMSMS;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.ImportDataSelection;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.ProjectWrapper;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.ProteomsEvent;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.RCPUtil;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.importdata.ImportParameters;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

public class ProteomSpectraPart implements IResourceChangeListener {

	private static final Logger log = Logger.getLogger(ProteomSpectraPart.class);
	@Inject
	ESelectionService selectionService;
	@Inject
	IEventBroker eventBroker;
	private TreeViewer treeViewer;
	private ContentProvider contentProvider;
	private Composite composite;

	@Inject
	@Optional
	void createNewProjectHandler(@UIEventTopic(ProteomsEvent.CREATED_NEW_PROJECT) IProject project) {

		log.debug("tree " + treeViewer);
		if(treeViewer != null)
			treeViewer.getControl().getDisplay().asyncExec(() -> {
				treeViewer.add(treeViewer.getInput(), new ProjectWrapper(project));
				log.debug("Create new project " + project);
			});
	}

	@Inject
	public void setSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) ImportDataSelection sel) {

		if(sel != null) {
			log.debug("Got selection " + sel);
			ImportParameters parameters = sel.getParameters();
			IProject project = parameters.getProject();
			// ProjectWrapper.saveToDisk(project, sel.getParsedMSlist());
			if(contentProvider != null) {
				contentProvider.refreshProject(project);
			}
		}
	}

	@PostConstruct
	public void createControls(Composite composite) {

		this.composite = composite;
		composite.setLayout(new FillLayout());
		// List<IProject> proteomsProjects = RCPUtil.findProteomsProjects();
		// if(proteomsProjects.isEmpty()) {
		// setUpButtonAddData(composite);
		// return;
		// }
		initTreeViewer(composite);
	}

	private void initTreeViewer(Composite composite) {

		Composite compositeMain = new Composite(composite, SWT.NONE);
		compositeMain.setLayout(new FillLayout(SWT.HORIZONTAL));
		treeViewer = new TreeViewer(compositeMain, SWT.BORDER);
		setUpTreeData();
		setProjectListener();
		setMenu();
	}

	private void setMenu() {

		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(manager1 -> {
			// fill menu
			ITreeSelection sel = treeViewer.getStructuredSelection();
			Object el = sel.getFirstElement();
			if(el instanceof ProjectWrapper) {
				ProjectWrapper p = (ProjectWrapper)el;
				manager1.add(new Action("Delete") {

					@Override
					public void run() {

						try {
							p.getProject().delete(true, new NullProgressMonitor());
						} catch(CoreException e) {
							e.printStackTrace();
							log.warn("Error ", e);
						}
					}
				});
			}
		});
		Menu menu = manager.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {

		log.debug(RCPUtil.getEventDescription(event));
		if(event == null || event.getDelta() == null)
			return;
		// log.debug("event type " + event.getType());
		if(event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			IResource resource = event.getResource();
			if(resource instanceof IProject) {
				IProject p = (IProject)resource;
				if(p.isOpen()) {
					log.debug("Close project " + p.getName());
				}
				// log.debug("PRE CLOSE project " + p.getName());
			}
		}
		{
			IResourceDelta d = event.getDelta();
			if(d != null) {
				int flags = d.getFlags();
				if((flags & IResourceDelta.OPEN) != 0) {
					log.debug("Open project " + d.getResource());
				}
			}
		}
		IResourceDelta d = event.getDelta();
		if(d != null) {
			// log.debug("Delta kind " + d.getKind() + " delta flags " + d.getFlags());
			if(d.getKind() == IResourceDelta.ADDED) {
				if(d.getResource().getType() == IResource.PROJECT) {
					// New project is added
					IProject project = (IProject)d.getResource();
					log.debug("Added new Project " + project);
				}
			}
			if(d.getFlags() == IResourceDelta.OPEN) {
				// project is open or close
				IProject project = (IProject)d.getResource();
				if(project != null)
					log.debug("Project " + project.getName() + " is open=" + project.isOpen());
			}
		}
	}

	private void setProjectListener() {

		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	@PreDestroy
	public void preDestroy() {

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	private void setUpTreeData() {

		contentProvider = new ContentProvider();
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new LabelPro());
		List<IProject> projects = RCPUtil.findProteomsProjects();
		ProjectWrapper[] projectsWrapperArray = new ProjectWrapper[projects.size()];
		int c = 0;
		for(IProject iProject : projects) {
			projectsWrapperArray[c++] = new ProjectWrapper(iProject);
		}
		treeViewer.setInput(projectsWrapperArray);
	}

	private void setUpButtonAddData(Composite composite) {

		Button button = new Button(composite, SWT.PUSH);
		button.setText("Create new Project");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				// WizardDialog d = new WizardDialog(e.display.getActiveShell(), new ImportWizard());
				// d.open();
				RCPUtil.openProteomsNewProject(e.display.getActiveShell());
			}
		});
	}
}

class LabelPro extends LabelProvider {

	private static final Logger log = Logger.getLogger(LabelPro.class);

	@Override
	public String getText(Object element) {

		if(element instanceof ProjectWrapper) {
			ProjectWrapper p = (ProjectWrapper)element;
			return p.getProject().getName();
		}
		if(element instanceof SpectrumMSMS) {
			SpectrumMSMS msms = (SpectrumMSMS)element;
			return msms.getName();
		}
		if(element instanceof SpectrumMS) {
			SpectrumMS ms = (SpectrumMS)element;
			return ms.getName();
		}
		log.warn("??? " + element);
		return "???";
	}
}

class ContentProvider implements ITreeContentProvider {

	private static final Logger log = Logger.getLogger(ContentProvider.class);
	/**
	 * MS id => project
	 */
	private HashMap<Long, ProjectWrapper> map;
	private static final Object[] EMPTY_ARRAY = new Object[0];
	private List<ProjectWrapper> projects;
	private Viewer viewer;

	@Override
	public void dispose() {

	}

	public void refreshProject(IProject project) {

		if(projects != null) {
			for(ProjectWrapper p : projects) {
				if(p.getProject() == project) {
					((TreeViewer)viewer).refresh(p);
					return;
				}
			}
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		// log.debug("Imput changed " + RCPUtil.getClass(oldInput) + " new= " + RCPUtil.getClass(newInput));
		if(newInput == null) {
			return;
		}
		this.viewer = viewer;
		map = new HashMap<>();
		ProjectWrapper[] wrappers = (ProjectWrapper[])newInput;
		for(ProjectWrapper projectWrapper : wrappers) {
			List<SpectrumMS> mSlist = projectWrapper.getMSlist();
			for(SpectrumMS ms : mSlist) {
				map.put(ms.getId(), projectWrapper);
			}
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {

		if(inputElement instanceof ProjectWrapper[]) {
			return (ProjectWrapper[])inputElement;
		}
		if(inputElement instanceof List<?>) {
			List<?> list = (List<?>)inputElement;
			return list.toArray();
		}
		log.warn("??? " + inputElement);
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		if(parentElement instanceof ProjectWrapper) {
			ProjectWrapper p = (ProjectWrapper)parentElement;
			List<SpectrumMS> msList = p.getMSlist();
			if(msList != null) {
				return msList.toArray();
			} else {
				return null;
			}
		}
		if(parentElement instanceof SpectrumMS) {
			SpectrumMS ms = (SpectrumMS)parentElement;
			if(ms.getNumberOfMSMS() == 0) {
				return EMPTY_ARRAY;
			}
			return ms.getMsmsSpectrumsChildren().toArray();
		}
		log.warn("??? " + parentElement);
		return null;
	}

	@Override
	public Object getParent(Object element) {

		if(element instanceof SpectrumMSMS) {
			SpectrumMSMS msms = (SpectrumMSMS)element;
			return msms.getParentMS();
		}
		if(element instanceof SpectrumMS) {
			SpectrumMS ms = (SpectrumMS)element;
			return map.get(ms.getId());
		}
		log.warn("??? " + element);
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {

		if(element instanceof ProjectWrapper) {
			ProjectWrapper p = (ProjectWrapper)element;
			if(!p.getProject().isOpen()) {
				return false;
			}
			if(p.getMSlist().isEmpty()) {
				return false;
			}
			return true;
		}
		if(element instanceof SpectrumMSMS) {
			return false;
		}
		if(element instanceof SpectrumMS) {
			SpectrumMS ms = (SpectrumMS)element;
			if(ms.getNumberOfMSMS() > 0) {
				return true;
			}
			return false;
		}
		log.warn("??? " + element);
		return false;
	}
}
