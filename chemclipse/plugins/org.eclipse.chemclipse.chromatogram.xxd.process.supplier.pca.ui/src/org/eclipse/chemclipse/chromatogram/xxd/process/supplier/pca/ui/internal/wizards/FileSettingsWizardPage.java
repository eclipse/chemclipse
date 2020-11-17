/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaExtractionFiles;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Algorithm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.AnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.DataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class FileSettingsWizardPage extends WizardPage {

	private static final Logger logger = Logger.getLogger(FileSettingsWizardPage.class);
	//
	private Text textFile;
	//
	private IAnalysisSettings analysisSettings = new AnalysisSettings();
	private Algorithm[] algorithms = Algorithm.getAlgorithms();

	public FileSettingsWizardPage() {

		super("Main Properties");
		setTitle("PCA");
		setDescription("Set main PCA parameters.");
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		//
		createLabel(composite, "Number of PCs:");
		createSpinnerPrincipleComponents(composite);
		//
		createLabel(composite, "Algorithm:");
		createComboViewerAlgorithm(composite);
		//
		createLabel(composite, "File Data Matrix:");
		textFile = createText(composite);
		createButtonSelectFile(composite);
		//
		createButtonDemoFile(composite);
		//
		setControl(composite);
	}

	public IAnalysisSettings getAnalysisSettings() {

		return analysisSettings;
	}

	public List<IDataInputEntry> getDataInputEntries() {

		List<IDataInputEntry> dataInputEntries = new ArrayList<>();
		String path = textFile.getText().trim();
		File file = new File(path);
		if(file.exists()) {
			DataInputEntry dataInputEntry = new DataInputEntry(path);
			dataInputEntries.add(dataInputEntry);
		}
		return dataInputEntries;
	}

	private Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	private Spinner createSpinnerPrincipleComponents(Composite parent) {

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setToolTipText("Number of Principal Components");
		spinner.setMinimum(PreferenceSupplier.MIN_NUMBER_OF_COMPONENTS);
		spinner.setIncrement(1);
		spinner.setSelection(analysisSettings.getNumberOfPrincipalComponents());
		spinner.setMaximum(PreferenceSupplier.MAX_NUMBER_OF_COMPONENTS);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		spinner.setLayoutData(gridData);
		spinner.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(analysisSettings != null) {
					analysisSettings.setNumberOfPrincipalComponents(spinner.getSelection());
				}
			}
		});
		//
		return spinner;
	}

	private ComboViewer createComboViewerAlgorithm(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setInput(algorithms);
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof Algorithm) {
					return ((Algorithm)element).getName();
				}
				return null;
			}
		});
		//
		Combo combo = comboViewer.getCombo();
		combo.setToolTipText("PCA Algorithm");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof Algorithm) {
					if(analysisSettings != null) {
						analysisSettings.setAlgorithm((Algorithm)object);
					}
				}
			}
		});
		//
		combo.select(getSelectedAlgorithmIndex());
		//
		return comboViewer;
	}

	private Text createText(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Path to data matrix file.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return text;
	}

	private void createButtonSelectFile(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select the data matrix.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FILE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.READ_ONLY);
				fileDialog.setText("PCA Data Matrix");
				fileDialog.setFilterExtensions(new String[]{"*.tsv"});
				fileDialog.setFilterNames(new String[]{"Data Matrix Tab Separated (*.tsv)"});
				String path = fileDialog.open();
				if(path != null) {
					File file = new File(path);
					if(file.exists()) {
						textFile.setText(file.getAbsolutePath());
					}
				}
			}
		});
	}

	private void createButtonDemoFile(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Demo Data Matrix");
		button.setToolTipText("Save a demo data matrix.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		button.setLayoutData(gridData);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.SAVE);
				fileDialog.setText("PCA Data Matrix");
				fileDialog.setFilterExtensions(new String[]{"*.tsv"});
				fileDialog.setFilterNames(new String[]{"Data Matrix Tab Separated (*.tsv)"});
				fileDialog.setOverwrite(true);
				String path = fileDialog.open();
				if(path != null) {
					/*
					 * Demo File
					 */
					File file = new File(path);
					//
					try (PrintWriter printWriter = new PrintWriter(file)) {
						PcaExtractionFiles.getDemoContent(printWriter);
						printWriter.flush();
					} catch(Exception e2) {
						logger.warn(e2);
					}
					//
					if(file.exists()) {
						textFile.setText(file.getAbsolutePath());
					}
				}
			}
		});
	}

	private int getSelectedAlgorithmIndex() {

		for(int i = 0; i < algorithms.length; i++) {
			Algorithm algorithm = algorithms[i];
			if(algorithm.equals(analysisSettings.getAlgorithm())) {
				return i;
			}
		}
		return -1;
	}
}
