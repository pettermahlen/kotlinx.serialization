/*
 * Copyright 2017-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization

import kotlin.test.*

class CachedSerializersTest {
    @Serializable
    object Object

    @Serializable
    sealed class SealedParent {
        @Serializable
        data class Child(val i: Int): SealedParent()
    }

    @Serializable
    abstract class Abstract

    @Test
    fun testObjectSerializersAreSame() {
        assertSame(Object.serializer(), Object.serializer())
    }

    @Test
    fun testSealedSerializersAreSame() {
        assertSame(SealedParent.serializer(), SealedParent.serializer())
    }

    @Test
    fun testAbstractSerializersAreSame() {
        assertSame(Abstract.serializer(), Abstract.serializer())
    }
}
