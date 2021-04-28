@file:Suppress("MayBeConstant")

package kotlinx.serialization.features

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("MemberVisibilityCanBePrivate", "unused", "ComplexRedundantLet")
class PropertyExternalInitializerTest {
    data class ExternalClass(
        val valProperty: Int,
        var varProperty: Int,
        val literalConst: Int = 3,
        val globalVarRef: Int = globalVar,
        val computed: Int = valProperty + varProperty + 2,
        val doubleRef: Int = literalConst + literalConst,
        var globalFun: Int = globalFun(),
        var globalFunExpr: Int = globalFun() + 1,
        val itExpr: Int = literalConst.let { it + 6 },
        @Transient val constTransient: Int = 6,
        @Transient val serializedRefTransient: Int = varProperty + 1,
        @Transient val refTransient: Int = serializedRefTransient,
        val transientRefFromProp: Int = constTransient + 4,
    ) {
        val valGetter: Int get() { return 5 }
        var bodyProp: Int = 11
        var dependBodyProp: Int = bodyProp + 1
        var getterDepend: Int = valGetter + 8
    }

    @Serializer(ExternalClass::class)
    object ExternalSerializer

    private val format = Json { encodeDefaults = true; prettyPrint = true }

    @Test
    fun testExternalSerializeDefault() {
        val encoded = format.encodeToString(ExternalSerializer, ExternalClass(1, 2))
        assertEquals(PROPERTY_INITIALIZER_JSON, encoded)
    }

    @Test
    fun testExternalDeserializeDefault() {
        val decoded = format.decodeFromString(ExternalSerializer,"""{"valProperty": 5, "varProperty": 6}""")
        assertEquals(ExternalClass(5, 6), decoded)
    }
}
