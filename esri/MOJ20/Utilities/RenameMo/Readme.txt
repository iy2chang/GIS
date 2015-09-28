 
 * Copyright (C) 2003 by Environmental Systems Research Institute Inc.
 * All Rights Reserved.

 * RenameMo can be used as the first step of modifying Java source code and properties files
 * that currently depend upon the MapObjects 1.0 api, to work with the MapObjects 2.0 api.
 * RenameMo reads a file directory containing input source and properties files, and copies
 * all files found in the input directory to a new file directory with changes that are defined
 * within the renameText method - basically, all references to "com.esri.mo." are converted to
 * "com.esri.mo2.".

 * A simple text substitution is performed on all input files
 * whose file type is defined within the method isSource - all other files are copied.
 * RenameMo descends the input directory structure and duplicates that directory
 * structure within the output directory.  It is assumed that changes to the directory
 * structure (if considered necessary) will be performed outside of RenameMo.  Most of the time
 * this won't be necessary.

 * To run this application, first compile it, then run it with two command line arguments
 * specifying the input and output directories.  The input directory must exist, and (for
 * simplicity) the output directory must <i>not</i> exist - it will be created by RenameMo.