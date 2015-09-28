/*
 * Copyright (C) 2002 by Environmental Systems Research Institute Inc.
 * All Rights Reserved.
 *
 * $Workfile: $  $Revision: $
 */

import java.io.File;
import java.io.FileFilter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;

/**
 * <p>Title: </p>
 * RenameMO
 * <p>Description: </p>
 * RenameMo can be used as the first step of modifying Java source code and properties files
 * that currently depend upon the MapObjects 1.0 api, to work with the MapObjects 2.0 api.
 * RenameMo reads a file directory containing input source and properties files, and copies
 * all files found in the input directory to a new file directory with changes that are defined
 * within the renameText method - basically, all references to "com.esri.mo." are converted to
 * "com.esri.mo2.".<p>
 * A simple text substitution is performed on all input files
 * whose file type is defined within the method isSource - all other files are copied.
 * RenameMo descends the input directory structure and duplicates that directory
 * structure within the output directory.  It is assumed that changes to the directory
 * structure (if considered necessary) will be performed outside of RenameMo.  Most of the time
 * this won't be necessary.<p>
 * To run this application, first compile it, then run it with two command line arguments
 * specifying the input and output directories.  The input directory must exist, and (for
 * simplicity) the output directory must <i>not</i> exist - it will be created by RenameMo.
 */

public class RenameMo {
    private static final boolean VERBOSE = true;

    /**
     * main program, requires 2 command line arguments
     */
    public static void main(String[] args) {
        int value = 0;
        try {
            if (args.length == 2)
                renamePackages(args[0],args[1]);
            else
                usage();
        } catch (Exception exception) {
            System.err.println(exception.toString());
            value = 1;
        }
        System.exit(value);
    }

    private static final void usage() throws Exception {
        throw new java.lang.RuntimeException("Usage: java RenameMo {input directory} {output directory}");
    }
    private static final void inputNoExists(String inputName) throws Exception {
        throw new Exception(inputName+": does not exist");
    }
    private static final void inputNotDirectory(String inputName) throws Exception {
        throw new Exception(inputName+": is not a file system directory");
    }
    private static final void outputExists(String outputName) throws Exception {
        throw new Exception(outputName+": already exists");
    }
    private static final void outputNoCreate(String outputName) throws Exception {
        throw new Exception(outputName+": cannot create");
    }

    private static void renamePackages(String inputName, String outputName) throws Exception {
        File input = new File(inputName);
        File output = new File(outputName);
        if (false == input.exists())
            inputNoExists(inputName);
        else if (false == input.isDirectory())
            inputNotDirectory(inputName);
        else if (output.exists())
            outputExists(outputName);
        else if (false == output.mkdirs())
            outputNoCreate(outputName);
        else
            renameDirectoryFiles(input,output);
    }

    private static void renameDirectoryFiles(File input, File output) throws Exception {
        File[] ifiles = input.listFiles();
        for (int i = 0; i < ifiles.length; i++) {
            File ifile = ifiles[i];
            if (ifile.isDirectory())
                renameNewDirectory(ifile,output);
            else if (isSource(ifile))
                renameNewFile(ifile, output);
            else
                copyFile(ifile, output);
        }
    }

    /*
    * this method defines the files that will be processed
    */
    private static final boolean isSource(File path) {
        return path.getName().endsWith(".java") ||
                   path.getName().endsWith(".jj") ||
                   path.getName().endsWith(".h") ||
                   path.getName().endsWith(".c") ||
                   path.getName().endsWith(".xml") ||
                   path.getName().endsWith(".txt") ||   // javadoc package list
                   path.getName().endsWith(".properties");
    }

    private static final void renameNewDirectory(File idir, File output) throws Exception {
        String dirname = idir.getName();
        File odir = new File(output,dirname);
        if (VERBOSE) {
            System.out.println(idir.getCanonicalPath()+" -->"+odir.getCanonicalPath());
        }
        if (false == odir.mkdir())
            outputNoCreate(odir.getCanonicalPath());
        else
            renameDirectoryFiles(idir,odir);
    }

    private static final void copyFile(File ifile, File odir) throws Exception {
        File ofile = makeOutputFile(ifile,odir);
        copyFileContents(ifile,ofile);
    }

    private static void copyFileContents(File ifile, File ofile) throws Exception {
        FileInputStream istream = new FileInputStream(ifile);
        FileOutputStream ostream = new FileOutputStream(ofile);
        BufferedInputStream bistream = new BufferedInputStream(istream);
        BufferedOutputStream bostream = new BufferedOutputStream(ostream);
        int ch = bistream.read();
        while (ch >= 0) {
            bostream.write(ch);
            ch = bistream.read();
        }
        bostream.close();
        bistream.close();
    }

    private static final void renameNewFile(File ifile, File odir) throws Exception {
        File ofile = makeOutputFile(ifile,odir);
        if (false == ofile.createNewFile())
            outputNoCreate(ofile.getCanonicalPath());
        else
            renameFileContents(ifile,ofile);
    }

    private static final File makeOutputFile(File ifile, File odir) {
        return new File(odir,ifile.getName());
    }

    private static void renameFileContents(File ifile, File ofile) throws Exception {
        FileReader reader = new FileReader(ifile);
        FileWriter writer = new FileWriter(ofile);
        BufferedReader breader = new BufferedReader(reader);
        BufferedWriter bwriter = new BufferedWriter(writer);
        String record = breader.readLine();
        while (record != null) {
            record = renameText(record);
            bwriter.write(record);
            bwriter.newLine();
            record = breader.readLine();
        }
        bwriter.close();
        breader.close();
    }

    /**
     * this method defines (and performs) the string replacements
     */
    private static final String renameText(String record) throws Exception {
        record = substitute(record,"com.esri.mo.","com.esri.mo2.");    // java code
        record = substitute(record,"com/esri/mo/","com/esri/mo2/");    // resource references
        record = substitute(record,"com_esri_mo_","com_esri_mo2_");    // C code  (JNI)
        return record;
    }

    private static String substitute(String record, String text, String replacement) throws Exception {
        int length = buffer.length();
        if (length > 0) buffer.delete(0,length);
        int textlen = text.length();
        int start = 0;
        int location = record.indexOf(text,start);
        while (location >= 0) {
            if (location > start) {
                buffer.append(record.substring(start,location));
            }
            buffer.append(replacement);
            start = location + textlen;
            location = record.indexOf(text,start);
        }
        if (start < record.length()) {
            buffer.append(record.substring(start));
        }
        return buffer.toString();
    }
    private static StringBuffer buffer = new StringBuffer();
}
