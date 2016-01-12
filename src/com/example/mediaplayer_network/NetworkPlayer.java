package com.example.mediaplayer_network;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.view.ViewGroup;
 public class NetworkPlayer extends Fragment implements
		View.OnClickListener, OnCompletionListener, OnPreparedListener {
	private NetWorkAudioPlayer netWorkAudioPlayer;

	private ImageButton txt;
	
	private TextView songWord;
	private ImageButton playButton;
	private ImageButton preButton;
	private ImageButton nextButton;
	private ImageButton loopButton;
	private SeekBar seekBar;
	private TextView startTextView;
	private TextView endTextView;
	private static final String INITSTRING_STRING = "0:00";
	boolean pause = true;
	private Intent intent;
	private View view;
	private static int totalTime,startTime,endTime,minutes;
	
	
	public NetworkPlayer(){
		
	}
	
	
	
	/*
	 * handler ��Ҫ�������̷߳��͵�����, ���ô�����������̸߳���UI.
	 * 
	 * �����ʱ��Ҫһ����ʱ�Ĳ���������: ������ȡ����,���߶�ȡ���ؽϴ��һ���ļ���ʱ���㲻�ܰ���Щ��������
	 * ���߳��У��������������߳��еĻ����������ּ�������, ���5���ӻ�û����ɵĻ��������յ�Android ϵͳ��һ��������ʾ "ǿ�ƹر�".
	 * ���ʱ��������Ҫ����Щ��ʱ�Ĳ���������һ�����߳���,��Ϊ���߳��漰��
	 * UI���£���Android���߳����̲߳���ȫ�ģ�Ҳ����˵������UIֻ�������߳��и��£����߳��в�����Σ�յ�.
	 * ���ʱ��Handler�ͳ�����.,�����������ӵ����� , ����Handler���������߳���(UI�߳���),
	 * �������߳̿���ͨ��Message��������������, ���ʱ��Handler�ͳе��Ž������̴߳�������(���߳���
	 * sendMessage()��������)Message����(�����������) , ����Щ��Ϣ�������̶߳����У������ �߳̽��и���UI��
	 */

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (netWorkAudioPlayer != null
						&& netWorkAudioPlayer.isPlaying()) {

					  totalTime = netWorkAudioPlayer.getDuration();// ��ȡʱ��
					int currentTime = netWorkAudioPlayer.getCurrentPosition();// //��ȡ��ǰ���ŵ�
					endTextView.setText(getTimeText(totalTime));
					int seekBarMax = seekBar.getMax();
					if (totalTime > 0 && currentTime > 0 && seekBarMax > 0) {
						startTextView.setText(getTimeText(currentTime));
						seekBar.setProgress((int) (seekBarMax
								* (float) currentTime / totalTime));// ���½�����
					}
				}
				break;

			}
		}

		private String getTimeText(int time) {
			// TODO Auto-generated method stub
			/*
			 * �����time������λΪmilliseconds�������� ����������Խ����뵥λ��ʱ��ת��Ϊ0��00��ʽ��ʱ��
			 */
			int totalSeconds = time / 1000;// ����ת��Ϊ��
			 minutes = totalSeconds / 60;// ת��Ϊ����
			int seconds = totalSeconds % 60;// ת��Ϊ��
			String showTime;
			if (seconds > 9 && seconds < 60) {
				showTime = minutes + ":" + seconds;
			} else {
				showTime = minutes + ":0" + seconds;

			}
			return showTime;
		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view=inflater.inflate(R.layout.tab_networkplayer, null);
		netWorkAudioPlayer = new NetWorkAudioPlayer(getActivity());
		initView();
		setListener();
		UpdateSeekBarThread thread = new UpdateSeekBarThread();
		thread.start();
		return view;
	}
	
	@Override
	public  void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		netWorkAudioPlayer.release();
		netWorkAudioPlayer = null;
		if (intent != null) {
			getActivity().stopService(intent);
		}
		super.onDestroy();
	}

	public void initView() {
		playButton = (ImageButton) view.findViewById(R.id.playButton);
		preButton = (ImageButton) view.findViewById(R.id.preButton);
		nextButton = (ImageButton) view.findViewById(R.id.nextButton);

//		back = (ImageButton) view.findViewById(R.id.back);
		txt = (ImageButton) view.findViewById(R.id.txt_change);
		songWord=(TextView) view.findViewById(R.id.songWord);
		seekBar = (SeekBar) view.findViewById(R.id.seekBar);
		startTextView = (TextView) view.findViewById(R.id.startTextView);
		endTextView = (TextView) view.findViewById(R.id.endTextView);
	}

	public void setListener() {
		playButton.setOnClickListener(this);
		preButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		
		txt.setOnClickListener(this);
		netWorkAudioPlayer.setOnPreparedListener(this);
		netWorkAudioPlayer.setOnCompletionListener(this);
		seekBar.setOnSeekBarChangeListener(new ProgressBarListener());

	}

	private void setOnSeekBarChangeListener(
			ProgressBarListener progressBarListener) {
		// TODO Auto-generated method stub

	}

	// ��ʱ������ʼ���߳�
	public class UpdateSeekBarThread extends Thread {
		public void run() {
			while (netWorkAudioPlayer != null) {
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
			}
		}
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		netWorkAudioPlayer.start();
		netWorkAudioPlayer.setPaused(false);
		netWorkAudioPlayer.setPrepared(true);
	}

	@Override
	public void onCompletion(MediaPlayer mediaPlayer) {
		// TODO Auto-generated method stub

	}

	private class ProgressBarListener implements OnSeekBarChangeListener {

		@Override
		/*
		 * ���� seekBar The SeekBar whose progress has changed progress The
		 * current progress level. This will be in the range 0..max where max
		 * was set by setMax(int). (The default value for max is 100.) fromUser
		 * True if the progress change was initiated by the user.
		 */
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if (fromUser) {
				netWorkAudioPlayer.seekTo((int) (netWorkAudioPlayer
						.getDuration() * (float) progress / seekBar.getMax()));
				seekBar.setProgress(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.playButton:
			if (pause) {
				netWorkAudioPlayer.play();
				playButton.setBackgroundResource(R.drawable.pause);
				pause=false;
			} else {
				netWorkAudioPlayer.playPause();
				playButton.setBackgroundResource(R.drawable.play);
				pause=true;
			}
			break;

		case R.id.preButton:
			netWorkAudioPlayer.playPrevious();
			// playButton.setText(PAUSE);
			break;
		case R.id.nextButton:
			netWorkAudioPlayer.playNext();
			// playButton.setText(PAUSE);
			break;
		
			
		case R.id.txt:
			songWord.setText("���ڼ��ظ�ʡ�����");
			
		}
		intent = new Intent(getActivity(), Myservice.class);
		intent.putExtra("songName", "This is networkPlayer's song.");
		getActivity().startService(intent);
	}
	
	
	
	
	public void TimeChange(){
		minutes=totalTime/60000;
		if(minutes<=1){
			
		}else if(minutes>1&&minutes<=2){
			
		}else if(minutes>2&&minutes<=3){
			
		}else if(minutes>3){
			
		}
	}

}
