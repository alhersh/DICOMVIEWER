# DICOMVIEWER
## A simple Dicom viewer for brain images
### DEVELOPMENT TOOLS AND ENVIRONMENTS
1. The following development tools were selected
2. NetBeans IDE 8.0 (Build 201403101706)
3. Java(TM) SE Runtime Environment 1.8.0-b132
4. Clinical Image and Object Management dcm4che 2 , dcm4che 3.3.1
5. Product Version: NetBeans IDE 7.4 (Build 201310111528)
6. Updates: Updates available to version NetBeans 7.4 Patch 3
7. Java: 1.7.0_51; Java HotSpot(TM) Client VM 24.51-b03
8. Runtime: Java(TM) SE Runtime Environment 1.7.0_51-b13
9. System: Windows 8 version 6.2 running on x86; Cp1256; en_US (nb)

### How to use it:

File chooser has been used to load multi-selected DICOM files.

A parser has been developed in order to extract the image details form the needed Tags and Tag values.

The tree was built from the information extracted from the selected files as follow: for each patient, the related study(ies) was / were extracted and then the related series for each study related to the selected patient were also selected and the images related to the specified series-study-patient were added to the tree.

Dcm4che library was used to read DICOM files

A normal drawing for the patient details (name and date of birth) and the modality where drawn inside a normal rectangle. This assignment part has to be developed to use an overlay.

### ADD CONTRAST AND BRIGHTNESS CHANGE FEATURE

Using the ChangeContrast class, the mouse dragging can have different purposes; increase / decrease the brightness / contrast. In this assignment, only one function has been handled while mouse dragging. A complementary work can be adding multi-functionality for the mouse dragging depending on the direction of dragging.

### TESTING

The program has been tested on the specified dataset, what has been noticed that the dataset belongs to one patient, one study and one series. Up to my knowledge, building the tree nodes is dynamic enough to handle any number of patients, studies, series and images.
