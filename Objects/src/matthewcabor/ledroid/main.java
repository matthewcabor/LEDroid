package matthewcabor.ledroid;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "matthewcabor.ledroid", "matthewcabor.ledroid.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "matthewcabor.ledroid", "matthewcabor.ledroid.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "matthewcabor.ledroid.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static int _brightness = 0;
public static String _ipaddress = "";
public anywheresoftware.b4a.objects.ButtonWrapper _editipbutton = null;
public anywheresoftware.b4a.objects.EditTextWrapper _ipaddresstext = null;
public anywheresoftware.b4a.objects.LabelWrapper _ipaddresslabel = null;
public anywheresoftware.b4a.objects.ButtonWrapper _powerbutton = null;
public anywheresoftware.b4a.objects.ButtonWrapper _increasebrightnessbutton = null;
public anywheresoftware.b4a.objects.ButtonWrapper _decreasebrightnessbutton = null;
public anywheresoftware.b4a.objects.ButtonWrapper _setredbutton = null;
public anywheresoftware.b4a.objects.ButtonWrapper _setgreenbutton = null;
public anywheresoftware.b4a.objects.ButtonWrapper _setbluebutton = null;
public anywheresoftware.b4a.objects.ButtonWrapper _setcyanbutton = null;
public anywheresoftware.b4a.objects.ButtonWrapper _setmagentabutton = null;
public anywheresoftware.b4a.objects.ButtonWrapper _setyellowbutton = null;
public anywheresoftware.b4a.objects.ButtonWrapper _setwhitebutton = null;
public anywheresoftware.b4a.objects.ButtonWrapper _setorangebutton = null;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
public matthewcabor.ledroid.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 32;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 33;BA.debugLine="Activity.LoadLayout(\"Layout\")";
mostCurrent._activity.LoadLayout("Layout",mostCurrent.activityBA);
 //BA.debugLineNum = 34;BA.debugLine="Activity.Title = \"LEDroid Remote for WLED\"";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("LEDroid Remote for WLED"));
 //BA.debugLineNum = 35;BA.debugLine="IPAddressText.Text = IPAddress";
mostCurrent._ipaddresstext.setText(BA.ObjectToCharSequence(_ipaddress));
 //BA.debugLineNum = 36;BA.debugLine="IPAddressLabel.Text = IPAddress";
mostCurrent._ipaddresslabel.setText(BA.ObjectToCharSequence(_ipaddress));
 //BA.debugLineNum = 37;BA.debugLine="Log(\"Width: \" & Activity.Width & \", Height: \" & A";
anywheresoftware.b4a.keywords.Common.LogImpl("7131077","Width: "+BA.NumberToString(mostCurrent._activity.getWidth())+", Height: "+BA.NumberToString(mostCurrent._activity.getHeight()),0);
 //BA.debugLineNum = 38;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 42;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 44;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 39;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static String  _decreasebrightnessbutton_click() throws Exception{
 //BA.debugLineNum = 74;BA.debugLine="Sub DecreaseBrightnessButton_Click";
 //BA.debugLineNum = 75;BA.debugLine="If brightness < 20 Then";
if (_brightness<20) { 
 //BA.debugLineNum = 76;BA.debugLine="brightness = Max(0, brightness - 2)";
_brightness = (int) (anywheresoftware.b4a.keywords.Common.Max(0,_brightness-2));
 }else {
 //BA.debugLineNum = 78;BA.debugLine="brightness = Max(0, brightness - 15)";
_brightness = (int) (anywheresoftware.b4a.keywords.Common.Max(0,_brightness-15));
 };
 //BA.debugLineNum = 80;BA.debugLine="SendGetRequest(\"http://\" & IPAddress & \"/win&T=1&";
_sendgetrequest("http://"+_ipaddress+"/win&T=1&A="+BA.NumberToString(_brightness));
 //BA.debugLineNum = 81;BA.debugLine="End Sub";
