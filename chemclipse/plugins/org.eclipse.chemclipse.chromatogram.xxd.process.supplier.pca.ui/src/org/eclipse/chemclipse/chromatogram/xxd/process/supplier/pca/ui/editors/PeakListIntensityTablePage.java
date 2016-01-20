/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Rafael Aguayo - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.DataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.TimeRangeWizard;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class PeakListIntensityTablePage {

	private PcaEditor pcaEditor;
	private int currentNumberOfPeaks;
	private InputFilesPage inputFilesPage;
	private Label tableHeader;
	private Table peakListIntensityTable;
	//
	private NumberFormat numberFormat;
	private static final int FRACTION_DIGITS = 3;

	public PeakListIntensityTablePage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		numberFormat = NumberFormat.getInstance();
		numberFormat.setMinimumFractionDigits(FRACTION_DIGITS);
		numberFormat.setMaximumFractionDigits(FRACTION_DIGITS);
		//
		this.pcaEditor = pcaEditor;
		/*
		 * Create the peak intensity table.
		 */
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Data Table");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Forms API
		 */
		formToolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm scrolledForm = formToolkit.createScrolledForm(composite);
		Composite scrolledFormComposite = scrolledForm.getBody();
		scrolledFormComposite.setLayout(new TableWrapLayout());
		scrolledForm.setText("Peak Intensity Table Editor");
		createPeakListIntensityTableSection(scrolledFormComposite, formToolkit);
		tabItem.setControl(composite);
	}

	public void reloadPeakListIntensityTable() {

		if(peakListIntensityTable != null) {
			String peakStartPoint;
			String peakEndPoint;
			/*
			 * Remove all entries.
			 */
			peakListIntensityTable.setRedraw(false);
			peakListIntensityTable.removeAll();
			while(peakListIntensityTable.getColumnCount() > 0) {
				/*
				 * Delete all columns.
				 */
				peakListIntensityTable.getColumns()[0].dispose();
			}
			peakListIntensityTable.setRedraw(true);
			/*
			 * Header
			 */
			List<String> titleList = new ArrayList<String>();
			titleList.add("Times");
			for(int retentionTime : pcaEditor.pcaResults.getExtractedRetentionTimes()) {
				titleList.add(numberFormat.format(retentionTime / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
			}
			final String[] titles = titleList.toArray(new String[titleList.size()]);
			currentNumberOfPeaks = titles.length - 1;
			TableColumn filenameColumn = new TableColumn(peakListIntensityTable, SWT.NONE);
			filenameColumn.setText(titles[0]);
			/*
			 * Makes filename entry clickable to be able to display certain time range
			 */
			filenameColumn.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent event) {

					/*
					 * Make wizard to allow user to input time range for table
					 */
					TimeRangeWizard tableWizard = new TimeRangeWizard();
					WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), tableWizard);
					int returnCode = wizardDialog.open();
					String range = tableWizard.getTextOne();
					/*
					 * If timerange entered
					 */
					if(returnCode == WizardDialog.OK) {
						System.out.println("Ok pressed");
						int split = range.indexOf("-");
						double startRange = Double.parseDouble(range.substring(0, split));
						double endRange = Double.parseDouble(range.substring(split + 1));
						if(endRange < startRange) {
							return;
						}
						TableColumn[] columns = peakListIntensityTable.getColumns();
						boolean startRowSet = false;
						int startRow = 0;
						int endRow = 0;
						double currentTitle = 0.0;
						for(int i = 1; i <= columns.length; i++) {
							currentTitle = Double.parseDouble(columns[i].getText());
							if(currentTitle > startRange && !startRowSet) {
								startRowSet = true;
								startRow = i;
							} else if(currentTitle > endRange) {
								if(i != 0) {
									endRow = i - 1;
								}
								break;
							}
						}
						// Deletes columns before start range
						for(int j = 1; j < startRow; j++) {
							columns[j].dispose();
							currentNumberOfPeaks--;
						}
						// Deletes columns after end range
						for(int k = endRow; k < columns.length; k++) {
							columns[k].dispose();
							currentNumberOfPeaks--;
						}
						redrawTableHeader(pcaEditor.dataInputEntries, currentNumberOfPeaks, columns[startRow].getText(), columns[endRow - 1].getText());
					} else {
						System.out.println("Cancel pressed");
					}
				}
			});
			for(int i = 1; i < titles.length; i++) {
				final TableColumn column = new TableColumn(peakListIntensityTable, SWT.NONE);
				column.setText(titles[i]);
				column.addSelectionListener(new SelectionAdapter() {

					public void widgetSelected(SelectionEvent event) {

						column.dispose();
						currentNumberOfPeaks--;
						TableColumn[] newColumns = peakListIntensityTable.getColumns();
						redrawTableHeader(pcaEditor.dataInputEntries, currentNumberOfPeaks, newColumns[1].getText(), newColumns[newColumns.length - 1].getText());
					}
				});
			}
			/*
			 * Data
			 */
			for(Map.Entry<ISample, IPcaResult> entry : pcaEditor.pcaResults.getPcaResultMap().entrySet()) {
				int index = 0;
				TableItem item = new TableItem(peakListIntensityTable, SWT.NONE);
				if(entry.getKey().isSelected()) {
					item.setChecked(true);
				}
				item.setText(index++, entry.getKey().getName());
				IPcaResult pcaResult = entry.getValue();
				double[] sampleData = pcaResult.getSampleData();
				for(double data : sampleData) {
					item.setText(index++, numberFormat.format(data));
				}
			}
			/*
			 * Pack to make the entries visible.
			 */
			for(int i = 0; i < titles.length; i++) {
				peakListIntensityTable.getColumn(i).pack();
			}
			peakStartPoint = titles[1];
			peakEndPoint = titles[titles.length - 1];
			redrawTableHeader(pcaEditor.dataInputEntries, currentNumberOfPeaks, peakStartPoint, peakEndPoint);
		}
	}

	private void createPeakListIntensityTableSection(Composite parent, FormToolkit formToolkit) {

		Section section;
		Composite client;
		GridLayout layout;
		/*
		 * Section
		 */
		section = formToolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Peak Intensity Table");
		section.setDescription("Click on the Times box to specify a certain timerange to display\n" + "Click on any time column header to delete the corresponding column\n\n" + "Click on any filename(not the checkboxes) to exclude/include that specific file in table\n" + "The checkboxes currently show what files are included\n\n" + "Click on the Re-Evaluate Button to recalcuate score plot and error chart\n");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Set the layout for the client.
		 */
		client = formToolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		createButtonForPeakListTable(client, formToolkit);
		createPeakIntensityTableLabels(client, formToolkit);
		GridData gridData;
		peakListIntensityTable = formToolkit.createTable(client, SWT.MULTI | SWT.VIRTUAL | SWT.CHECK);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 300;
		gridData.widthHint = 100;
		gridData.verticalSpan = 3;
		peakListIntensityTable.setLayoutData(gridData);
		peakListIntensityTable.setHeaderVisible(true);
		peakListIntensityTable.setLinesVisible(true);
		peakListIntensityTable.addListener(SWT.MouseDoubleClick, new Listener() {

			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {

				TableItem[] selection = peakListIntensityTable.getSelection();
				for(int i = 0; i < selection.length; i++) {
					selection[i].dispose();
				}
			}
		});
		peakListIntensityTable.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {

				TableItem item = (TableItem)event.item;
				String filename = item.getText();
				Map<ISample, IPcaResult> resultMap = pcaEditor.pcaResults.getPcaResultMap();
				for(ISample key : resultMap.keySet()) {
					if(key.getName().equals(filename)) {
						if(key.isSelected()) {
							key.setSelected(false);
							item.setChecked(false);
							for(IDataInputEntry entry : pcaEditor.dataInputEntries) {
								// TODO: Check if there are other file types to consider. Only works when dealing with ocb files for now
								if(entry.getName().equals(key.getName() + ".ocb")) {
									pcaEditor.dataInputEntries.remove(entry);
								}
							}
							return;
						} else {
							key.setSelected(true);
							item.setChecked(true);
							// TODO: Check if there are other file types to consider. Only works when dealing with ocb files for now
							DataInputEntry inputEntry = new DataInputEntry(key.getName() + ".ocb");
							pcaEditor.dataInputEntries.add(inputEntry);
							return;
						}
					}
				}
			}
		});
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void createButtonForPeakListTable(Composite client, FormToolkit formToolkit) {

		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		createReevaluateButton(client, gridData, formToolkit);
	}

	/**
	 * Creates the peak intensity table labels.
	 * 
	 * @param client
	 */
	private void createPeakIntensityTableLabels(Composite client, FormToolkit formToolkit) {

		tableHeader = formToolkit.createLabel(client, "Peaks: " + " \t\tStart Peak: " + " \t End Peak: ", SWT.NONE);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		tableHeader.setLayoutData(gridData);
	}

	private void redrawTableHeader(List<IDataInputEntry> inputEntries, int numPeaks, String startPoint, String endPoint) {

		// if peaks
		// TODO
		System.out.println("GENERALIZE - PCA");
		// if(overviewPage.getExtractionType() == 0) {
		// tableHeader.setText(Integer.toString(inputEntries.size()) + "\t\tPeaks: " + numPeaks + " \t\tStart Peak: " + startPoint + "\t\tEnd Peak: " + endPoint);
		// }
		// // if scans
		// else {
		// tableHeader.setText(Integer.toString(inputEntries.size()) + "\t\tScans: " + numPeaks + " \t\tStart Scan: " + startPoint + "\t\tEnd Scan: " + endPoint);
		// }
		tableHeader.setText(Integer.toString(inputEntries.size()) + "\t\tPeaks: " + numPeaks + " \t\tStart Peak: " + startPoint + "\t\tEnd Peak: " + endPoint);
	}

	private void createReevaluateButton(Composite client, GridData gridData, FormToolkit formToolkit) {

		Button reevaluate;
		reevaluate = formToolkit.createButton(client, "Re-Evaluate", SWT.PUSH);
		reevaluate.setLayoutData(gridData);
		// final PrincipleComponentProcessor principleComponentProcessor = new PrincipleComponentProcessor();
		reevaluate.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				System.out.println("Size: " + pcaEditor.dataInputEntries.size());
				pcaEditor.runPcaCalculation();
				// TODO
				System.out.println("GENERALIZE - PCA");
				// peakListIntensityTablePage.reloadPeakListIntensityTable();
				// scorePlotPage.updateSpinnerPCMaxima();
				// scorePlotPage.reloadScorePlotChart();
				// errorResiduePage.reloadErrorResidueChart();
			}
		});
	}
}
