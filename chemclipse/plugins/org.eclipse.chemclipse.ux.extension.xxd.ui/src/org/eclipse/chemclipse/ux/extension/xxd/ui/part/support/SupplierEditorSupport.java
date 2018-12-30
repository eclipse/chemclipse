/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.part.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.converter.quantitation.QuantDBConverter;
import org.eclipse.chemclipse.converter.sequence.SequenceConverter;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.core.IMeasurementInfo;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.nmr.converter.core.ScanConverterNMR;
import org.eclipse.chemclipse.pcr.converter.core.PlateConverterPCR;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.ux.extension.ui.provider.AbstractSupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ChromatogramEditorCSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ChromatogramEditorMSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ChromatogramEditorWSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.PlateEditorPCR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ProcessMethodEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.QuantitationDatabaseEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ScanEditorNMR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ScanEditorXIR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.SequenceEditor;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.xir.converter.core.ScanConverterXIR;
import org.eclipse.e4.core.services.events.IEventBroker;

public class SupplierEditorSupport extends AbstractSupplierFileEditorSupport implements ISupplierEditorSupport {

	private String type = "";
	//
	private String elementId = "";
	private String contributionURI = "";
	private String iconURI = "";
	private String tooltip = "";
	private String topicUpdateRawfile = "";
	private String topicUpdateOverview = "";

	public SupplierEditorSupport(DataType dataType) {
		super(getSupplier(dataType));
		initialize(dataType);
	}

	private static List<ISupplier> getSupplier(DataType dataType) {

		List<ISupplier> supplier = new ArrayList<ISupplier>();
		switch(dataType) {
			case MSD_NOMINAL:
			case MSD_TANDEM:
			case MSD_HIGHRES:
			case MSD:
				supplier = ChromatogramConverterMSD.getInstance().getChromatogramConverterSupport().getSupplier();
				break;
			case CSD:
				supplier = ChromatogramConverterCSD.getInstance().getChromatogramConverterSupport().getSupplier();
				break;
			case WSD:
				supplier = ChromatogramConverterWSD.getInstance().getChromatogramConverterSupport().getSupplier();
				break;
			case XIR:
				supplier = ScanConverterXIR.getScanConverterSupport().getSupplier();
				break;
			case NMR:
				supplier = ScanConverterNMR.getScanConverterSupport().getSupplier();
				break;
			case PCR:
				supplier = PlateConverterPCR.getScanConverterSupport().getSupplier();
				break;
			case SEQ:
				supplier = SequenceConverter.getSequenceConverterSupport().getSupplier();
				break;
			case MTH:
				supplier = MethodConverter.getMethodConverterSupport().getSupplier();
				break;
			case QDB:
				supplier = QuantDBConverter.getQuantDBConverterSupport().getSupplier();
				break;
			default:
				// No action
		}
		//
		return supplier;
	}

