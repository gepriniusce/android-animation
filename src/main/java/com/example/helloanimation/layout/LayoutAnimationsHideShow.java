/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.helloanimation.layout;

import com.example.helloanimation.R;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.widget.LinearLayout;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

/**
 * This application demonstrates how to use LayoutTransition to automate
 * transition animations
 * as items are hidden or shown in a container.
 */
public class LayoutAnimationsHideShow extends Activity {

	private ViewGroup container = null;
	private LayoutTransition mTransitioner;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_animations_hideshow);

		final CheckBox hideGoneCB = (CheckBox) findViewById(R.id.hideGoneCB);

		container = new LinearLayout(this);
		container.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		// Add a slew of buttons to the container. We won't add any more buttons
		// at runtime, but
		// will just show/hide the buttons we've already created
		for (int i = 0; i < 4; ++i) {
			Button newButton = new Button(this);
			newButton.setText(String.valueOf(i));
			container.addView(newButton);
			newButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					// 如果Hide（GONE）被选中：View被点击之后是GONE
					// 如果Hide（GONE）没有被选中：View被点击之后是INVISIBLE，其他View不移动
					v.setVisibility(hideGoneCB.isChecked() ? View.GONE
							: View.INVISIBLE);
				}
			});
		}

		// 重置Transition：重新生成LayoutTransition对象并设置给container
		resetTransition();

		ViewGroup parent = (ViewGroup) findViewById(R.id.parent);
		parent.addView(container);

		Button addButton = (Button) findViewById(R.id.addNewButton);
		addButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for (int i = 0; i < container.getChildCount(); ++i) {
					View view = (View) container.getChildAt(i);
					view.setVisibility(View.VISIBLE);
				}
			}
		});

		CheckBox customAnimCB = (CheckBox) findViewById(R.id.customAnimCB);
		customAnimCB
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						long duration;
						if (isChecked) {
							// 设置延迟时间
							mTransitioner.setStagger(
									LayoutTransition.CHANGE_APPEARING, 30);
							mTransitioner.setStagger(
									LayoutTransition.CHANGE_DISAPPEARING, 30);
							// 建立自定义动画
							setupCustomAnimations();
							duration = 500;
						}
						else {
							// 重置Transition
							resetTransition();
							duration = 300;
						}
						mTransitioner.setDuration(duration);
					}
				});
	}

	// 重新生成LayoutTransition对象并设置给container
	private void resetTransition() {
		mTransitioner = new LayoutTransition();
		container.setLayoutTransition(mTransitioner);
	}

	// 生成自定义动画
	private void setupCustomAnimations() {
		// 动画：CHANGE_APPEARING
		// Changing while Adding
		PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", 0, 1);
		PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top", 0, 1);
		PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right", 0,
				1);
		PropertyValuesHolder pvhBottom = PropertyValuesHolder.ofInt("bottom",
				0, 1);
		PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX",
				1f, 0f, 1f);
		PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY",
				1f, 0f, 1f);

		final ObjectAnimator changeIn = ObjectAnimator.ofPropertyValuesHolder(
				this, pvhLeft, pvhTop, pvhRight, pvhBottom, pvhScaleX,
				pvhScaleY).setDuration(
				mTransitioner.getDuration(LayoutTransition.CHANGE_APPEARING));
		mTransitioner.setAnimator(LayoutTransition.CHANGE_APPEARING, changeIn);
		changeIn.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setScaleX(1f);
				view.setScaleY(1f);
			}
		});

		// 动画：CHANGE_DISAPPEARING
		// Changing while Removing
		Keyframe kf0 = Keyframe.ofFloat(0f, 0f);
		Keyframe kf1 = Keyframe.ofFloat(.9999f, 360f);
		Keyframe kf2 = Keyframe.ofFloat(1f, 0f);
		PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe(
				"rotation", kf0, kf1, kf2);
		final ObjectAnimator changeOut = ObjectAnimator
				.ofPropertyValuesHolder(this, pvhLeft, pvhTop, pvhRight,
						pvhBottom, pvhRotation)
				.setDuration(
						mTransitioner
								.getDuration(LayoutTransition.CHANGE_DISAPPEARING));
		mTransitioner.setAnimator(LayoutTransition.CHANGE_DISAPPEARING,
				changeOut);
		changeOut.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setRotation(0f);
			}
		});

		// 动画：APPEARING
		// Adding
		ObjectAnimator animIn = ObjectAnimator.ofFloat(null, "rotationY", 90f,
				0f).setDuration(
				mTransitioner.getDuration(LayoutTransition.APPEARING));
		mTransitioner.setAnimator(LayoutTransition.APPEARING, animIn);
		animIn.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setRotationY(0f);
			}
		});

		// 动画：DISAPPEARING
		// Removing
		ObjectAnimator animOut = ObjectAnimator.ofFloat(null, "rotationX", 0f,
				90f).setDuration(
				mTransitioner.getDuration(LayoutTransition.DISAPPEARING));
		mTransitioner.setAnimator(LayoutTransition.DISAPPEARING, animOut);
		animOut.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setRotationX(0f);
			}
		});

	}
}