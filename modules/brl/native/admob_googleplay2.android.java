
//import com.google.android.gms.ads.*;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import android.util.Log;
//import android.widget.Toast;


final class Variables
{
    static boolean __bb__MobileAds_init=false;
}

class BBAdmob implements Runnable{

	static BBAdmob _admob;
	
	int _adStyle;
	int _adLayout;
	boolean _adVisible;

	AdView _adView;
	AdRequest.Builder _builder;
	boolean _adValid=true;

	static Activity _activity;
	
	static public BBAdmob GetAdmob(){
		if( _admob==null ) _admob=new BBAdmob();
		_activity = BBAndroidGame.AndroidGame().GetActivity();
		if( Variables.__bb__MobileAds_init == false ) {
		    MobileAds.initialize(_activity, new OnInitializationCompleteListener() {
		        @Override
		        public void onInitializationComplete(InitializationStatus initializationStatus) {}
		    });
		    Variables.__bb__MobileAds_init = true ;
	    }
		return _admob;
	}
	
	public void ShowAdView( int style,int layout ){
		_adStyle=style;
		_adLayout=layout;
		_adVisible=true;
		
		invalidateAdView();
	}
	
	public void HideAdView(){
		_adVisible=false;
		
		invalidateAdView();
	}
	
	public int AdViewWidth(){
		return (_adView!=null) ? _adView.getWidth() : 0;
	}
	
	public int AdViewHeight(){
		return (_adView!=null) ? _adView.getHeight() : 0 ;
	}

	private void addTestDevice( String _test_dev ){
		if( _test_dev.length()==0 ) return;
		if( _test_dev.equals( "TEST_EMULATOR" ) ) _test_dev=AdRequest.DEVICE_ID_EMULATOR;
		//_builder.addTestDevice( _test_dev );
	}
	
	private void invalidateAdView(){
		if( _adValid ){
			_adValid=false;
			BBAndroidGame.AndroidGame().GetGameView().post( this );
		}
	}
	
	private void updateAdView(){
	
		_adValid=true;
	
		RelativeLayout parent=(RelativeLayout)_activity.findViewById( R.id.mainLayout );
		
		if( _adView!=null ){
			parent.removeView( _adView );
			_adView.destroy();
			_adView=null;
		}
		
		if( !_adVisible ){
			return;
		}
		
		AdSize sz=AdSize.BANNER;
		switch( _adStyle ){
		case 2:sz=AdSize.SMART_BANNER;break;
		case 3:sz=AdSize.SMART_BANNER;break;
		}
		
		_adView=new AdView( _activity );
		_adView.setAdSize( sz );
		_adView.setAdUnitId( CerberusConfig.ADMOB_PUBLISHER_ID );

		//weird voodoo to make adView appear instantly(ish). Without this, you have to wait about 60 seconds regardless of ad timeout setting.
		_adView.setBackgroundColor( Color.BLACK );
		
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT );

		int rule1=RelativeLayout.CENTER_HORIZONTAL,rule2=RelativeLayout.CENTER_VERTICAL;
		
		switch( _adLayout ){
		case 1:rule1=RelativeLayout.ALIGN_PARENT_TOP;rule2=RelativeLayout.ALIGN_PARENT_LEFT;break;
		case 2:rule1=RelativeLayout.ALIGN_PARENT_TOP;rule2=RelativeLayout.CENTER_HORIZONTAL;break;
		case 3:rule1=RelativeLayout.ALIGN_PARENT_TOP;rule2=RelativeLayout.ALIGN_PARENT_RIGHT;break;
		case 4:rule1=RelativeLayout.ALIGN_PARENT_BOTTOM;rule2=RelativeLayout.ALIGN_PARENT_LEFT;break;
		case 5:rule1=RelativeLayout.ALIGN_PARENT_BOTTOM;rule2=RelativeLayout.CENTER_HORIZONTAL;break;
		case 6:rule1=RelativeLayout.ALIGN_PARENT_BOTTOM;rule2=RelativeLayout.ALIGN_PARENT_RIGHT;break;
		}
		
