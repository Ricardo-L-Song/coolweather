package com.example.sl.coolweather.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sl.coolweather.MainActivity;
import com.example.sl.coolweather.R;
import com.example.sl.coolweather.activity.WeatherActivity;
import com.example.sl.coolweather.db.City;
import com.example.sl.coolweather.db.County;
import com.example.sl.coolweather.db.Province;
import com.example.sl.coolweather.util.HttpUtil;
import com.example.sl.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
//这是用来选择省市县级的碎片
public class ChooseAreaFragment extends Fragment {


    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.back_button)
    Button mBackButton;
    //以上控件绑定
    public static final int LEVEL_PROVINCE = 0;//静态整型常量
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    @BindView(R.id.list_view)
    ListView mListView;
    private ProgressDialog mProgressDialog;
    private ArrayAdapter<String> adapter;//listview的适配器
    private List<String> datalist = new ArrayList<>();//这个的String数组是用来适配listview的（由于不知道存放的是省，市，县哪个的数据，所以县单独列出来）可以通过queryCity等3个query方法填充
    private List<Province> mProvinceList;//省数组用来存储省列表填充的数据
    private List<City> mCityList;//市数组用来存储市列表填充的数据
    private List<County> mCountyList;//县数组用来存储县列表填充的数据
    private Province selectedProvince;//选中的省份
    private City selectedCity;//选中的城市
    private int currentLevel;//当前选中的级别 是省0，还是市1，还是县2

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);//视图绑定
        ButterKnife.bind(this, view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, datalist);//将datalist数组适配的adapter指定
        mListView.setAdapter(adapter);//使用相应的adapter，在底层数据与listview之间建立联系 数据适配完毕
        return view;//返回操作过后的view
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {//非静态的View（Listview）需要进行更新数据操作 在这个方法中调用 依托于托管的Activity
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//下级列表 setOnItemClickListener用来适配listview，RecyclerView则是完全交由adapter
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//由于是listview的点击事件 position可以确定传入 在我们点击的时候
                if (currentLevel == LEVEL_PROVINCE) {//如果当前在省级选项列表 显示的省级数据
                    selectedProvince = mProvinceList.get(position);//选择listview省级列表中的省显示市
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {//如果当前在市级选项列表 显示的市级数据
                    selectedCity = mCityList.get(position);
                    queryCounties();
                } else if (currentLevel==LEVEL_COUNTY){//如果当前在县级选项列表 显示县级数据
                    String weatherId=mCountyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);//跳转Intent
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();//结束当前Activity的生命周期 优化
                    }else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity=(WeatherActivity)getActivity();
                        activity.drawerLayout.closeDrawers();//关闭侧滑菜单
                        activity.swipeRefresh.setRefreshing(true);//显示刷新进度条
                        activity.requestWeather(weatherId);//刷新天气信息
                    }
                }
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {//返回按钮 返回上一级列表
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();//在这一步，加载非静态的ListView，默认加载为省级列表
    }

    //    查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryProvinces() {
        mTitleText.setText("中国");
        mBackButton.setVisibility(View.GONE);//设置成不可见
        mProvinceList = DataSupport.findAll(Province.class);//查找Province表，将表填充进数组，数组中的每个元素对应一个Province(泛型类对象为Province)
        if (mProvinceList.size() > 0) {//如果表中有数据填充进数组
            datalist.clear();//清理这个数组，准备将填充好的mProvinceList集合中的province对象的ProvinceName属性取出放入String数组datalist
            for (Province province : mProvinceList) {//遍历mProvinceList集合中的Province对象，每次遍历的对象用province替代
                datalist.add(province.getProvinceName());
            }
            //相当于 for (int i = 0; i <mProvinceList.size() ; i++) {
//            datalist.add(mProvinceList.get(i).getProvinceName());
//        }
            adapter.notifyDataSetChanged();//通知数据变化
            mListView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;//listView中显示了省级列表 当前所在级为省级
        } else {//否则从服务器查询数据 通过网络接口api的方式
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    //    查询全国所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryCities() {
        mTitleText.setText(selectedProvince.getProvinceName());//标题为选中的省
        mBackButton.setVisibility(View.VISIBLE);//使得返回按钮可见，返回按钮的逻辑已经写好
        mCityList = DataSupport.where("provinceid=?", String.valueOf(selectedProvince.getId())).find(City.class);
        //当前选中的省份的id用来查询数据库，City实体类对应的表中，有provinceId这一字段用以查询
        //查询城市表 找到所有城市中provinceId符合选中省份的城市 填充进数组 数组中的每一元素对应城市表中一个城市的记录
        if (mCityList.size() > 0) {//如果填充进的数据不为空
            datalist.clear();
            for (int i=0;i<mCityList.size();i++) {
                datalist.add(mCityList.get(i).getCityName());//改变适配listview的datalist数组
            }
            adapter.notifyDataSetChanged();//通知adapter 适配给listview的数据发生了变化
            mListView.setSelection(0);
            currentLevel = LEVEL_CITY;//listview显示了市级列表，当前所在级为市级
        } else {//否则从服务器查询数据 需要provinceCode用来访问接口 通过网络接口api的方式查询
            int provinceCode = selectedProvince.getProvinceCode();//取得省的代号用来发送与服务器交互的请求
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    //    查询全国所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryCounties() {
        mTitleText.setText(selectedCity.getCityName());
        mBackButton.setVisibility(View.VISIBLE);
        mCountyList = DataSupport.where("cityId=?", String.valueOf(selectedCity.getId())).find(County.class);
        if (mCountyList.size() > 0) {//如果填充进的数据不为空
            datalist.clear();
            for (int i = 0; i <mCountyList.size() ; i++) {
                datalist.add(mCountyList.get(i).getCountyName());
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {//否则从服务器查询数据 需要provinceCode以及cityCode用来访问接口 通过网络接口api的方式查询
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    //    根据传入的地址和类型从服务器上查询省市县数据 参数为形参，实参由上面三个query方法传入
    private void queryFromServer(String address, final String type) {
        showProgressDialog();//开始从服务器查询数据 显示进度对话框
        HttpUtil.sendOkHttpRequest(address, new Callback() {//调用工具类 新建一个回调（相当于开辟了一个子线程来进行耗时操作）
            @Override
            public void onFailure(Call call, IOException e) {//失败函数 由于同样在会调用，同样要回到主线程进行UI操作中的关闭进度对话框
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();//从回调函数中（sendOkHttpRequest()返回的数据会返回到新建的回调中）回到主线程 关闭进度对话框
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {//成功函数 响应的数据会回调到onResponse()方法中
                String responseText = response.body().string();//将返回的response对象的数据体本地化 全部化成String 而后可以解析成JsonArray
                boolean result = false;//默认
                //如果之后的结果为true，即相应的数据已经从服务器进入数据库 进入数据库中的相应表中
                if ("province".equals(type)) {//如果传入的实参是province 响应Province的请求
                    result = Utility.handleProvinceResponse(responseText);//解析处理服务器返回的省级数据(填入省表)
                } else if ("city".equals(type)) {//如果传入的实参是city 响应city的请求
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());//解析处理服务器返回的市级数据（填入市表）
                } else if ("county".equals(type)) {//如果传入的实参是county 响应county的请求
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result) {//已经从服务器获取了数据并且入库
                    getActivity().runOnUiThread(new Runnable() {//从回调函数中回到主线程，进行UI操作，这里是刷新listView
                        @Override
                        public void run() {
                            closeProgressDialog();//完成数据入库，关闭进度对话框
                            if ("province".equals(type)) {
                                queryProvinces();//再次从数据库查询并更新listView
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    //    显示进度对话框
    private void showProgressDialog() {
        if (mProgressDialog == null) {//如果没有进度对话框的实例对象为空
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载");
            mProgressDialog.setCanceledOnTouchOutside(false);//点击物理返回键进度对话框才消失
        }
        mProgressDialog.show();//进度对话框对象的显示函数方法
    }

    //    关闭进度对话框
    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();//去除进度对话框的函数
        }
    }

}
