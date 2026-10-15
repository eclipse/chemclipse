/*******************************************************************************
 * Copyright (c) 2018, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - support file selection, refactor for new settings model, use validators, support for longs
 * Matthias Mailänder - add labeled combo boxes
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.methods;

import java.io.File;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.model.settings.AbstractProcessSettings;
import org.eclipse.chemclipse.support.settings.ComboSettingsProperty.ComboSupplier;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty.DialogType;
import org.eclipse.chemclipse.support.settings.SettingsPropertyState;
import org.eclipse.chemclipse.support.settings.parser.InputValue;
import org.eclipse.chemclipse.support.settings.validation.InputValidator;
import org.eclipse.chemclipse.support.settings.validation.RegularExpressionValidator;
import org.eclipse.chemclipse.support.text.ILabel;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.AdapterLabelProvider;
import org.eclipse.chemclipse.support.ui.services.IAnnotationWidgetService;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
import org.eclipse.chemclipse.ux.extension.ui.l10n.ExtensionMessages;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class WidgetItem {

	private final InputValue inputValue;

	private InputValidator inputValidator;
	private ControlDecoration controlDecoration;
	private Control control;
	private Object currentSelection;
	private Label label;
	private SettingsPropertyState settingsPropertyState;

	public WidgetItem(InputValue inputValue, Object currentSelection) {

		this.inputValue = inputValue;
		this.currentSelection = currentSelection;
	}

	public InputValue getInputValue() {

		return inputValue;
	}

	public void restoreDefaults() {

		restoreDefaults(control);
	}

	public void restoreDefaults(Control control) {

		if(control instanceof Spinner spinner) {
			Object defaultValue = inputValue.getDefaultValue();
			if(defaultValue instanceof Number number) {
				spinner.setSelection(number.intValue());
			} else if(defaultValue != null) {
				try {
					spinner.setSelection(Integer.parseInt(defaultValue.toString().trim()));
				} catch(NumberFormatException e) {
					spinner.setSelection(0);
				}
			}
		} else if(control instanceof Text text) {
			text.setText((String)inputValue.getDefaultValue());
		} else if(control instanceof Button button) {
			button.setSelection(Boolean.valueOf((String)inputValue.getDefaultValue()));
		} else if(control instanceof Combo combo) {
			Object enumValue = getEnumFrom(inputValue.getDefaultValue(), inputValue.getRawType());
			if(enumValue instanceof ILabel labeledEnum) {
				setSelection(combo, labeledEnum);
			} else {
				setSelection(combo, enumValue.toString());
			}
		} else if(control instanceof Composite composite) {
			for(Control child : composite.getChildren()) {
				restoreDefaults(child);
			}
		}
	}

	private static Object getEnumFrom(Object defaultValue, Class<?> rawType) {

		if(rawType.isEnum()) {
			for(Object constant : rawType.getEnumConstants()) {
				if(constant.toString().equals(defaultValue)) {
					return constant;
				}
			}
		}
		return defaultValue;
	}

	private void setSelection(Combo combo, Object selection) {

		if(selection != null) {
			String label;
			if(selection instanceof ILabel labeledEnum) {
				label = labeledEnum.label();
			} else {
				label = selection.toString();
			}

			String[] items = combo.getItems();
			int selectedIndex = -1;
			for(int i = 0; i < items.length; i++) {
				if(items[i].equals(label)) {
					selectedIndex = i;
					break;
				}
			}

			if(selectedIndex >= 0) {
				combo.select(selectedIndex);
			} else if((combo.getStyle() & SWT.READ_ONLY) == 0) {
				combo.setText(label);
			} else {
				combo.select(selectedIndex);
			}
			if(selection instanceof Enum<?> enumeration) {
				currentSelection = enumeration.name();
			} else {
				currentSelection = label;
			}
		}
	}

	public ControlDecoration getControlDecoration() {

		return controlDecoration;
	}

	public Control getControl() {

		return control;
	}

	public Label getLabel() {

		return label;
	}

	public void setLabel(Label label) {

		this.label = label;
	}

	/**
	 * Returns true if the widget is not disabled by a settings dependency.
	 * 
	 * @return boolean
	 */
	public boolean isDependencyEnabled() {

		return settingsPropertyState == null || settingsPropertyState.isEnabled();
	}

	/**
	 * Returns the current value of the widget. Enum values are returned as enum constants.
	 * 
	 * @return Object
	 */
	public Object getDependencyValue() {

		Object value;
		try {
			value = getValue();
		} catch(RuntimeException e) {
			value = null;
		}

		Class<?> rawType = inputValue.getRawType();
		if(value != null && rawType != null && rawType.isEnum() && !rawType.isInstance(value)) {
			for(Object constant : rawType.getEnumConstants()) {
				if(((Enum<?>)constant).name().equals(value.toString())) {
					return constant;
				}
			}
			return getEnumFrom(value.toString(), rawType);
		}
		return value;
	}

	/**
	 * Overrides the static settings annotations of the field on demand.
	 * 
	 * @param settingsPropertyState
	 */
	public void applySettingsPropertyState(SettingsPropertyState settingsPropertyState) {

		this.settingsPropertyState = settingsPropertyState;
		if(control == null || control.isDisposed()) {
			return;
		}

		boolean enabled = isDependencyEnabled();
		String description = getDescription();
		setEnabledAndToolTip(control, enabled, description);
		if(label != null && !label.isDisposed()) {
			label.setEnabled(enabled);
			if(inputValue.getTooltip() == null || inputValue.getTooltip().isEmpty()) {
				label.setToolTipText(description);
			}
		}

		if(!enabled && controlDecoration != null) {
			controlDecoration.hide();
		}
	}

	private void setEnabledAndToolTip(Control control, boolean enabled, String description) {

		control.setEnabled(enabled);
		control.setToolTipText(description);
		if(control instanceof Composite composite) {
			for(Control child : composite.getChildren()) {
				setEnabledAndToolTip(child, enabled, description);
			}
		}
	}

	private String getDescription() {

		if(settingsPropertyState != null && settingsPropertyState.getDescription() != null) {
			return settingsPropertyState.getDescription();
		}
		return inputValue.getDescription();
	}

	public void initializeControl(Composite parent) {

		control = createControl(parent);
		inputValidator = new InputValidator(inputValue);
		controlDecoration = new ControlDecoration(control, SWT.LEFT | SWT.TOP);
	}

	public IStatus validate() {

		IStatus status = getStatus();
		if(status.isOK()) {
			controlDecoration.hide();
		} else {
			controlDecoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
			controlDecoration.showHoverText(status.getMessage());
			controlDecoration.show();
		}
		return status;
	}

	private IStatus getStatus() {

		if(!isDependencyEnabled()) {
			return ValidationStatus.ok();
		}

		Object currentValue;
		try {
			currentValue = getValue();
		} catch(RuntimeException e) {
			currentValue = null;
		}
		for(IValidator<Object> validator : inputValue.getValidators()) {
			if(settingsPropertyState != null && validator instanceof RegularExpressionValidator) {
				/*
				 * Replaced by the settings dependency.
				 */
				continue;
			}
			IStatus validate = validator.validate(currentValue);
			if(!validate.isOK()) {
				return validate;
			}
		}
		if(settingsPropertyState != null && currentValue instanceof String stringValue) {
			IStatus validate = validate(settingsPropertyState, stringValue);
			if(!validate.isOK()) {
				return validate;
			}
		}
		return inputValidator.validate(currentValue);
	}

	private IStatus validate(SettingsPropertyState settingsPropertyState, String value) {

		String name = inputValue.getName();
		if(value.isEmpty()) {
			return settingsPropertyState.isAllowEmpty() ? ValidationStatus.ok() : ValidationStatus.error("A value is required for " + name);
		}

		String regExp = settingsPropertyState.getRegExp();
		if(!regExp.isEmpty()) {
			RegularExpressionValidator validator = new RegularExpressionValidator(name, Pattern.compile(regExp), settingsPropertyState.getRegExpDescription(), settingsPropertyState.isMultiLine(), settingsPropertyState.isAllowEmpty());
			return validator.validate(value);
		}
		return ValidationStatus.ok();
	}

	public Object getValue() {

		Class<?> rawType = inputValue.getRawType();
		if(rawType != null) {
			/*
			 * File
			 */
			if(rawType == File.class) {
				return currentSelection;
			}
			/*
			 * Spinner
			 */
			if(control instanceof Spinner spinner) {
				return spinner.getSelection();
			}
			/*
			 * Text
			 */
			if(control instanceof Text text) {
				String textValue = text.getText().trim();
				if(rawType == int.class || rawType == Integer.class) {
					if(textValue.isEmpty()) {
						return 0;
					}
					return Integer.parseInt(textValue);
				} else if(rawType == long.class || rawType == Long.class) {
					if(textValue.isEmpty()) {
						return (long)0;
					}
					return Long.parseLong(textValue);
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
				} else if(rawType == byte.class || rawType == Byte.class) {
					if(textValue.isEmpty()) {
						return (byte)0;
					}
					return Byte.parseByte(textValue);
				} else if(rawType == short.class || rawType == Short.class) {
					if(textValue.isEmpty()) {
						return (short)0;
					}
					return Short.parseShort(textValue);
				} else {
					return textValue;
				}
			} else if(control instanceof Button button) {
				/*
				 * Checkbox
				 */
				return button.getSelection();
			} else if(control instanceof Combo) {
				/*
				 * Combo
				 */
				ComboSupplier<?> comboSupplier = inputValue.getComboSupplier();
				if(comboSupplier != null) {
					if(inputValue.isComboEdit()) {
						String userSpecificSelection = currentSelection.toString();
						if(!userSpecificSelection.isBlank()) {
							return userSpecificSelection;
						}
					}
					return getValueAsString(comboSupplier);
				} else if(rawType.isEnum()) {
					return currentSelection.toString();
				}
			} else {
				/*
				 * Specific controls
				 */
				for(Object object : Activator.getDefault().getAnnotationWidgetServices()) {
					if(object instanceof IAnnotationWidgetService annotationWidgetService) {
						Class<?> supportedClass = annotationWidgetService.getSupportedClass();
						if(supportedClass.equals(rawType)) {
							return annotationWidgetService.getValue(currentSelection);
						}
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> String getValueAsString(ComboSupplier<T> comboSupplier) {

		try {
			return comboSupplier.asString((T)currentSelection);
		} catch(ClassCastException cce) {
			return null;
		}
	}

	private void initializeComboEditDefault(ComboViewer comboViewer, ComboSupplier<?> comboSupplier) {

		Object defaultValue = inputValue.getDefaultValue();
		if(defaultValue != null) {
			Object defaultItem = comboSupplier.fromString(defaultValue.toString());
			if(defaultItem != null) {
				comboViewer.setSelection(new StructuredSelection(defaultItem));
				currentSelection = defaultItem;
				return;
			}
		}
		comboViewer.setSelection(StructuredSelection.EMPTY);
	}

	private Control createControl(Composite parent) {

		Class<?> rawType = inputValue.getRawType();
		if(rawType != null) {
			if(inputValue.getComboSupplier() != null) {
				/*
				 * Combo Supplier
				 */
				ComboViewer viewer = createGenericCombo(parent, inputValue.getComboSupplier());
				return viewer.getControl();
			} else if(rawType == int.class || rawType == Integer.class) {
				if(inputValue.isUseSpinner()) {
					return createSpinnerWidget(parent);
				}
				return createTextWidgetNormal(parent);
			} else if(rawType == long.class || rawType == Long.class) {
				return createTextWidgetNormal(parent);
			} else if(rawType == float.class || rawType == Float.class) {
				return createTextWidgetNormal(parent);
			} else if(rawType == double.class || rawType == Double.class) {
				return createTextWidgetNormal(parent);
			} else if(rawType == short.class || rawType == Short.class) {
				return createTextWidgetNormal(parent);
			} else if(rawType == byte.class || rawType == Byte.class) {
				return createTextWidgetNormal(parent);
			} else if(rawType == String.class) {
				if(inputValue.isMultiLine()) {
					return createTextWidgetMultiLine(parent);
				} else {
					Control textControl = createTextWidgetNormal(parent);
					String[] proposals = inputValue.getProposals();
					if(proposals != null && proposals.length > 0) {
						SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider(proposals);
						proposalProvider.setFiltering(true);
						ContentProposalAdapter adapter = new ContentProposalAdapter(textControl, new TextContentAdapter(), proposalProvider, null, null);
						adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
					}
					return textControl;
				}
			} else if(rawType == boolean.class || rawType == Boolean.class) {
				/*
				 * Checkbox
				 */
				return createCheckboxWidget(parent);
			} else if(rawType.isEnum()) {
				/*
				 * Combo
				 */
				if(rawType.getEnumConstants() instanceof Enum[] enums) {
					ComboViewer viewer = createLabeledEnumComboViewerWidget(parent, enums);
					return viewer.getControl();
				}
			} else if(rawType == File.class) {
				return createFileWidget(parent);
			} else {
				/*
				 * Specific controls
				 */
				Control control = getSpecificControl(parent);
				if(control != null) {
					return control;
				} else {
					Label label = new Label(parent, SWT.NONE);
					label.setText("The type is not supported: " + rawType);
					return label;
				}
			}
		}
		return null;
	}

	private Control getSpecificControl(Composite parent) {

		Class<?> rawType = inputValue.getRawType();
		for(Object object : Activator.getDefault().getAnnotationWidgetServices()) {
			if(object instanceof IAnnotationWidgetService annotationWidgetService) {
				Class<?> supportedClass = annotationWidgetService.getSupportedClass();
				if(supportedClass.equals(rawType)) {
					return annotationWidgetService.createWidget(parent, inputValue.getDescription(), currentSelection);
				}
			}
		}

		return null;
	}

	private ComboViewer createGenericCombo(Composite parent, ComboSupplier<?> comboSupplier) {

		ComboViewer comboViewer = new ComboViewer(parent, inputValue.isComboEdit() ? SWT.NONE : SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AdapterLabelProvider());
		combo.setToolTipText(inputValue.getDescription());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		comboViewer.setInput(comboSupplier.items());

		if(currentSelection instanceof String stringSelection) {
			Object currentValue = comboSupplier.fromString(stringSelection);
			if(currentValue != null) {
				if(inputValue.isComboEdit()) {
					comboViewer.getCombo().setText(stringSelection);
				} else {
					comboViewer.setSelection(new StructuredSelection(currentValue));
				}
				currentSelection = currentValue;
			} else if(inputValue.isComboEdit()) {
				if(!stringSelection.isEmpty()) {
					combo.setText(stringSelection);
				} else {
					initializeComboEditDefault(comboViewer, comboSupplier);
				}
			} else {
				comboViewer.setSelection(StructuredSelection.EMPTY);
				currentSelection = null;
			}
		}
		comboViewer.addSelectionChangedListener(_ -> currentSelection = comboViewer.getStructuredSelection().getFirstElement());

		return comboViewer;
	}

	private Control createFileWidget(Composite parent) {

		FileSettingProperty fileSettingProperty = inputValue.getFileSettingProperty();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		composite.setLayout(layout);

		Text text = new Text(composite, SWT.BORDER);
		text.setToolTipText(inputValue.getDescription());
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		text.setText(AbstractProcessSettings.getCleanedFileValue(getValueAsString()));
		text.addModifyListener(_ -> currentSelection = new File(text.getText()));
		text.setFocus();

		Button button = new Button(composite, SWT.PUSH);
		button.setText(" ... ");
		button.setToolTipText(ExtensionMessages.chooseLocation);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean filechooser;
				int style;
				/*
				 * Determine whether to use a File or Directory Dialog
				 * in open or save modus.
				 */
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
				/*
				 *
				 */
				if(filechooser) {
					/*
					 * Navigate to the currently selected directory if available.
					 */
					FileDialog dialog = new FileDialog(button.getShell(), style);
					if(currentSelection != null) {
						File file = new File(currentSelection.toString());
						if(file.exists()) {
							String filterPath;
							if(file.isFile()) {
								filterPath = file.getParentFile().getAbsolutePath();
							} else {
								filterPath = file.getAbsolutePath();
							}
							dialog.setFilterPath(filterPath);
						}
					}
					/*
					 * Show Extensions
					 */
					if(fileSettingProperty != null) {
						String[] extensions = fileSettingProperty.validExtensions();
						String[] extensionNames = fileSettingProperty.extensionNames();
						if(extensions.length > 0 && extensionNames.length == extensions.length) {
							dialog.setFilterExtensions(extensions);
							dialog.setFilterNames(extensionNames);
						}
					}
					/*
					 * Open File Dialog
					 */
					String open = dialog.open();
					if(open != null) {
						text.setText(open);
						currentSelection = new File(open);
					}
				} else {
					/*
					 * Navigate to the currently selected directory if available.
					 */
					DirectoryDialog dialog = new DirectoryDialog(button.getShell(), style);
					if(currentSelection != null) {
						File file = new File(currentSelection.toString());
						if(file.exists()) {
							dialog.setFilterPath(file.getAbsolutePath());
						}
					}
					/*
					 * Open Directory Dialog
					 */
					String open = dialog.open();
					if(open != null) {
						text.setText(open);
						currentSelection = new File(open);
					}
				}
				/*
				 * Handle the events.
				 */
				Listener[] listeners = composite.getListeners(SWT.Selection);
				for(Listener listener : listeners) {
					listener.handleEvent(new Event());
				}
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
		text.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_TRANSPARENT)); // workaround
		text.setData("org.eclipse.e4.ui.css.id", "MethodWidgetItem");
		text.setText(getValueAsString());
		text.setToolTipText(inputValue.getDescription());
		text.setLayoutData(gridData);
		return text;
	}

	private Control createCheckboxWidget(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setSelection(getValueAsBoolean());
		button.setToolTipText(inputValue.getDescription());
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return button;
	}

	private Control createSpinnerWidget(Composite parent) {

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		/*
		 * Widen the widget range so that typed out-of-range values are not
		 * silently clamped. The MinMaxValidator attached from the annotation
		 * will flag out-of-range values via the ControlDecoration, mirroring
		 * the behavior of the plain text input.
		 */
		spinner.setMinimum(Integer.MIN_VALUE);
		spinner.setMaximum(Integer.MAX_VALUE);
		spinner.setIncrement(inputValue.getSpinnerStep());
		spinner.setSelection(getValueAsInt());
		spinner.setToolTipText(inputValue.getDescription());
		spinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return spinner;
	}

	private int getValueAsInt() {

		String value = getValueAsString();
		if(value == null || value.isEmpty()) {
			return 0;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	private ComboViewer createLabeledEnumComboViewerWidget(Composite parent, Enum<?>[] input) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ILabel label) {
					return label.label();
				}
				return element.toString();
			}
		});

		combo.setToolTipText(inputValue.getDescription());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);

		comboViewer.setInput(input);
		Enum<?> initialSelection = Enum.valueOf(input[0].getDeclaringClass(), getValueAsString());
		comboViewer.setSelection(new StructuredSelection(initialSelection));

		comboViewer.addSelectionChangedListener(_ -> currentSelection = comboViewer.getStructuredSelection().getFirstElement());

		return comboViewer;
	}

	private boolean getValueAsBoolean() {

		if(currentSelection instanceof Boolean booleanSelection) {
			return booleanSelection.booleanValue();
		}

		if(currentSelection instanceof String stringSelection) {
			return Boolean.valueOf(stringSelection);
		}

		return false;
	}

	private String getValueAsString() {

		if(currentSelection == null) {
			return "";
		}

		if(currentSelection instanceof String stringSelection) {
			return stringSelection;
		} else {
			return currentSelection.toString();
		}
	}
}