		params.addRule( rule1 );
		params.addRule( rule2 );
		
		parent.addView( _adView,params );
		
		_builder = new AdRequest.Builder();
		
		addTestDevice( CerberusConfig.ADMOB_ANDROID_TEST_DEVICE1 );
		addTestDevice( CerberusConfig.ADMOB_ANDROID_TEST_DEVICE2 );
		addTestDevice( CerberusConfig.ADMOB_ANDROID_TEST_DEVICE3 );
		addTestDevice( CerberusConfig.ADMOB_ANDROID_TEST_DEVICE4 );
		
		AdRequest req=_builder.build();

		_adView.loadAd( req );
	}
	
	public void run(){
		updateAdView();
	}
	
}

class BBAdmobInterstitial implements Runnable {

	// the kind of "singleton"
	static BBAdmobInterstitial _admob;
	// the ad
	//AdRequest.Builder _builder;
	//InterstitialAd _interstitialAd;
	// ad Unit ID
	String adUnitId;
	
	boolean loaded;
	
	private InterstitialAd mInterstitialAd;
	private static final String TAG = "[Cerberus]";
	static Activity _activity;
	
	// creates an instance of the object and start the thread
	static public BBAdmobInterstitial GetAdmobInterstitial(String adUnitId){
		//Toast.makeText(MyActivity.this, "GetAdmobInterstitial", Toast.LENGTH_SHORT).show();

		if( _admob==null ) _admob=new BBAdmobInterstitial();
		_activity = BBAndroidGame.AndroidGame().GetActivity();

		if( Variables.__bb__MobileAds_init == false ) {
		    MobileAds.initialize(_activity, new OnInitializationCompleteListener() {
		        @Override
		        public void onInitializationComplete(InitializationStatus initializationStatus) {}
		    });
		    Variables.__bb__MobileAds_init = true ;
	    }

		_admob.startAd(adUnitId);
		return _admob;
	}

	// displays the ad to the user if it is ready
	public void ShowAdViewInterstitial( ){
		if (mInterstitialAd != null ) {
			if( loaded ){
				loaded=false;
				BBAndroidGame.AndroidGame().GetGameView().post(this);
			}
		}
	}
	
	// start the thread 
	private void startAd(String adUnitId){
		this.adUnitId = adUnitId;
		BBAndroidGame.AndroidGame().GetGameView().post(this);
	}
	
	// loads an ad
	private void loadAd(){
		Log.i(TAG, "onAdLoad started ************");	
		AdRequest adRequest = new AdRequest.Builder().build();
      	InterstitialAd.load(_activity, this.adUnitId, adRequest, 
      	new InterstitialAdLoadCallback() {
	      @Override
	      public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
	        // The mInterstitialAd reference will be null until
	        // an ad is loaded.
	        mInterstitialAd = interstitialAd;
	        loaded=true;
	       Log.i(TAG, "onAdLoaded");
	       
		   interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
			  @Override
			  public void onAdDismissedFullScreenContent() {
			    // Called when fullscreen content is dismissed.
			    mInterstitialAd = null;
			    Log.d("TAG", "The ad was dismissed.");
			  }
			
			  @Override
			  public void onAdFailedToShowFullScreenContent(AdError adError) {
			    // Called when fullscreen content failed to show.
			    Log.d("TAG", "The ad failed to show.");
			  }
			
			  @Override
			  public void onAdShowedFullScreenContent() {
			    // Called when fullscreen content is shown.
			    // Make sure to set your reference to null so you don't
			    // show it a second time.
			    mInterstitialAd = null;
			    Log.d("TAG", "The ad was shown.");
			    loadAd();
			  }
			});	       
	       
	      }

	      @Override
	      public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
	        // Handle the error
	        Log.e(TAG, loadAdError.getMessage());
	        mInterstitialAd = null;
	      }
    	});
    	
	}
	
	// the runner
	public void run(){
 	
		if( mInterstitialAd != null ){
			mInterstitialAd.show(_activity);
			return;
		}
		
		// load the first ad
		loadAd();
	}
}