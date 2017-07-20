/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Daniel Mariano, Rafael Aguayo - additional functionality and UI improvements
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class OverviewPage {

	private static final int DEFAULT_RETENTION_TIME_WINDOW = 200;
	private static final Logger logger = Logger.getLogger(OverviewPage.class);
	/*
	 * ExtractionType - 0 for peaks, 1 for scans
	 */
	private int extractionType;
	//
	private PcaEditor pcaEditor;
	private Spinner principleComponentSpinner;
	private Text retentionTimeWindowText;

	public OverviewPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	/**
	 * Creates the run section.
	 *
	 * @param parent
	 */
	private void createExecuteSection(Composite parent, FormToolkit formToolkit) {

		Label label;
		/*
		 * Section
		 */
		Section section = formToolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
		section.setText("Evaluation");
		section.setDescription("Run the PCA evaluation after the entries have been edited.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		Composite client = formToolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 20;
		gridData.heightHint = 30;
		/*
		 * Input files section.
		 */
		label = formToolkit.createLabel(client, "Select the input chromatograms:\n");
		label.setLayoutData(gridData);
		createInputFilesPageHyperlink(client, gridData, formToolkit);
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void createExtractionTypeButtons(Composite client, FormToolkit formToolkit) {

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.heightHint = 30;
		/*
		 * Extraction type radio buttons.
		 */
		Label radioLabels = formToolkit.createLabel(client, "Select the extraction type:");
		radioLabels.setLayoutData(gridData);
		SelectionListener selectionListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {

				Button button = ((Button)event.widget);
				if(button.getText().equals("Peaks")) {
					extractionType = 0;
				} else {
					extractionType = 2;
				}
			};
		};
		Button[] radioButtons = new Button[2];
		//
		radioButtons[0] = new Button(client, SWT.RADIO);
		radioButtons[0].setSelection(true);
		radioButtons[0].setText("Peaks");
		radioButtons[0].setLayoutData(gridData);
		radioButtons[0].addSelectionListener(selectionListener);
		//
		radioButtons[1] = new Button(client, SWT.RADIO);
		radioButtons[1].setText("Scans");
		radioButtons[1].setLayoutData(gridData);
		radioButtons[1].addSelectionListener(selectionListener);
	}

	private void createInputFilesPageHyperlink(Composite client, GridData gridData, FormToolkit formToolkit) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = formToolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImageProvider.SIZE_16x16));
		imageHyperlink.setText("Data Input Files");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {

				pcaEditor.showInputFilesPage();
			}
		});
	}

	private void createPrincipleComponentSpinner(Composite client, FormToolkit formToolkit) {

		formToolkit.createLabel(client, "Number of Principle Components");
		//
		principleComponentSpinner = new Spinner(client, SWT.NONE);
		principleComponentSpinner.setMinimum(3);
		principleComponentSpinner.setMaximum(10);
		principleComponentSpinner.setIncrement(1);
		//
		GridData gridData = new GridData();
		gridData.widthHint = 50;
		gridData.heightHint = 20;
		principleComponentSpinner.setLayoutData(gridData);
	}

	/**
	 * Creates the properties section.
	 */
	private void createPropertiesSection(Composite parent, FormToolkit formToolkit) {

		/*
		 * Section
		 */
		Section section = formToolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
		section.setText("Properties");
		section.setDescription("Use the properties to define the retention time window and the number of components.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		Composite client = formToolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		Label label = formToolkit.createLabel(client, "Select the PCA settings:");
		label.setLayoutData(gridData);
		/*
		 * Settings
		 */
		createRetentionTimeWindowText(client, formToolkit);
		createPrincipleComponentSpinner(client, formToolkit);
		createExtractionTypeButtons(client, formToolkit);
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void createRetentionTimeWindowText(Composite client, FormToolkit formToolkit) {

		formToolkit.createLabel(client, "Retention Time Window (milliseconds)");
		//
		retentionTimeWindowText = formToolkit.createText(client, Integer.toString(DEFAULT_RETENTION_TIME_WINDOW), SWT.NONE);
		//
		GridData gridData = new GridData();
		gridData.widthHint = 300;
		retentionTimeWindowText.setLayoutData(gridData);
	}

	public int getExtractionType() {

		return extractionType;
	}

	public int getNumberOfPrincipleComponents() {

		return principleComponentSpinner.getSelection();
	}

	public int getRetentionTimeWindow() {

		int retentionTimeWindow = DEFAULT_RETENTION_TIME_WINDOW;
		try {
			retentionTimeWindow = Integer.parseInt(retentionTimeWindowText.getText().trim());
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		return retentionTimeWindow;
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Overview");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Forms API
		 */
		formToolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm scrolledForm = formToolkit.createScrolledForm(composite);
		Composite scrolledFormComposite = scrolledForm.getBody();
		formToolkit.decorateFormHeading(scrolledForm.getForm());
		scrolledFormComposite.setLayout(new TableWrapLayout());
		scrolledForm.setText("Principle Component Analysis");
		/*
		 * Add the sections
		 */
		createPropertiesSection(scrolledFormComposite, formToolkit);
		createExecuteSection(scrolledFormComposite, formToolkit);
		//
		tabItem.setControl(composite);
	}
}
