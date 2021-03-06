package com.map.tjsubway.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

//import com.amap.api.fence.PoiItem;
//import com.amap.api.location.AMapLocation;
//import com.amap.api.location.AMapLocationClient;
//import com.amap.api.location.AMapLocationClientOption;
//import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;
import com.map.tjsubway.R;
import com.map.tjsubway.helper.SensorEventHelper;
import com.map.tjsubway.route.BusRouteActivity;
import com.map.tjsubway.route.DriveRouteActivity;
import com.map.tjsubway.route.RideRouteActivity;
import com.map.tjsubway.route.WalkRouteActivity;
import com.map.tjsubway.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public  class NearbyAct extends Activity implements AMap.OnMyLocationChangeListener,
        AMap.OnMapClickListener, AMap.OnMarkerClickListener,
        AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, PoiSearch.OnPoiSearchListener {

    private AMap mAMap;
    private MapView mapview;
    private LocationSource.OnLocationChangedListener mListener;
//    private AMapLocationClient mlocationClient;
//    private AMapLocationClientOption mLocationOption;
    private MyLocationStyle myLocationStyle;

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);


    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类

    private Double Longitude;
    private Double latitude;
    private LatLng ll;
    private LatLonPoint lp ;//= new LatLonPoint(39.0983125873, 117.0954142386);// 116.472995,39.993743
    private LatLonPoint lpLat ;

    private Marker locationMarker; // 选择的点
    private Marker detailMarker;
    private Marker mlastMarker;
    private PoiSearch poiSearch;
    private myPoiOverlay poiOverlay;// poi图层
    private ArrayList<com.amap.api.services.core.PoiItem> poiItems;// poi数据

    private RelativeLayout mPoiDetail;
    private TextView stationtxt2;
    private TextView routetxt2;
    private TextView mPoiName, mPoiAddress;
    private String keyWord = "";
    private EditText mSearchText;

    private int sProgress = 3;
    private CardView cardView1;
    private CardView cardView2;

    private SensorEventHelper mSensorHelper;
    private boolean mFirstFix = false;
    private Marker mLocMarker;
    private Circle mCircle;
    public static final String LOCATION_MARKER_FLAG = "mylocation";

    private RelativeLayout.LayoutParams relaParams;

    private Bundle lpData;
    private String lp_lat,lp_lon,lpLat_lat,lpLat_lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
        setContentView(R.layout.activity_nearby);

        mapview = (MapView) findViewById(R.id.nearbyMap);
        mapview.onCreate(savedInstanceState);// 此方法必须重写
        init();
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(18));

        final TextView mileageTxt = (TextView) findViewById(R.id.mileageTxt);
        SeekBar mileageSeekbar = (SeekBar) findViewById(R.id.mileageSeekBar);
        mileageSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mileageTxt.setText(progress+"KM");
                sProgress = progress;
                doSearchQuery();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        stationtxt2 = (TextView) findViewById(R.id.stationTxt2);
        routetxt2 = (TextView) findViewById(R.id.routeTxt2);
        CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circleMenu);
        circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {
            @Override
            public void onItemClick(CircleMenuButton menuButton) {
                switch (menuButton.getId()) {
                    case R.id.bike:

                        lpData = new Bundle();
                        lp_lat = String.valueOf(lp.getLatitude());
                        lp_lon = String.valueOf(lp.getLongitude());
                        lpLat_lat = String.valueOf(lpLat.getLatitude());
                        lpLat_lon = String.valueOf(lpLat.getLongitude());
                        lpData.putString("lp_lat",lp_lat);
                        lpData.putString("lp_lon",lp_lon);
                        lpData.putString("lpLat_lat",lpLat_lat);
                        lpData.putString("lpLat_lon",lpLat_lon);

                        Intent bikeIntent = new Intent(NearbyAct.this,RideRouteActivity.class);
                        bikeIntent.putExtras(lpData);
                        startActivity(bikeIntent);
                        break;
                    case R.id.bus:

                        lpData = new Bundle();
                        lp_lat = String.valueOf(lp.getLatitude());
                        lp_lon = String.valueOf(lp.getLongitude());
                        lpLat_lat = String.valueOf(lpLat.getLatitude());
                        lpLat_lon = String.valueOf(lpLat.getLongitude());
                        lpData.putString("lp_lat",lp_lat);
                        lpData.putString("lp_lon",lp_lon);
                        lpData.putString("lpLat_lat",lpLat_lat);
                        lpData.putString("lpLat_lon",lpLat_lon);

                        Intent busIntent = new Intent(NearbyAct.this, BusRouteActivity.class);
                        busIntent.putExtras(lpData);
                        startActivity(busIntent);
                        break;
                    case R.id.walk:

                        lpData = new Bundle();
                        lp_lat = String.valueOf(lp.getLatitude());
                        lp_lon = String.valueOf(lp.getLongitude());
                        lpLat_lat = String.valueOf(lpLat.getLatitude());
                        lpLat_lon = String.valueOf(lpLat.getLongitude());
                        lpData.putString("lp_lat",lp_lat);
                        lpData.putString("lp_lon",lp_lon);
                        lpData.putString("lpLat_lat",lpLat_lat);
                        lpData.putString("lpLat_lon",lpLat_lon);

                        Intent walkIntent = new Intent(NearbyAct.this,WalkRouteActivity.class);
                        walkIntent.putExtras(lpData);
                        startActivity(walkIntent);

                        break;
                    case R.id.taxi:
                        lpData = new Bundle();
                        lp_lat = String.valueOf(lp.getLatitude());
                        lp_lon = String.valueOf(lp.getLongitude());
                        lpLat_lat = String.valueOf(lpLat.getLatitude());
                        lpLat_lon = String.valueOf(lpLat.getLongitude());
                        lpData.putString("lp_lat",lp_lat);
                        lpData.putString("lp_lon",lp_lon);
                        lpData.putString("lpLat_lat",lpLat_lat);
                        lpData.putString("lpLat_lon",lpLat_lon);

                        Intent taxiIntent = new Intent(NearbyAct.this,DriveRouteActivity.class);
                        taxiIntent.putExtras(lpData);
                        startActivity(taxiIntent);
                        break;
                    case R.id.edit:
//                        showMssage("Edit");
                        break;
                }
            }
        });

         cardView1 = (CardView) findViewById(R.id.cardview1);
