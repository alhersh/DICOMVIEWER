/*
 ** Parser class to extract and relate the information from the selected DICOM
 ** files though processing the Tags and Tags values needed in this work
** The print statments are for code tracing and testing
 */

/**
 *
 * @author Taha Alhersh
 */


import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.util.TagUtils;

public class DicomParser {
    String [][] filesInfo;
    public DicomParser(File [] inFiles){
    filesInfo = new String [inFiles.length][9];
}//constructor

// Generating the files information from reading and extracting the DICOM header
    //information through the getHeaderInfo method
public String [][] getFilesInfo(File [] inFiles){
    //System.out.println("Number of file : " + inFiles.length);
    DicomObject object = null;
    for (int i =0 ; i < inFiles.length; i ++){
        try {
                filesInfo[i][0] = inFiles[i].getPath();
                DicomInputStream dis = new DicomInputStream(new File(inFiles[i].getPath()));
                object = dis.readDicomObject();
                dis.close();
            } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
            }//catch
        getHeaderInfo(object,i);
    }//for
    return filesInfo;
}//getFilesInfo

// Extracting the needed Tags and values
public void getHeaderInfo(DicomObject object, int i) {
   Iterator iter = object.datasetIterator();
   while(iter.hasNext()) {
      DicomElement element = (DicomElement) iter.next();
      int tag = element.tag();
      
      try {
         String tagName = object.nameOf(tag);
         String tagAddr = TagUtils.toString(tag);
         String tagVR = object.vrOf(tag).toString();
        if (tagVR.equals("SQ")) {
            if (element.hasItems()) {
              // System.out.println(tagAddr +" ["+  tagVR +"] "+ tagName);
               getHeaderInfo(element.getDicomObject(),i);
               continue;
            }
         }
         String tagValue = object.getString(tag);
             // [UI] [LO] Patient ID
            if (tagAddr.equals("(0010,0020)")){
             filesInfo[i][1] = tagValue;
             //System.out.println(tagValue);
             continue;
         }
            //[SH] Study ID
         if (tagAddr.equals("(0020,0010)")){
              filesInfo[i][2] = tagValue;
                 //System.out.println(tagValue); 
                 continue;
         }
         //[IS] Series Number
         if (tagAddr.equals("(0020,0011))")){
             filesInfo[i][3] = tagValue;
                // System.out.println(tagValue);         
                 continue;
         }
         //[UI] SOP Class UID
         if (tagAddr.equals("(0008,0016)")){
             filesInfo[i][4] = tagValue;
               //  System.out.println(tagValue);         
                 continue;
         }
         //[CS] Modality
         if (tagAddr.equals("(0008,0060)")){
              filesInfo[i][5] = tagValue;
              //   System.out.println(tagValue);         
                 continue;
         }
         //[PN] Patient’s Name
         if (tagAddr.equals("(0010,0010)")){
               filesInfo[i][6] = tagValue;
             //  System.out.println(tagValue);       
               continue;
         }
         //[DA] Patient’s Birth Date
         if (tagAddr.equals("(0010,0030)")){
                filesInfo[i][7] = tagValue; 
              //   System.out.println(tagValue);      
                 continue;
         }
         //[UI] SOP Instance UID
         if (tagAddr.equals("(0008,0018)")){
                 filesInfo[i][8] = tagValue; 
               //  System.out.println(tagValue);     
                 continue;
         }
             
      } catch (Exception e) {
         e.printStackTrace();
      }
       
   }//while

}//getHeaderInfo

//This method is reserved for testing purposes
public void printFilesInfo(){
    for (int i =0; i < filesInfo.length; i++){
        System.out.println("File path : " + filesInfo[i][0]);
        //int count = StringUtils.count(filesInfo[0][7], '.');
        
    }
    getPatientID();
    
}//printFilesInfo

