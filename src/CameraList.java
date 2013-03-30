package nz.gen.geek_central.Compass3D;
/*
    Display a list of available cameras and let the user choose one to use.
    This Activity requires API level 9 or later.

    Copyright 2013 by Lawrence D'Oliveiro <ldo@geek-central.gen.nz>.

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.
*/

import android.hardware.Camera;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

public class CameraList extends android.app.Activity
  {
    public static final String CameraIDID = "nz.gen.geek_central.Compass3D.CameraID";

    private static boolean Reentered = false; /* sanity check */
    private static CameraList Current = null;

    private static int CurCameraID;

    private android.widget.ListView CameraListView;
    private SelectedItemAdapter TheCameras;

    public static class CameraItem
      {
        public final int CameraID;
        public final Camera.CameraInfo TheCameraInfo;
        public boolean Selected;

        public CameraItem
          (
            int CameraID,
            boolean Selected
          )
          {
            this.CameraID = CameraID;
            this.TheCameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(CameraID, TheCameraInfo);
            this.Selected = Selected;
          } /*CameraItem*/

      } /*CameraItem*/;

    class SelectedItemAdapter extends android.widget.ArrayAdapter<CameraItem>
      {
        final int ResID;
        final android.view.LayoutInflater TemplateInflater;
        CameraItem CurSelected;
        RadioButton LastChecked;

        class OnSetCheck implements View.OnClickListener
          {
            final CameraItem MyItem;

            public OnSetCheck
              (
                CameraItem TheItem
              )
              {
                MyItem = TheItem;
              } /*OnSetCheck*/

            public void onClick
              (
                View TheView
              )
              {
                if (MyItem != CurSelected)
                  {
                  /* only allow one item to be selected at a time */
                    if (CurSelected != null)
                      {
                        CurSelected.Selected = false;
                        LastChecked.setChecked(false);
                      } /*if*/
                    LastChecked =
                        TheView instanceof RadioButton ?
                            (RadioButton)TheView
                        :
                            (RadioButton)
                            ((ViewGroup)TheView).findViewById(R.id.item_checked);
                    CurSelected = MyItem;
                    MyItem.Selected = true;
                    LastChecked.setChecked(true);
                  } /*if*/
              } /*onClick*/
          } /*OnSetCheck*/

        SelectedItemAdapter
          (
            android.content.Context TheContext,
            int ResID,
            android.view.LayoutInflater TemplateInflater
          )
          {
            super(TheContext, ResID);
            this.ResID = ResID;
            this.TemplateInflater = TemplateInflater;
            CurSelected = null;
            LastChecked = null;
          } /*SelectedItemAdapter*/

        @Override
        public View getView
          (
            int Position,
            View ReuseView,
            ViewGroup Parent
          )
          {
            View TheView = ReuseView;
            if (TheView == null)
              {
                TheView = TemplateInflater.inflate(ResID, null);
              } /*if*/
            final CameraItem ThisItem = (CameraItem)this.getItem(Position);
            ((android.widget.TextView)TheView.findViewById(R.id.camera_id)).setText
              (
                Integer.toString(ThisItem.CameraID)
              );
            ((android.widget.TextView)TheView.findViewById(R.id.camera_facing)).setText
              (
                ThisItem.TheCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK ?
                    getString(R.string.back)
                : ThisItem.TheCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT ?
                    getString(R.string.front)
                :
                    "?" + Integer.toString(ThisItem.TheCameraInfo.facing)
              );
            ((android.widget.TextView)TheView.findViewById(R.id.camera_orientation)).setText
              (
                Integer.toString(ThisItem.TheCameraInfo.orientation) + "°"
              );
            final RadioButton ThisChecked = (RadioButton)TheView.findViewById(R.id.item_checked);
            ThisChecked.setChecked(ThisItem.Selected);
            if (ThisItem.Selected)
              {
                CurSelected = ThisItem;
                LastChecked = ThisChecked;
              } /*if*/
            final OnSetCheck ThisSetCheck = new OnSetCheck(ThisItem);
            ThisChecked.setOnClickListener(ThisSetCheck);
              /* otherwise radio button can get checked but I don't notice */
            TheView.setOnClickListener(ThisSetCheck);
            return
                TheView;
          } /*getView*/

      } /*SelectedItemAdapter*/;

    @Override
    public boolean dispatchKeyEvent
      (
        KeyEvent TheEvent
      )
      {
        boolean Handled = false;
        if
          (
                TheEvent.getAction() == KeyEvent.ACTION_UP
            &&
                TheEvent.getKeyCode() == KeyEvent.KEYCODE_BACK
          )
          {
            if (TheCameras.CurSelected != null)
              {
                setResult
                  (
                    android.app.Activity.RESULT_OK,
                    new android.content.Intent()
                        .putExtra(CameraIDID, TheCameras.CurSelected.CameraID)
                  );
              } /*if*/
            finish();
            Handled = true;
          } /*if*/
        if (!Handled)
          {
            Handled = super.dispatchKeyEvent(TheEvent);
          } /*if*/
        return
            Handled;
      } /*dispatchKeyEvent*/

    @Override
    public void onCreate
      (
        android.os.Bundle SavedInstanceState
      )
      {
        super.onCreate(SavedInstanceState);
        Current = this;
        setContentView(R.layout.camera_list);
        TheCameras = new SelectedItemAdapter(this, R.layout.camera_list_item, getLayoutInflater());
        TheCameras.clear();
        for (int i = 0; i < Camera.getNumberOfCameras(); ++i)
          {
            TheCameras.add(new CameraItem(i, i == CurCameraID));
          } /*for*/
        TheCameras.notifyDataSetChanged(); /* is this necessary? */
        CameraListView = (android.widget.ListView)findViewById(R.id.list);
        CameraListView.setAdapter(TheCameras);
      } /*onCreate*/

    @Override
    public void onDestroy()
      {
        Current = null;
        super.onDestroy();
      } /*onDestroy*/

    public static void Launch
      (
        android.app.Activity Caller,
        int RequestCode,
        int CurCameraID
      )
      {
        if (!Reentered)
          {
            Reentered = true; /* until CameraList activity terminates */
            CameraList.CurCameraID = CurCameraID;
            Caller.startActivityForResult
              (
                new android.content.Intent(android.content.Intent.ACTION_PICK)
                    .setClass(Caller, CameraList.class),
                RequestCode
              );
          }
        else
          {
          /* can happen if user gets impatient and selects from menu twice, just ignore */
          } /*if*/
      } /*Launch*/

    public static void Cleanup()
      /* Client must call this to do explicit cleanup; I tried doing it in
        onDestroy, but of course that gets called when user rotates screen,
        which means picker context is lost. */
      {
        Reentered = false;
      } /*Cleanup*/

  } /*CameraList*/;

