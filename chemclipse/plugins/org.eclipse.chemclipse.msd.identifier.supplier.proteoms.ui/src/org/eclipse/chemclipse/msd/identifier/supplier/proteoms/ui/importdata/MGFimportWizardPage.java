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
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.importdata;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMS;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.parser.MGFParser;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.RCPUtil;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.parts.MSTreeNavigator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

public class MGFimportWizardPage extends WizardPage {

	private static final Logger log = Logger.getLogger(MGFimportWizardPage.class);
	public static final String PAGE_NAME = "importMGF";
	private static final String OLD_DIR_PATH = "DIALOG_SETTING_OLD_DIR_PATH";
	private Text textPath;
	private ImportParameters parameters;
	private TreeViewer treeViewer;
	protected List<SpectrumMS> parsedMSlist;

	/**
	 * Create the wizard.
	 * 
	 * @param selection
	 * @param parameters
	 */
	public MGFimportWizardPage(ImportParameters parameters, IStructuredSelection selection) {
		super(PAGE_NAME);
		this.parameters = parameters;
		setTitle("Import MGF data");
		setDescription("Select a directory of MGF files");
	}

	public List<SpectrumMS> getParsedMSlist() {

		return parsedMSlist;
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(3, false));
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Directory:");
		textPath = new Text(container, SWT.BORDER);
		textPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setErrorMessage(null);
				DirectoryDialog d = new DirectoryDialog(getShell());
				d.setText("Choose MSG directory");
				// if(parameters.getMgfDirectoryPath() != null) {
				// d.setFilterPath(parameters.getMgfDirectoryPath());
				if(textPath.getText() != null || !textPath.getText().isEmpty()) {
					File f = new File(textPath.getText());
					if(f.isDirectory()) {
						d.setFilterPath(textPath.getText());
					} else {
						d.setFilterPath(getPathFromDialogSetting());
					}
				} else {
					d.setFilterPath(getPathFromDialogSetting());
				}
				String path = d.open();
				if(path != null) {
					textPath.setText(path);
					if(checkDirectory(path)) {
						// TODO: check is files in directory can read
						parameters.setMgfDirectoryPath(path);
						savePathToDialogSetting(path);
						getWizard().getContainer().updateButtons();
					}
				}
			}
		});
		btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnNewButton.setText("Browse...");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		treeViewer = new TreeViewer(composite, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		treeViewer.setContentProvider(new MSTreeNavigator.TreeContentProvider());
		treeViewer.setLabelProvider(new MSTreeNavigator.TreeLabelProvider());
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite_1.setLayout(new GridLayout(1, false));
		Button btnParseSpectra = new Button(composite_1, SWT.NONE);
		btnParseSpectra.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		btnParseSpectra.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(checkDirectory(parameters.getMgfDirectoryPath())) {
					startParseSpectra();
				} else {
					RCPUtil.showWarningMessageDialog("Wrong directory path!", getShell());
				}
			}
		});
		btnParseSpectra.setText("Parse spectra");
		Button btnClear = new Button(composite_1, SWT.NONE);
		btnClear.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				treeViewer.setInput(null);
			}
		});
		btnClear.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnClear.setText("Clear");
	}

	protected void savePathToDialogSetting(String path) {

		IDialogSettings ds = getDialogSettings();
		if(path != null && ds != null) {
			ds.put(OLD_DIR_PATH, path);
		} else {
			log.debug("DB null " + ds);
		}
	}

	protected String getPathFromDialogSetting() {

		IDialogSettings ds = getDialogSettings();
		if(ds != null) {
			return ds.get(OLD_DIR_PATH);
		}
		log.debug("wizard dialog null");
		return null;
	}

	protected void startParseSpectra() {

		if(!checkDirectory(textPath.getText())) {
			return;
		}
		try {
			getWizard().getContainer().run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					if(checkDirectory(parameters.getMgfDirectoryPath())) {
						MGFParser p = new MGFParser();
						File dir = new File(parameters.getMgfDirectoryPath());
						File[] listFiles = dir.listFiles();
						List<SpectrumMS> msList = new ArrayList<>();
						monitor.setTaskName("Parse MGF files (" + listFiles.length + ").");
						monitor.beginTask("Start parse...", listFiles.length);
						for(File file : listFiles) {
							if(monitor.isCanceled()) {
								return;
							}
							if(file.isFile()) {
								try {
									SpectrumMS ms = p.parse(file.getAbsolutePath());
									msList.add(ms);
									monitor.worked(1);
								} catch(IOException e1) {
									e1.printStackTrace();
								}
							}
						}
						monitor.done();
						if(!monitor.isCanceled()) {
							setUpTreeData(msList);
							parsedMSlist = msList;
						}
					}
				}
			});
		} catch(InvocationTargetException e1) {
			e1.printStackTrace();
		} catch(InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	protected void setUpTreeData(List<SpectrumMS> msList) {

		getShell().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {

				treeViewer.setInput(msList);
				setPageComplete(true);
				// MessageDialog.openInformation(getShell(), "Found ", "Gotovo " + msList.size());
			}
		});
	}

	protected boolean checkDirectory(String path) {

		if(path == null) {
			return false;
		}
		File f = new File(path);
		if(!f.isDirectory()) {
			setErrorMessage("It is not a directory!");
			return false;
		}
		return true;
	}

	@Override
	public boolean isPageComplete() {

		if(checkDirectory(parameters.getMgfDirectoryPath())) {
			return true;
		}
		return false;
	}
}