//        cardView1.getBackground().setAlpha(150);
        whetherToShowDetailInfo(false);
        relaParams = (RelativeLayout.LayoutParams) cardView1.getLayoutParams();
        relaParams.height = 300;
        cardView1.setLayoutParams(relaParams);

    }


    /**
     * 初始化AMap对象
     */
    private void init() {
        if (mAMap == null) {
            mAMap = mapview.getMap();
//            setUpMap();
            mAMap.setOnMapClickListener(this);
            mAMap.setOnMarkerClickListener(this);
            mAMap.setOnInfoWindowClickListener(this);
            mAMap.setInfoWindowAdapter(this);

            mAMap.setOnMyLocationChangeListener(this);

//            locationMarker = mAMap.addMarker(new MarkerOptions()
//                    .anchor(0.5f, 0.5f)
//                    .icon(BitmapDescriptorFactory
//                            .fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.navi_map_gps_locked)))
//                    .position(ll));//new LatLng(lp.getLatitude(), lp.getLongitude())
//            locationMarker.showInfoWindow();

        }

//        mSensorHelper = new SensorEventHelper(this);
//        if (mSensorHelper != null) {
//            mSensorHelper.registerSensorListener();
//        }
        setup();

        //mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 14));
    }

    private void setup() {
        mPoiDetail = (RelativeLayout) findViewById(R.id.poi_detail);
        mPoiDetail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//				Intent intent = new Intent(PoiSearchActivity.this,
//						SearchDetailActivity.class);
//				intent.putExtra("poiitem", mPoi);
//				startActivity(intent);

            }
        });
        mPoiName = (TextView) findViewById(R.id.stationTxt2);
        mPoiAddress = (TextView) findViewById(R.id.routeTxt2);

        etupLocationStyle();
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        mAMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE));
    }

    private void etupLocationStyle(){
        // 自定义系统定位蓝点
        myLocationStyle = new MyLocationStyle();

        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.interval(10000);

        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.navi_map_gps_locked));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        mAMap.setMyLocationStyle(myLocationStyle);
    }


    public void onMyLocationChange(Location location) {
        // 定位回调监听
        if(location != null) {
            Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            Bundle bundle = location.getExtras();
                latitude = location.getLatitude();
                Longitude = location.getLongitude();
                lp = new LatLonPoint(latitude,Longitude);
                ll = new LatLng(lp.getLatitude(), lp.getLongitude());

            if(bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);

                Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType );
            } else {
                Log.e("amap", "定位信息， bundle is null ");

            }

        } else {
            Log.e("amap", "定位失败");
        }
    }


    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        String keyWord = "地铁站";
        //keyWord = mSearchText.getText().toString().trim();
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, sProgress*1000, true));//
            // 设置搜索区域为以lp点为圆心，其周围为滑动条确定的范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
        whetherToShowDetailInfo(true);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
