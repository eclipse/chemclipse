/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - refactor to use a TileDefinition as model
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.swt;

import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
import org.eclipse.chemclipse.ux.extension.ui.definitions.TileDefinition;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class TaskTile extends Composite {

	private static final Logger logger = Logger.getLogger(TaskTile.class);
	//
	public static final int LARGE_TITLE = (1 << 1);
	public static final int HIGHLIGHT = (1 << 2);
	public static final int WRAP_IMAGE = (1 << 3);
	//
	private Color colorInactive;
	private Color colorActive;
	//
	private Label labelImage;
	private Label textSection;
	private Label textDesciption;
	//
	private final TileDefinition definition;
	private final Consumer<TileDefinition> definitionConsumer;
	private final Color[] colors;
	private final Function<TileDefinition, Integer> styleFunction;

	public TaskTile(Composite parent, TileDefinition definition, Consumer<TileDefinition> definitionConsumer, Function<TileDefinition, Integer> styleFunction, Color[] colors) {

		super(parent, SWT.NONE);
		//
		if(colors.length < 4) {
			/*
			 * Warn and create default colors.
			 */
			logger.warn("The task tile requires at least 4 colors (active, inactive, title, description)."); //$NON-NLS-1$
			Color colorActive = TaskTileContainer.DEFAULT_COLOR_ACTIVE;
			Color colorInactive = TaskTileContainer.DEFAULT_COLOR_INACTIVE;
			Color colorTitle = TaskTileContainer.DEFAULT_COLOR_TITLE;
			Color colorDescription = TaskTileContainer.DEFAULT_COLOR_DESCRIPTION;
			colors = new Color[]{colorActive, colorInactive, colorTitle, colorDescription};
		}
		//
		this.definition = definition;
		this.definitionConsumer = definitionConsumer;
		this.styleFunction = styleFunction;
		this.colors = colors;
		//
		initialize();
		updateFromDefinition();
	}

	@Override
	public void dispose() {

		super.dispose();
	}

	public TileDefinition getDefinition() {

		return definition;
	}

	private void setContent(Image image, String section, String description, boolean wrapImage) {

		labelImage.setImage(image);
		if(image == null) {
			modifyLabelImage(true, wrapImage);
		} else {
			modifyLabelImage(false, wrapImage);
		}
		//
		textSection.setText(section);
		textDesciption.setText(description == null ? "" : description); //$NON-NLS-1$
	}

	public void setActive() {

		setBackgroundColor(colorActive);
	}

	public void setInactive() {

		setBackgroundColor(colorInactive);
	}

	private void initialize() {

		setLayout(new GridLayout(1, true));
		Composite composite = new Composite(this, SWT.NONE);
		composite.setBackgroundMode(SWT.INHERIT_FORCE);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		composite.setLayout(new GridLayout(2, true));
		addControlListener(composite);
		addControlListener(this);
		labelImage = addLabelImage(composite);
		textSection = addTextSection(composite);
		textDesciption = addTextDescription(composite);
	}

	private Label addLabelImage(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(getGridData(SWT.END, SWT.END, 1));
		addControlListener(label);
		return label;
	}

	private Label addTextSection(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(""); //$NON-NLS-1$
		label.setLayoutData(getGridData(SWT.BEGINNING, SWT.END, 1));
		label.setForeground(colors[2]);
		addControlListener(label);
		return label;
	}

	private Label addTextDescription(Composite parent) {

		Label label = new Label(parent, SWT.CENTER | SWT.WRAP);
		label.setText(""); //$NON-NLS-1$
		label.setLayoutData(getGridData(SWT.CENTER, SWT.BEGINNING, 2));
		label.setForeground(colors[3]);
		addControlListener(label);
		return label;
	}

	private GridData getGridData(int horizontalAlignment, int verticalAlignment, int horizontalSpan) {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalAlignment = horizontalAlignment;
		gridData.verticalAlignment = verticalAlignment;
		gridData.horizontalSpan = horizontalSpan;
		gridData.grabExcessHorizontalSpace = true;
		return gridData;
	}

	private void setBackgroundColor(Color color) {

		setBackground(color);
		labelImage.setBackground(color);
		textSection.setBackground(color);
		textDesciption.setBackground(color);
	}

	private void modifyLabelImage(boolean exclude, boolean wrap) {

		GridData gridDataLabel = (GridData)labelImage.getLayoutData();
		gridDataLabel.exclude = exclude;
		gridDataLabel.horizontalSpan = wrap ? 2 : 1;
		gridDataLabel.horizontalAlignment = wrap ? SWT.CENTER : SWT.END;
		gridDataLabel.grabExcessVerticalSpace = !wrap;
		labelImage.setVisible(!exclude);
		GridData gridDataText = (GridData)textSection.getLayoutData();
		gridDataText.horizontalAlignment = (exclude || wrap) ? SWT.CENTER : SWT.BEGINNING;
		gridDataText.horizontalSpan = (exclude || wrap) ? 2 : 1;
	}

	private void addControlListener(Control control) {

		control.addMouseListener(new MouseAdapter() {

			private boolean active;

			@Override
			public void mouseDown(MouseEvent e) {

				if(e.button == 1) {
					active = true;
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {

				if(active && matches(e)) {
					handleSelection();
				}
			}

			private boolean matches(MouseEvent e) {

				Point size = control.getSize();
				return e.x >= 0 && e.x <= size.x && e.y >= 0 && e.y <= size.y;
			}
		});
		//
		control.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.CR) {
					handleSelection();
				}
			}
		});
	}

	private void handleSelection() {

		if(definition != null) {
			try {
				definitionConsumer.accept(definition);
			} catch(RuntimeException e) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, "TaskTile", "invoke of consumer failed", e)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	private void updateStyle(int style) {

		if((style & HIGHLIGHT) != 0) {
			colorActive = colors[0];
			colorInactive = colors[1];
			labelImage.setEnabled(true);
			textSection.setEnabled(true);
			textDesciption.setEnabled(true);
		} else {
			colorActive = colors[1];
			colorInactive = colors[1];
			labelImage.setEnabled(false);
			textSection.setEnabled(false);
			textDesciption.setEnabled(false);
		}
		//
		int fontSize;
		//
		if((style & LARGE_TITLE) != 0) {
			fontSize = 40;
		} else {
			fontSize = 18;
		}
		//
		Font font = new Font(getDisplay(), "Arial", fontSize, SWT.BOLD); //$NON-NLS-1$
		textSection.setFont(font);
		font.dispose();
	}

	public void updateFromDefinition() {

		if(definition != null) {
			Integer style = styleFunction.apply(definition);
			updateStyle(style != null ? style.intValue() : 0);
			setContent(definition.getIcon(), definition.getTitle(), definition.getDescription(), (style & WRAP_IMAGE) != 0);
			setInactive();
			layout(true);
			redraw();
		}
	}
}