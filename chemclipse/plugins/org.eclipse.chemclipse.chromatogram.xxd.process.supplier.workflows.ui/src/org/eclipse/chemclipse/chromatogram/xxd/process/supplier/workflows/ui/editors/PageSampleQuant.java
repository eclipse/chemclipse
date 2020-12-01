/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.editors;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantReport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantSubstance;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.runnables.ImportChromatogramRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.swt.SampleQuantTableViewerUI;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

public class PageSampleQuant {

	private static final Logger logger = Logger.getLogger(PageSampleQuant.class);
	//
	private EditorSampleQuant editorSampleQuant;
	//
	private Label labelName;
	private Label labelDataName;
	private Label labelDate;
	private Label labelOperator;
	private Label labelMiscInfo;
	//
	private Composite control;
	private Text textSearch;
	private SampleQuantTableViewerUI sampleQuantTableViewerUI;
	//
	private ISampleQuantReport sampleQuantReport;
	private IChromatogramMSD chromatogramMSD;

	public PageSampleQuant(Composite container, EditorSampleQuant editorSampleQuant) {

		createControl(container);
		this.editorSampleQuant = editorSampleQuant;
	}

	public Composite getControl() {

		return control;
	}

	public void showData(ISampleQuantReport sampleQuantReport) {

		this.sampleQuantReport = sampleQuantReport;
		if(sampleQuantReport != null) {
			setTableInput();
			//
			labelName.setText("Name: " + sampleQuantReport.getName());
			labelDataName.setText("Date Name: " + sampleQuantReport.getDataName());
			labelDate.setText("Date: " + sampleQuantReport.getDate());
			labelOperator.setText("Operator: " + sampleQuantReport.getOperator());
			labelMiscInfo.setText("Misc Info: " + sampleQuantReport.getMiscInfo());
			//
			ImportChromatogramRunnable runnable = new ImportChromatogramRunnable(sampleQuantReport.getPathChromatogramEdited());
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			try {
				monitor.run(true, false, runnable);
				chromatogramMSD = runnable.getChromatogram();
			} catch(InterruptedException e) {
				logger.warn(e);
			} catch(InvocationTargetException e) {
				logger.warn(e);
			}
		}
	}

	private void createControl(Composite container) {

		control = new Composite(container, SWT.NONE);
		control.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		control.setLayout(new GridLayout(2, false));
		//
		createInfoField(control);
		createSearchField(control);
		createTableAndButtonsField(control);
	}

	private void createInfoField(Composite parent) {

		GridData gridDataLabel = new GridData(GridData.FILL_HORIZONTAL);
		gridDataLabel.horizontalSpan = 2;
		//
		labelName = new Label(parent, SWT.NONE);
		labelName.setText("");
		labelName.setLayoutData(gridDataLabel);
		//
		labelDataName = new Label(parent, SWT.NONE);
		labelDataName.setText("");
		labelDataName.setLayoutData(gridDataLabel);
		//
		labelDate = new Label(parent, SWT.NONE);
		labelDate.setText("");
		labelDate.setLayoutData(gridDataLabel);
		//
		labelOperator = new Label(parent, SWT.NONE);
		labelOperator.setText("");
		labelOperator.setLayoutData(gridDataLabel);
		//
		labelMiscInfo = new Label(parent, SWT.NONE);
		labelMiscInfo.setText("");
		labelMiscInfo.setLayoutData(gridDataLabel);
		//
		Label label = new Label(parent, SWT.NONE);
		label.setText("");
		label.setLayoutData(gridDataLabel);
	}

