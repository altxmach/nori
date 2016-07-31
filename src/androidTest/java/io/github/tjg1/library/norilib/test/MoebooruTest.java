/*
 * This file is part of nori.
 * Copyright (c) 2014 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 * License: ISC
 */

package io.github.tjg1.library.norilib.test;

import io.github.tjg1.library.norilib.clients.DanbooruLegacy;
import io.github.tjg1.library.norilib.clients.SearchClient;

/** Tests support for Moebooru-based boards in the DanbooruLegacy client. */
// Moebooru support is currently broken or requires authentication.
public class MoebooruTest {
//public class MoebooruTest extends SearchClientTestCase {

  //@Override
  protected SearchClient createSearchClient() {
    return new DanbooruLegacy("yande.re", "https://yande.re");
  }
}
