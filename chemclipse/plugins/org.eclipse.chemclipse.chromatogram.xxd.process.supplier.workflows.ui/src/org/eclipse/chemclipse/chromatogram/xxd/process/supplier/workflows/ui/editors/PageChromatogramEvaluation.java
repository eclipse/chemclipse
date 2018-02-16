/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISingleChromatogramReport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.editors.AbstractExtendedEditorPage;
import org.eclipse.chemclipse.support.ui.editors.IExtendedEditorPage;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class PageChromatogramEvaluation extends AbstractExtendedEditorPage implements IExtendedEditorPage {

	private ISingleChromatogramReport chromatogramReport;
	private ImageHyperlink hyperlinkProcess;
	//
	private Label labelName;
	private Label labelEvaluationDate;
	private Label labelDescription;
	private Label labelProcessorNames;
	private Label labelNotes;

	public PageChromatogramEvaluation(Composite container) {
		super("Chromatogram Evaluation", container, true);
	}

	@Override
	public void fillBody(ScrolledForm scrolledForm) {

		Composite body = scrolledForm.getBody();
		body.setLayout(new TableWrapLayout());
		body.setLayout(createFormTableWrapLayout(true, 3));
		/*
		 * 3 column layout
		 */
		createInfoSection(body);
		createProcessSection(body);
	}

	public void showData(ISingleChromatogramReport chromatogramReport) {

		this.chromatogramReport = chromatogramReport;
		if(chromatogramReport != null) {
			hyperlinkProcess.setText(chromatogramReport.getChromatogramName());
			labelName.setText("Name: " + chromatogramReport.getChromatogramName());
			labelEvaluationDate.setText("Date: " + chromatogramReport.getEvaluationDate().toString());
			labelDescription.setText("Description: " + chromatogramReport.getDescription());
			StringBuilder builder = new StringBuilder();
			builder.append("Processor Steps:");
			builder.append("\n");
			int i = 1;
			for(String processorName : chromatogramReport.getProcessorNames()) {
				builder.append("\t");
				builder.append(Integer.toString(i++));
				builder.append(") ");
				builder.append(processorName);
				builder.append("\n");
			}
			labelProcessorNames.setText(builder.toString());
			labelNotes.setText(chromatogramReport.getNotes());
		} else {
			hyperlinkProcess.setText("");
			labelName.setText("Name:");
			labelEvaluationDate.setText("Date:");
			labelDescription.setText("Description:");
			labelProcessorNames.setText("Processor Steps:");
			labelNotes.setText("");
		}
	}

	private void createInfoSection(Composite parent) {

		Section section = createSection(parent, 3);
		Composite client = createClientInfo(section);
		//
		labelName = createLabel(client, "");
		labelName.setLayoutData(new GridData(GridData.FILL_BOTH));
		labelEvaluationDate = createLabel(client, "");
		labelEvaluationDate.setLayoutData(new GridData(GridData.FILL_BOTH));
		labelDescription = createLabel(client, "");
		labelDescription.setLayoutData(new GridData(GridData.FILL_BOTH));
		labelProcessorNames = createLabel(client, "");
		labelProcessorNames.setLayoutData(new GridData(GridData.FILL_BOTH));
		labelNotes = createLabel(client, "");
		labelNotes.setBackground(Colors.YELLOW);
		labelNotes.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Add the client to the section.
		 */
		section.setClient(client);
	}

	private void createProcessSection(Composite parent) {

		Section section = createSection(parent, 3, "Open Raw File", "Just click on the button to open the evaluated chromatogram.");
		Composite client = createClient(section);
		/*
		 * Edit
		 */
		createLabel(client, "Open Chromatogram");
		hyperlinkProcess = createProcessHyperlink(client);
		/*
		 * Add the client to the section.
		 */
		section.setClient(client);
	}

	private ImageHyperlink createProcessHyperlink(Composite client) {

		ImageHyperlink imageHyperlink = getFormToolkit().createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		imageHyperlink.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = HORIZONTAL_INDENT;
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {

				if(chromatogramReport != null) {
					ISupplierEditorSupport chromatogramEditorSupport = ChromatogramSupport.getInstanceEditorSupport();
					File file = new File(chromatogramReport.getChromatogramPath());
					chromatogramEditorSupport.openEditor(file);
				}
			}
		});
		return imageHyperlink;
	}
}