//        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapview.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
//        if(null != mlocationClient){
//            mlocationClient.onDestroy();
//        }
    }


    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        //清除POI信息显示
                        whetherToShowDetailInfo(false);
                        //并还原点击marker样式
                        if (mlastMarker != null) {
                            resetlastmarker();
                        }
                        //清理之前搜索结果的marker
                        if (poiOverlay !=null) {
                            poiOverlay.removeFromMap();
                        }
                        mAMap.clear();
                        poiOverlay = new myPoiOverlay(mAMap, poiItems);
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

//                        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
//                        etupLocationStyle();
//                        mAMap.addMarker(new MarkerOptions()
//                                .anchor(0.5f, 0.5f)
//                                .icon(BitmapDescriptorFactory
//                                        .fromBitmap(BitmapFactory.decodeResource(
//                                                getResources(), R.drawable.navi_map_gps_locked)))
//                                .position(ll));

                        mAMap.addCircle(new CircleOptions()
                                .center(ll).radius(sProgress*1000)
                                .strokeColor(STROKE_COLOR)
                                .fillColor(FILL_COLOR)
                                .strokeWidth(2));

                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(NearbyAct.this,
                                R.string.no_result);
                       // mAMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                        poiOverlay.removeFromMap();
                    }
                }
            } else {
                ToastUtil
                        .show(NearbyAct.this, R.string.no_result);
                //mAMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                poiOverlay.removeFromMap();
            }
        } else  {
            ToastUtil.showerror(this.getApplicationContext(), rcode);
        }
    }

    @Override
    public void onPoiItemSearched(com.amap.api.services.core.PoiItem poiItem, int i) {

    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        if (marker.getObject() != null) {
            whetherToShowDetailInfo(true);
            try {

                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    // 将之前被点击的marker置为原来的状态
                    resetlastmarker();
                    mlastMarker = marker;
                }

                detailMarker = marker;
                detailMarker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.mipmap.poi_marker_pressed)));
//                设置cardview的高度为正常高度700
                relaParams = (RelativeLayout.LayoutParams) cardView1.getLayoutParams();
                relaParams.height = 700;
                cardView1.setLayoutParams(relaParams);

                PoiItem PoiLat = (PoiItem) marker.getObject();
                setPoiItemDisplayContent(PoiLat);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }else {
            whetherToShowDetailInfo(false);
            resetlastmarker();
        }

        return true;
    }

    // 将之前被点击的marker置为原来的状态
    private void resetlastmarker() {
        int index = poiOverlay.getPoiIndex(mlastMarker);
        if (index < 10) {
            mlastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            markers[index])));

            relaParams = (RelativeLayout.LayoutParams) cardView1.getLayoutParams();
            relaParams.height = 300;
            cardView1.setLayoutParams(relaParams);

        }else {
            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
        }
        mlastMarker = null;

    }


    private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
        mPoiName.setText(mCurrentPoi.getTitle());
        mPoiAddress.setText(mCurrentPoi.getSnippet());
        lpLat = mCurrentPoi.getLatLonPoint();

