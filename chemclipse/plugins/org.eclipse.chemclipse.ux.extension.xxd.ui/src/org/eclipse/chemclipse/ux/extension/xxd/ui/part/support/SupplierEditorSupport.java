/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - E4/support snippet launching
 * Matthias Mailänder - add MS converter for MALDI
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.part.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.converter.quantitation.QuantDBConverter;
import org.eclipse.chemclipse.converter.sequence.SequenceConverter;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.core.IMeasurementInfo;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverter;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.nmr.converter.core.ScanConverterNMR;
import org.eclipse.chemclipse.pcr.converter.core.PlateConverterPCR;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.tsd.converter.chromatogram.ChromatogramConverterTSD;
import org.eclipse.chemclipse.ux.extension.msd.ui.editors.DatabaseEditor;
import org.eclipse.chemclipse.ux.extension.msd.ui.editors.MassSpectrumEditor;
import org.eclipse.chemclipse.ux.extension.ui.editors.EditorDescriptor;
import org.eclipse.chemclipse.ux.extension.ui.provider.AbstractSupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ChromatogramEditorCSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ChromatogramEditorMSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ChromatogramEditorVSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ChromatogramEditorWSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.PlateEditorPCR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ProcessMethodEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.QuantitationDatabaseEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ScanEditorNMR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ScanEditorVSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ScanEditorWSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.SequenceEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.services.EditorServicesSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.services.IEditorService;
import org.eclipse.chemclipse.vsd.converter.chromatogram.ChromatogramConverterVSD;
import org.eclipse.chemclipse.vsd.converter.core.ScanConverterVSD;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.converter.core.ScanConverterWSD;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

public class SupplierEditorSupport extends AbstractSupplierFileEditorSupport implements ISupplierEditorSupport {

	private static final Object NO_EXECUTE_METHOD = new Object();
	private String type = "";
	//
	private String elementId = "";
	private String contributionURI = "";
	private String iconURI = "";
	private String tooltip = "";
	private String topicUpdateRawfile = "";
	private String topicUpdateOverview = "";
	private final Supplier<IEclipseContext> contextSupplier;

	public SupplierEditorSupport(DataType dataType, Supplier<IEclipseContext> contextSupplier) {

		super(getSupplier(dataType));
		this.contextSupplier = contextSupplier;
		refreshEditorReferences(dataType);
	}