// Retrieving the patients unique IDs from the extracted data
public String[] getPatientID(){
    String [][] inFilesInfo = filesInfo;
    String [] PTemp = new String [inFilesInfo.length];
    //PTemp = null;
    for (int i = 0 ; i < inFilesInfo.length; i++){
        PTemp[i] = inFilesInfo[i][1];
    }
    int cntr =0;
    String[] unique = new HashSet<String>(Arrays.asList(PTemp)).toArray(new String[PTemp.length]);
    for (int i =0; i < unique.length; i++){
        //System.out.println("Patient ID : "+ unique[i]);
        if (unique[i] == null)
            continue;
        else{
            cntr++;
        }
        
    }//for
    
    String [] PatientsID = new String[cntr];
    for (int i =0; i < unique.length; i++){
        if (unique[i] == null)
            continue;
        else{
            PatientsID[i] = unique[i];
            //getStudy(PatientsID[i],inFilesInfo);
        }
            
    }//for
   // System.out.println("PatientIDs : "+PatientsID.length);
    
    return PatientsID;
}//getPatientID

// Retrieving the related studies to the patient ID
public String [] getStudy(String PatientID){
    String [][] inFilesInfo = filesInfo;
    String [] STemp = new String [inFilesInfo.length];
   // STemp = null;
    for (int i =0; i < inFilesInfo.length; i++){
        if (inFilesInfo[i][1].equals(PatientID)){
            STemp[i] = inFilesInfo[i][2];
        }//if
    }//for
    int cntr =0;
    String[] unique = new HashSet<String>(Arrays.asList(STemp)).toArray(new String[STemp.length]);
    for (int i =0; i < unique.length; i++){
        //System.out.println("Patient ID : "+ unique[i]);
        if (unique[i] == null)
            continue;
        else{
            cntr++;
        }
        
    }//for
    
    String [] StudyID = new String[cntr];
    for (int i =0; i < unique.length; i++){
        if (unique[i] == null)
            continue;
        else{
            StudyID[i] = unique[i];
            //getSeries(PatientID, StudyID[i],inFilesInfo);
        }
            
    }//for
    //System.out.println("StudyID : "+StudyID.length);
    
    return StudyID;    
}//get Study

// Retrieving the related series for the specified patient and specified study
public String [] getSeries(String PatientID, String StudyID){
    String [][] inFilesInfo = filesInfo;
    String [] STemp = new String [inFilesInfo.length];
   // STemp = null;
    for (int i =0; i < inFilesInfo.length; i++){
        if (inFilesInfo[i][1].equals(PatientID) && inFilesInfo[i][2].equals(StudyID)){
            STemp[i] = inFilesInfo[i][4];
        }//if
    }//for
    int cntr =0;
    String[] unique = new HashSet<String>(Arrays.asList(STemp)).toArray(new String[STemp.length]);
    for (int i =0; i < unique.length; i++){
        //System.out.println("Patient ID : "+ unique[i]);
        if (unique[i] == null)
            continue;
        else{
            cntr++;
        }
        
    }//for
    
    String [] SeriesID = new String[cntr];
    for (int i =0; i < unique.length; i++){
        if (unique[i] == null)
            continue;
        else{
            SeriesID[i] = unique[i];
            //getImages(PatientID, StudyID, StudyID, inFilesInfo);
        }
            
    }//for
   // System.out.println("SeriesID : "+SeriesID.length);
    
    return SeriesID;  
}//getSeries

// Retrieving the related images to the patient, study and series
public String [] getImages(String PatientID, String StudyID,String SeriesID){
    String [][] inFilesInfo = filesInfo;
    String [] ITemp = new String [inFilesInfo.length];
   // STemp = null;
    for (int i =0; i < inFilesInfo.length; i++){
        if (inFilesInfo[i][1].equals(PatientID) && inFilesInfo[i][2].equals(StudyID) && inFilesInfo[i][4].equals(SeriesID)){
            ITemp[i] = inFilesInfo[i][0];
        }//if
    }//for
    
    //System.out.println("ImageID : "+ITemp.length);
    
    return ITemp;  
}//getSeries

}//class