//        String s = lpLat.toString();
//        String s1 = lp.toString();
//        Toast toast = Toast.makeText(NearbyAct.this,s,Toast.LENGTH_SHORT);
//        toast.show();
    }


    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }


//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.mileageSeekBar:
//                doSearchQuery();
//                break;
//
//            default:
//                break;
//        }

//    }

    private int[] markers = {
            R.mipmap.poi_marker_1,
            R.mipmap.poi_marker_1,
            R.mipmap.poi_marker_1,
            R.mipmap.poi_marker_1,
            R.mipmap.poi_marker_1,
            R.mipmap.poi_marker_1,
            R.mipmap.poi_marker_1,
            R.mipmap.poi_marker_1,
            R.mipmap.poi_marker_1,
            R.mipmap.poi_marker_1
    };

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mPoiDetail.setVisibility(View.VISIBLE);
//            stationtxt2.setVisibility(View.VISIBLE);
//            routetxt2.setVisibility(View.VISIBLE);

        } else {
            mPoiDetail.setVisibility(View.GONE);
//            stationtxt2.setVisibility(View.GONE);
//            routetxt2.setVisibility(View.GONE);
        }
    }


    @Override
    public void onMapClick(LatLng arg0) {
        whetherToShowDetailInfo(false);
        if (mlastMarker != null) {
            resetlastmarker();
        }
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(this, infomation);

    }


    /**
     * 自定义PoiOverlay
     *
     */

    private class myPoiOverlay {
        private AMap mamap;
        private ArrayList<com.amap.api.services.core.PoiItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
        public myPoiOverlay(AMap amap , ArrayList<com.amap.api.services.core.PoiItem> pois) {
            mamap = amap;
            mPois = pois;
        }

        /**
         * 添加Marker到地图中。
         * @since V2.1.0
         */
        public void addToMap() {
            for (int i = 0; i < mPois.size(); i++) {
                Marker marker = mamap.addMarker(getMarkerOptions(i));
                com.amap.api.services.core.PoiItem item = mPois.get(i);
                marker.setObject(item);
                mPoiMarks.add(marker);
            }
        }

        /**
         * 去掉PoiOverlay上所有的Marker。
         *
         * @since V2.1.0
         */
        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
            }
        }

        /**
         * 移动镜头到当前的视角。
         * @since V2.1.0
         */
        public void zoomToSpan() {
            if (mPois != null && mPois.size() > 0) {
                if (mamap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                if (sProgress == 3 || sProgress==4 ){
                mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));}
                else if (sProgress == 1||sProgress == 2){
                    mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                    mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 14));
                }
                else {
                    mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                }
//
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < mPois.size(); i++) {
                b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                        mPois.get(i).getLatLonPoint().getLongitude()));

                lpLat = new LatLonPoint(mPois.get(i).getLatLonPoint().getLatitude(),
                        mPois.get(i).getLatLonPoint().getLongitude());
            }
            return b.build();
        }

        private MarkerOptions getMarkerOptions(int index) {
            return new MarkerOptions()
                    .position(
                            new LatLng(mPois.get(index).getLatLonPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(index));
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            return mPois.get(index).getSnippet();
        }

        /**
         * 从marker中得到poi在list的位置。
         *
         * @param marker 一个标记的对象。
         * @return 返回该marker对应的poi在list的位置。
         * @since V2.1.0
         */
        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 返回第index的poi的信息。
         * @param index 第几个poi。
         * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
         * @since V2.1.0
         */
        public com.amap.api.services.core.PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            if (arg0 < 10) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), markers[arg0]));
                return icon;
            }else {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight));
                return icon;
            }
        }
    }



}