	private void createSearchField(Composite parent) {

		textSearch = new Text(parent, SWT.BORDER);
		textSearch.setText("Search...");
		textSearch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textSearch.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				search();
			}
		});
		//
		final IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		final Button buttonCaseSensitive = new Button(parent, SWT.CHECK);
		buttonCaseSensitive.setText("Case sensitive");
		buttonCaseSensitive.setSelection(preferences.getBoolean(PreferenceSupplier.P_SAMPLEQUANT_SEARCH_CASE_SENSITIVE, PreferenceSupplier.DEF_SAMPLEQUANT_SEARCH_CASE_SENSITIVE));
		buttonCaseSensitive.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					preferences.put(PreferenceSupplier.P_SAMPLEQUANT_SEARCH_CASE_SENSITIVE, Boolean.toString(buttonCaseSensitive.getSelection()));
					preferences.flush();
				} catch(BackingStoreException e1) {
					logger.warn(e1);
				}
			}
		});
	}

	private void createTableAndButtonsField(Composite parent) {

		Composite compositeMain = new Composite(parent, SWT.NONE);
		GridData gridDataMain = new GridData(GridData.FILL_BOTH);
		gridDataMain.horizontalSpan = 2;
		compositeMain.setLayoutData(gridDataMain);
		compositeMain.setLayout(new GridLayout(2, false));
		//
		sampleQuantTableViewerUI = new SampleQuantTableViewerUI(compositeMain, SWT.BORDER);
		GridData gridDataTable = new GridData(GridData.FILL_BOTH);
		sampleQuantTableViewerUI.getTable().setLayoutData(gridDataTable);
		sampleQuantTableViewerUI.getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = sampleQuantTableViewerUI.getStructuredSelection().getFirstElement();
				if(object instanceof ISampleQuantSubstance) {
					ISampleQuantSubstance sampleQuantSubstance = (ISampleQuantSubstance)object;
					int maxScan = sampleQuantSubstance.getMaxScan();
					if(chromatogramMSD != null) {
						IScan scan = chromatogramMSD.getScan(maxScan);
						if(scan instanceof IScanMSD) {
							editorSampleQuant.setDirty(true);
							UpdateNotifierUI.update(e.display, scan);
						}
					}
				}
			}
		});
		//
		Composite compositeButtons = new Composite(compositeMain, SWT.NONE);
		compositeButtons.setLayout(new GridLayout(1, true));
		compositeButtons.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		GridData gridDataButtons = new GridData(GridData.FILL_HORIZONTAL);
		gridDataButtons.minimumWidth = 150;
		//
		Text textMinMatchQuality = new Text(compositeButtons, SWT.BORDER);
		textMinMatchQuality.setText(Double.toString(PreferenceSupplier.INSTANCE().getPreferences().getDouble(PreferenceSupplier.P_SAMPLEQUANT_MIN_MATCH_QUALITY, PreferenceSupplier.DEF_SAMPLEQUANT_MIN_MATCH_QUALITY)));
		textMinMatchQuality.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Button button1 = new Button(compositeButtons, SWT.PUSH);
		button1.setText("Set Min MQ");
		button1.setLayoutData(gridDataButtons);
		button1.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Shell shell = Display.getCurrent().getActiveShell();
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
				messageBox.setText("Set Min Match Quality");
				messageBox.setMessage("Would you like to set the min match quality? Manual verifications will be overwritten.");
				if(SWT.YES == messageBox.open()) {
					if(sampleQuantReport != null) {
						try {
							double minMatchQuality = Double.parseDouble(textMinMatchQuality.getText().trim());
							sampleQuantReport.setMinMatchQuality(minMatchQuality);
							setTableInput();
							editorSampleQuant.setDirty(true);
						} catch(NumberFormatException e1) {
							logger.warn(e1);
						}
					}
				}
			}
		});
		//
		Button button2 = new Button(compositeButtons, SWT.PUSH);
		button2.setText("Export targets.txt");
		button2.setLayoutData(gridDataButtons);
		button2.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Save a demo targets.txt
				 */
				Shell shell = Display.getCurrent().getActiveShell();
				FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
				fileDialog.setText("Export targets.txt");
				fileDialog.setFilterExtensions(new String[]{"*.txt"});
				fileDialog.setFilterNames(new String[]{"Targets *.txt"});
				fileDialog.setFileName("targets.txt");
				//
				String targetsFile = fileDialog.open();
				if(targetsFile != null) {
					try {
						PrintWriter printWriter = new PrintWriter(new FileWriter(new File(targetsFile)));
						printWriter.println("#NAME	CAS");
						if(sampleQuantReport != null) {
							for(ISampleQuantSubstance sampleQuantSubstance : sampleQuantReport.getSampleQuantSubstances()) {
								printWriter.print(sampleQuantSubstance.getName());
								printWriter.print("\t");
								printWriter.println(sampleQuantSubstance.getCasNumber().equals("") ? "Please add CAS#." : sampleQuantSubstance.getCasNumber());
							}
						}
						printWriter.flush();
						printWriter.close();
						//
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION);
						messageBox.setText("Export targets.txt");
						messageBox.setMessage("Export of targets.txt file finished successfully.");
						messageBox.open();
						//
					} catch(Exception e1) {
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
						messageBox.setText("Export targets.txt");
						messageBox.setMessage("Something has gone wrong whilst exporting the targets.txt file.");
						messageBox.open();
					}
				}
			}
		});
	}

	private void search() {

		if(sampleQuantReport != null) {
			String searchTerms = textSearch.getText().trim();
			sampleQuantTableViewerUI.setInput(sampleQuantReport.getSampleQuantSubstances(searchTerms));
		}
	}

	private void setTableInput() {

		if(sampleQuantReport != null && sampleQuantTableViewerUI != null) {
			sampleQuantTableViewerUI.setInput(sampleQuantReport.getSampleQuantSubstances());
		}
	}
}
