/*
 * Copyright (C) 2026 DevEmperor (Dictate)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package dev.patrickgold.florisboard.ime.nlp.latin

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The bundled Spanish dictionary is Rioplatense-enhanced: the voseo forms the stock Peninsular list
 * lacks are present and, for the everyday ones, above the silent-correction floor. The list was built
 * from the same OPUS frequencies as the shipped es.json, with the RLA-ES es_AR word set as the
 * spelling oracle; this test holds the result to what the keyboard needs from it.
 */
class SpanishDictionaryTest {

    private val dict: Map<String, Int> by lazy { EvalKeyboard.readDict("es.json") }

    @Test
    fun `voseo forms are present and the everyday ones clear the floor`() {
        for (word in listOf(
            "tenés", "podés", "querés", "sabés", "hacés", "hablás", "comés",
            "vivís", "escribís", "venís", "salís", "andás", "opinás",
        )) {
            val freq = dict[word]
            assertTrue(
                freq != null && freq >= AutoCommitGate.MIN_FREQ,
                "$word is missing or below the floor (freq=$freq)",
            )
        }
    }

    @Test
    fun `the imperatives people actually write are there`() {
        for (word in listOf("andá", "mirá", "tomá", "vení", "hacé", "decí", "poné", "decile", "hacelo")) {
            assertTrue(dict[word] != null, "$word is missing")
        }
    }

    @Test
    fun `rioplatense vocabulary is there`() {
        for (word in listOf(
            "laburo", "bondi", "subte", "finde", "celu", "compu", "quilombo",
            "pibe", "boludo", "guita", "copado", "che", "nomás",
        )) {
            assertTrue(dict[word] != null, "$word is missing")
        }
    }

    @Test
    fun `typos and corpus noise did not make it in`() {
        for (word in listOf("oponás", "depués", "através", "erás", "tenes", "podes")) {
            assertTrue(dict[word] == null, "$word should not be in the dictionary")
        }
    }

    @Test
    fun `every frequency is on the expected scale`() {
        assertTrue(dict.size > 65_982, "the enhanced list should add words, not replace them")
        for ((word, freq) in dict) {
            assertTrue(word.isNotBlank(), "empty word")
            assertTrue(freq in 128..255, "$word has frequency $freq outside 128..255")
        }
    }
}
