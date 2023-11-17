/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - add color compensation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.text.DecimalFormat;
import java.util.Iterator;

import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.pcr.model.core.Position;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.swt.ISelectionHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class PCRWell extends Composite {

	private Color colorInactive = Colors.GRAY;
	private Color colorActive = Colors.RED;
	//
	private Label label;
	//
	private ISelectionHandler selectionHandler;
	private IWell well;

	public PCRWell(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setWell(IWell well) {

		this.well = well;
		refresh();
	}

	public IWell getWell() {

		return well;
	}

	public void refresh() {

		if(well != null) {
			String wellInfo = getWellInfo(well);
			Position position = well.getPosition();
			setContent(position.getRow() + position.getColumn(), wellInfo);
			if(well != null) {
				if(well.isEmptyMeasurement() || !well.isActiveSubset()) {
					setColors(Colors.GRAY, Colors.DARK_GRAY, Colors.WHITE);
				} else {
					if(well.isPositiveMeasurement()) {
						setColors(Colors.RED, Colors.DARK_RED, Colors.WHITE);
					} else {
						setColors(Colors.GREEN, Colors.DARK_GREEN, Colors.BLACK);
					}
				}
			}
		} else {
			setContent("", "No well data available.");
			setColors(Colors.GRAY, Colors.GRAY, Colors.BLACK);
		}
	}

	public void setSelectionHandler(ISelectionHandler selectionHandler) {

		this.selectionHandler = selectionHandler;
	}

	public void setContent(String text, String tooltip) {

		label.setText(text);
		label.setToolTipText(tooltip);
		setToolTipText(tooltip);
	}

	public void setColors(Color colorInactive, Color colorActive, Color colorText) {

		this.colorInactive = colorInactive;
		this.colorActive = colorActive;
		//
		setBackgroundColor(colorInactive);
		setForegroundColor(colorText);
	}

	public void setActive() {

		setBackgroundColor(colorActive);
	}

	public void setInactive() {

		setBackgroundColor(colorInactive);
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		setBackground(colorInactive);
		addControlListener(this);
		//
		addLabel(this);
	}

	private void setBackgroundColor(Color color) {

		setBackground(color);
		label.setBackground(color);
	}

	private void setForegroundColor(Color color) {

		setForeground(color);
		label.setForeground(color);
	}

	private Label addLabel(Composite parent) {

		label = new Label(parent, SWT.NONE);
		label.setText("");
		label.setToolTipText("");
		label.setLayoutData(getGridData());
		addControlListener(label);
		return label;
	}

	private GridData getGridData() {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		return gridData;
	}

	private void addControlListener(Control control) {

		control.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				handleSelection();
			}
		});
		control.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				handleSelection();
			}
		});
	}

	private void handleSelection() {

		if(selectionHandler != null) {
			selectionHandler.handleEvent();
		}
	}

	public String getWellInfo(IWell well) {

		String text = "";
		if(well != null) {
			if(well.isEmptyMeasurement()) {
				text = "Position " + getPosition(well);
			} else {
				if(well.isActiveSubset()) {
					StringBuilder builder = new StringBuilder();
					builder.append(well.getSampleName());
					appendHeaderInfo(well, builder);
					appendCrossingPointInfo(well, builder);
					text = builder.toString();
				} else {
					String sampleSubset = well.getSampleSubset();
					text = sampleSubset.equals("") ? "--" : sampleSubset + " <" + getPosition(well) + ">";
				}
			}
		}
		return text;
	}

	private static void appendHeaderInfo(IWell well, StringBuilder builder) {

		String sampleSubset = well.getSampleSubset();
		String targetName = well.getTargetName();
		builder.append("\n");
		builder.append(sampleSubset.equals("") ? "--" : sampleSubset);
		builder.append(" | ");
		builder.append(targetName.equals("") ? "--" : targetName);
	}

	private void appendCrossingPointInfo(IWell well, StringBuilder builder) {

		builder.append("\n");
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
					builder.append(" | ");
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
			double crossingPoint = channel.getCrossingPoint();
			if(crossingPoint > 0.0d) {
				DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();
				builder.append(decimalFormat.format(crossingPoint));
			} else {
				builder.append("--");
			}
		}
	}

	private int getPosition(IWell well) {

		return well.getPosition().getId() + 1;
	}
}
