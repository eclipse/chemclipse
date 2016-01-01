/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.editors;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.internal.runnables.BatchProcessRunnable;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public class BatchProcessEditorPage implements IMultiEditorPage {

	private static final Logger logger = Logger.getLogger(BatchProcessEditorPage.class);
	private FormToolkit toolkit;
	private int pageIndex;

	public BatchProcessEditorPage(BatchProcessJobEditor editorPart, Composite container) {
		createPage(editorPart, container);
	}

	@Override
	public int getPageIndex() {

		return pageIndex;
	}

	@Override
	public void dispose() {

		if(toolkit != null) {
			toolkit.dispose();
		}
	}

	@Override
	public void setBatchProcessJob(IBatchProcessJob batchProcessJob) {

		if(batchProcessJob != null) {
			/*
			 * Set the batch process job if needed.
			 */
		}
	}

	// ---------------------------------------private methods
	/**
	 * Creates the page.
	 * 
	 */
	private void createPage(BatchProcessJobEditor editorPart, Composite container) {

		/*
		 * Create the parent composite.
		 */
		Composite parent = new Composite(container, SWT.NONE);
		parent.setLayout(new FillLayout());
		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		/*
		 * Forms API
		 */
		toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm scrolledForm = toolkit.createScrolledForm(parent);
		Composite scrolledFormComposite = scrolledForm.getBody();
		scrolledFormComposite.setLayout(new TableWrapLayout());
		scrolledForm.setText("Batch Process Editor");
		/*
		 * Create the sections.
		 */
		createPropertiesSection(scrolledFormComposite, editorPart);
		createExecuteSection(editorPart, scrolledFormComposite);
		/*
		 * Get the page index.
		 */
		pageIndex = editorPart.addPage(parent);
	}

	/**
	 * Creates the properties section.
	 */
	private void createPropertiesSection(Composite parent, final BatchProcessJobEditor editorPart) {

		Section section;
		Composite client;
		GridLayout layout;
		GridData gridData;
		Label label;
		/*
		 * Section
		 */
		section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Info");
		section.setDescription("Please use the report supplier tab to select report formats for the processed chromatograms.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		client = toolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		label = toolkit.createLabel(client, "");
		label.setLayoutData(gridData);
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		toolkit.paintBordersFor(client);
	}

	/**
	 * Creates the run section.
	 * 
	 * @param parent
	 */
	private void createExecuteSection(BatchProcessJobEditor editorPart, Composite parent) {

		Section section;
		Composite client;
		GridLayout layout;
		GridData gridData;
		Label label;
		/*
		 * Section
		 */
		section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Progress");
		section.setDescription("Run the batch job, after the entries have been edited.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		client = toolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 20;
		gridData.heightHint = 30;
		/*
		 * Label I
		 */
		label = toolkit.createLabel(client, "1. Edit the input,process and output entries.");
		label.setLayoutData(gridData);
		/*
		 * Link Pages
		 */
		createInputFilesPageHyperlink(editorPart, client, gridData);
		createProcessEntriesPageHyperlink(editorPart, client, gridData);
		createOutputFilesPageHyperlink(editorPart, client, gridData);
		createReportEntriesPageHyperlink(editorPart, client, gridData);
		/*
		 * Label II
		 */
		label = toolkit.createLabel(client, "2. Run the batch process job after editing the entries.");
		label.setLayoutData(gridData);
		/*
		 * Link Run
		 * The label is a dirty workaround to make the run button full visible.
		 * Otherwise, it is cutted at its bottom. I think, it has something to
		 * do with the client height.
		 */
		createProcessHyperlink(editorPart, client, gridData);
		label = toolkit.createLabel(client, "");
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		toolkit.paintBordersFor(client);
	}

	/**
	 * Input files page.
	 * 
	 * @param editorPart
	 * @param client
	 * @param gridData
	 */
	private void createInputFilesPageHyperlink(final BatchProcessJobEditor editorPart, Composite client, GridData gridData) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = toolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		imageHyperlink.setText("Configure input files");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {

				editorPart.setActivePage(BatchProcessJobEditor.INPUT_FILES_PAGE);
			}
		});
	}

	/**
	 * Process entries page.
	 * 
	 * @param editorPart
	 * @param client
	 * @param gridData
	 */
	private void createProcessEntriesPageHyperlink(final BatchProcessJobEditor editorPart, Composite client, GridData gridData) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = toolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		imageHyperlink.setText("Configure process entries");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {

				editorPart.setActivePage(BatchProcessJobEditor.PROCESS_ENTRIES_PAGE);
			}
		});
	}

	/**
	 * Output files page.
	 * 
	 * @param editorPart
	 * @param client
	 * @param gridData
	 */
	private void createOutputFilesPageHyperlink(final BatchProcessJobEditor editorPart, Composite client, GridData gridData) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = toolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		imageHyperlink.setText("Configure output files");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {

				editorPart.setActivePage(BatchProcessJobEditor.OUTPUT_FILES_PAGE);
			}
		});
	}

	/**
	 * Report entries page.
	 * 
	 * @param editorPart
	 * @param client
	 * @param gridData
	 */
	private void createReportEntriesPageHyperlink(final BatchProcessJobEditor editorPart, Composite client, GridData gridData) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = toolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		imageHyperlink.setText("Configure report supplier");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {

				editorPart.setActivePage(BatchProcessJobEditor.REPORT_ENTRIES_PAGE);
			}
		});
	}

	/**
	 * Process hyperlink.
	 * 
	 * @param client
	 * @param gridData
	 */
	private void createProcessHyperlink(final BatchProcessJobEditor editorPart, Composite client, GridData gridData) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = toolkit.createImageHyperlink(client, SWT.WRAP);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		imageHyperlink.setText("Run the batch process");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {

				/*
				 * Try to select and show the perspective and view.
				 */
				PerspectiveSwitchHandler.focusPerspectiveAndView(IPerspectiveAndViewIds.PERSPECTIVE_MSD, IPerspectiveAndViewIds.VIEW_PROCESSING_INFO);
				Display display = Display.getCurrent();
				/*
				 * The file must be saved.
				 * Don't show a progress monitor.
				 */
				if(editorPart.isDirty()) {
					editorPart.doSave(new NullProgressMonitor());
				}
				/*
				 * Run the batch process.
				 */
				IPath path = ((IFileEditorInput)editorPart.getEditorInput()).getFile().getLocation();
				String filePath = path.toFile().toString();
				if(filePath != null && !filePath.equals("")) {
					IRunnableWithProgress runnable = new BatchProcessRunnable(filePath);
					ProgressMonitorDialog monitor = new ProgressMonitorDialog(display.getActiveShell());
					try {
						monitor.run(true, true, runnable);
						/*
						 * Refresh the workspace
						 */
						refreshWorkspace(path);
					} catch(InvocationTargetException ex) {
						logger.warn(ex);
					} catch(InterruptedException ex) {
						logger.warn(ex);
					}
				}
			}
		});
	}

	private void refreshWorkspace(IPath path) {

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		String workspace = root.getLocation().toFile().toString();
		String file = path.toFile().getParent();
		String containerName = file.replace(workspace, "");
		IResource resource = root.findMember(new Path("/" + containerName));
		if(resource != null && resource.exists() && (resource instanceof IContainer)) {
			IContainer container = (IContainer)resource;
			try {
				container.refreshLocal(1, new NullProgressMonitor());
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
	}
	// ---------------------------------------private methods
}