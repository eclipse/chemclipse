/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.MSLReader;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.MSLWriter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

public class DatabaseCollectorWizard extends Wizard implements IImportWizard {

	private static final Logger logger = Logger.getLogger(DatabaseCollectorWizard.class);
	//
	public static final int DEFAULT_WIDTH = 350;
	public static final int DEFAULT_HEIGHT = 200;
	//
	private static final String DESCRIPTION = "Database Collector";
	//
	private DatabaseCollectorPage databaseCollectorPage;

	public DatabaseCollectorWizard() {

		setNeedsProgressMonitor(true);
		setWindowTitle(DESCRIPTION);
	}

	@Override
	public void addPages() {

		addPage(databaseCollectorPage = new DatabaseCollectorPage());
	}

	@Override
	public boolean performFinish() {

		IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				List<File> librarySourceFiles = databaseCollectorPage.getLibrarySourceFiles();
				int sizeSourceFiles = librarySourceFiles.size();
				SubMonitor subMonitor = SubMonitor.convert(monitor, DESCRIPTION, sizeSourceFiles * 2);
				//
				try {
					/*
					 * Load concatenate and save the *.msl library.
					 */
					MSLReader massSpectraReader = new MSLReader();
					MSLWriter mslWriter = new MSLWriter();
					File librarySinkFile = databaseCollectorPage.getLibrarySinkFile();
					/*
					 * Create
					 */
					if(!librarySinkFile.exists()) {
						librarySinkFile.createNewFile();
					}
					/*
					 * Write
					 */
					for(int i = 0; i < sizeSourceFiles; i++) {
						/*
						 * Read/Write each file to ensure that the entries are valid.
						 */
						boolean append = i > 0;
						File librarySourceFile = librarySourceFiles.get(i);
						IMassSpectra massSpectra = massSpectraReader.read(librarySourceFile, subMonitor);
						subMonitor.worked(1);
						mslWriter.write(librarySinkFile, massSpectra, append, subMonitor);
						subMonitor.worked(1);
					}
				} catch(Exception e) {
					logger.warn(e);
				} finally {
					SubMonitor.done(subMonitor);
				}
			}
		};
		//
		try {
			getContainer().run(true, false, runnableWithProgress);
			MessageDialog.openInformation(getShell(), DESCRIPTION, "The databases have been collected and saved.");
		} catch(Exception e) {
			MessageDialog.openError(getShell(), DESCRIPTION, e.getMessage());
			return false;
		}
		//
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}
}