/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart3d;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import org.eclipse.chemclipse.support.text.ValueFormat;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Axes extends Group {

	final private PhongMaterial cornMaterial = new PhongMaterial();
	private NumberFormat format;
	final private PhongMaterial gridaxisMaterial = new PhongMaterial();
	private double lableDistance;
	private double lableDistanceNameAxis;
	final private PhongMaterial planeMaterial = new PhongMaterial();
	private Chart3DSettings settings;
	private double tickLenght;
	private double widthCorn;
	double sX = 1;
	double sY = 1;
	double sZ = 1;
	double lenghtX = 0;
	double lenghtY = 0;
	double lenghtZ = 0;
	final private DoubleProperty labelMaxWidth = new SimpleDoubleProperty();
	//
	private Font labelFont = new Font("Arial", 35);
	private Font labelAxisFont = new Font("Arial", 50);
	//

	public Axes(Chart3DSettings settings) {
		this.settings = settings;
		double absMaximum = Arrays.stream(new double[]{settings.getMinX(), settings.getMaxX(), settings.getMinY(), settings.getMaxY(), settings.getMinZ(), settings.getMaxZ()}).map(d -> Math.abs(d)).max().getAsDouble();
		double numberDigits = Math.floor(Math.log10(absMaximum));
		if(Math.abs(numberDigits) > 4) {
			format = new DecimalFormat("#.##E0", new DecimalFormatSymbols(Locale.US));
		} else {
			format = ValueFormat.getNumberFormatEnglish();
			format.setMaximumFractionDigits(2);
		}
		/*
		 *
		 */
		cornMaterial.setDiffuseColor(Color.BLACK);
		cornMaterial.setSpecularColor(Color.BLACK.brighter());
		/*
		 * set look plane
		 */
		gridaxisMaterial.setDiffuseColor(Color.GRAY);
		gridaxisMaterial.setSpecularColor(Color.GRAY.brighter());
		planeMaterial.setDiffuseColor(Color.LIGHTGRAY);
		planeMaterial.setSpecularColor(Color.LIGHTGRAY.brighter());
		/*
		 * set fix size
		 */
		tickLenght = 40;
		lableDistance = 70;
		lableDistanceNameAxis = 270;
		widthCorn = 4;
		sX = settings.getScaleX();
		sY = settings.getScaleY();
		sZ = settings.getScaleZ();
		lenghtX = settings.getAxisXlenght();
		lenghtY = settings.getAxisYlenght();
		lenghtZ = settings.getAxisZlenght();
	}

	private Node createCorn(double height, Point3D rotation, double rotate, double translationX, double translationY, double translationZ, double lengthening) {

		Box corn = new Box(height + lengthening, widthCorn, widthCorn);
		corn.setRotationAxis(rotation);
		corn.setRotate(rotate);
		corn.setTranslateX(translationX);
		corn.setTranslateY(translationY);
		corn.setTranslateZ(translationZ);
		corn.setMaterial(cornMaterial);
		return corn;
	}

	private Group createCorns() {

		Group group = new Group();
		Node corn = createCorn((lenghtX) * sX, Rotate.X_AXIS, 0, 0, -lenghtY * sY / 2, -lenghtZ * sZ / 2, 500);
		group.getChildren().add(corn);
		corn = createCorn((lenghtX) * sX, Rotate.X_AXIS, 0, 0, lenghtY * sY / 2, -lenghtZ * sZ / 2, 0);
		group.getChildren().add(corn);
		corn = createCorn((lenghtX) * sX, Rotate.X_AXIS, 0, 0, -lenghtY * sY / 2, lenghtZ * sZ / 2, 0);
		group.getChildren().add(corn);
		corn = createCorn((lenghtX) * sX, Rotate.X_AXIS, 0, 0, lenghtY * sY / 2, lenghtZ * sZ / 2, 0);
		group.getChildren().add(corn);
		corn = createCorn((lenghtY) * sY, Rotate.Z_AXIS, 90, -lenghtX * sX / 2, 0, -lenghtZ * sZ / 2, 500);
		group.getChildren().add(corn);
		corn = createCorn((lenghtY) * sY, Rotate.Z_AXIS, 90, lenghtX * sX / 2, 0, -lenghtZ * sZ / 2, 0);
		group.getChildren().add(corn);
		corn = createCorn((lenghtY) * sY, Rotate.Z_AXIS, 90, -lenghtX * sX / 2, 0, lenghtZ * sZ / 2, 0);
		group.getChildren().add(corn);
		corn = createCorn((lenghtY) * sY, Rotate.Z_AXIS, 90, lenghtX * sX / 2, 0, lenghtZ * sZ / 2, 0);
		group.getChildren().add(corn);
		corn = createCorn((lenghtZ) * sZ, Rotate.Y_AXIS, 90, -lenghtX * sX / 2, -lenghtY * sY / 2, 0, 500);
		group.getChildren().add(corn);
		corn = createCorn((lenghtZ) * sZ, Rotate.Y_AXIS, 90, lenghtX * sX / 2, -lenghtY * sY / 2, 0, 0);
		group.getChildren().add(corn);
		corn = createCorn((lenghtZ) * sZ, Rotate.Y_AXIS, 90, -lenghtX * sX / 2, lenghtY * sY / 2, 0, 0);
		group.getChildren().add(corn);
		corn = createCorn((lenghtZ) * sZ, Rotate.Y_AXIS, 90, lenghtX * sX / 2, lenghtY * sY / 2, 0, 0);
		group.getChildren().add(corn);
		return group;
	}

	private Node createGridLine(Point3D origin, Point3D target) {

		Point3D yAxis = new Point3D(0, 1, 0);
		Point3D diff = target.subtract(origin);
		double height = diff.magnitude();
		Point3D mid = target.midpoint(origin);
		Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());
		Point3D axisOfRotation = diff.crossProduct(yAxis);
		double angle = Math.acos(diff.normalize().dotProduct(yAxis));
		Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);
		Box line = new Box(2, height, 2);
		line.setMaterial(gridaxisMaterial);
		line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
		return line;
	}

	private Group createPlane(double lenghtX, double lenghtY, double lineSpacingH, double lineSpacingV) {

		Group group = new Group();
		double lineSpacingScaled = lineSpacingH;
		double minX = -lenghtX / 2;
		double maxX = lenghtX / 2;
		for(int i = (int)(minX / lineSpacingScaled) + 1; i < (int)(maxX / lineSpacingScaled); i++) {
			group.getChildren().add(createGridLine(new Point3D(i * lineSpacingScaled, -lenghtY / 2, 0), new Point3D(i * lineSpacingScaled, lenghtY / 2, 0)));
		}
		lineSpacingScaled = lineSpacingV;
		double minY = -lenghtY / 2;
		double maxY = lenghtY / 2;
		for(int i = (int)(minY / lineSpacingScaled) + 1; i < (int)(maxY / lineSpacingScaled); i++) {
			group.getChildren().add(createGridLine(new Point3D(-lenghtX / 2, i * lineSpacingScaled, 0), new Point3D(lenghtX / 2, i * lineSpacingScaled, 0)));
		}
		return group;
	}

	private Group createXYPlane() {

		Group group = createPlane(lenghtX * sX, lenghtY * sY, settings.getLineSpacingX() * sX, settings.getLineSpacingY() * sY);
		group.setTranslateZ(-lenghtZ * sZ / 2);
		return group;
	}

	private Group createXZPlane() {

		Group group = createPlane(lenghtX * sX, lenghtZ * sZ, settings.getLineSpacingX() * sX, settings.getLineSpacingZ() * sZ);
		group.setTranslateY(-(lenghtY) * sY / 2);
		group.setRotationAxis(Rotate.X_AXIS);
		group.setRotate(90);
		return group;
	}

	private Group createYZPlane() {

		Group group = createPlane(lenghtZ * sZ, lenghtY * sY, settings.getLineSpacingZ() * sZ, settings.getLineSpacingY() * sY);
		group.setTranslateX(-(lenghtX) * sX / 2);
		group.setRotationAxis(Rotate.Y_AXIS);
		group.setRotate(90);
		return group;
	}

	private Group createXLabels() {

		Group groupTick = new Group();
		Group groupLabel = new Group();
		String nameAxis = settings.getLabelAxisX();
		Label name = new Label(nameAxis);
		name.setTranslateZ(lableDistanceNameAxis);
		name.setRotationAxis(Rotate.X_AXIS);
		name.setRotate(90);
		setLabelStyleAxis(name);
		groupTick.getChildren().add(name);
		double lineSpacingScaled = settings.getLineSpacingX() * sX;
		double minX = -lenghtX * sX / 2;
		double maxX = lenghtX * sX / 2;
		double minY = -lenghtY * sY / 2;
		double maxZ = lenghtZ * sZ / 2;
		boolean firstLabel = true;
		for(int i = (int)(minX / lineSpacingScaled) + 1; i < (int)(maxX / lineSpacingScaled); i++) {
			Label label = new Label(format.format(i * settings.getLineSpacingX() - settings.getShiftX()));
			if(firstLabel) {
				label.heightProperty().addListener((obs, oldVal, newVal) -> {
					double labelHight = newVal.doubleValue();
					groupLabel.setTranslateX((maxX - minX) / 2 + minX - labelHight / 2);
				});
				firstLabel = false;
			}
			label.widthProperty().addListener((obs, oldVal, newVal) -> {
				double value = newVal.doubleValue();
				if(value > labelMaxWidth.doubleValue()) {
					labelMaxWidth.set(value);
				}
			});
			setLabelStyle(label);
			Rotate xRotate = new Rotate(90, 0, 0, 0, Rotate.X_AXIS);
			Rotate yRotate = new Rotate(90, 0, 0, 0, Rotate.Y_AXIS);
			label.getTransforms().addAll(yRotate, xRotate);
			label.setTranslateX(i * lineSpacingScaled);
			label.setTranslateZ(2 * lableDistance);
			Node tick = createGridLine(new Point3D(i * lineSpacingScaled, 0, -tickLenght / 2), new Point3D(i * lineSpacingScaled, 0, tickLenght / 2));
			groupTick.getChildren().add(tick);
			groupLabel.getChildren().add(label);
		}
		groupTick.setTranslateY(minY);
		groupTick.setTranslateZ(maxZ);
		groupTick.setTranslateX((maxX - minX) / 2 + minX);
		groupLabel.setTranslateY(minY);
		groupLabel.setTranslateZ(maxZ);
		Group group = new Group();
		group.getChildren().addAll(groupLabel, groupTick);
		return group;
	}

	private Group createYLabels() {

		Group groupLabel = new Group();
		Group groupTick = new Group();
		String axisName = settings.getLabelAxisY();
		Label name = new Label(axisName);
		Rotate zRotateAxis = new Rotate(-90, 0, 0, 0, Rotate.Z_AXIS);
		Rotate yRotateAxis = new Rotate(-90, 0, 0, 0, Rotate.Y_AXIS);
		name.getTransforms().addAll(yRotateAxis, zRotateAxis);
		name.setTranslateZ(lableDistanceNameAxis);
		setLabelStyleAxis(name);
		groupTick.getChildren().add(name);
		double minY = -lenghtY * sY / 2;
		double maxY = lenghtY * sY / 2;
		double maxZ = lenghtZ * sZ / 2;
		double minX = -lenghtX * sX / 2;
		double lineSpacingScaled = settings.getLineSpacingY() * sY;
		boolean firstLabel = true;
		for(int i = (int)(minY / lineSpacingScaled) + 1; i < (int)(maxY / lineSpacingScaled); i++) {
			Label label = new Label(format.format(i * settings.getLineSpacingY() - settings.getShiftY()));
			if(firstLabel) {
				label.heightProperty().addListener((obs, oldVal, newVal) -> {
					double labelHight = newVal.doubleValue();
					groupLabel.setTranslateY((maxY - minY) / 2 + minY + labelHight / 2);
				});
				firstLabel = false;
			}
			label.widthProperty().addListener((obs, oldVal, newVal) -> {
				double value = newVal.doubleValue();
				if(value > labelMaxWidth.doubleValue()) {
					labelMaxWidth.set(value);
				}
			});
			setLabelStyle(label);
			Rotate xRotate = new Rotate(180, 0, 0, 0, Rotate.X_AXIS);
			Rotate yRotate = new Rotate(90, 0, 0, 0, Rotate.Y_AXIS);
			label.getTransforms().addAll(yRotate, xRotate);
			label.setTranslateY(i * lineSpacingScaled);
			label.setTranslateZ(2 * lableDistance);
			Node tick = createGridLine(new Point3D(0, i * lineSpacingScaled, -tickLenght / 2), new Point3D(0, i * lineSpacingScaled, tickLenght / 2));
			groupTick.getChildren().add(tick);
			groupLabel.getChildren().add(label);
		}
		groupLabel.setTranslateX(minX);
		groupLabel.setTranslateZ(maxZ);
		groupTick.setTranslateX(minX);
		groupTick.setTranslateZ(maxZ);
		groupTick.setTranslateY((maxY - minY) / 2 + minY);
		Group group = new Group();
		group.getChildren().addAll(groupLabel, groupTick);
		return group;
	}

	private Group createZLanels() {

		Group groupTick = new Group();
		Group groupLabel = new Group();
		String nameAxis = settings.getLabelAxisZ();
		Label name = new Label(nameAxis);
		Rotate yRotateAxis = new Rotate(90, 0, 0, 0, Rotate.Y_AXIS);
		Rotate zRotateAxis = new Rotate(-90, 0, 0, 0, Rotate.Z_AXIS);
		name.getTransforms().addAll(zRotateAxis, yRotateAxis);
		name.setTranslateX(lableDistanceNameAxis);
		setLabelStyleAxis(name);
		groupTick.getChildren().add(name);
		double minZ = -lenghtZ * sZ / 2;
		double maxZ = lenghtZ * sZ / 2;
		double maxX = lenghtX * sX / 2;
		double minY = -lenghtY * sY / 2;
		double lineSpacingScaled = settings.getLineSpacingZ() * sZ;
		for(int i = (int)(minZ / lineSpacingScaled) + 1; i < (int)(maxZ / lineSpacingScaled); i++) {
			Label label = new Label(format.format(i * settings.getLineSpacingZ() - settings.getShiftZ()));
			label.widthProperty().addListener((obs, oldVal, newVal) -> {
				double value = newVal.doubleValue();
				if(value > labelMaxWidth.doubleValue()) {
					labelMaxWidth.set(value);
				}
			});
			setLabelStyle(label);
			label.setRotationAxis(Rotate.X_AXIS);
			label.setRotate(90);
			label.setTranslateZ(i * lineSpacingScaled);
			label.setTranslateX(2 * lableDistance);
			Node tick = createGridLine(new Point3D(-tickLenght / 2, 0, i * lineSpacingScaled), new Point3D(tickLenght / 2, 0, i * lineSpacingScaled));
			groupLabel.getChildren().add(tick);
			groupLabel.getChildren().add(label);
		}
		groupTick.setTranslateZ((maxZ - minZ) / 2 + minZ);
		groupTick.setTranslateX(maxX);
		groupTick.setTranslateY(minY);
		groupLabel.setTranslateZ((maxZ - minZ) / 2 + minZ);
		groupLabel.setTranslateX(maxX);
		groupLabel.setTranslateY(minY);
		Group group = new Group();
		group.getChildren().addAll(groupLabel, groupTick);
		return group;
	}

	public void buildAxes() {

		getChildren().setAll(createXYPlane(), createYZPlane(), createXZPlane(), createCorns(), createXLabels(), createYLabels(), createZLanels());
	}

	private void setLabelStyle(Label label) {

		label.setAlignment(Pos.CENTER);
		label.setFont(labelFont);
	}

	private void setLabelStyleAxis(Label label) {

		label.setFont(labelAxisFont);
	}
}
