package com.example.map0802;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;


import com.example.R;
import com.example.base.BaseMessage;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.example.base.BaseUi;
import com.example.base.C;
import com.example.list.CameraComment;
import com.example.model.Camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;


public class MainActivity extends BaseUi {
    MapView mMapView = null;
   private BaiduMap mBaiduMap;
   private BitmapDescriptor mIconMaker; 
  private RelativeLayout mMarkerInfoLy;
  private boolean flag = false;
  private ImageView image;
  private ImageButton addMark;
  private TextView tv;
  int tag=0;
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.bmapView); 
	mMarkerInfoLy = (RelativeLayout) findViewById(R.id.id_marker_info);
       
	mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
	mBaiduMap = mMapView.getMap();
	image = (ImageView) mMarkerInfoLy.findViewById(R.id.info_img);
	tv = (TextView) mMarkerInfoLy.findViewById(R.id.info_name);
	addMark=(ImageButton) findViewById(R.id.main_top_add);
	tv.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			Intent intent = new Intent();
        	intent.setClass(MainActivity.this, CommentActivity.class);
        	startActivity(intent);
		}
		
	});
	image.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			 Intent intent = new Intent();  
               
             intent.setType("image/*");  
               
             intent.setAction(Intent.ACTION_GET_CONTENT);   
              
             startActivityForResult(intent, 1); 
		}
		
	});
	//showMark();
	setOnclickListener();
	initClickMap();
	initMarkerClickEvent();
   	doTaskAsync(C.task.getCamera, C.api.getCamera);
    } 
   
    private void setOnclickListener() {
		// TODO Auto-generated method stub
    	
    	
    	
		addMark.setOnClickListener(new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					tag = 1;
				}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == RESULT_OK) {  
            Uri uri = data.getData();  
            Log.e("wang", uri.toString());  
            ContentResolver cr = this.getContentResolver();  
            try {  
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));  
                 
                /* 灏咮itmap璁惧畾鍒癐mageView */  
                image.setImageBitmap(bitmap);  
            } catch (FileNotFoundException e) {  
                Log.e("Exception", e.getMessage(),e);  
            }  
        }  
        super.onActivityResult(requestCode, resultCode, data);  
    }  

    public void showMark(){
		OverlayOptions overlayOptions = null;
                Marker marker = null;
		LatLng latLng = null;
		 latLng = new LatLng(34.242652, 108.971171);
                        // 鍥炬爣
                        overlayOptions = new MarkerOptions().position(latLng)
                                        .icon(mIconMaker).zIndex(5);
                        marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
                        //Bundle bundle = new Bundle();
                        //bundle.putSerializable("info", info);
                        //marker.setExtraInfo(bundle);
    }	