	private void initialize(DataType dataType) {

		switch(dataType) {
			case MSD_NOMINAL:
			case MSD_TANDEM:
			case MSD_HIGHRES:
			case MSD:
				type = TYPE_MSD;
				elementId = ChromatogramEditorMSD.ID;
				contributionURI = ChromatogramEditorMSD.CONTRIBUTION_URI;
				iconURI = ChromatogramEditorMSD.ICON_URI;
				tooltip = ChromatogramEditorMSD.TOOLTIP;
				topicUpdateRawfile = IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_OVERVIEW;
				break;
			case CSD:
				type = TYPE_CSD;
				elementId = ChromatogramEditorCSD.ID;
				contributionURI = ChromatogramEditorCSD.CONTRIBUTION_URI;
				iconURI = ChromatogramEditorCSD.ICON_URI;
				tooltip = ChromatogramEditorCSD.TOOLTIP;
				topicUpdateRawfile = IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_OVERVIEW;
				break;
			case WSD:
				type = TYPE_WSD;
				elementId = ChromatogramEditorWSD.ID;
				contributionURI = ChromatogramEditorWSD.CONTRIBUTION_URI;
				iconURI = ChromatogramEditorWSD.ICON_URI;
				tooltip = ChromatogramEditorWSD.TOOLTIP;
				topicUpdateRawfile = IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_RAWFILE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_OVERVIEW;
				break;
			case XIR:
				type = TYPE_XIR;
				elementId = ScanEditorXIR.ID;
				contributionURI = ScanEditorXIR.CONTRIBUTION_URI;
				iconURI = ScanEditorXIR.ICON_URI;
				tooltip = ScanEditorXIR.TOOLTIP;
				topicUpdateRawfile = IChemClipseEvents.TOPIC_SCAN_XIR_UPDATE_RAWFILE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_SCAN_XIR_UPDATE_OVERVIEW;
				break;
			case NMR:
				type = TYPE_NMR;
				elementId = ScanEditorNMR.ID;
				contributionURI = ScanEditorNMR.CONTRIBUTION_URI;
				iconURI = ScanEditorNMR.ICON_URI;
				tooltip = ScanEditorNMR.TOOLTIP;
				topicUpdateRawfile = IChemClipseEvents.TOPIC_SCAN_NMR_UPDATE_RAWFILE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_SCAN_NMR_UPDATE_OVERVIEW;
				break;
			case PCR:
				type = TYPE_PCR;
				elementId = PlateEditorPCR.ID;
				contributionURI = PlateEditorPCR.CONTRIBUTION_URI;
				iconURI = PlateEditorPCR.ICON_URI;
				tooltip = PlateEditorPCR.TOOLTIP;
				topicUpdateRawfile = IChemClipseEvents.TOPIC_PLATE_PCR_UPDATE_RAWFILE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_PLATE_PCR_UPDATE_OVERVIEW;
				break;
			case SEQ:
				type = TYPE_SEQ;
				elementId = SequenceEditor.ID;
				contributionURI = SequenceEditor.CONTRIBUTION_URI;
				iconURI = SequenceEditor.ICON_URI;
				tooltip = SequenceEditor.TOOLTIP;
				topicUpdateRawfile = IChemClipseEvents.TOPIC_SEQUENCE_UPDATE_RAWFILE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_SEQUENCE_UPDATE_OVERVIEW;
				break;
			case MTH:
				type = TYPE_MTH;
				elementId = ProcessMethodEditor.ID;
				contributionURI = ProcessMethodEditor.CONTRIBUTION_URI;
				iconURI = ProcessMethodEditor.ICON_URI;
				tooltip = ProcessMethodEditor.TOOLTIP;
				topicUpdateRawfile = IChemClipseEvents.TOPIC_METHOD_UPDATE_RAWFILE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_METHOD_UPDATE_OVERVIEW;
				break;
			case QDB:
				type = TYPE_QDB;
				elementId = QuantitationDatabaseEditor.ID;
				contributionURI = QuantitationDatabaseEditor.CONTRIBUTION_URI;
				iconURI = QuantitationDatabaseEditor.ICON_URI;
				tooltip = QuantitationDatabaseEditor.TOOLTIP;
				topicUpdateRawfile = IChemClipseEvents.TOPIC_QUANTIATION_DATABASE_UPDATE_RAWFILE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_QUANTIATION_DATABASE_UPDATE_OVERVIEW;
				break;
			default:
				type = "";
				elementId = "";
				contributionURI = "";
				iconURI = "";
				tooltip = "";
				topicUpdateRawfile = "";
				topicUpdateOverview = "";
		}
	}

	@Override
	public String getType() {

		return type;
	}

	@Override
	public boolean openEditor(File file) {

		return openEditor(file, false);
	}

	@Override
	public boolean openEditor(final File file, boolean batch) {

		if(isSupplierFile(file) || isSupplierFileDirectory(file)) {
			openEditor(file, null, elementId, contributionURI, iconURI, tooltip, batch);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void openEditor(IMeasurement measurement) {

		openEditor(null, measurement, elementId, contributionURI, iconURI, tooltip);
	}

	@Override
	public void openOverview(final File file) {

		if(isSupplierFile(file) || isSupplierFileDirectory(file)) {
			IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
			eventBroker.send(topicUpdateRawfile, file);
		}
	}

	@Override
	public void openOverview(IMeasurementInfo measurementInfo) {

		IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
		eventBroker.send(topicUpdateOverview, measurementInfo);
	}
}
