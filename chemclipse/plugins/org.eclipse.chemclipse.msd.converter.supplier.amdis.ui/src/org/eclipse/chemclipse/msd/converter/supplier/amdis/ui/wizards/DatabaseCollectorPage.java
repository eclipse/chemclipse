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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.msd.converter.supplier.amdis.format.MSL;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DatabaseCollectorPage extends WizardPage {

	private static final int LIMIT_SOURCE_FILES = 25;
	//
	private AtomicReference<Text> textLibraryFileControl = new AtomicReference<>();
	private AtomicReference<Text> textLibraryDirectoryControl = new AtomicReference<>();
	//
	private File librarySinkFile;
	private List<File> librarySourceFiles = new ArrayList<>();

	public DatabaseCollectorPage() {

		super(DatabaseCollectorPage.class.getName(), "Select a directory to collect *.msl files and compile them in a single library.", null);
	}

	public File getLibrarySinkFile() {

		return librarySinkFile;
	}

	public List<File> getLibrarySourceFiles() {

		return librarySourceFiles;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		//
		createFileSection(composite);
		createDirectorySection(composite);
		//
		validate(getShell());
		setControl(composite);
	}

	private void createFileSection(Composite parent) {

		createLabel(parent, "Export Library (*.msl)");
		createTextExportFile(parent);
		createButtonFile(parent);
	}

	private void createDirectorySection(Composite parent) {

		createLabel(parent, "Import Directory");
		createTextImportDirectory(parent);
		createButtonDirectory(parent);
	}

	private void createTextExportFile(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Name of the export database");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate(e.display.getActiveShell());
			}
		});
		//
		textLibraryFileControl.set(text);
	}

	private void createButtonFile(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("...");
		button.setToolTipText("Select the *.msl export file.");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(e.display.getActiveShell(), SWT.SAVE);
				fileDialog.setOverwrite(true);
				fileDialog.setText(MSL.DESCRIPTION);
				fileDialog.setFilterExtensions(new String[]{MSL.FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{MSL.FILTER_NAME});
				fileDialog.setFilterPath(PreferenceSupplier.getPathExport());
				fileDialog.setFileName("CollectedLibrary" + MSL.FILE_EXTENSION);
				String pathname = fileDialog.open();
				//
				if(pathname != null) {
					/*
					 * Settings
					 */
					File file = new File(pathname);
					PreferenceSupplier.setPathExport(file.getParentFile().getAbsolutePath());
					textLibraryFileControl.get().setText(file.getAbsolutePath());
					validate(e.display.getActiveShell());
				}
			}
		});
	}

	private void createTextImportDirectory(Composite parent) {

		Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		text.setText("");
		text.setToolTipText("Path to the directory, that shall be parsed for *.msl files.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		textLibraryDirectoryControl.set(text);
	}

	private void createButtonDirectory(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("...");
		button.setToolTipText("Select the *.msl import directory.");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				DirectoryDialog directoryDialog = new DirectoryDialog(e.display.getActiveShell(), SWT.READ_ONLY);
				directoryDialog.setText("Database Directory");
				directoryDialog.setMessage("Select the directory, that shall be parsed for *.msl files.");
				directoryDialog.setFilterPath(PreferenceSupplier.getPathImport());
				String directory = directoryDialog.open();
				if(directory != null) {
					PreferenceSupplier.setPathImport(directory);
					textLibraryDirectoryControl.get().setText(directory);
					validate(e.display.getActiveShell());
				}
			}
		});
	}

	private void createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
	}

	private void validate(Shell shell) {

		String message = null;
		/*
		 * Validation
		 */
		File file = new File(textLibraryFileControl.get().getText().trim());
		File parenFile = file.getParentFile();
		if(parenFile != null && parenFile.exists()) {
			librarySinkFile = file;
		} else {
			message = "Please select a valid *.msl export file";
		}
		//
		librarySourceFiles.clear();
		File directory = new File(textLibraryDirectoryControl.get().getText().trim());
		if(directory.isDirectory()) {
			findLibraryFiles(directory, librarySourceFiles);
			if(librarySourceFiles.isEmpty()) {
				message = "No *.msl files have been found.";
			} else {
				if(librarySourceFiles.size() > LIMIT_SOURCE_FILES) {
					if(!MessageDialog.openQuestion(getShell(), "Collector", "More than " + LIMIT_SOURCE_FILES + "*.msl files have been collected. Would you like to proceed?")) {
						message = "Too many library files have been found.";
					}
				}
			}
		} else {
			message = "Please select a valid export file";
		}
		//
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	private void findLibraryFiles(File file, List<File> librarySourceFiles) {

		if(file.isDirectory()) {
			for(File subFile : file.listFiles()) {
				findLibraryFiles(subFile, librarySourceFiles);
			}
		} else {
			if(file.getName().endsWith(MSL.FILE_EXTENSION)) {
				librarySourceFiles.add(file);
			}
		}
	}
}