return "";
}
public static String  _editipbutton_click() throws Exception{
 //BA.debugLineNum = 46;BA.debugLine="Sub EditIPButton_Click";
 //BA.debugLineNum = 47;BA.debugLine="IPAddressLabel.Visible = False";
mostCurrent._ipaddresslabel.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 48;BA.debugLine="IPAddressText.Visible = True";
mostCurrent._ipaddresstext.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 49;BA.debugLine="IPAddressText.Enabled = True";
mostCurrent._ipaddresstext.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 17;BA.debugLine="Dim EditIPButton As Button";
mostCurrent._editipbutton = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim IPAddressText As EditText";
mostCurrent._ipaddresstext = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim IPAddressLabel As Label";
mostCurrent._ipaddresslabel = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim PowerButton As Button";
mostCurrent._powerbutton = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim IncreaseBrightnessButton As Button";
mostCurrent._increasebrightnessbutton = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim DecreaseBrightnessButton As Button";
mostCurrent._decreasebrightnessbutton = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim SetRedButton As Button";
mostCurrent._setredbutton = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim SetGreenButton As Button";
mostCurrent._setgreenbutton = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim SetBlueButton As Button";
mostCurrent._setbluebutton = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim SetCyanButton As Button";
mostCurrent._setcyanbutton = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim SetMagentaButton As Button";
mostCurrent._setmagentabutton = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim SetYellowButton As Button";
mostCurrent._setyellowbutton = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim SetWhiteButton As Button";
mostCurrent._setwhitebutton = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim SetOrangeButton As Button";
mostCurrent._setorangebutton = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="End Sub";
return "";
}
public static String  _increasebrightnessbutton_click() throws Exception{
 //BA.debugLineNum = 66;BA.debugLine="Sub IncreaseBrightnessButton_Click";
 //BA.debugLineNum = 67;BA.debugLine="If brightness < 20 Then";
if (_brightness<20) { 
 //BA.debugLineNum = 68;BA.debugLine="brightness = Min(255, brightness + 2)";
_brightness = (int) (anywheresoftware.b4a.keywords.Common.Min(255,_brightness+2));
 }else {
 //BA.debugLineNum = 70;BA.debugLine="brightness = Min(255, brightness + 15)";
_brightness = (int) (anywheresoftware.b4a.keywords.Common.Min(255,_brightness+15));
 };
 //BA.debugLineNum = 72;BA.debugLine="SendGetRequest(\"http://\" & IPAddress & \"/win&T=1&";
_sendgetrequest("http://"+_ipaddress+"/win&T=1&A="+BA.NumberToString(_brightness));
 //BA.debugLineNum = 73;BA.debugLine="End Sub";
return "";
}
public static String  _ipaddresstext_enterpressed() throws Exception{
 //BA.debugLineNum = 55;BA.debugLine="Sub IPAddressText_EnterPressed";
 //BA.debugLineNum = 56;BA.debugLine="IPAddressText.Enabled = False";
mostCurrent._ipaddresstext.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 57;BA.debugLine="IPAddressText.Visible = False";
mostCurrent._ipaddresstext.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 58;BA.debugLine="IPAddressLabel.Visible = True";
mostCurrent._ipaddresslabel.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 59;BA.debugLine="IPAddressLabel.Text = IPAddress";
mostCurrent._ipaddresslabel.setText(BA.ObjectToCharSequence(_ipaddress));
 //BA.debugLineNum = 60;BA.debugLine="End Sub";
return "";
}
public static String  _ipaddresstext_textchanged(String _oldtext,String _newtext) throws Exception{
 //BA.debugLineNum = 52;BA.debugLine="Sub IPAddressText_TextChanged (OldText As String,";
 //BA.debugLineNum = 53;BA.debugLine="IPAddress = NewText";
_ipaddress = _newtext;
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
 //BA.debugLineNum = 113;BA.debugLine="Sub JobDone (job As HttpJob)";
 //BA.debugLineNum = 114;BA.debugLine="If job.Success Then";
if (_job._success) { 
 //BA.debugLineNum = 115;BA.debugLine="Log(\"Response from server: \" & job.GetString)";
anywheresoftware.b4a.keywords.Common.LogImpl("71310722","Response from server: "+_job._getstring(),0);
 }else {
 //BA.debugLineNum = 117;BA.debugLine="Log(\"Error: \" & job.ErrorMessage)";
anywheresoftware.b4a.keywords.Common.LogImpl("71310724","Error: "+_job._errormessage,0);
 };
 //BA.debugLineNum = 119;BA.debugLine="job.Release";
_job._release();
 //BA.debugLineNum = 120;BA.debugLine="End Sub";
return "";
}
public static String  _powerbutton_click() throws Exception{
 //BA.debugLineNum = 62;BA.debugLine="Sub PowerButton_Click";
 //BA.debugLineNum = 63;BA.debugLine="SendGetRequest(\"http://\" & IPAddress & \"/win&T=2\"";
_sendgetrequest("http://"+_ipaddress+"/win&T=2");
 //BA.debugLineNum = 64;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        anywheresoftware.b4a.samples.httputils2.httputils2service._process_globals();
main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 13;BA.debugLine="Dim brightness As Int = 20";
_brightness = (int) (20);
 //BA.debugLineNum = 14;BA.debugLine="Dim IPAddress As String = \"192.168.1.5\"";
_ipaddress = "192.168.1.5";
 //BA.debugLineNum = 15;BA.debugLine="End Sub";
return "";
}
public static String  _sendgetrequest(String _url) throws Exception{
anywheresoftware.b4a.samples.httputils2.httpjob _job = null;
 //BA.debugLineNum = 108;BA.debugLine="Sub SendGetRequest(url As String)";
 //BA.debugLineNum = 109;BA.debugLine="Dim job As HttpJob";
_job = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 110;BA.debugLine="job.Initialize(\"job1\", Me)";
_job._initialize(processBA,"job1",main.getObject());
 //BA.debugLineNum = 111;BA.debugLine="job.Download(url)";
_job._download(_url);
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
public static String  _setbluebutton_click() throws Exception{
 //BA.debugLineNum = 89;BA.debugLine="Sub SetBlueButton_Click";
 //BA.debugLineNum = 90;BA.debugLine="SendGetRequest(\"http://\" & IPAddress & \"/win&T=1&";
_sendgetrequest("http://"+_ipaddress+"/win&T=1&R=0&G=0&B=255");
 //BA.debugLineNum = 91;BA.debugLine="End Sub";
return "";
}
public static String  _setcyanbutton_click() throws Exception{
 //BA.debugLineNum = 92;BA.debugLine="Sub SetCyanButton_Click";
 //BA.debugLineNum = 93;BA.debugLine="SendGetRequest(\"http://\" & IPAddress & \"/win&T=1&";
_sendgetrequest("http://"+_ipaddress+"/win&T=1&R=0&G=255&B=255");
 //BA.debugLineNum = 94;BA.debugLine="End Sub";
return "";
}
public static String  _setgreenbutton_click() throws Exception{
 //BA.debugLineNum = 86;BA.debugLine="Sub SetGreenButton_Click";
 //BA.debugLineNum = 87;BA.debugLine="SendGetRequest(\"http://\" & IPAddress & \"/win&T=1&";
_sendgetrequest("http://"+_ipaddress+"/win&T=1&R=0&G=255&B=0");
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
return "";
}
public static String  _setmagentabutton_click() throws Exception{
 //BA.debugLineNum = 95;BA.debugLine="Sub SetMagentaButton_Click";
 //BA.debugLineNum = 96;BA.debugLine="SendGetRequest(\"http://\" & IPAddress & \"/win&T=1&";
_sendgetrequest("http://"+_ipaddress+"/win&T=1&R=255&G=0&B=255");
 //BA.debugLineNum = 97;BA.debugLine="End Sub";
return "";
}
public static String  _setorangebutton_click() throws Exception{
 //BA.debugLineNum = 104;BA.debugLine="Sub SetOrangeButton_Click";
 //BA.debugLineNum = 105;BA.debugLine="SendGetRequest(\"http://\" & IPAddress & \"/win&T=1&";
_sendgetrequest("http://"+_ipaddress+"/win&T=1&R=255&G=165&B=0");
 //BA.debugLineNum = 106;BA.debugLine="End Sub";
return "";
}
public static String  _setredbutton_click() throws Exception{
 //BA.debugLineNum = 83;BA.debugLine="Sub SetRedButton_Click";
 //BA.debugLineNum = 84;BA.debugLine="SendGetRequest(\"http://\" & IPAddress & \"/win&T=1&";
_sendgetrequest("http://"+_ipaddress+"/win&T=1&R=255&G=0&B=0");
 //BA.debugLineNum = 85;BA.debugLine="End Sub";
return "";
}
public static String  _setwhitebutton_click() throws Exception{
 //BA.debugLineNum = 101;BA.debugLine="Sub SetWhiteButton_Click";
 //BA.debugLineNum = 102;BA.debugLine="SendGetRequest(\"http://\" & IPAddress & \"/win&T=1&";
_sendgetrequest("http://"+_ipaddress+"/win&T=1&R=255&G=255&B=255");
 //BA.debugLineNum = 103;BA.debugLine="End Sub";
return "";
}
public static String  _setyellowbutton_click() throws Exception{
 //BA.debugLineNum = 98;BA.debugLine="Sub SetYellowButton_Click";
 //BA.debugLineNum = 99;BA.debugLine="SendGetRequest(\"http://\" & IPAddress & \"/win&T=1&";
_sendgetrequest("http://"+_ipaddress+"/win&T=1&R=255&G=255&B=0");
 //BA.debugLineNum = 100;BA.debugLine="End Sub";
return "";
}
}
