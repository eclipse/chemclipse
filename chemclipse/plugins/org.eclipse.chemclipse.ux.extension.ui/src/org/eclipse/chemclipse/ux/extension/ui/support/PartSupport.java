/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - remove references to ModelAddon
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MArea;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class PartSupport {

	//
	public static final String PERSPECTIVE_DATA_ANALYSIS = "org.eclipse.chemclipse.ux.extension.xxd.ui.perspective.main";
	//
	public static final String AREA = "org.eclipse.chemclipse.rcp.app.ui.editor";
	/*
	 * Parts
	 */
	public static final String PART_OVERLAY_CHROMATOGRAM = "org.eclipse.chemclipse.ux.extension.xxd.ui.parts.ChromatogramOverlayPart";
	/*
	 * Part Descriptors
	 */
	public static final String PARTDESCRIPTOR_OVERLAY_CHROMATOGRAM = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.chromatogramOverlayPartDescriptor";
	public static final String PARTDESCRIPTOR_OVERLAY_NMR = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.nmrOverlayPartDescriptor";
	public static final String PARTDESCRIPTOR_OVERLAY_XIR = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.xirOverlayPartDescriptor";
	public static final String PARTDESCRIPTOR_BASELINE = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.baselinePartDescriptor";
	public static final String PARTDESCRIPTOR_HEADER_DATA = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.headerDataPartDescriptor";
	public static final String PARTDESCRIPTOR_CHROMATOGRAM_OVERVIEW = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.chromatogramOverviewPartDescriptor";
	public static final String PARTDESCRIPTOR_MISCELLANEOUS_INFO = "org.eclipse.chemclipse.ux.extension.xxd.ui.partdescriptor.miscellaneousInfoPartDescriptor";
	public static final String PARTDESCRIPTOR_CHROMATOGRAM_SCAN_INFO = "org.eclipse.chemclipse.ux.extension.xxd.ui.partdescriptor.chromatogramScanInfoPartDescriptor";
	public static final String PARTDESCRIPTOR_SCAN_CHART = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.scanChartPartDescriptor";
	public static final String PARTDESCRIPTOR_SCAN_TABLE = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.scanTablePartDescriptor";
	public static final String PARTDESCRIPTOR_TARGETS = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.targetsPartDescriptor";
	public static final String PARTDESCRIPTOR_PEAK_CHART = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.peakChartPartDescriptor";
	public static final String PARTDESCRIPTOR_PEAK_DETAILS = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.peakDetailsPartDescriptor";
	public static final String PARTDESCRIPTOR_PEAK_DETECTOR = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.peakDetectorPartDescriptor";
	public static final String PARTDESCRIPTOR_PEAK_SCAN_LIST = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.peakScanListPartDescriptor";
	public static final String PARTDESCRIPTOR_PEAK_QUANTITATION_LIST = "org.eclipse.chemclipse.ux.extension.xxd.ui.partdescriptor.peakQuantitationListPartDescriptor";
	public static final String PARTDESCRIPTOR_SUBTRACT_SCAN = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.subtractScanPartDescriptor";
	public static final String PARTDESCRIPTOR_COMBINED_SCAN = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.combinedScanPartDescriptor";
	public static final String PARTDESCRIPTOR_COMPARISON_SCAN = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.comparisonScanPartDescriptor";
	public static final String PARTDESCRIPTOR_INTEGRATION_AREA = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.integrationAreaPartDescriptor";
	public static final String PARTDESCRIPTOR_QUANTITATION = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.quantitationPartDescriptor";
	public static final String PARTDESCRIPTOR_INTERNAL_STANDARDS = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.internalStandardsPartDescriptor";
	public static final String PARTDESCRIPTOR_MEASUREMENT_RESULTS = "org.eclipse.chemclipse.ux.extension.xxd.ui.partdescriptor.measurementResultsPartDescriptor";
	public static final String PARTDESCRIPTOR_CHROMATOGRAM_HEATMAP = "org.eclipse.chemclipse.ux.extension.xxd.ui.partdescriptor.chromatogramHeatmapPartDescriptor";
	public static final String PARTDESCRIPTOR_PEAK_QUANTITATION_REFERENCES = "org.eclipse.chemclipse.ux.extension.xxd.ui.partdescriptor.peakQuantitationReferencesPartDescriptor";
	public static final String PARTDESCRIPTOR_PlATE_CHARTS = "org.eclipse.chemclipse.ux.extension.xxd.ui.partdescriptor.plateChartsPartDescriptor";
	public static final String PARTDESCRIPTOR_WELL_DATA = "org.eclipse.chemclipse.ux.extension.xxd.ui.partdescriptor.wellDataPartDescriptor";
	public static final String PARTDESCRIPTOR_WELL_CHART = "org.eclipse.chemclipse.ux.extension.xxd.ui.partdescriptor.wellChartPartDescriptor";
	public static final String PARTDESCRIPTOR_WELL_CHANNELS = "org.eclipse.chemclipse.ux.extension.xxd.ui.partdescriptor.wellChannelsPartDescriptor";
	public static final String PARTDESCRIPTOR_PlATE_DATA = "org.eclipse.chemclipse.ux.extension.xxd.ui.partdescriptor.plateDataPartDescriptor";
	public static final String PARTDESCRIPTOR_QUANT_RESPONSE_CHART = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.quantResponseChartPartDescriptor";
	public static final String PARTDESCRIPTOR_QUANT_RESPONSE_LIST = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.quantResponseListPartDescriptor";
	public static final String PARTDESCRIPTOR_QUANT_PEAKS_CHART = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.quantPeaksChartPartDescriptor";
	public static final String PARTDESCRIPTOR_QUANT_PEAKS_LIST = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.quantPeaksListPartDescriptor";
	public static final String PARTDESCRIPTOR_QUANT_SIGNALS_LIST = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.quantSignalsListPartDescriptor";
	//
	public static final String PARTSTACK_NONE = "";
	public static final String PARTSTACK_QUICKACCESS = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.quickaccess";
	public static final String PARTSTACK_LEFT_TOP = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.left.top";
	public static final String PARTSTACK_LEFT_CENTER = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.left.center"; // Default hidden
	public static final String PARTSTACK_RIGHT_TOP = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.right.top"; // Default hidden
	public static final String PARTSTACK_BOTTOM_LEFT = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.bottom.left";
	public static final String PARTSTACK_BOTTOM_CENTER = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.bottom.center";
	public static final String PARTSTACK_BOTTOM_RIGHT = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.bottom.right";
	//
	private static final String COMPATIBILITY_EDITOR_ELEMENT_ID = "org.eclipse.e4.ui.compatibility.editor";
	//
	private static Set<String> hiddenPartStacks = new HashSet<String>();
	//
	private static Map<String, String> partMap = new HashMap<String, String>();
	private static Map<String, Map<Boolean, Map<Button, Image>>> partImageMap = new HashMap<String, Map<Boolean, Map<Button, Image>>>();
	//
	static {
		hiddenPartStacks.add(PARTSTACK_LEFT_CENTER);
		hiddenPartStacks.add(PARTSTACK_RIGHT_TOP);
	}

	/**
	 * Might return null.
	 * Use this method e.g. to get a 3.x editor part.
	 * The 3.x editorId is the id that is used in the plugin.xml
	 * to declare the editor.
	 * 
	 * @param partService
	 * 
	 * @param partId
	 * @return MPart
	 */
	public static MPart get3xEditorPart(String editorId, EPartService partService, EModelService service, MApplication application) {

		for(MPart mPart : partService.getParts()) {
			if(is3xEditorPart(mPart, editorId)) {
				return mPart;
			}
		}
		return null;
	}

	public static MPart getPart(String partId, String partStackId, EPartService partService, EModelService modelService, MApplication application) {

		MPart part = null;
		MUIElement element = modelService.find(partId, application);
		if(element instanceof MPart) {
			/*
			 * Get the part or create it.
			 */
			part = (MPart)element;
			if(!partService.getParts().contains(part)) {
				partService.createPart(part.getElementId());
			}
		} else {
			part = partService.createPart(partId);
			MPartStack partStack = (MPartStack)modelService.find(partStackId, application);
			partStack.getChildren().add(part);
		}
		//
		return part;
	}

	/***
	 * Returns true if the part is visible after running the method.
	 * 
	 * @param part
	 * @param partStackId
	 * @return boolean
	 */
	public static boolean togglePartVisibility(MPart part, String partStackId, EPartService partService) {

		boolean isVisible = false;
		if(part != null) {
			if(part.isVisible()) {
				part.setVisible(false);
				partService.hidePart(part);
			} else {
				part.setVisible(true);
				partService.showPart(part, PartState.ACTIVATE);
				isVisible = true;
			}
		}
		return isVisible;
	}

	public static boolean isChildrenOfPartStack(MPartStack partStack, String elementId) {

		if(partStack != null) {
			for(MStackElement stackElement : partStack.getChildren()) {
				if(stackElement.getElementId().equals(elementId)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isPartVisible(MPart part, EPartService partService) {

		if(part != null) {
			if(partService.isPartVisible(part)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static boolean isPartVisible(String partId, String partStackId, EPartService partService, EModelService modelService, MApplication application) {

		MPart part = getPart(partId, partStackId, partService, modelService, application);
		if(part != null) {
			if(partService.isPartVisible(part)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static void showPart(String partId, String partStackId, EPartService partService, EModelService modelService, MApplication application) {

		MPart part = getPart(partId, partStackId, partService, modelService, application);
		showPart(part, partService);
	}

	public static void showPart(MPart part, EPartService partService) {

		if(part != null) {
			partService.showPart(part, PartState.ACTIVATE);
		}
	}

	public static void hidePart(MPart part, EPartService partService) {

		if(part != null) {
			partService.hidePart(part);
		}
	}

	public static MPartStack getPartStack(String partStackId, EModelService modelService, MApplication application) {

		return (MPartStack)modelService.find(partStackId, application);
	}

	public static void setAreaVisibility(String areaId, boolean visible, EModelService modelService, MApplication application) {

		MArea area = (MArea)modelService.find(areaId, application);
		if(area != null) {
			area.setVisible(visible);
		}
	}

	public static boolean toggleCompositeVisibility(Composite composite) {

		boolean visible = !composite.isVisible();
		setCompositeVisibility(composite, visible);
		return visible;
	}

	public static void setCompositeVisibility(Composite composite, boolean visible) {

		if(composite != null) {
			composite.setVisible(visible);
			Object layoutData = composite.getLayoutData();
			if(layoutData instanceof GridData) {
				GridData gridData = (GridData)layoutData;
				gridData.exclude = !visible;
			}
			Composite parent = composite.getParent();
			parent.layout(true);
			parent.redraw();
		}
	}

	public static boolean toggleControlVisibility(Control control) {

		boolean visible = !control.isVisible();
		setControlVisibility(control, visible);
		return visible;
	}

	public static void setControlVisibility(Control control, boolean visible) {

		control.setVisible(visible);
		GridData gridData = (GridData)control.getLayoutData();
		if(gridData != null) {
			gridData.exclude = !visible;
		}
		//
		Composite parent = control.getParent();
		Composite parentParent = parent.getParent();
		if(parentParent != null) {
			parent = parentParent;
		}
		//
		parent.layout(true);
		parent.redraw();
	}

	public static void setPartVisibility(String partId, String partStackId, boolean visible, EPartService partService, EModelService modelService, MApplication application, IEventBroker eventBroker) {

		MPart part = getPart(partId, partStackId, partService, modelService, application);
		setPartVisibility(part, visible, eventBroker);
	}

	public static void setPartVisibility(MPart part, boolean visible, IEventBroker eventBroker) {

		if(part != null) {
			part.setVisible(visible);
			String partId = part.getElementId();
			//
			if(eventBroker != null) {
				if(visible) {
					eventBroker.post(IChemClipseEvents.TOPIC_TOGGLE_PART_VISIBILITY_TRUE, partId);
				} else {
					eventBroker.post(IChemClipseEvents.TOPIC_TOGGLE_PART_VISIBILITY_FALSE, partId);
				}
			}
		}
	}

	/**
	 * Prefer to use this method if you'd like to toggle the part visibility.
	 * 
	 * @param partId
	 * @param partStackId
	 */
	public static boolean togglePartVisibility(String partId, String partStackId, EPartService partService, EModelService modelService, MApplication application, IEventBroker eventBroker) {

		boolean visible = false;
		if(PartSupport.PARTSTACK_NONE.equals(partStackId)) {
			/*
			 * Hide the part if it is visible.
			 */
			String currentPartStackId = partMap.get(partId);
			if(currentPartStackId != null) {
				setPartVisibility(partId, currentPartStackId, false, partService, modelService, application, eventBroker);
			}
		} else {
			/*
			 * Initialize the part status if the user
			 * has chosen another than the initial position.
			 */
			String currentPartStackId = partMap.get(partId);
			if(currentPartStackId == null) {
				/*
				 * Initialize the part.
				 */
				setPartVisibility(partId, partStackId, false, partService, modelService, application, eventBroker);
			} else {
				/*
				 * Move the part to another part stack.
				 */
				if(!partStackId.equals(currentPartStackId)) {
					MPart part = PartSupport.getPart(partId, currentPartStackId, partService, modelService, application);
					MPartStack defaultPartStack = PartSupport.getPartStack(currentPartStackId, modelService, application);
					MPartStack partStack = PartSupport.getPartStack(partStackId, modelService, application);
					defaultPartStack.getChildren().remove(part);
					partStack.getChildren().add(part);
				}
			}
			partMap.put(partId, partStackId);
			/*
			 * Some part stacks are set hidden initially, see fragment.e4xmi.
			 * Activate them on demand.
			 */
			if(hiddenPartStacks.contains(partStackId)) {
				setPartStackVisibility(partStackId, true, modelService, application);
			}
			/*
			 * Toggle visibility.
			 */
			MPart part = getPart(partId, partStackId, partService, modelService, application);
			visible = togglePartVisibility(part, partStackId, partService);
			/*
			 * Fire an event.
			 * E.g. the icons in the toolbar "TaskQuickAccessPart.java" will be modified.
			 */
			if(visible) {
				eventBroker.post(IChemClipseEvents.TOPIC_TOGGLE_PART_VISIBILITY_TRUE, partId);
			} else {
				eventBroker.post(IChemClipseEvents.TOPIC_TOGGLE_PART_VISIBILITY_FALSE, partId);
			}
		}
		//
		return visible;
	}

	/**
	 * Prefer to use this method if you'd like to toggle the part stack visibility.
	 * 
	 * @param partId
	 * @param partStackId
	 */
	public static void setPartStackVisibility(String partStackId, boolean visible, EModelService modelService, MApplication application) {

		MPartStack partStack = getPartStack(partStackId, modelService, application);
		if(partStack != null) {
			partStack.setVisible(visible);
		}
	}

	public static void addPartImageMappings(String partId, Button button, Image imageActive, Image imageDefault) {

		addImageMappings(partImageMap, partId, button, imageActive, imageDefault);
	}

	public static void setButtonImage(String id, boolean visible) {

		Map<Boolean, Map<Button, Image>> imageMap = partImageMap.get(id);
		if(imageMap != null) {
			Map<Button, Image> buttonMap = imageMap.get(visible);
			if(buttonMap != null) {
				for(Button button : buttonMap.keySet()) {
					Image image = buttonMap.get(button);
					if(image != null) {
						button.setImage(image);
					}
				}
			}
		}
	}

	private static void addImageMappings(Map<String, Map<Boolean, Map<Button, Image>>> buttonImageMap, String partId, Button button, Image imageActive, Image imageDefault) {

		HashMap<Boolean, Map<Button, Image>> imageMap = new HashMap<Boolean, Map<Button, Image>>();
		/*
		 * Active
		 */
		Map<Button, Image> activeMap = new HashMap<Button, Image>();
		activeMap.put(button, imageActive);
		imageMap.put(true, activeMap);
		/*
		 * Default
		 */
		Map<Button, Image> defaultMap = new HashMap<Button, Image>();
		defaultMap.put(button, imageDefault);
		imageMap.put(false, defaultMap);
		//
		buttonImageMap.put(partId, imageMap);
	}

	private static boolean is3xEditorPart(MPart mPart, String editorId) {

		return mPart.getElementId().equals(COMPATIBILITY_EDITOR_ELEMENT_ID) && mPart.getTags().contains(editorId);
	}
}
