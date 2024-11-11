/*
 * Copyright 2012 - 2014 Benjamin Weiss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.synchroteam.customtoast;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

// TODO: Auto-generated Javadoc
/** Builds the default animations for showing and hiding a {@link Crouton}. */
final class DefaultAnimationsBuilder {
  
  /** The Constant DURATION. */
  private static final long DURATION = 400;
  
  /** The slide out up animation. */
  private static Animation slideInDownAnimation, slideOutUpAnimation;
  
  /** The last out animation height. */
  private static int lastInAnimationHeight, lastOutAnimationHeight;

  /**
   * Instantiates a new default animations builder.
   */
  private DefaultAnimationsBuilder() {
    /* no-op */
  }

  /**
   * Builds the default slide in down animation.
   *
   * @param croutonView   The croutonView which gets animated.
   * @return The default Animation for a showing {@link Crouton}.
   */
  static Animation buildDefaultSlideInDownAnimation(View croutonView) {
    if (!areLastMeasuredInAnimationHeightAndCurrentEqual(croutonView) || (null == slideInDownAnimation)) {
      slideInDownAnimation = new TranslateAnimation(
        0, 0,                               // X: from, to
        -croutonView.getMeasuredHeight(), 0 // Y: from, to
      );
      slideInDownAnimation.setDuration(DURATION);
      setLastInAnimationHeight(croutonView.getMeasuredHeight());
    }
    return slideInDownAnimation;
  }

  /**
   * Builds the default slide out up animation.
   *
   * @param croutonView   The croutonView which gets animated.
   * @return The default Animation for a hiding {@link Crouton}.
   */
  static Animation buildDefaultSlideOutUpAnimation(View croutonView) {
    if (!areLastMeasuredOutAnimationHeightAndCurrentEqual(croutonView) || (null == slideOutUpAnimation)) {
      slideOutUpAnimation = new TranslateAnimation(
        0, 0,                               // X: from, to
        0, -croutonView.getMeasuredHeight() // Y: from, to
      );
      slideOutUpAnimation.setDuration(DURATION);
      setLastOutAnimationHeight(croutonView.getMeasuredHeight());
    }
    return slideOutUpAnimation;
  }

  /**
   * Are last measured in animation height and current equal.
   *
   * @param croutonView the crouton view
   * @return true, if successful
   */
  private static boolean areLastMeasuredInAnimationHeightAndCurrentEqual(View croutonView) {
    return areLastMeasuredAnimationHeightAndCurrentEqual(lastInAnimationHeight, croutonView);
  }

  /**
   * Are last measured out animation height and current equal.
   *
   * @param croutonView the crouton view
   * @return true, if successful
   */
  private static boolean areLastMeasuredOutAnimationHeightAndCurrentEqual(View croutonView) {
    return areLastMeasuredAnimationHeightAndCurrentEqual(lastOutAnimationHeight, croutonView);
  }

  /**
   * Are last measured animation height and current equal.
   *
   * @param lastHeight the last height
   * @param croutonView the crouton view
   * @return true, if successful
   */
  private static boolean areLastMeasuredAnimationHeightAndCurrentEqual(int lastHeight, View croutonView) {
    return lastHeight == croutonView.getMeasuredHeight();
  }

  /**
   * Sets the last in animation height.
   *
   * @param lastInAnimationHeight the new last in animation height
   */
  private static void setLastInAnimationHeight(int lastInAnimationHeight) {
    DefaultAnimationsBuilder.lastInAnimationHeight = lastInAnimationHeight;
  }

  /**
   * Sets the last out animation height.
   *
   * @param lastOutAnimationHeight the new last out animation height
   */
  private static void setLastOutAnimationHeight(int lastOutAnimationHeight) {
    DefaultAnimationsBuilder.lastOutAnimationHeight = lastOutAnimationHeight;
  }
}
