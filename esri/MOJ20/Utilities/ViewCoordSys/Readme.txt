Copyright (C) 2003 by Environmental Systems Research Institute Inc.
All Rights Reserved.

The ViewCoordSys.java utility will extract coordinate system, datum, and projection information from the GeodeticDB.txt file in the esri_mo20res.jar.  Data is retrieved in a table structure format from each of the following categories:

Referenced Datums
Prime Meridians
Parameter Schemas
Projection Schemas
Datum Transformation Schemas
Ellipsoids
Geographic Coordinate Systems
Linear Units
Angular Units
Coordinate System Sets

Each category will have specific attribute columns displayed.  The EPSG IDs are also included for the Geographic Coordinate Systems and Referenced Datums.  

To run the application, first compile it, then run it, making sure all the MapObjects-Java jars installed in the MOJ20/lib directory are referenced in your classpath.  