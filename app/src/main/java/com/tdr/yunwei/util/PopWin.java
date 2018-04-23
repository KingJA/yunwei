package com.tdr.yunwei.util;

/**
 * Created by Administrator on 2016/4/26.
 */
public class PopWin {




//    public static void PopList(final Activity mActivity, final TextView txt_view, final List<String> list) {
//
//
//        View contentView = LayoutInflater.from(mActivity).inflate(
//                R.layout.pop_lv_guzhang, null);
//
//
//        final PopupWindow popWin = new PopupWindow(
//                contentView,ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//
//
//
//        popWin.setTouchable(true);
//        popWin.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//
//        popWin.setTouchInterceptor(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
//
//
//        popWin.setBackgroundDrawable(mActivity.getResources().getDrawable(
//                R.color.transparent));
//        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
//        lp.alpha = 0.5f;
//        mActivity.getWindow().setAttributes(lp);
//        popWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
//
//            @Override
//            public void onDismiss() {
//                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
//                lp.alpha = 1f;
//                mActivity.getWindow().setAttributes(lp);
//
//            }
//        });
//
//        popWin.showAtLocation(txt_view, Gravity.CENTER, 0, 0);
//
//
//        if(list.size()==0){
//            TextView txt_nodata=(TextView)contentView.findViewById(R.id.txt_nodata);
//            txt_nodata.setVisibility(View.VISIBLE);
//
//
//        }else {
//
//            StringAdapter adapter = new StringAdapter(mActivity, list, txt_view.getText().toString());
//            ListView lv = (ListView) contentView.findViewById(R.id.lv);
//            lv.setAdapter(adapter);
//            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                    txt_view.setText(list.get(position));
//                    popWin.dismiss();
//
//                }
//            });
//        }
//    }






}
