/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.pcr.model.core.Position;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.swt.ISelectionHandler;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class PCRPlate extends Composite {

	private static final String LABEL_DATA = "LABEL_DATA";
	//
	private Map<Position, PCRWell> pcrWells = new HashMap<>();
	private IWell well = null;
	private Label wellDetailsLabel;

	public PCRPlate(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void refresh() {

		for(PCRWell pcrWell : pcrWells.values()) {
			pcrWell.refresh();
		}
	}

	public IWell getSelectedWell() {

		return well;
	}

	public void setPlate(IPlate plate) {

		/*
		 * Reset
		 */
		for(PCRWell pcrWell : pcrWells.values()) {
			pcrWell.setWell(null);
		}
		/*
		 * Set active
		 */
		if(plate != null) {
			Set<IWell> wells = plate.getWells();
			for(IWell well : wells) {
				Position position = well.getPosition();
				PCRWell pcrWell = pcrWells.get(position);
				if(pcrWell != null) {
					pcrWell.setWell(well);
				}
			}
		}
		/*
		 * Select the first well.
		 */
		PCRWell pcrWell = pcrWells.get(new Position("A", 1));
		handleSelection(pcrWell);
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(13, true);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		/*
		 * Headline
		 */
		createCornerWell(composite);
		for(int i = 1; i <= 12; i++) {
			createDesciptionWell(composite, Integer.toString(i));
		}
		/*
		 * Wells
		 */
		for(int i = 65; i <= 72; i++) {
			String row = Character.toString(((char)i));
			createDesciptionWell(composite, row);
			for(int j = 1; j <= 12; j++) {
				Position position = new Position(row, j);
				createDataWell(composite, position);
			}
		}
		//
		wellDetailsLabel = createLabel(composite);
	}

	private void createCornerWell(Composite parent) {

		createWell(parent, null, Colors.GRAY, Colors.GRAY, Colors.BLACK, "", "");
	}

	private void createDesciptionWell(Composite parent, String content) {

		createWell(parent, null, Colors.WHITE, Colors.WHITE, Colors.BLACK, content, "");
	}

	private void createDataWell(Composite parent, Position position) {

		createWell(parent, position, Colors.GRAY, Colors.DARK_GRAY, Colors.BLACK, position.getRow() + position.getColumn(), position.toString());
	}

	private void createWell(Composite parent, Position position, Color colorInactive, Color colorActive, Color colorText, String text, String tooltip) {

		PCRWell pcrWell = new PCRWell(parent, SWT.BORDER);
		pcrWell.setContent(text, tooltip);
		pcrWell.setLayoutData(new GridData(GridData.FILL_BOTH));
		pcrWell.setColors(colorInactive, colorActive, colorText);
		//
		pcrWell.setSelectionHandler(new ISelectionHandler() {

			@Override
			public void handleEvent() {

				handleSelection(pcrWell);
			}
		});
		//
		pcrWell.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {

				highlightComposite(pcrWell);
				showWellDetails(pcrWell.getWell());
			}
		});
		//
		if(position != null) {
			pcrWells.put(position, pcrWell);
		}
	}

	private void handleSelection(PCRWell pcrWell) {

		showWellDetails(pcrWell.getWell());
		well = pcrWell.getWell();
		String topic = (well != null) ? IChemClipseEvents.TOPIC_WELL_PCR_UPDATE_SELECTION : IChemClipseEvents.TOPIC_WELL_PCR_UNLOAD_SELECTION;
		pcrWell.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {

				IEventBroker eventBroker = Activator.getDefault().getEventBroker();
				if(eventBroker != null) {
					eventBroker.send(topic, (well != null) ? well : null);
				}
			}
		});
	}

	private Label createLabel(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 13;
		label.setLayoutData(gridData);
		return label;
	}

	private void showWellDetails(IWell well) {

		if(well == null) {
			wellDetailsLabel.setText("");
			wellDetailsLabel.setData(LABEL_DATA, null);
		} else {
			Object object = wellDetailsLabel.getData(LABEL_DATA);
			if(!well.equals(object)) {
				wellDetailsLabel.setData(LABEL_DATA, well);
				StringBuilder builder = new StringBuilder();
				String sampleSubset = well.getSampleSubset();
				String targetName = well.getTargetName();
				builder.append(well.getSampleId());
				if(!well.isEmptyMeasurement()) {
					builder.append(" | ");
					builder.append(sampleSubset.equals("") ? "--" : sampleSubset);
					builder.append(" | ");
					builder.append(targetName.equals("") ? "--" : targetName);
					builder.append(" [ ");
					appendCrossingPointInfo(well, builder);
					builder.append(" ] ");
				}
				//
				wellDetailsLabel.setText(builder.toString());
			}
		}
	}

	private void appendCrossingPointInfo(IWell well, StringBuilder builder) {

		/*
		 * Crossing Points
		 */
		IChannel activeChannel = well.getActiveChannel();
		if(activeChannel == null) {
			/*
			 * All channels
			 */
			Iterator<IChannel> iterator = well.getChannels().values().iterator();
			while(iterator.hasNext()) {
				IChannel channel = iterator.next();
				appendChannelCrossingPoint(channel, builder);
				if(iterator.hasNext()) {
					builder.append(" , ");
				}
			}
		} else {
			/*
			 * Active channel
			 */
			appendChannelCrossingPoint(activeChannel, builder);
		}
	}

	private void appendChannelCrossingPoint(IChannel channel, StringBuilder builder) {

		if(channel != null) {
			IPoint crossingPoint = channel.getCrossingPoint();
			if(crossingPoint != null && crossingPoint.getX() > 0.0d) {
				DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();
				builder.append(decimalFormat.format(crossingPoint.getX()));
			} else {
				builder.append("--");
			}
		}
	}

	private void highlightComposite(PCRWell pcrWellHighlight) {

		for(PCRWell pcrWell : pcrWells.values()) {
			if(pcrWell == pcrWellHighlight) {
				pcrWell.setActive();
			} else {
				pcrWell.setInactive();
			}
		}
	}
}