private void initClickMap(){
			mBaiduMap.setOnMapClickListener(new OnMapClickListener(){

				@Override
				public void onMapClick(LatLng arg0) {
					// TODO Auto-generated method stub
					if(tag == 1) {
						OverlayOptions overlayOptions = null;
						 overlayOptions = new MarkerOptions().position(arg0)
	                             .icon(mIconMaker).zIndex(5);
						 mBaiduMap.addOverlay(overlayOptions);
						 HashMap<String, String> locationParams = new HashMap<String, String>();
						 locationParams.put("longitude", String.valueOf(arg0.longitude));
						 locationParams.put("latitude", String.valueOf(arg0.latitude));
						 doTaskAsync(C.task.createCamera, C.api.createCamera, locationParams);
						
						 tag = 0;
					}
					 if(flag){
						 mMarkerInfoLy.setVisibility(View.GONE);
						 flag = false;
					 }
				}

				@Override
				public boolean onMapPoiClick(MapPoi arg0) {
					// TODO Auto-generated method stub
					return false;
				}
				
			});
}
public void onTaskComplete(int taskId, BaseMessage message) {
	super.onTaskComplete(taskId, message);
	switch(taskId){
		case C.task.createCamera:
			toast("create camera succefully");
			break;
		case C.task.getCamera:
			Log.d("wang","entry getCamera");
			ArrayList<Camera> cameraList;
		try {
			cameraList = (ArrayList<Camera>) message.getResultList("Camera");
			Log.d("wang","camera list size is " + cameraList.size());
			OverlayOptions overlayOptions = null;
			Marker marker = null;
			LatLng latLng = null;
			for(Camera data : cameraList){
			
				latLng = new LatLng(Double.valueOf(data.getLatitude()),Double.valueOf(data.getLongitude()));
				overlayOptions = new MarkerOptions().position(latLng)
                         .icon(mIconMaker).zIndex(5);
                marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
				//Log.d("wang", "latitude = " + data.getLatitude());
				//Log.d("wang", "longitude = " + data.getLongitude());
				//Log.d("wang", "id = " + data.getId());
				
			}
			toast("get camera successfully");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		break;
	}
}
 private void initMarkerClickEvent() {
               
                mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener()
                {
                        @Override
                        public boolean onMarkerClick(final Marker marker)
                        {
                                

                                InfoWindow mInfoWindow;
                               
                                /*TextView location = new TextView(getApplicationContext());
                                	location.setClickable(true);
                                	 location.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
											mBaiduMap.hideInfoWindow();
										}
                                		 
                                	 });
                                location.setBackgroundResource(R.drawable.location_tips);
                                location.setPadding(30, 20, 30, 50);
                                location.setText("hello map");*/
				View location = getView();
                                final LatLng ll = marker.getPosition();
                                Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				Log.d("wang","haha x = " + p.x + "y = " + p.y);
                                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
		 		//LatLng llInfo = new LatLng(34.242652, 108.971171);
				Log.d("wang","latitude = " + llInfo.latitude + " longitude" + llInfo.longitude);
                                
                                p.y = -100;
                                mInfoWindow = new InfoWindow(location, llInfo,p.y
                                               /* new OnInfoWindowClickListener()
                                                {

                                                        @Override
                                                        public void onInfoWindowClick()
                                                        {
                                                                // 闅愯棌InfoWindow
                                                                mBaiduMap.hideInfoWindow();
                                                        }
                                                }*/);
                                
                                mBaiduMap.showInfoWindow(mInfoWindow);
                                
                                mMarkerInfoLy.setVisibility(View.VISIBLE);
                                
                                	flag = true;
                                //popupInfo(mMarkerInfoLy);
                                return true;
                        }
                });
        }
  /* protected void popupInfo(RelativeLayout mMarkerLy)
        {
                ViewHolder viewHolder = null;
                if (mMarkerLy.getTag() == null)
                {
                        viewHolder = new ViewHolder();
                        viewHolder.infoImg = (ImageView) mMarkerLy
                                        .findViewById(R.id.info_img);
                        viewHolder.infoName = (TextView) mMarkerLy
                                        .findViewById(R.id.info_name);
                        viewHolder.infoDistance = (TextView) mMarkerLy
                                        .findViewById(R.id.info_distance);
                        viewHolder.infoZan = (TextView) mMarkerLy
                                        .findViewById(R.id.info_zan);

                        mMarkerLy.setTag(viewHolder);
                }
                viewHolder = (ViewHolder) mMarkerLy.getTag();
                //viewHolder.infoImg.setImageResource(info.getImgId());
                viewHolder.infoDistance.setText("wang");
                viewHolder.infoName.setText("map");
                viewHolder.infoZan.setText("100");
        }


        private class ViewHolder
        {
                ImageView infoImg;
                TextView infoName;
                TextView infoDistance;
                TextView infoZan;
        }
*/
public View getView(){
	View v = View.inflate(getApplicationContext(),R.layout.tpl_list_blogs , null);
	Button bt = (Button) v.findViewById(R.id.ok);
	bt.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
                	intent.setClass(MainActivity.this, CameraInfo.class);
                	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                	startActivity(intent);
			mBaiduMap.hideInfoWindow();
		}
		
	});
	return v;
}
   public class Mylistern implements InfoWindow.OnInfoWindowClickListener {
	   public void onInfoWindowClick()
             {
                     // 闅愯棌InfoWindow
                     mBaiduMap.hideInfoWindow();
             }
 
   }
    @Override 
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }  
    @Override 
    protected void onResume() {
        super.onResume();
        mMapView.onResume(); 
        } 
    @Override 
    protected void onPause() {
        super.onPause(); 
        mMapView.onPause();
        }
 }

