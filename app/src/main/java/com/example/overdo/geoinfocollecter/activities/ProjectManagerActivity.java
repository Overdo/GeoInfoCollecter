package com.example.overdo.geoinfocollecter.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.example.overdo.geoinfocollecter.R;
import com.example.overdo.geoinfocollecter.adapter.ProjectAdapter;
import com.example.overdo.geoinfocollecter.db.Project;
import com.example.overdo.geoinfocollecter.listener.IOnItemClickListener;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.example.overdo.geoinfocollecter.R.id.position;
import static com.yydcdut.sdlv.Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
import static com.yydcdut.sdlv.Menu.ITEM_NOTHING;

/**
 * Created by Overdo on 2017/1/29.
 */
public class ProjectManagerActivity extends BaseActivity implements SlideAndDragListView.OnMenuItemClickListener {

    @InjectView(R.id.lv_projects)
    ListView mLvProjects;
    @InjectView(R.id.tv_nodata)
    TextView mTvNodata;
    private List<Project> mProjectList;
    private ProjectAdapter mAdapter;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_manager);
        ButterKnife.inject(this);

        initToolbar();
        intiView();
        initListener();


    }

    private void initListener() {

    }

    private void intiView() {
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
                    ((SwipeLayout)(mLvProjects.getChildAt(position - mLvProjects.getFirstVisiblePosition()))).open(true);
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

    @Override
    public int onMenuItemClick(View v, final int itemPosition, int buttonPosition, int direction) {

           showDeleteProjectDialog(position);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case ITEM_DELETE_FROM_BOTTOM_TO_TOP:
                        DataSupport.delete(Project.class, mProjectList.get(itemPosition).getId());
                        mProjectList.remove(itemPosition);
                        showToast("删除成功");
                        mAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };
        return ITEM_NOTHING;
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

}