	private static List<ISupplier> getSupplier(DataType dataType) {

		List<ISupplier> supplier = new ArrayList<>();
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
			case VSD:
				supplier = ChromatogramConverterVSD.getInstance().getChromatogramConverterSupport().getSupplier();
				break;
			case TSD:
				supplier = ChromatogramConverterTSD.getInstance().getChromatogramConverterSupport().getSupplier();
				break;
			case SCAN_VSD:
				supplier = ScanConverterVSD.getScanConverterSupport().getSupplier();
				break;
			case SCAN_WSD:
				supplier = ScanConverterWSD.getScanConverterSupport().getSupplier();
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
			case MALDI:
				supplier = MassSpectrumConverter.getMassSpectrumConverterSupport().getSupplier();
				break;
			case MSD_DATABASE:
				supplier = DatabaseConverter.getDatabaseConverterSupport().getSupplier();
				break;
			default:
				// No action
		}
		//
		return supplier;
	}

	@Override
	public String getType() {

		return type;
	}

	@Override
	public boolean openEditor(File file, Map<HeaderField, String> headerMap, boolean batch) {

		if(isSupplierFile(file)) {
			refreshEditorReferences();
			openEditor(file, null, elementId, contributionURI, iconURI, tooltip, headerMap, batch);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean openEditor(File file, Map<HeaderField, String> headerMap, ISupplier supplier) {

		IEclipseContext eclipseContext = contextSupplier.get();
		IEclipseContext parameterContext = EclipseContextFactory.create();
		try {
			parameterContext.set(File.class, file);
			parameterContext.set(ISupplier.class, supplier);
			Object[] executables = {Adapters.adapt(supplier, EditorDescriptor.class), supplier};
			for(Object executable : executables) {
				if(executable == null) {
					continue;
				}
				Object invoke = ContextInjectionFactory.invoke(executable, Execute.class, eclipseContext, parameterContext, NO_EXECUTE_METHOD);
				if(NO_EXECUTE_METHOD != invoke) {
					if(invoke instanceof Boolean booleanInvoke) {
						return booleanInvoke.booleanValue();
					}
					return true;
				}
			}
		} finally {
			parameterContext.dispose();
		}
		//
		return openEditor(file, headerMap, false);
	}

	@Override
	public void openEditor(IMeasurement measurement) {

		refreshEditorReferences();
		openEditor(null, measurement, elementId, contributionURI, iconURI, tooltip);
	}

	@Override
	public void openOverview(final File file) {

		if(isSupplierFile(file)) {
			IEventBroker eventBroker = Activator.getDefault().getEventBroker();
			eventBroker.send(topicUpdateRawfile, file);
		}
	}

	@Override
	public void openOverview(IMeasurementInfo measurementInfo) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		eventBroker.send(topicUpdateOverview, measurementInfo);
	}

	private void refreshEditorReferences() {

		/*
		 * Dynamically reload the TSD editors.
		 */
		if(TYPE_TSD.equals(type)) {
			refreshEditorReferences(DataType.TSD);
		} else if(TYPE_VSD.equals(type)) {
			refreshEditorReferences(DataType.VSD);
		}
	}

	private void refreshEditorReferences(DataType dataType) {

		/*
		 * Clear existing values.
		 */
		type = "";
		elementId = "";
		contributionURI = "";
		iconURI = "";
		tooltip = "";
		topicUpdateRawfile = "";
		topicUpdateOverview = "";
		//
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
			case VSD:
				type = TYPE_VSD;
				elementId = ChromatogramEditorVSD.ID;
				contributionURI = ChromatogramEditorVSD.CONTRIBUTION_URI;
				iconURI = ChromatogramEditorVSD.ICON_URI;
				tooltip = ChromatogramEditorVSD.TOOLTIP;
				topicUpdateRawfile = IChemClipseEvents.TOPIC_CHROMATOGRAM_VSD_UPDATE_RAWFILE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_CHROMATOGRAM_VSD_UPDATE_OVERVIEW;
				break;
			case TSD:
				type = TYPE_TSD;
				IEditorService editorService = EditorServicesSupport.getSelectedEditorService(type);
				elementId = editorService.getElementId();
				contributionURI = editorService.getContributionURI();
				iconURI = editorService.getIconURI();
				tooltip = editorService.getTooltip();
				topicUpdateRawfile = IChemClipseEvents.TOPIC_CHROMATOGRAM_TSD_UPDATE_RAWFILE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_CHROMATOGRAM_TSD_UPDATE_OVERVIEW;
				break;
			case SCAN_VSD:
				type = TYPE_SCAN_VSD;
				elementId = ScanEditorVSD.ID;
				contributionURI = ScanEditorVSD.CONTRIBUTION_URI;
				iconURI = ScanEditorVSD.ICON_URI;
				tooltip = ScanEditorVSD.TOOLTIP;
				topicUpdateRawfile = IChemClipseEvents.TOPIC_SCAN_VSD_UPDATE_RAWFILE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_SCAN_VSD_UPDATE_OVERVIEW;
				break;
			case SCAN_WSD:
				type = TYPE_SCAN_WSD;
				elementId = ScanEditorWSD.ID;
				contributionURI = ScanEditorWSD.CONTRIBUTION_URI;
				iconURI = ScanEditorWSD.ICON_URI;
				tooltip = ScanEditorWSD.TOOLTIP;
				topicUpdateRawfile = IChemClipseEvents.TOPIC_SCAN_WSD_UPDATE_RAWFILE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_SCAN_WSD_UPDATE_OVERVIEW;
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
			case MALDI:
				type = TYPE_SCAN_MSD;
				elementId = MassSpectrumEditor.ID;
				contributionURI = MassSpectrumEditor.CONTRIBUTION_URI;
				iconURI = MassSpectrumEditor.ICON_URI;
				tooltip = MassSpectrumEditor.TOOLTIP;
				topicUpdateRawfile = IChemClipseEvents.TOPIC_MASS_SPECTRUM_UPDATE_RAWFILE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_MASS_SPECTRUM_UPDATE_OVERVIEW;
				break;
			case MSD_DATABASE:
				type = TYPE_DATABASE_MSD;
				elementId = DatabaseEditor.ID;
				contributionURI = DatabaseEditor.CONTRIBUTION_URI;
				iconURI = DatabaseEditor.ICON_URI;
				tooltip = DatabaseEditor.TOOLTIP;
				topicUpdateRawfile = IChemClipseEvents.TOPIC_LIBRARY_MSD_UPDATE;
				topicUpdateOverview = IChemClipseEvents.TOPIC_LIBRARY_MSD_UPDATE;
				break;
			default:
				break;
		}
	}
}