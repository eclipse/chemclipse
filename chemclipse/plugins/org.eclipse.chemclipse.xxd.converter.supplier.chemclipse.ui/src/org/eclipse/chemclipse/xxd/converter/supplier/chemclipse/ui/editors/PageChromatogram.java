/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.ui.editors;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.chemclipse.converter.chromatogram.IChromatogramImportConverter;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramOverviewImportConverterProcessingInfo;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.converter.MagicNumberMatcherCSD;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.converter.MagicNumberMatcherMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.editors.AbstractExtendedEditorPage;
import org.eclipse.chemclipse.support.ui.editors.IExtendedEditorPage;
import org.eclipse.chemclipse.ux.extension.ui.provider.IChromatogramEditorSupport;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.converter.MagicNumberMatcherWSD;
import org.eclipse.core.runtime.NullProgressMonitor;
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

public class PageChromatogram extends AbstractExtendedEditorPage implements IExtendedEditorPage {

	private File file;
	//
	private Label labelName;
	private Label labelDataName;
	private Label labelOperator;
	private Label labelDate;
	private Label labelScans;
	private Label labelRTStart;
	private Label labelRTStop;
	//
	private ImageHyperlink hyperlinkProcess;
	//
	private MagicNumberMatcherMSD magicNumberMatcherMSD;
	private MagicNumberMatcherCSD magicNumberMatcherCSD;
	private MagicNumberMatcherWSD magicNumberMatcherWSD;
	//
	private SimpleDateFormat dateFormat;
	private DecimalFormat decimalFormat;

	public PageChromatogram(Composite container) {
		super("Chromatogram", container, true);
		//
		magicNumberMatcherMSD = new MagicNumberMatcherMSD();
		magicNumberMatcherCSD = new MagicNumberMatcherCSD();
		magicNumberMatcherWSD = new MagicNumberMatcherWSD();
		//
		dateFormat = ValueFormat.getDateFormatEnglish();
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
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

	public void showData(File file) {

		this.file = file;
		hyperlinkProcess.setText("");
		labelName.setText("Name:");
		labelDataName.setText("Data Name:");
		labelOperator.setText("Operator:");
		labelDate.setText("Date:");
		labelScans.setText("Scans:");
		labelRTStart.setText("Start RT (min):");
		labelRTStop.setText("Stop RT (min):");
		//
		if(file != null) {
			//
			labelName.setText(file.getName());
			hyperlinkProcess.setText(file.getName());
			//
			IChromatogramOverview chromatogramOverview = getChromatogramOverview(file);
			if(chromatogramOverview != null) {
				labelName.setText("Name: " + chromatogramOverview.getName());
				labelDataName.setText("Data Name: " + chromatogramOverview.getDataName());
				labelOperator.setText("Operator: " + chromatogramOverview.getOperator());
				Date date = chromatogramOverview.getDate();
				if(date != null) {
					labelDate.setText("Date: " + dateFormat.format(chromatogramOverview.getDate()));
				} else {
					labelDate.setText("Date: ");
				}
				labelScans.setText("Scans: " + Integer.toString(chromatogramOverview.getNumberOfScans()));
				labelRTStart.setText("Start RT (min): " + decimalFormat.format(chromatogramOverview.getStartRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
				labelRTStop.setText("Stop RT (min): " + decimalFormat.format(chromatogramOverview.getStopRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
			}
		}
	}

	private void createInfoSection(Composite parent) {

		Section section = createSection(parent, 3, "Overview", "Some header values of the file are displayed.");
		Composite client = createClientInfo(section);
		//
		labelName = createLabel(client, "");
		labelDataName = createLabel(client, "");
		labelOperator = createLabel(client, "");
		labelDate = createLabel(client, "");
		labelScans = createLabel(client, "");
		labelRTStart = createLabel(client, "");
		labelRTStop = createLabel(client, "");
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
		gridData.verticalIndent = 20;
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {

				/*
				 * Use the editor to show the file.
				 */
				IChromatogramEditorSupport chromatogramEditorSupport = getChromatogramEditorSupport(file);
				if(chromatogramEditorSupport != null) {
					chromatogramEditorSupport.openEditor(file);
				}
			}
		});
		return imageHyperlink;
	}

	private IChromatogramEditorSupport getChromatogramEditorSupport(File file) {

		if(magicNumberMatcherMSD.checkFileFormat(file)) {
			/*
			 * MSD
			 */
			return org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramSupport.getInstanceEditorSupport();
		} else if(magicNumberMatcherCSD.checkFileFormat(file)) {
			/*
			 * CSD
			 */
			return org.eclipse.chemclipse.ux.extension.csd.ui.support.ChromatogramSupport.getInstanceEditorSupport();
		} else if(magicNumberMatcherWSD.checkFileFormat(file)) {
			/*
			 * WSD
			 */
			return org.eclipse.chemclipse.ux.extension.wsd.ui.support.ChromatogramSupport.getInstanceEditorSupport();
		} else {
			return null;
		}
	}

	private IChromatogramOverview getChromatogramOverview(File file) {

		IChromatogramImportConverter importConverter = null;
		if(magicNumberMatcherMSD.checkFileFormat(file)) {
			/*
			 * MSD
			 */
			importConverter = new org.eclipse.chemclipse.msd.converter.supplier.chemclipse.converter.ChromatogramImportConverter();
		} else if(magicNumberMatcherCSD.checkFileFormat(file)) {
			/*
			 * CSD
			 */
			importConverter = new org.eclipse.chemclipse.csd.converter.supplier.chemclipse.converter.ChromatogramImportConverter();
		} else if(magicNumberMatcherWSD.checkFileFormat(file)) {
			/*
			 * WSD
			 */
			importConverter = new org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.converter.ChromatogramImportConverter();
		}
		//
		if(importConverter != null) {
			IChromatogramOverviewImportConverterProcessingInfo processingInfo = importConverter.convertOverview(file, new NullProgressMonitor());
			return processingInfo.getChromatogramOverview();
		} else {
			return null;
		}
	}
}
