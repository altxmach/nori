/*
 * This file is part of nori.
 * Copyright (c) 2014 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 * License: ISC
 */

package com.cuddlesoft.nori.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.cuddlesoft.nori.api.Image;
import com.cuddlesoft.nori.api.Tag;

import java.util.Locale;
import java.util.regex.Pattern;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests and utilities for testing the {@link com.cuddlesoft.nori.api.Image} class.
 */
public class ImageTests extends AndroidTestCase {

  /** LogCat tag. */
  private static final String TAG = "com.cuddlesoft.nori.test.ImageTests";

  /**
   * RegEx Pattern used for matching URLs.
   * Courtesy of John Gruber (http://daringfireball.net/2010/07/improved_regex_for_matching_urls)
   * (Public Domain)
   */
  private static final Pattern urlPattern = Pattern.compile("(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))");
  /** RegEx pattern for matching numerical Strings (used for IDs). */
  private static final Pattern integerPattern = Pattern.compile("^\\d+$");

  // TODO: Check parceling images.

  /**
   * Verify validity of an {@link com.cuddlesoft.nori.api.Image} object.
   * Used to ensure that Image values returned by each individual API client are correct.
   *
   * @throws Throwable Assertion failure.
   */
  public static void verifyImage(Image image) throws Throwable {
    // Verify URLs.
    assertThat(image.fileUrl).matches(urlPattern);
    assertThat(image.previewUrl).matches(urlPattern);
    assertThat(image.sampleUrl).matches(urlPattern);
    assertThat(image.webUrl).matches(urlPattern);

    // Verify image sizes are set and that they are positive integers.
    assertThat(image.width).isPositive();
    assertThat(image.height).isPositive();
    if (image.previewWidth == 0)
      Log.w(TAG, String.format(Locale.US, "Preview width was 0 for image: %s", image.webUrl));
    assertThat(image.previewWidth).isGreaterThanOrEqualTo(0);
    if (image.previewHeight == 0)
      Log.w(TAG, String.format(Locale.US, "Preview height was 0 for image: %s", image.webUrl));
    assertThat(image.previewHeight).isGreaterThanOrEqualTo(0);
    if (image.sampleWidth == 0)
      Log.w(TAG, String.format(Locale.US, "Sample width was 0 for image: %s", image.webUrl));
    assertThat(image.sampleWidth).isGreaterThanOrEqualTo(0);
    if (image.sampleHeight == 0)
      Log.w(TAG, String.format(Locale.US, "Sample height was 0 for image: %s", image.webUrl));
    assertThat(image.sampleHeight).isGreaterThanOrEqualTo(0);

    // Verify tags.
    if (image.tags.length == 0)
      Log.w(TAG, String.format(Locale.US, "No tags for image: %s", image.webUrl));
    for (Tag tag : image.tags) {
      assertThat(tag.getName()).isNotEmpty();
      assertThat(tag.getType()).isNotNull();
    }

    // Verify numerical strings (IDs)
    assertThat(image.id).isNotEmpty().matches(integerPattern);
    if (image.parentId != null && !image.parentId.isEmpty())
      assertThat(image.parentId).matches(integerPattern);
    else
      Log.w(TAG, String.format(Locale.US, "No parent ID for image: %s", image.webUrl));
    if (image.pixivId != null)
      assertThat(image.pixivId).isNotEmpty().matches(integerPattern);
    else
      Log.w(TAG, String.format(Locale.US, "No Pixiv ID for image: %s", image.webUrl));

    // Misc stuff.
    assertThat(image.obscenityRating).isNotNull();
    assertThat(image.score).isGreaterThanOrEqualTo(0);
    if (image.source == null || image.source.isEmpty())
      Log.w(TAG, String.format(Locale.US, "No source for image: %s", image.webUrl));
    else
      assertThat(image.source).isNotEmpty();
    assertThat(image.md5).hasSize(32); // MD5 hashes are always 32 characters long.
    assertThat(image.createdAt).isNotNull();
  }

}
