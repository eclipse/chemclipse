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
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.internal.resources.WorkspaceRoot;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;

public class RCPUtil {

	private static final Logger log = Logger.getLogger(RCPUtil.class);
	private static IEventBroker eventBroker;

	public static void throwCoreException(String message) throws CoreException {

		IStatus status = new Status(IStatus.ERROR, "org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	public static java.util.List<IProject> findProteomsProjects() {

		ArrayList<IProject> resultProjects = new ArrayList<>();
		IWorkspace w = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = w.getRoot();
		IProject[] projects = root.getProjects();
		for(IProject project : projects) {
			try {
				if(!project.isOpen()) {
					File file = project.getLocation().toFile();
					File projectFile = new File(file, ".project");
					// log.debug(projectFile);
					IPath projectDotProjectFile = new org.eclipse.core.runtime.Path(projectFile.getAbsolutePath());
					IProjectDescription desc = w.loadProjectDescription(projectDotProjectFile);
					if(desc.hasNature(ProteomsProjectNature.NATURE_ID)) {
						resultProjects.add(project);
					}
				} else {
					if(project.hasNature(ProteomsProjectNature.NATURE_ID)) {
						resultProjects.add(project);
					}
				}
				// if(contatinProteomsNature(project.getDescription().getNatureIds())) {
				// resultProjects.add(project);
				// }
			} catch(CoreException e) {
				log.error("Error resource project", e);
				RCPUtil.handleError(e);
			}
		}
		return resultProjects;
	}

	private static void handleError(Throwable e) {

		// TODO: show error dialog
		e.printStackTrace();
	}

	/**
	 * @param natureIds
	 * @return
	 */
	private static boolean contatinProteomsNature(String[] natureIds) {

		for(String id : natureIds) {
			if(ProteomsProjectNature.NATURE_ID.equals(id)) {
				return true;
			}
		}
		return false;
	}

	public static void showWarningMessageDialog(String message, Shell shell) {

		Shell shell2;
		if(shell == null) {
			shell2 = Display.getDefault().getActiveShell();
		} else {
			shell2 = shell;
		}
		if(shell.getDisplay().getThread() == Thread.currentThread()) {
			MessageDialog.openWarning(shell, "Warning", message);
		} else {
			shell.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {

					MessageDialog.openWarning(shell2, "Warning", message);
				}
			});
		}
	}

	public static Path getProjectPath(IProject project) {

		File file = project.getLocation().toFile();
		Path path = file.toPath();
		return path;
	}

	public static void openProteomsNewProject(Shell shell) {

		IWizardRegistry w = PlatformUI.getWorkbench().getNewWizardRegistry();
		IWizardDescriptor wiz = w.findWizard("org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.wizards.CreateProjectWizard");
		IWorkbenchWizard wizard;
		try {
			wizard = wiz.createWizard();
			WizardDialog wd = new WizardDialog(shell, wizard);
			wd.setTitle(wizard.getWindowTitle());
			wizard.init(PlatformUI.getWorkbench(), null);
			wd.open();
		} catch(CoreException e1) {
			log.error("Error open new project wizard. " + e1.getMessage(), e1);
			handleError(e1);
		}
	}

	public static void openProteomsImportProject(Shell shell) {

		IWizardRegistry w = PlatformUI.getWorkbench().getImportWizardRegistry();
		IWizardDescriptor wiz = w.findWizard("org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.import.ImportMSdataWizard");
		IWorkbenchWizard wizard;
		try {
			wizard = wiz.createWizard();
			WizardDialog wd = new WizardDialog(shell, wizard);
			wd.setTitle(wizard.getWindowTitle());
			wizard.init(PlatformUI.getWorkbench(), null);
			wd.open();
		} catch(CoreException e) {
			log.error("Error open import wizard. " + e.getMessage(), e);
			handleError(e);
		}
	}

	public static String getClass(Object object) {

		// TODO: move to another class
		if(object != null) {
			if(object.getClass().isArray()) {
				return object.getClass().getName() + "[]";
			}
			return object.getClass().getName();
		}
		return "null";
	}

	public static void sendEvent(String topic, Object data, Shell shell) {

		IEventBroker broker = getEventBroker();
		if(broker == null) {
			log.debug("Canot find brokker");
			return;
		}
		broker.post(topic, data);
	}

	private static IEventBroker getEventBroker() {

		if(eventBroker == null) {
			IEclipseContext serviceContext = EclipseContextFactory.getServiceContext(Activator.getDefault().getBundle().getBundleContext());
			eventBroker = serviceContext.get(IEventBroker.class);
		}
		return eventBroker;
	}

	public static boolean isUIthread(Shell shell) {

		if(shell == null) {
			return false;
		}
		if(Thread.currentThread() == shell.getDisplay().getThread()) {
			return true;
		}
		return false;
	}

	public static Display getStandardDisplay() {

		Display d;
		d = Display.getCurrent();
		if(d == null)
			d = Display.getDefault();
		return d;
	}

	public static StringBuilder getEventDescription(IResourceChangeEvent event) {

		{
			IResourceDelta delta = event.getDelta();
			if(delta != null) {
				IResource resource = delta.getResource();
				int type = resource.getType();
				int kind = delta.getKind();
				int flags = delta.getFlags();
				if(kind == IResourceDelta.CHANGED && type == IResource.PROJECT) {
					if((flags & IResourceDelta.OPEN) != 0) {
						log.debug("OPEN PROJECT " + resource);
					}
				}
			}
		}
		StringBuilder b = new StringBuilder();
		IResource res = event.getResource();
		b.append("Resource " + res + " ");
		System.out.print("Event type: ");
		switch(event.getType()) {
			case IResourceChangeEvent.PRE_CLOSE:
				b.append("PRE_CLOSE  "); // project close is here
				break;
			case IResourceChangeEvent.PRE_DELETE:
				b.append("PRE DELETE ");
				break;
			case IResourceChangeEvent.POST_CHANGE:
				b.append("POST_CHANGE ");
				// event.getDelta().accept(new DeltaPrinter());
				break;
			case IResourceChangeEvent.PRE_BUILD:
				b.append("PRE BUILKD");
				// event.getDelta().accept(new DeltaPrinter());
				break;
			case IResourceChangeEvent.POST_BUILD:
				b.append("POST_BUIDL. ");
				// event.getDelta().accept(new DeltaPrinter());
				break;
		}
		IResourceDelta delta = event.getDelta();
		if(delta != null) {
			b.append(" DELTA KIND: ");
			IResource resDelta = delta.getResource();
			b.append(" Resource delta: " + resDelta);
			switch(delta.getKind()) {
				case IResourceDelta.ADDED:
					b.append(" ADDED ");
					break;
				case IResourceDelta.REMOVED:
					b.append(" REMOVED ");
					break;
				case IResourceDelta.OPEN:
					b.append(" OPEN ");
					break;
				case IResourceDelta.CHANGED:
					b.append(" CHANGED ");
					int flags = delta.getFlags();
					b.append(" Flags: " + flags + " ");
					if((flags & IResourceDelta.CONTENT) != 0) {
						b.append(" contend change ");
					}
					if((flags & IResourceDelta.REPLACED) != 0) {
						b.append(" Content Replaced ");
					}
					if((flags & IResourceDelta.MARKERS) != 0) {
						b.append(" Marker Change ");
						// IMarkerDelta[] markers = delta.getMarkerDeltas();
						// if interested in markers, check these deltas
					}
					if(((flags & IResourceDelta.OPEN) != 0)) {
						b.append(" OPEN "); // not work
					}
					if(flags == 0) {
						b.append(" resource type=" + resDelta.getType() + " ");
						if(resDelta instanceof WorkspaceRoot) {
							WorkspaceRoot p = (WorkspaceRoot)resDelta;
							IResourceDelta[] affectedChildren = delta.getAffectedChildren();
							for(IResourceDelta iResourceDelta : affectedChildren) {
								// Got project affected
								log.debug("Afected " + iResourceDelta.getResource());
							}
							b.append(" flags 0 Project is open= " + resDelta.getProject());
						}
					}
					break;
			}
		}
		return b;
	}
}
