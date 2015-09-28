/**********************************************************
*	parameters.js
*
**********************************************************/

// the following overview map parameters have values if they are different from the main map
// otherwise the values are empty strings
var ovServerName = "";
var ovServiceName = "";

// if extent coords are all set to zero,
// coords will be dynamically set by mapservice envelope
/*
// starting extent
var minX = -126.0;
var minY = 19.0;
var maxX = -60.0;
var maxY = 50.0;

// fullextent coords
var fullMinX = -180.0;
var fullMinY = -90.0;
var fullMaxX = 180.0;
var fullMaxY = 90.0;
*/

// number of data samples retrieved for query form
var numberDataSamples = 50;

// starting extent, could be modified by JavaScripts
var minX = 0;
var minY = 0;
var maxX = 0;
var maxY = 0;

// last extent
var updateLastExtent = true;
var lastMinX = 0;
var lastMinY = 0;
var lastMaxX = 0;
var lastMaxY = 0;

// current extent, only modified by returning values from server
var curMinX = 0;
var curMinY = 0;
var curMaxX = 0;
var curMaxY = 0;

// fullextent coords
var fullExtentSet = false;
var fullMinX = 0;
var fullMinY = 0;
var fullMaxX = 0;
var fullMaxY = 0;

var scalebar1Units = "MILES";
var scalebar2Units = "KILOMETERS";

var currentMapImageURL = "";
// area around click used in identify
	// helps in locating points and lines
var pixelTolerance = 10;
var tolerance = 0.0001;

var currentLayoutImageURL = "";

//alert("parameters.js loaded");
var setMapUnits=false;
	// ScaleBarUnits=KILOMETERS,METERS,MILES,FEET
var ScaleBarBackground = "TRUE";
var ScaleBarBackColor = "0,0,0";
var ScaleBarFontColor = "255,255,255";
var ScaleBarColor = "192,192,192";
var ScaleBarFont = "Arial";
var ScaleBarStyle = "Bold";
var ScaleBarRound = "1";
var ScaleBarSize = "14";
var ScaleBarWidth = "7";
var ScaleBarPrecision = 2;
var numDecimals = ScaleBarPrecision;

// variables for setting component colors
//panning factor for arrow buttons
var panFactor = 50/100;
//zoom factors for v.3
var zoomFactor = 2

var modeBlurb = modeList[0];

var mapBackColor = '225,225,225';
var ovBoxColor = '#ff0000';
var ovBoxSize = 3;
var zoomBoxColor = '#ff0000';

var hasOVMap = false;
var hasTOC = true;
var useModeFrame = true;

var usePan=true;
var usePanNorth=true;
var usePanWest=true;
var usePanEast=true;
var usePanSouth=true;
var useZoomIn=true;
var useZoomOut=true;
var useFullExtent=true;
var useZoomActive=true;
var useZoomLast=true;
var useIdentify=true;
var useMeasure=false;
var useSetUnits=true;
var useSelect=true;
var useQuery=true;
var useFind=true;
var useGeocode=false;
var useStoredQuery=false;
var useClearSelect=true;
var usePrint=true;
var useGeoNetwork=false;
var useBuffer=true;
var useExtract=false;
var usePrintLayout=true

var MapUnits = "DECIMAL_DEGREES";
var ScaleBarUnits = "MILES";

var MOJServletController = ""; //"/JspServletViewer/servlet/MOJServletController";
var ContextPath = "";

var canAddService = true;
var ServiceName = "xxxx";

var ActiveLayerIndex = 0;

var BufferDistance = 0;
var BufferUnit = "";
var TargetLayer = -1; // means no target layer has been set

var HasSelection = false;
var SelectMinX = 0;
var SelectMinY = 0;
var SelectMaxX = 0;
var SelectMaxY = 0;
var QueryString = "";
var FindString = "";
var SelectionQueryFindCmd = "";
// color for Main Map zoombox in html hex RGB format
//var zoomBoxColor = "#ff0000";

// variables for using individual components
var useTextFrame=true;
// use external window for dialogs
var useExternalWindow=false;

// colors for tables 
var textFrameBackColor="Silver";
var tableBackColor="White";
var textFrameTextColor="Black";
var textFrameLinkColor="Blue";
var textFrameFormColor="Gray";


var LayerName = new Array();
var LayerID = new Array();
var LayerVisible = new Array();
var LayerType = new Array();
var LayerIsFeature = new Array();
var LayerExtent = new Array();
var LayerMinScale = new Array();
var LayerMaxScale = new Array();
var LayerRenderString = new Array();
var LayerShapeField = new Array();
var LayerIDField = new Array();
var LayerFieldList = new Array();
var LayerFieldTypeList = new Array();
var LayerFieldSizeList = new Array();
var LayerFieldPrecisionList = new Array();
var LayerFields = new Array();
var LayerFieldType = new Array();
var LayerFieldCount=0;
var ActiveLayer="";
var ActiveLayerType="";
var layerCount = 0;
var layerLeft = 0;
var layerRight = 0;
var layerTop = 0;
var layerBottom = 0;
var fieldIndex = 0;
var FeatureLayerCount = 0;

// can features be selected if invisible or not within layer scale threshholds?
var canSelectInvisible=false;
