package com.see.game.game_pintu;

import com.see.game.view.GamePintuLayout;
import com.see.game.view.GamePintuListener;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Menu;

public class MainActivity extends Activity {

	private GamePintuLayout mGamePintuLayout;

	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGamePintuLayout = (GamePintuLayout) findViewById(R.id.id_gamepintu);
		mGamePintuLayout.setOnGamePintuListener(new GamePintuListener() {

			@Override
			public void timechanged(int currentTime) {
				// TODO Auto-generated method stub
			}

			@Override
			public void nextLevel(int nextLevel) {

				new AlertDialog.Builder(MainActivity.this)
						.setTitle("Game Info").setMessage("LEVEL UP!!!")
						.setPositiveButton("NEXT LEVEL", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								mGamePintuLayout.nextLevel();
								
							}
						}).show();
			}

			@Override
			public void gameover() {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
