package com.example.overdo.geoinfocollecter.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.example.overdo.geoinfocollecter.R;
import com.example.overdo.geoinfocollecter.adapter.ProjectAdapter;
import com.example.overdo.geoinfocollecter.db.GeoInfo;
import com.example.overdo.geoinfocollecter.db.Project;
import com.example.overdo.geoinfocollecter.listener.IOnItemClickListener;
import com.example.overdo.geoinfocollecter.util.ToastUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by Overdo on 2017/1/29.
 */
public class ProjectManagerActivity extends BaseActivity {

    @InjectView(R.id.lv_projects)
    ListView mLvProjects;
    @InjectView(R.id.tv_nodata)
    TextView mTvNodata;
    private List<Project> mProjectList;
    private ProjectAdapter mAdapter;
    private ProgressBar mProgress;
    private Handler mHandler;
    private String dir1;
    private File mFile;
    private static final String TAG = "ProjectManagerActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_manager);
        ButterKnife.inject(this);

        initToolbar();
        initView();
    }


    private void initView() {

        mProgress = (ProgressBar) findViewById(R.id.progress);
        mProjectList = DataSupport.findAll(Project.class);

        if (mProjectList.isEmpty()) {
            mLvProjects.setVisibility(View.GONE);
            mTvNodata.setVisibility(View.VISIBLE);
        } else {
            mLvProjects.setVisibility(View.VISIBLE);
            mTvNodata.setVisibility(View.GONE);

            mAdapter = new ProjectAdapter(this, mProjectList, new IOnItemClickListener() {
                @Override
                public void onclick(int position) {
                    Intent intent = new Intent(ProjectManagerActivity.this, GeoInfoManagerActivity.class);
                    intent.putExtra("geoinfo_list", mProjectList.get(position));
                    startActivity(intent);
                }

                @Override
                public void ondelete(int position) {
                    showDeleteProjectDialog(position);
                }
            });
            mLvProjects.setAdapter(mAdapter);
            mAdapter.setMode(Attributes.Mode.Single);
            mLvProjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ((SwipeLayout) (mLvProjects.getChildAt(position - mLvProjects.getFirstVisiblePosition()))).open(true);
                }
            });
        }
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("项目管理");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showDeleteProjectDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProjectManagerActivity.this);
        builder.setTitle("警告！");
        builder.setMessage("这个操作会删除这个项目的所有数据！");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                DataSupport.delete(Project.class, mProjectList.get(position).getId());
                mProjectList.remove(position);
                showToast("删除成功");
                mAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.output:

                if(mProjectList.isEmpty()){
                    ToastUtil.show(getApplicationContext(),"没有可以导出的数据");
                    return super.onOptionsItemSelected(item);
                }
                //后台导出同事显示进度条
                //成功隐藏进度条同事吐司
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what) {
                            case 111:
                                mProgress.setVisibility(View.GONE);
                                showToast("导出成功，数据文件保存在" + dir1 + "目录下");
                                break;
                        }
                    }
                };
                mProgress.setVisibility(View.VISIBLE);
                writeToExcel();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void writeToExcel() {

        dir1 = Environment.getExternalStorageDirectory().getPath() + "/geoInfoCollector";
        Log.d(TAG, "writeToExcel: " + dir1);
        File dir = new File(dir1);
        mFile = new File(dir, "地理信息采集助手数据库" + ".xls");
        if (dir == null || !mFile.isDirectory()) {
            dir.mkdirs();
        }
        new Thread() {
            @Override
            public void run() {
                super.run();

                String[] title = {"编号", "日期", "地理位置"
                        , "latitude", "longtitude", "高程", "备注", "项目名称", "负责人", "记录人"};
                try {
                    WritableWorkbook wwb;
                    OutputStream os = new FileOutputStream(mFile);
                    wwb = Workbook.createWorkbook(os);

                    for (int j = 0; j < mProjectList.size(); j++) {

                        //查询下数据
                        final List<GeoInfo> mlist = DataSupport
                                .where("project_id = ?", mProjectList.get(j).getId() + "")
                                .find(GeoInfo.class);

                        WritableSheet sheet = wwb.createSheet(mProjectList.get(j).getProjectname(), j);

                        Label label;
                        for (int i = 0; i < title.length; i++) {
                            // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
                            // 在Label对象的子对象中指明单元格的位置和内容
                            label = new Label(i, 0, title[i], getHeader());
                            // 将定义好的单元格添加到工作表中
                            sheet.addCell(label);
                        }

                        for (int i = 0; i < mlist.size(); i++) {
                            GeoInfo geoInfo = mlist.get(i);

                            Label number = new Label(0, i + 1, geoInfo.getCode());
                            Label date = new Label(1, i + 1, geoInfo.getDate());
                            Label address = new Label(2, i + 1, geoInfo.getAddress());
                            Label latitude = new Label(3, i + 1, geoInfo.getLatitude());
                            Label longtitude = new Label(4, i + 1, geoInfo.getLongtitude());
                            Label elevation = new Label(5, i + 1, geoInfo.getElevation());
                            Label note = new Label(6, i + 1, geoInfo.getNote());
                            Label projectName = new Label(7, i + 1, mProjectList.get(j).getProjectname());
                            Label charger = new Label(8, i + 1, mProjectList.get(j).getLeader());
                            Label collector = new Label(9, i + 1, mProjectList.get(j).getLeader());

                            sheet.addCell(number);
                            sheet.addCell(date);
                            sheet.addCell(address);
                            sheet.addCell(latitude);
                            sheet.addCell(longtitude);
                            sheet.addCell(elevation);
                            sheet.addCell(note);
                            sheet.addCell(projectName);
                            sheet.addCell(charger);
                            sheet.addCell(collector);
                        }
                    }
                    // 写入数据
                    wwb.write();
                    // 关闭文件
                    wwb.close();
                    mHandler.sendEmptyMessage(111);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (RowsExceededException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10,
                WritableFont.BOLD);// 定义字体
        try {
            font.setColour(Colour.BLUE);// 蓝色字体
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
            format.setBorder(Border.ALL, BorderLineStyle.THIN,
                    Colour.BLACK);// 黑色边框
            format.setBackground(Colour.YELLOW);// 黄色背景
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.geo_manager, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
