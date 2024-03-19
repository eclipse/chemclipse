/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.ui.files.ExtendedFileDialog;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.services.IMoleculeImageService;
import org.eclipse.chemclipse.swt.ui.services.ImageServiceInput;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageMolecule;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtchart.extensions.clipboard.ImageArrayTransfer;
import org.eclipse.swtchart.extensions.core.IKeyboardSupport;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ExtendedMoleculeUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedMoleculeUI.class);
	//
	private static final String EMPTY_MESSAGE = "Please select a target to view a molecular structure.";
	private static final String ERROR_MESSAGE = "The molecule image couldn't be created.";
	//
	private static final double SCALE_DEFAULT = 1.0d;
	private static final double SCALE_DELTA = 0.1d;
	//
	private AtomicReference<Button> buttonToolbarInfo = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarEdit = new AtomicReference<>();
	private AtomicReference<Composite> toolbarEdit = new AtomicReference<>();
	private AtomicReference<ComboViewer> comboViewerServices = new AtomicReference<>();
	private AtomicReference<Text> textInput = new AtomicReference<>();
	private AtomicReference<ComboViewer> comboViewerInput = new AtomicReference<>();
	private AtomicReference<Canvas> canvasMolecule = new AtomicReference<>();
	private AtomicReference<Text> textMolecule = new AtomicReference<>();
	//
	//
	private double scaleFactor = SCALE_DEFAULT;
	private Image imageMolecule = null;
	private ILibraryInformation libraryInformation;
	private ILibraryInformation renderedLibraryInformation;
	private Point renderedSize;
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public ExtendedMoleculeUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(ILibraryInformation libraryInformation) {

		this.libraryInformation = libraryInformation;
		updateContent();
	}

	@Override
	public void dispose() {

		if(imageMolecule != null) {
			imageMolecule.dispose();
		}
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createToolbarEdit(this);
		createTabFolderSection(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo.get(), IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarEdit, buttonToolbarEdit.get(), IMAGE_EDIT, TOOLTIP_EDIT, false);
		/*
		 * Molecule Services / Types
		 */
		updateComboViewerImageServices();
		updateComboViewerInputTypes();
		/*
		 * Create the image
		 */
		updateContent();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(6, false));
		//
		createButtonToggleInfo(composite);
		createButtonToggleEdit(composite);
		createComboViewerServices(composite);
		createButtonReset(composite);
		createButtonExport(composite);
		createSettingsButton(composite);
	}

	private void createButtonToggleInfo(Composite parent) {

		buttonToolbarInfo.set(createButtonToggleToolbar(parent, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO));
	}

	private void createButtonToggleEdit(Composite parent) {

		buttonToolbarEdit.set(createButtonToggleToolbar(parent, toolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT));
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createTextInput(composite);
		createComboViewerInput(composite);
		createButtonCalculate(composite);
		//
		toolbarEdit.set(composite);
	}

	private void createTextInput(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setToolTipText("Input");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(isEnterPressed(e)) {
					libraryInformation = createLibraryInformationByInput();
					updateContent();
				}
			}
		});
		//
		textInput.set(text);
	}

	private void createComboViewerInput(Composite composite) {

		ComboViewer comboViewer = new EnhancedComboViewer(composite, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ImageServiceInput imageServiceInput) {
					return imageServiceInput.label();
				}
				//
				return null;
			}
		});
		/*
		 * Select the item.
		 */
		combo.setToolTipText("Select an image service input type.");
		GridData gridData = new GridData();
		gridData.widthHint = 80;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setTextInput();
			}
		});
		//
		comboViewerInput.set(comboViewer);
	}

	private Button createButtonCalculate(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Calculate the molecule image.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CALCULATE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				libraryInformation = createLibraryInformationByInput();
				updateContent();
			}
		});
		//
		return button;
	}

	private void createTabFolderSection(Composite parent) {

		TabFolder tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setBackgroundMode(SWT.INHERIT_DEFAULT);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		createMoleculeImage(tabFolder);
		createMoleculeContent(tabFolder);
	}

	private void createMoleculeImage(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Molecule");
		//
		Composite composite = new Composite(tabFolder, SWT.BORDER);
		composite.setLayout(new FillLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		Canvas canvas = createCanvasMolecule(composite);
		tabItem.setControl(composite);
		//
		canvasMolecule.set(canvas);
	}

	private Canvas createCanvasMolecule(Composite parent) {

		Canvas canvas = new Canvas(parent, SWT.FILL);
		canvas.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		//
		canvas.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {

				canvas.redraw();
			}
		});
		//
		canvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(MouseEvent event) {

				if(getMoleculeImageService().isOnline()) {
					return;
				} else {
					scaleFactor += (event.count > 0) ? SCALE_DELTA : -SCALE_DELTA;
					canvas.redraw();
				}
			}
		});
		//
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent event) {

				drawImage(canvas, event);
			}
		});
		//
		canvas.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.stateMask == SWT.MOD1 && e.keyCode == IKeyboardSupport.KEY_CODE_LC_C) {
					ImageData imageData = imageMolecule.getImageData();
					if(imageData != null) {
						Clipboard clipboard = new Clipboard(e.display);
						try {
							if(OperatingSystemUtils.isWindows()) {
								clipboard.setContents(new Object[]{imageData, imageData}, new Transfer[]{ImageArrayTransfer.getImageTransferWindows(), ImageArrayTransfer.getInstanceWindows()});
							} else if(OperatingSystemUtils.isLinux()) {
								clipboard.setContents(new Object[]{imageData}, new Transfer[]{ImageArrayTransfer.getInstanceLinux()});
							} else if(OperatingSystemUtils.isMac() || OperatingSystemUtils.isUnix()) {
								clipboard.setContents(new Object[]{imageData}, new Transfer[]{ImageArrayTransfer.getImageTransferMacOS()});
							}
						} finally {
							if(!clipboard.isDisposed()) {
								clipboard.dispose();
							}
						}
					}
				}
			}
		});
		//
		return canvas;
	}

	private void createMoleculeContent(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Content");
		//
		Text text = createTextMolecule(tabFolder);
		text.setEditable(false);
		tabItem.setControl(text);
		//
		textMolecule.set(text);
	}

	private Text createTextMolecule(Composite parent) {

		Text text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setText("");
		text.setToolTipText("Molecule Structure");
		//
		return text;
	}

	private void createComboViewerServices(Composite composite) {

		ComboViewer comboViewer = new EnhancedComboViewer(composite, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IMoleculeImageService moleculeImageService) {
					combo.setToolTipText(moleculeImageService.getDescription());
					StringBuilder builder = new StringBuilder();
					builder.append(moleculeImageService.getName());
					builder.append(" (");
					builder.append(moleculeImageService.isOnline() ? "online" : "offline");
					builder.append(")");
					return builder.toString();
				}
				//
				return null;
			}
		});
		/*
		 * Select the item.
		 */
		combo.setToolTipText("Select a service to create the molecule image.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 200;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(comboViewer.getStructuredSelection().getFirstElement() instanceof IMoleculeImageService moleculeImageService) {
					PreferenceSupplier.setMoleculeImageServiceId(moleculeImageService.getClass().getName());
				}
				reset(e.display);
			}
		});
		//
		comboViewerServices.set(comboViewer);
	}

	private Button createButtonReset(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Reset molecule image.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset(e.display);
			}
		});
		//
		return button;
	}

	private void reset(Display display) {

		scaleFactor = SCALE_DEFAULT;
		renderedLibraryInformation = null;
		updateContent();
		createMoleculeImage(display);
	}

	private Button createButtonExport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Export the molecule image");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(imageMolecule != null) {
					FileDialog fileDialog = ExtendedFileDialog.create(e.display.getActiveShell(), SWT.SAVE);
					fileDialog.setOverwrite(true);
					fileDialog.setText("Save Molecule");
					fileDialog.setFilterExtensions(new String[]{"*.png"});
					fileDialog.setFilterNames(new String[]{"Portable Network Graphics  (*.png)"});
					fileDialog.setFileName(getExportName());
					fileDialog.setFilterPath(preferenceStore.getString(PreferenceSupplier.P_MOLECULE_PATH_EXPORT));
					//
					String pathname = fileDialog.open();
					if(pathname != null) {
						preferenceStore.setValue(PreferenceSupplier.P_MOLECULE_PATH_EXPORT, fileDialog.getFilterPath());
						ImageData data = imageMolecule.getImageData();
						ImageLoader loader = new ImageLoader();
						loader.data = new ImageData[]{data};
						loader.save(pathname, SWT.IMAGE_PNG);
					}
				}
			}
		});
		//
		return button;
	}

	private void createSettingsButton(Composite parent) {

		/*
		 * Default page
		 */
		List<Class<? extends IPreferencePage>> preferencePages = new ArrayList<>();
		preferencePages.add(PreferencePageMolecule.class);
		/*
		 * Additional pages.
		 */
		Object[] moleculeImageServices = Activator.getDefault().getMoleculeImageServices();
		if(moleculeImageServices != null) {
			for(Object object : moleculeImageServices) {
				if(object instanceof IMoleculeImageService moleculeImageService) {
					Class<? extends IWorkbenchPreferencePage> preferencePage = moleculeImageService.getPreferencePage();
					if(preferencePage != null) {
						preferencePages.add(preferencePage);
					}
				}
			}
		}
		//
		createSettingsButton(parent, preferencePages, new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				reset(display);
			}
		});
	}

	private boolean isEnterPressed(KeyEvent e) {

		return (e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR);
	}

	private void createMoleculeImage(Display display) {

		String moleculeInfo = "";
		IMoleculeImageService moleculeImageService = getMoleculeImageService();
		//
		if(moleculeImageService != null) {
			/*
			 * Canvas and a scale factor are used.
			 */
			Point size = calculateImageSize();
			int width = size.x;
			int height = size.y;
			/*
			 * If the library information is null, take the text.
			 */
			if(libraryInformation == null) {
				libraryInformation = new LibraryInformation();
				ImageServiceInput imageInput = getImageInput();
				String text = textInput.get().getText().trim();
				switch(imageInput) {
					case SMILES:
						libraryInformation.setSmiles(text);
						break;
					default:
						libraryInformation.setName(text);
						break;
				}
			}
			/*
			 * Skip creation if the molecule image exists already.
			 * Take a size change of the canvas into account.
			 */
			if(!size.equals(renderedSize) || renderedLibraryInformation != libraryInformation) {
				moleculeInfo = getMoleculeInformation(libraryInformation);
				if(isSourceDataAvailable(libraryInformation)) {
					/*
					 * Dispose is required on images.
					 */
					if(imageMolecule != null) {
						imageMolecule.dispose();
						imageMolecule = null;
					}
					/*
					 * Create a new molecule image.
					 */
					imageMolecule = moleculeImageService.create(display, libraryInformation, width, height);
					renderedLibraryInformation = libraryInformation;
					renderedSize = size;
				} else {
					logger.info(ERROR_MESSAGE);
				}
			}
		}
		//
		toolbarInfo.get().setText(moleculeInfo);
	}

	private Point calculateImageSize() {

		Point size = canvasMolecule.get().getSize();
		int width = size.x;
		int height = size.y;
		//
		if(scaleFactor != 1.0d) {
			width += (int)(size.x * scaleFactor);
			height += (int)(size.y * scaleFactor);
		}
		//
		return adjustSize(width, height);
	}

	private Point adjustSize(int width, int height) {

		width = (width <= 0) ? 1 : width;
		height = (height <= 0) ? 1 : height;
		//
		return new Point(width, height);
	}

	private void drawImage(Canvas canvas, PaintEvent paintEvent) {

		drawImage(canvas, paintEvent.display, paintEvent.gc);
	}

	private void drawImage(Canvas canvas, Display display, GC gc) {

		if(libraryInformation == null || !isSourceDataAvailable(libraryInformation)) {
			/*
			 * Instructions
			 */
			Font font = getFont();
			FontData[] fontData = font.getFontData();
			int width = gc.stringExtent(EMPTY_MESSAGE).x;
			int height = fontData[0].getHeight();
			//
			Point size = canvas.getSize();
			int x = (int)(size.x / 2.0d - width / 2.0d);
			int y = (int)(size.y / 2.0d - height / 2.0d);
			gc.drawText(EMPTY_MESSAGE, x, y, true);
		} else {
			/*
			 * Molecule
			 */
			createMoleculeImage(display);
			if(imageMolecule != null) {
				/*
				 * Image of molecule.
				 */
				Rectangle bounds = imageMolecule.getBounds();
				int srcX = 0;
				int srcY = 0;
				int srcWidth = bounds.width;
				int srcHeight = bounds.height;
				int destX = 0;
				int destY = 0;
				int destWidth = bounds.width;
				int destHeight = bounds.height;
				//
				if(scaleFactor != 1.0d) {
					destWidth = (int)(bounds.width * scaleFactor);
					destHeight = (int)(bounds.height * scaleFactor);
					Point size = canvasMolecule.get().getSize();
					int corrwidth = (int)(size.x * scaleFactor);
					int corrheight = (int)(size.y * scaleFactor);
					destX = (int)(srcWidth / 2.0d - destWidth / 2.0d - corrwidth / 2.0d);
					destY = (int)(srcHeight / 2.0d - destHeight / 2.0d - corrheight / 2.0d);
				}
				/*
				 * Correction
				 */
				Point destSize = adjustSize(destWidth, destHeight);
				gc.drawImage(imageMolecule, srcX, srcY, srcWidth, srcHeight, destX, destY, destSize.x, destSize.y);
			} else {
				/*
				 * Can't create molecule display.
				 */
				Font font = getFont();
				FontData[] fontData = font.getFontData();
				int width = gc.stringExtent(ERROR_MESSAGE).x;
				int height = fontData[0].getHeight();
				//
				Point size = canvas.getSize();
				int x = (int)(size.x / 2.0d - width / 2.0d);
				int y = (int)(size.y / 2.0d - height / 2.0d);
				gc.drawText(ERROR_MESSAGE, x, y, true);
			}
		}
	}

	private boolean isSourceDataAvailable(ILibraryInformation libraryInformation) {

		if(!libraryInformation.getName().isEmpty()) {
			return true;
		} else if(!libraryInformation.getCasNumber().isEmpty()) {
			return true;
		} else if(!libraryInformation.getSmiles().isEmpty()) {
			return true;
		} else if(!libraryInformation.getInChI().isEmpty()) {
			return true;
		}
		//
		return false;
	}

	private String getMoleculeInformation(ILibraryInformation libraryInformation) {

		StringBuilder builder = new StringBuilder();
		//
		builder.append(getInfo(libraryInformation.getName()));
		builder.append(" | ");
		builder.append(getInfo(libraryInformation.getCasNumber()));
		builder.append(" | ");
		builder.append(getInfo(libraryInformation.getSmiles()));
		//
		return builder.toString();
	}

	private String getInfo(String value) {

		return value.isEmpty() ? "--" : value;
	}

	private IMoleculeImageService getMoleculeImageService() {

		Object object = comboViewerServices.get().getStructuredSelection().getFirstElement();
		if(object instanceof IMoleculeImageService moleculeImageService) {
			return moleculeImageService;
		}
		//
		return null;
	}

	private ImageServiceInput getImageInput() {

		Object object = comboViewerInput.get().getStructuredSelection().getFirstElement();
		if(object instanceof ImageServiceInput imageServiceInput) {
			return imageServiceInput;
		}
		/*
		 * Default
		 */
		return ImageServiceInput.NAME;
	}

	private String getExportName() {

		String input = textInput.get().getText().trim();
		String name = input.replace(":", "");
		name = name.isEmpty() ? "Unkown" : name;
		int length = preferenceStore.getInt(PreferenceSupplier.P_LENGTH_MOLECULE_NAME_EXPORT);
		if(length >= PreferenceSupplier.MIN_LENGTH_NAME_EXPORT && name.length() > length) {
			return name.substring(0, 20);
		} else {
			return name;
		}
	}

	private void updateContent() {

		updateWidgets();
		canvasMolecule.get().redraw();
	}

	private void updateWidgets() {

		setTextMolecule();
		setTextInput();
	}

	private void setTextMolecule() {

		if(libraryInformation != null) {
			textMolecule.get().setText(libraryInformation.getMoleculeStructure());
		} else {
			textMolecule.get().setText("");
		}
	}

	private void setTextInput() {

		if(libraryInformation != null) {
			ImageServiceInput imageInput = getImageInput();
			switch(imageInput) {
				case SMILES:
					String smiles = libraryInformation.getSmiles();
					if(!smiles.isEmpty()) {
						textInput.get().setText(smiles);
					}
					break;
				default:
					String name = libraryInformation.getName();
					if(!name.isEmpty()) {
						textInput.get().setText(name);
					}
					break;
			}
		} else {
			textInput.get().setText("");
		}
	}

	private ILibraryInformation createLibraryInformationByInput() {

		ILibraryInformation libraryInformationByInput = new LibraryInformation();
		//
		ImageServiceInput imageInput = getImageInput();
		String text = textInput.get().getText().trim();
		switch(imageInput) {
			case SMILES:
				libraryInformationByInput.setSmiles(text);
				break;
			default:
				libraryInformationByInput.setName(text);
				break;
		}
		//
		return libraryInformationByInput;
	}

	private void updateComboViewerImageServices() {

		Object[] moleculeImageServices = Activator.getDefault().getMoleculeImageServices();
		if(moleculeImageServices != null) {
			comboViewerServices.get().setInput(moleculeImageServices);
			IMoleculeImageService moleculeImageServiceSelection = getMoleculeImageServiceSelection(moleculeImageServices);
			if(moleculeImageServiceSelection != null) {
				comboViewerServices.get().setSelection(new StructuredSelection(moleculeImageServiceSelection));
			}
		} else {
			comboViewerServices.get().setInput(null);
		}
	}

	private IMoleculeImageService getMoleculeImageServiceSelection(Object[] moleculeImageServices) {

		IMoleculeImageService moleculeImageServiceSelection = null;
		if(moleculeImageServices.length > 0) {
			/*
			 * Get the stored service preference or the
			 * first available offline service.
			 */
			moleculeImageServiceSelection = getMoleculeImageServicesPreference(moleculeImageServices);
			if(moleculeImageServiceSelection == null) {
				List<IMoleculeImageService> moleculeImageServicesOffline = getMoleculeImageServicesOffline(moleculeImageServices);
				if(!moleculeImageServicesOffline.isEmpty()) {
					moleculeImageServiceSelection = moleculeImageServicesOffline.get(0);
				}
			}
		}
		//
		return moleculeImageServiceSelection;
	}

	/*
	 * May return null.
	 */
	private IMoleculeImageService getMoleculeImageServicesPreference(Object[] moleculeImageServices) {

		String imageServiceSelection = PreferenceSupplier.getMoleculeImageServiceId();
		for(int i = 0; i < moleculeImageServices.length; i++) {
			IMoleculeImageService moleculeImageService = (IMoleculeImageService)moleculeImageServices[i];
			if(moleculeImageService.getClass().getName().equals(imageServiceSelection)) {
				return moleculeImageService;
			}
		}
		//
		return null;
	}

	private List<IMoleculeImageService> getMoleculeImageServicesOffline(Object[] moleculeImageServices) {

		List<IMoleculeImageService> moleculeImageServicesOffline = new ArrayList<IMoleculeImageService>();
		for(int i = 0; i < moleculeImageServices.length; i++) {
			IMoleculeImageService moleculeImageService = (IMoleculeImageService)moleculeImageServices[i];
			if(!moleculeImageService.isOnline()) {
				moleculeImageServicesOffline.add(moleculeImageService);
			}
		}
		//
		return moleculeImageServicesOffline;
	}

	private void updateComboViewerInputTypes() {

		comboViewerInput.get().setInput(ImageServiceInput.values());
		comboViewerInput.get().getCombo().select(0);
	}
}