/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
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

import javax.swing.event.HyperlinkEvent;

import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.converter.MagicNumberMatcherCSD;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.converter.MagicNumberMatcherMSD;
import org.eclipse.chemclipse.rcp.app.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.editors.AbstractExtendedEditorPage;
import org.eclipse.chemclipse.support.ui.editors.IExtendedEditorPage;
import org.eclipse.chemclipse.ux.extension.ui.provider.IChromatogramEditorSupport;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.converter.MagicNumberMatcherWSD;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class PageChromatogram extends AbstractExtendedEditorPage implements IExtendedEditorPage {

	private File file;
	//
	private Label labelName;
	private ImageHyperlink hyperlinkProcess;
	//
	private MagicNumberMatcherMSD magicNumberMatcherMSD;
	private MagicNumberMatcherCSD magicNumberMatcherCSD;
	private MagicNumberMatcherWSD magicNumberMatcherWSD;

	public PageChromatogram(Composite container) {
		super("Chromatogram", container, true);
		//
		magicNumberMatcherMSD = new MagicNumberMatcherMSD();
		magicNumberMatcherCSD = new MagicNumberMatcherCSD();
		magicNumberMatcherWSD = new MagicNumberMatcherWSD();
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
		if(file != null) {
			labelName.setText(file.getName());
			hyperlinkProcess.setText(file.getName());
		} else {
			labelName.setText("");
			hyperlinkProcess.setText("");
		}
	}

	private void createInfoSection(Composite parent) {

		Section section = createSection(parent, 3);
		Composite client = createClientInfo(section);
		//
		labelName = createLabel(client, "");
		labelName.setLayoutData(new GridData(GridData.FILL_BOTH));
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

				if(file != null) {
					/*
					 * Use the editor to show the file.
					 */
					IChromatogramEditorSupport chromatogramEditorSupport = getChromatogramEditorSupport(file);
					if(chromatogramEditorSupport != null) {
						EModelService eModelService = ModelSupportAddon.getModelService();
						MApplication mApplication = ModelSupportAddon.getApplication();
						EPartService ePartService = ModelSupportAddon.getPartService();
						chromatogramEditorSupport.openEditor(file, eModelService, mApplication, ePartService);
					}
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
	// private IChromatogramOverview getChromatogramOverview(File file) {
	//
	// IChromatogramImportConverter importConverter = null;
	// if(magicNumberMatcherMSD.checkFileFormat(file)) {
	// /*
	// * MSD
	// */
	// importConverter = new org.eclipse.chemclipse.msd.converter.supplier.chemclipse.converter.ChromatogramImportConverter();
	// } else if(magicNumberMatcherCSD.checkFileFormat(file)) {
	// /*
	// * CSD
	// */
	// importConverter = new org.eclipse.chemclipse.csd.converter.supplier.chemclipse.converter.ChromatogramImportConverter();
	// } else if(magicNumberMatcherWSD.checkFileFormat(file)) {
	// /*
	// * WSD
	// */
	// importConverter = new org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.converter.ChromatogramImportConverter();
	// }
	// //
	// if(importConverter != null) {
	// IChromatogramOverviewImportConverterProcessingInfo processingInfo = importConverter.convertOverview(file, new NullProgressMonitor());
	// return processingInfo.getChromatogramOverview();
	// } else {
	// return null;
	// }
	// }
}
