/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - support file selection, refactor for new settings model
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty.DialogType;
import org.eclipse.chemclipse.support.settings.parser.InputValue;
import org.eclipse.chemclipse.support.settings.validation.InputValidator;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class WidgetItem {

	private InputValue inputValue;
	private InputValidator inputValidator;
	private ControlDecoration controlDecoration;
	private Control control;
	private Object currentSelection;

	public WidgetItem(InputValue inputValue, Object currentSelection) {
		this.inputValue = inputValue;
		this.currentSelection = currentSelection;
	}

	public InputValue getInputValue() {

		return inputValue;
	}

	public InputValidator getInputValidator() {

		return inputValidator;
	}

	public ControlDecoration getControlDecoration() {

		return controlDecoration;
	}

	public Control getControl() {

		return control;
	}

	public void initializeControl(Composite parent) {

		control = createControl(parent);
		inputValidator = new InputValidator(inputValue);
		controlDecoration = new ControlDecoration(control, SWT.LEFT | SWT.TOP);
	}

	public String validate() {

		String message = null;
		/*
		 * Get the input.
		 */
		Object input = currentSelection;
		if(control instanceof Text) {
			input = ((Text)control).getText().trim();
		} else if(control instanceof Button) {
			input = Boolean.toString(((Button)control).getSelection());
		} else if(control instanceof Combo) {
			input = ((Combo)control).getText().trim();
		}
		/*
		 * Validate
		 */
		IStatus status = inputValidator.validate(input);
		if(status.isOK()) {
			controlDecoration.hide();
		} else {
			message = status.getMessage();
			controlDecoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
			controlDecoration.showHoverText(status.getMessage());
			controlDecoration.show();
		}
		//
		return message;
	}

	public Object getValue() {

		Class<?> rawType = inputValue.getRawType();
		if(rawType != null) {
			if(rawType == File.class) {
				return currentSelection;
			}
			if(control instanceof Text) {
				/*
				 * Text
				 */
				Text text = (Text)control;
				String textValue = text.getText().trim();
				//
				if(rawType == int.class || rawType == Integer.class) {
					if(textValue.isEmpty()) {
						return 0;
					}
					return Integer.parseInt(textValue);
				} else if(rawType == float.class || rawType == Float.class) {
					if(textValue.isEmpty()) {
						return 0f;
					}
					return Float.parseFloat(textValue);
				} else if(rawType == double.class || rawType == Double.class) {
					if(textValue.isEmpty()) {
						return 0d;
					}
					return Double.parseDouble(textValue);
				} else {
					return textValue;
				}
			} else if(control instanceof Button) {
				/*
				 * Checkbox
				 */
				Button button = (Button)control;
				return button.getSelection();
			} else if(control instanceof Combo) {
				/*
				 * Combo
				 */
				Combo combo = (Combo)control;
				if(rawType.isEnum()) {
					return combo.getText().trim();
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private Control createControl(Composite parent) {

		Class<?> rawType = inputValue.getRawType();
		if(rawType != null) {
			if(rawType == int.class || rawType == Integer.class) {
				return createTextWidgetNormal(parent);
			} else if(rawType == float.class || rawType == Float.class) {
				return createTextWidgetNormal(parent);
			} else if(rawType == double.class || rawType == Double.class) {
				return createTextWidgetNormal(parent);
			} else if(rawType == String.class) {
				if(inputValue.isMultiLine()) {
					return createTextWidgetMultiLine(parent);
				} else {
					return createTextWidgetNormal(parent);
				}
			} else if(rawType == boolean.class || rawType == Boolean.class) {
				return createCheckboxWidget(parent);
			} else if(rawType.isEnum()) {
				List<String> input = new ArrayList<>();
				Enum[] enums = (Enum[])rawType.getEnumConstants();
				for(int i = 0; i < enums.length; i++) {
					input.add(enums[i].toString());
				}
				return createComboViewerWidget(parent, input);
			} else if(rawType == File.class) {
				return createFileWidget(parent);
			} else {
				Label label = new Label(parent, SWT.NONE);
				label.setText("Unsupported Type: " + rawType);
				return label;
			}
		}
		return null;
	}

	private Control createFileWidget(Composite parent) {

		FileSettingProperty fileSettingProperty = inputValue.getFileSettingProperty();
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		CLabel label = new CLabel(composite, SWT.NONE);
		label.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		String value = getValueAsString();
		if(value == null || value.isEmpty()) {
			label.setText("Please choose a location ...");
		} else {
			label.setText(value);
		}
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		Button button = new Button(composite, SWT.PUSH);
		button.setText(" ... ");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean filechooser;
				int style;
				if(fileSettingProperty != null) {
					filechooser = !fileSettingProperty.onlyDirectory();
					if(fileSettingProperty.dialogType() == DialogType.OPEN_DIALOG) {
						style = SWT.OPEN;
					} else {
						style = SWT.SAVE;
					}
				} else {
					filechooser = true;
					style = SWT.OPEN;
				}
				if(filechooser) {
					FileDialog dialog = new FileDialog(button.getShell(), style);
					if(fileSettingProperty != null) {
						String[] extensions = fileSettingProperty.validExtensions();
						String[] extensionNames = fileSettingProperty.extensionNames();
						if(extensions.length > 0) {
							dialog.setFilterExtensions(extensions);
						}
						if(extensionNames.length > 0) {
							dialog.setFilterNames(extensionNames);
						}
					}
					String open = dialog.open();
					if(open != null) {
						label.setText(open);
						currentSelection = new File(open);
					}
				} else {
					DirectoryDialog dialog = new DirectoryDialog(button.getShell(), style);
					String open = dialog.open();
					if(open != null) {
						label.setText(open);
						currentSelection = new File(open);
					}
				}
				Listener[] listeners = composite.getListeners(SWT.Selection);
				for(Listener listener : listeners) {
					listener.handleEvent(new Event());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		return composite;
	}

	private Control createTextWidgetNormal(Composite parent) {

		return createTextWidget(parent, SWT.BORDER, new GridData(GridData.FILL_HORIZONTAL));
	}

	private Control createTextWidgetMultiLine(Composite parent) {

		return createTextWidget(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI, new GridData(GridData.FILL_BOTH));
	}

	private Control createTextWidget(Composite parent, int style, GridData gridData) {

		Text text = new Text(parent, style);
		text.setText(getValueAsString());
		text.setToolTipText(inputValue.getDescription());
		text.setLayoutData(gridData);
		return text;
	}

	private Control createCheckboxWidget(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText("");
		button.setSelection(getValueAsBoolean());
		button.setToolTipText(inputValue.getDescription());
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return button;
	}

	private Control createComboViewerWidget(Composite parent, List<String> input) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		//
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				return element.toString();
			}
		});
		combo.setToolTipText(inputValue.getDescription());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		//
		comboViewer.setInput(input);
		combo.setText(getValueAsString());
		//
		return combo;
	}

	private boolean getValueAsBoolean() {

		if(currentSelection instanceof Boolean) {
			return ((Boolean)currentSelection).booleanValue();
		}
		if(currentSelection instanceof String) {
			return Boolean.valueOf((String)currentSelection);
		}
		return false;
	}

	private String getValueAsString() {

		if(currentSelection == null) {
			return "";
		}
		if(currentSelection instanceof String) {
			return (String)currentSelection;
		} else {
			return currentSelection.toString();
		}
	}
}
