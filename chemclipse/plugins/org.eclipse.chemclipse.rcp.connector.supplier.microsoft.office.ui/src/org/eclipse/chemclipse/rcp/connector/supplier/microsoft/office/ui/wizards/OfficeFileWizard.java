/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.connector.supplier.microsoft.office.ui.wizards;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.rcp.connector.supplier.microsoft.office.ui.Activator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public abstract class OfficeFileWizard extends Wizard implements INewWizard {

	private static final Logger logger = Logger.getLogger(OfficeFileWizard.class);
	private OfficeFileWizardPage page;

	/**
	 * Constructor for ExcelFileXLSXWizard.
	 */
	protected OfficeFileWizard() {

		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPage(IWizardPage page) {

		super.addPage(page);
		if(page instanceof OfficeFileWizardPage officeFileWizardPage) {
			this.page = officeFileWizardPage;
		}
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {

		final String containerName = page.getContainerName();
		final String fileName = page.getFileName();
		IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException {

				try {
					doFinish(containerName, fileName, monitor);
				} catch(CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, runnableWithProgress);
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		} catch(InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */
	private void doFinish(String containerName, String fileName, IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if(!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer)resource;
		final IFile file = container.getFile(new Path(fileName));
		/*
		 * Create appropriate files using Apache POI.
		 */
		if(!file.exists()) {
			try (FileOutputStream outputStream = new FileOutputStream(file.getLocation().toOSString())) {
				/*
				 * XLS, XLSX, DOC, DOCX
				 */
				String extension = file.getFileExtension();
				if(extension.equals("xls")) {
					try (HSSFWorkbook hssfWorkbook = new HSSFWorkbook()) {
						hssfWorkbook.createSheet("ChemClipse Worksheet");
						hssfWorkbook.write(outputStream);
						outputStream.flush();
					}
				} else if(extension.equals("xlsx")) {
					try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook()) {
						xssfWorkbook.createSheet("ChemClipse Worksheet");
						xssfWorkbook.write(outputStream);
						outputStream.flush();
					}
				} else if(extension.equals("doc")) {
					try (XWPFDocument xwpfDocument = new XWPFDocument()) {
						xwpfDocument.write(outputStream);
						outputStream.flush();
					}
				} else if(extension.equals("docx")) {
					try (XWPFDocument xwpfDocument = new XWPFDocument()) {
						xwpfDocument.write(outputStream);
						outputStream.flush();
					}
				}
			} catch(IOException e) {
				logger.warn(e);
			}
		}
		/*
		 * Refresh the workspace to show the created files.
		 */
		container.refreshLocal(1, monitor);
		/*
		 * Opens the file.
		 */
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {

				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch(PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}

	private void throwCoreException(String message) throws CoreException {

		IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), IStatus.OK, message, null);
		throw new CoreException(status);
	}
}
