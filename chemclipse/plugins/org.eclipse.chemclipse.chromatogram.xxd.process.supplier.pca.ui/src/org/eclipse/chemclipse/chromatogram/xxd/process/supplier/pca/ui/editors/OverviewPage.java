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

import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.handlers.CreatePcaEvaluation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.EigenvaluesCovarianceMatrixTable;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
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

	/*
	 * ExtractionType - 0 for peaks, 1 for scans
	 */
	private int extractionType;
	//
	private PcaEditor pcaEditor;
	private Spinner principleComponentSpinner;

	public OverviewPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	private void createEigenvaluesHyperlink(Composite client, GridData gridData, FormToolkit formToolkit) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = formToolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImageProvider.SIZE_16x16));
		imageHyperlink.setText("Display Eigenvalues table (Experimental)");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {

				if(pcaEditor.getSamples().isPresent()) {
					Shell shell = new Shell(Display.getCurrent(), SWT.DIALOG_TRIM | SWT.RESIZE);
					shell.setLayout(new FillLayout());
					Runnable thread = () -> {
						EigenvaluesCovarianceMatrixTable eigenvaluesCovarianceMatrixTable = new EigenvaluesCovarianceMatrixTable(shell, null);
						shell.open();
						eigenvaluesCovarianceMatrixTable.update(pcaEditor.getSamples().get(), 0.01);
						shell.pack();
					};
					Display.getCurrent().asyncExec(thread);
				}
			}
		});
	}

	/**
	 * Creates the run section.
	 *
	 * @param parent
	 */
	private void createExecuteSection(Composite parent, FormToolkit formToolkit) {

		/*
		 * Section
		 */
		Section section = formToolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
		section.setText("Data Selection");
		section.setDescription("Start the PCA by using the wizard:\n");
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
		/*
		 * Input files section.
		 */
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 20;
		gridData.heightHint = 30;
		createWizardPeaksInputHyperlink(client, gridData, formToolkit);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 20;
		gridData.heightHint = 30;
		createWizardScansInputHyperlink(client, gridData, formToolkit);
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void createOthersSection(Composite parent, FormToolkit formToolkit) {

		/*
		 * Section
		 */
		Section section = formToolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
		section.setText("Others");
		section.setDescription("\n");
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
		/*
		 * Others
		 */
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 20;
		gridData.heightHint = 30;
		createEigenvaluesHyperlink(client, gridData, formToolkit);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 20;
		gridData.heightHint = 30;
		createSavePCAResultHyperlink(client, gridData, formToolkit);
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void createPrincipleComponentSpinner(Composite client, GridData gridData, FormToolkit formToolkit) {

		Composite composite = new Composite(client, SWT.None);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		formToolkit.createLabel(composite, "Number of Principle Components");
		principleComponentSpinner = new Spinner(composite, SWT.NONE);
		principleComponentSpinner.setMinimum(3);
		principleComponentSpinner.setMaximum(10);
		principleComponentSpinner.setIncrement(1);
		principleComponentSpinner.addListener(SWT.Selection, e -> pcaEditor.setNumberOfPrincipleComponents(principleComponentSpinner.getSelection()));
	}

	/**
	 * Creates the properties section.
	 */
	private void createReEvaluateSection(Composite parent, FormToolkit formToolkit) {

		/*
		 * Section
		 */
		Section section = formToolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
		section.setText("Re-evaluation");
		section.setDescription("Use the properties to define  the number of components.\n");
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
		/*
		 * Settings
		 */
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 20;
		gridData.heightHint = 30;
		createPrincipleComponentSpinner(client, gridData, formToolkit);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 20;
		gridData.heightHint = 30;
		createReEvaluationHyperlink(client, gridData, formToolkit);
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void createReEvaluationHyperlink(Composite client, GridData gridData, FormToolkit formToolkit) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = formToolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImageProvider.SIZE_16x16));
		imageHyperlink.setText("Re-evaluate");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {

				if(pcaEditor.getSamples().isPresent()) {
					pcaEditor.reEvaluatePcaCalculation();
				}
			}
		});
	}

	private void createSavePCAResultHyperlink(Composite client, GridData gridData, FormToolkit formToolkit) {

		ImageHyperlink imageHyperlink;
		imageHyperlink = formToolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImageProvider.SIZE_16x16));
		imageHyperlink.setText("Save PCA results");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {

				if(pcaEditor.getPcaResults().isPresent()) {
					CreatePcaEvaluation.createPart(pcaEditor.getSamples().get());
				}
			}
		});
	}

	private void createWizardPeaksInputHyperlink(Composite client, GridData gridData, FormToolkit formToolkit) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = formToolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImageProvider.SIZE_16x16));
		imageHyperlink.setText("Run Wizard (Peaks input)");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {

				if(pcaEditor.openWizardPcaPeaksInput() == Window.OK) {
					pcaEditor.reEvaluatePcaCalculation();
				}
			}
		});
	}

	private void createWizardScansInputHyperlink(Composite client, GridData gridData, FormToolkit formToolkit) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = formToolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImageProvider.SIZE_16x16));
		imageHyperlink.setText("Run Wizard (Scans input)");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {

				if(pcaEditor.openWizardPcaScansInput() == Window.OK) {
					pcaEditor.reEvaluatePcaCalculation();
				}
			}
		});
	}

	public int getExtractionType() {

		return extractionType;
	}

	public int getNumberOfPrincipleComponents() {

		return principleComponentSpinner.getSelection();
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Overview");
		Composite parent = new Composite(tabFolder, SWT.None);
		parent.setLayout(new GridLayout());
		pcaEditor.getNewPCAWorkflow(parent, new GridData(GridData.FILL_HORIZONTAL), pcaEditor);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
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
		createExecuteSection(scrolledFormComposite, formToolkit);
		createReEvaluateSection(scrolledFormComposite, formToolkit);
		createOthersSection(scrolledFormComposite, formToolkit);
		//
		tabItem.setControl(parent);
	}

	public void update() {

		Optional<IPcaResults> pcaResults = pcaEditor.getPcaResults();
		if(pcaResults.isPresent()) {
			principleComponentSpinner.setSelection(pcaResults.get().getNumberOfPrincipleComponents());
		}
	}
}
