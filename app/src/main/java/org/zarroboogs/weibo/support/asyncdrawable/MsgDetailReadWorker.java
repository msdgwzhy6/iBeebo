
package org.zarroboogs.weibo.support.asyncdrawable;

import org.zarroboogs.utils.ImageUtility;
import org.zarroboogs.utils.file.FileDownloaderHttpHelper;
import org.zarroboogs.utils.file.FileLocationMethod;
import org.zarroboogs.utils.file.FileManager;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.gallery.GalleryAnimationActivity;
import org.zarroboogs.weibo.support.lib.AnimationRect;
import org.zarroboogs.weibo.widget.WeiboDetailImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * insert progress update listener into download worker if it exists or
 * create a new download worker
 */
public class MsgDetailReadWorker extends MyAsyncTask<Void, Integer, String> {

    private WeiboDetailImageView view;

    private ProgressBar pb;

    private Button retry;

    private MessageBean msg;

    public MsgDetailReadWorker(WeiboDetailImageView view, MessageBean msg) {
        this.view = view;
        this.pb = this.view.getProgressBar();
        this.msg = msg;
        this.retry = view.getRetryButton();
        retry.setVisibility(View.INVISIBLE);

        String oriPath = FileManager.getFilePathFromUrl(msg.getOriginal_pic(), FileLocationMethod.picture_large);

        if (ImageUtility.isThisBitmapCanRead(oriPath) && TaskCache.isThisUrlTaskFinished(msg.getOriginal_pic())) {

            onPostExecute(oriPath);
            cancel(true);
            return;
        }

        String middlePath = FileManager.getFilePathFromUrl(msg.getBmiddle_pic(), FileLocationMethod.picture_bmiddle);

        if (ImageUtility.isThisBitmapCanRead(middlePath) && TaskCache.isThisUrlTaskFinished(msg.getBmiddle_pic())) {
            onPostExecute(middlePath);
            cancel(true);
            return;
        }

        pb.setVisibility(View.VISIBLE);
        pb.setIndeterminate(true);

    }

    public void setView(WeiboDetailImageView view) {
        this.view = view;
        this.pb = this.view.getProgressBar();
        this.retry = view.getRetryButton();
        retry.setVisibility(View.INVISIBLE);
    }

    @Override
    protected String doInBackground(Void... arg) {
        if (isCancelled()) {
            return null;
        }

        if (SettingUtils.getEnableBigPic()) {
            boolean result = TaskCache.waitForPictureDownload(msg.getOriginal_pic(), downloadListener,
                    FileManager.generateDownloadFileName(msg.getOriginal_pic()), FileLocationMethod.picture_large);

            return result ? FileManager.getFilePathFromUrl(msg.getOriginal_pic(), FileLocationMethod.picture_large) : null;
        } else {
            boolean result = TaskCache.waitForPictureDownload(msg.getBmiddle_pic(), downloadListener,
                    FileManager.generateDownloadFileName(msg.getBmiddle_pic()), FileLocationMethod.picture_bmiddle);

            return result ? FileManager.getFilePathFromUrl(msg.getBmiddle_pic(), FileLocationMethod.picture_bmiddle) : null;
        }

    }

    FileDownloaderHttpHelper.DownloadListener downloadListener = new FileDownloaderHttpHelper.DownloadListener() {

        @Override
        public void pushProgress(int progress, int max) {
            onProgressUpdate(progress, max);
        }
    };

    @Override
    protected void onProgressUpdate(final Integer... values) {
        super.onProgressUpdate(values);
        if (this.getStatus() == Status.RUNNING) {
        	mHadler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
		            pb.setVisibility(View.VISIBLE);
		            pb.setIndeterminate(false);

		            Integer progress = values[0];
		            Integer max = values[1];

		            pb.setMax(max);
		            pb.setProgress(progress);
				}
			});


        }
    }

    Handler mHadler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCancelled(String bitmap) {
        pb.setVisibility(View.INVISIBLE);
        super.onCancelled(bitmap);
    }

    @Override
    protected void onPostExecute(String path) {
        retry.setVisibility(View.INVISIBLE);
        pb.setIndeterminate(true);

        if (!TextUtils.isEmpty(path)) {

            if (!path.endsWith(".gif")) {
                readNormalPic(path);
            } else {
                view.setGif(path);
            }
            pb.setVisibility(View.INVISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AnimationRect rect = AnimationRect.buildFromImageView(view.getImageView());
                    ArrayList<AnimationRect> animationRectArrayList = new ArrayList<AnimationRect>();
                    animationRectArrayList.add(rect);
                    Intent intent = GalleryAnimationActivity.newIntent(msg, animationRectArrayList, 0);
                    BeeboApplication.getInstance().getActivity().startActivity(intent);
                }
            });
        } else {
            pb.setVisibility(View.INVISIBLE);
            view.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
            retry.setVisibility(View.VISIBLE);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MsgDetailReadWorker picTask = new MsgDetailReadWorker(view, msg);
                    picTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
        }

    }

    private void readNormalPic(String path) {

        Bitmap bitmap = ImageUtility.readNormalPic(path, 2000, 2000);

        view.setTag(true);
        view.getImageView().setTag(true);
        view.setVisibility(View.VISIBLE);
        view.setImageBitmap(bitmap);
        view.setAlpha(0.0f);
        view.animate().alpha(1.0f).setDuration(200);
    }

}
